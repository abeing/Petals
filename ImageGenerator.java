package net.folds.flower.wallpaper;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.geom.Point2D;
import java.util.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;


public class ImageGenerator {
    
    public static void main(String[] args) throws Exception {
        String flowerFileName = "flower.jpg";
        
        List<String> argsList = Arrays.asList(args);
        String fileArgName = "--fileName";
        // If --fileName is present
        if(argsList.contains(fileArgName)) {
            // and if --fileName is not the last argument
            if(argsList.size() > argsList.indexOf(fileArgName)) {
                flowerFileName = argsList.get(argsList.indexOf(fileArgName) + 1);
            }
        }
        
        if(argsList.contains("--alternateFileNames")) {
            Calendar calendar = Calendar.getInstance();
            int fileNameModifier = calendar.get(Calendar.DAY_OF_YEAR) % 2;
            
            int dotLocation = flowerFileName.indexOf('.');
            flowerFileName = flowerFileName.substring(0, dotLocation) + fileNameModifier + flowerFileName.substring(dotLocation, flowerFileName.length());
            
        }
        
        createImage(flowerFileName);
    }
    
    public static void createImage(String fileName) throws Exception {
        int imageEdgeSize = 2048;
        
        BufferedImage bufferedImage = new BufferedImage(imageEdgeSize, imageEdgeSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setBackground(Color.WHITE);
        
        GradientPaint paint;
        
        Point2D flowerOrigin = new Point2D.Double(imageEdgeSize/2, imageEdgeSize/2);
        
        // The dull way to get phi.  The entertaining way is to divide a fibonacci number by its predecessor in the sequence.
        double phi = (1.0 + Math.sqrt(5))/2.0;
        double angleBetweenPetals = 360.0/phi;
        
        
        Flower flower = new Flower(flowerOrigin.getX(), flowerOrigin.getY(), 10, 1000, angleBetweenPetals);
        
        List<Path2D> paths = flower.getPetalPaths();
        List<Point2D> tipPoints = flower.getPetalTipPoints();
        // Petals appear from the inside toward the outside,
        // but they need to be drawn in the other order so that they all show.
        Collections.reverse(paths);
        Collections.reverse(tipPoints);
        
        Point2D tip;
        Point2D intermediatePaintPoint;
        
        Color innerPaintColor;
        Color outerPaintColor;
        
        double colorSchemeRandomizer = Flower.randomNumberInRange(0.0, 1.0);
        if(colorSchemeRandomizer < 0.25) {
            // Intense to slightly pastel, of any color, fading to white.
            outerPaintColor = Color.getHSBColor(
                                                (float)Flower.randomNumberInRange(0.0, 1.0),
                                                (float)Flower.randomNumberInRange(0.75, 1.0),
                                                (float)1.0);
            innerPaintColor = Color.WHITE;
        } else if(colorSchemeRandomizer < 0.5) {
            // Intense to somewhat bold, of any color, fading to white.
            outerPaintColor = Color.getHSBColor(
                                                (float)Flower.randomNumberInRange(0.0, 1.0),
                                                (float)1.0,
                                                (float)Flower.randomNumberInRange(0.8, 1.0));
            innerPaintColor = Color.WHITE;
        } else if(colorSchemeRandomizer < 0.75) {
            // Pastel, of any color,
            // fading to a color about 1/3 around from it in either direction on the color wheel, a little more pastel.
            float hue = (float)Flower.randomNumberInRange(0.0, 1.0);
            float saturation = (float)Flower.randomNumberInRange(0.5, 0.75);
            outerPaintColor = Color.getHSBColor(
                                                hue,
                                                saturation,
                                                (float)1.0);
            float secondHue = makeRelatedHue(hue);
            innerPaintColor = Color.getHSBColor(
                                                secondHue,
                                                (float)(saturation - 0.1),
                                                (float)1.0);
        } else {
            // Bold, of any color,
            // fading to a color about 1/3 around from it in either direction on the color wheel, a little darker.
            float hue = (float)Flower.randomNumberInRange(0.0, 1.0);
            float brightness = (float)Flower.randomNumberInRange(0.6, 0.8);
            outerPaintColor = Color.getHSBColor(
                                                hue,
                                                (float)1.0,
                                                brightness);
            float secondHue = makeRelatedHue(hue);
            innerPaintColor = Color.getHSBColor(
                                                secondHue,
                                                (float)1.0,
                                                (float)(brightness - 0.1));
        }
        
        for(int i = 0; i < paths.size(); i++) {
            Path2D path = paths.get(i);
            tip = tipPoints.get(i);
            
            intermediatePaintPoint = new Point2D.Double(
                                                        0.5*tip.getX() + 0.5*flowerOrigin.getX(),
                                                        0.5*tip.getY() + 0.5*flowerOrigin.getY());
            
            paint = new GradientPaint(
                                      (float)intermediatePaintPoint.getX(), (float)intermediatePaintPoint.getY(), innerPaintColor,
                                      (float)tip.getX(), (float)tip.getY(), outerPaintColor,
                                      false);
            graphics.setPaint(paint);
            graphics.fill(path);
        }
        
        // Now to write/draw a tiny little blurb about parameters.
        String blurb = "xOrigin:  " + flower.getXOrigin() +
        "      yOrigin:  " + flower.getYOrigin() +
        "      petalIncrementAngle:  " + flower.getPetalIncrementAngle() +
        "      petalCount:  " + flower.getPetalCount() +
        "      petalSeControlPoint:  " + flower.getPetalSeControlPoint() +
        "      petalNeControlPoint:  " + flower.getPetalNeControlPoint() +
        "      \npetalNwControlPoint:  " + flower.getPetalNwControlPoint() +
        "      petalSwControlPoint:  " + flower.getPetalSwControlPoint() +
        "      petalTipPoint:  " + flower.getPetalTipPoint();
        
        graphics.setPaint(Color.BLACK);
        int slicePoint = blurb.indexOf('\n');
        graphics.drawChars(blurb.toCharArray(), 0, slicePoint, 5, imageEdgeSize - 20);
        graphics.drawChars(blurb.toCharArray(), slicePoint, blurb.length() - slicePoint, 5, imageEdgeSize - 5);
        
        
        
        Iterator<ImageWriter> imageWriterIterator = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter imageWriter = imageWriterIterator.next();
        
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(1);
        
        
        File outputFile = new File(fileName);
        
        FileImageOutputStream output = new FileImageOutputStream(outputFile);
        imageWriter.setOutput(output);
        IIOImage image = new IIOImage(bufferedImage, null, null);
        imageWriter.write(null, image, imageWriteParam);
        imageWriter.dispose();
        
    }
    
    private static double midRange = 1.0/3.0;
    private static double plusOrMinus = 1.0/18.0;
    private static double lowEnd = midRange - plusOrMinus;
    private static double highEnd = midRange + plusOrMinus;
    private static Random random = new Random();
    
    private static float makeRelatedHue(float hue) {
        double nextHue = hue + (random.nextBoolean() ? 1 : -1) * (Flower.randomNumberInRange(lowEnd, highEnd));
        if(nextHue < 0.0) {
            nextHue = nextHue + 1.0;
        }
        if(nextHue > 1.0) {
            nextHue = nextHue - 1.0;
        }
        return (float)nextHue;
    }
    
}

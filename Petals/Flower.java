package net.folds.flower.wallpaper;

import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Flower {
    
    private static Random random = new Random();
    
    private double xOrigin;
    private double yOrigin;
    private int petalCount;
    private double petalIncrementAngle;
    
    private List<Petal> petals;
    
    public Flower(double xOrigin, double yOrigin, double scale, int petalCount, double petalIncrementalAngle) {
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.petalCount = petalCount;
        this.petalIncrementAngle = petalIncrementalAngle;
        
        this.petals = new ArrayList<Petal>();
        
        double totalScaleX = 1;
        double totalScaleY = 1;
        // We'll do increments instead of multipliers, because we want linear growth, not exponential explosion.
        // It's easier to think about this starting with y.
        // X is calculated as a ratio of y.
        double scaleIncrementYPerPetal = randomNumberInRange(0.2, 0.5);
        double scaleIncrementXPerPetal = scaleIncrementYPerPetal * randomNumberInRange(0.25, 1.5);
        
        AffineTransform startingScale = AffineTransform.getScaleInstance(scale, scale);
        
        AffineTransform originTransform = AffineTransform.getTranslateInstance(xOrigin, yOrigin);
        AffineTransform totalRotation = new AffineTransform();
        AffineTransform incrementalRotation = AffineTransform.getRotateInstance(Math.toRadians(petalIncrementalAngle));
        
        AffineTransform combinedRotateAndScale;
        
        Petal basePetal = new Petal(originTransform, startingScale);
        petals.add(basePetal);
        while(petals.size() < petalCount) {
            totalRotation.concatenate(incrementalRotation);
            totalScaleX = totalScaleX + scaleIncrementXPerPetal;
            totalScaleY = totalScaleY + scaleIncrementYPerPetal;
            
            combinedRotateAndScale = new AffineTransform();
            combinedRotateAndScale.concatenate(totalRotation);
            combinedRotateAndScale.scale(totalScaleX, totalScaleY);
            
            petals.add(new Petal(basePetal, combinedRotateAndScale));
        }
    }
    
    public double getXOrigin() {
        return xOrigin;
    }
    
    public double getYOrigin() {
        return yOrigin;
    }
    
    public double getPetalIncrementAngle() {
        return petalIncrementAngle;
    }
    
    public int getPetalCount() {
        return petalCount;
    }
    
    public Point2D getPetalSeControlPoint() {
        return petals.get(0).seControlPoint;
    }
    public Point2D getPetalNeControlPoint() {
        return petals.get(0).neControlPoint;
    }
    
    public Point2D getPetalNwControlPoint() {
        return petals.get(0).nwControlPoint;
    }
    
    public Point2D getPetalSwControlPoint() {
        return petals.get(0).swControlPoint;
    }
    
    public Point2D getPetalTipPoint() {
        return petals.get(0).tipPoint;
    }
    
    public List<Path2D> getPetalPaths() {
        List<Path2D> petalPaths = new ArrayList<Path2D>();
        for(Petal petal : petals) {
            petalPaths.add(petal.getPetalPath());
        }
        return petalPaths;
    }
    
    public List<Point2D> getPetalTipPoints() {
        List<Point2D> petalTips = new ArrayList<Point2D>();
        for(Petal petal : petals) {
            petalTips.add(petal.getPetalPoint());
        }
        return petalTips;
    }
    
    public static double randomNumberInRange(double low, double high) {
        return low + ((high - low) * random.nextDouble());
    }
    
    public static class Petal {
        
        // The move to be applied before making the path
        private AffineTransform finalMove;
        
        private Point2D origin;
        private Point2D seControlPoint;
        private Point2D neControlPoint;
        private Point2D tipPoint;
        private Point2D nwControlPoint;
        private Point2D swControlPoint;
        
        
        public Petal(AffineTransform finalMove, AffineTransform transform) {
            this.finalMove = finalMove;
            origin = new Point2D.Double(0.0, 0.0);
            neControlPoint = new Point2D.Double(randomNumberInRange(0.2, 0.7), randomNumberInRange(0.5, 1.0));
            // Y gets plenty of flexibility, while avoiding having the handles cross.
            seControlPoint = new Point2D.Double(randomNumberInRange(0.2, 0.7), randomNumberInRange(0.0, neControlPoint.getY()));
            
            nwControlPoint = new Point2D.Double(-neControlPoint.getX(), neControlPoint.getY());
            swControlPoint = new Point2D.Double(-seControlPoint.getX(), seControlPoint.getY());
            
            tipPoint = new Point2D.Double(0.0, 1.0);
            // This asymmetry will be kind of rare.  It's pretty strange.
            if(randomNumberInRange(0.0, 1.0) < 0.1) {
                double wiggleFactor = randomNumberInRange(0.1 * neControlPoint.getX(), 1.5 * neControlPoint.getX());
                // Left or right?
                // Also, let's nudge the ne or nw corner some, to help make sure we don't get into knots.
                if(random.nextBoolean()) {
                    tipPoint.setLocation(wiggleFactor, 1.0);
                    nwControlPoint.setLocation(nwControlPoint.getX(), nwControlPoint.getY() + randomNumberInRange(0.25, 0.75) * wiggleFactor);
                } else {
                    tipPoint.setLocation(-wiggleFactor, 1.0);
                    neControlPoint.setLocation(neControlPoint.getX(), neControlPoint.getY() + randomNumberInRange(0.25, 0.75) * wiggleFactor);
                }
            }
            
            transformSelf(transform);
        }
        
        // Copy a different petal, and apply the requested transform to it.
        public Petal(Petal petal, AffineTransform transform) {
            this.finalMove = petal.finalMove;
            
            this.origin = petal.origin;
            this.seControlPoint = petal.seControlPoint;
            this.neControlPoint = petal.neControlPoint;
            this.tipPoint = petal.tipPoint;
            this.nwControlPoint = petal.nwControlPoint;
            this.swControlPoint = petal.swControlPoint;
            
            transformSelf(transform);
        }
        
        private void transformSelf(AffineTransform transform) {
            this.origin = transform.transform(this.origin, null);
            this.seControlPoint = transform.transform(this.seControlPoint, null);
            this.neControlPoint = transform.transform(this.neControlPoint, null);
            this.tipPoint = transform.transform(this.tipPoint, null);
            this.nwControlPoint = transform.transform(this.nwControlPoint, null);
            this.swControlPoint = transform.transform(this.swControlPoint, null);
        }
        
        public Path2D getPetalPath() {
            Point2D localOrigin = finalMove.transform(origin, null);
            Point2D localSeControlPoint = finalMove.transform(seControlPoint, null);
            Point2D localNeControlPoint = finalMove.transform(neControlPoint, null);
            Point2D localTipPoint = finalMove.transform(tipPoint, null);
            Point2D localNwControlPoint = finalMove.transform(nwControlPoint, null);
            Point2D localSwControlPoint = finalMove.transform(swControlPoint, null);
            
            Path2D.Double path = new Path2D.Double();
            path.append(new CubicCurve2D.Double(
                                                localOrigin.getX(), localOrigin.getY(),
                                                localSeControlPoint.getX(), localSeControlPoint.getY(),
                                                localNeControlPoint.getX(), localNeControlPoint.getY(),
                                                localTipPoint.getX(), localTipPoint.getY()),
                        true);
            path.append(new CubicCurve2D.Double(
                                                localTipPoint.getX(), localTipPoint.getY(),
                                                localNwControlPoint.getX(), localNwControlPoint.getY(),
                                                localSwControlPoint.getX(), localSwControlPoint.getY(),
                                                localOrigin.getX(), localOrigin.getY()),
                        true);
            
            return path;
        }
        
        public Point2D getPetalPoint() {
            return finalMove.transform(this.tipPoint, null);
        }
    }
    
}

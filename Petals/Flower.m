//
//  Flower.m
//  Petals
//
//  Created by Adam Miezianko on 7/20/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Flower.h"

double randomDoubleInRange(double, double);

#define DEGREES_TO_RADIAN(radians) radians * 180 / M_PI

@implementation Flower

@synthesize xOrigin = _xOrigin;
@synthesize yOrigin = _yOrigin;
@synthesize petalCount = _petalCount;
@synthesize petalIncrementalAngle = _petalIncrementalAngle;

- (Flower*) initWithXOrigin: (double) xOrigin yOrigin: (double) yOrigin 
                        scale: (double) scale petalCount: (int) petalCount 
         petalIncrementalAgle: (double) petalIncrementalAngle;
{
    self = [super init];
    if (self) {
        self->_xOrigin = xOrigin;
        self->_yOrigin = yOrigin;
        self->_petalCount = petalCount;
        self->_petalIncrementalAngle = petalIncrementalAngle;
        
        double totalScaleX = 1.0;
        double totalScaleY = 1.0;
        // We'll do increments instead of multipliers, because we want linear growth, not exponential explosion.
        // It's easier to think about this starting with y.
        // X is calculated as a ratio of y.
        double scaleIncrementYPerPetal = randomDoubleInRange(0.2, 0.5);
        double scaleIncrementXPerPetal = scaleIncrementYPerPetal * randomDoubleInRange(0.25, 1.5);
        
        CGAffineTransform scaleTransform = CGAffineTransformMakeScale((CGFloat)scale, (CGFloat) scale);
        CGAffineTransform originTransform = CGAffineTransformMakeTranslation((CGFloat) xOrigin, (CGFloat) yOrigin);
        CGAffineTransform rotationTransform = CGAffineTransformMakeRotation(
                                                                (CGFloat) DEGREES_TO_RADIAN(petalIncrementalAngle));
        
    }
    return self;
}

@end

double randomDoubleInRange(double min, double max) {
    double uniform = arc4random();
    while ((uniform > max) || (uniform < min)) {
        uniform /= 2.0;
    }
}

//
//  Flower.m
//  Petals
//
//  Created by Adam Miezianko on 7/20/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Flower.h"

@implementation Flower

@synthesize xOrigin = _xOrigin;
@synthesize yOrigin = _yOrigin;
@synthesize petalCount = _petalCount;
@synthesize petalIncrementalAngle = _petalIncrementalAngle;

+ (Flower*) flowerWithXOrigin: (double) xOrigin yOrigin: (double) yOrigin 
                        scale: (double) scale petalCount: (int) petalCount 
         petalIncrementalAgle: (double) petalIncrementalAngle;
{
    Flower* flower = [[Flower alloc] init];
    
    
    return flower;
}

@end

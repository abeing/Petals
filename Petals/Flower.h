//
//  Flower.h
//  Petals
//
//  Created by Adam Miezianko on 7/20/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Flower : NSObject

+ (Flower*) flowerWithXOrigin: (double) xOrigin yOrigin: (double) yOrigin 
                        scale: (double) scale petalCount: (int) petalCount 
         petalIncrementalAgle: (double) petalIncrementalAngle;
@end

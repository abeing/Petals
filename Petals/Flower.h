//
//  Flower.h
//  Petals
//
//  Created by Adam Miezianko on 7/20/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Flower : NSObject

@property (readonly) double xOrigin;
@property (readonly) double yOrigin;
@property (readonly) int petalCount;
@property (readonly) double petalIncrementalAngle;

- (id) initWithXOrigin: (double) xOrigin yOrigin: (double) yOrigin 
                        scale: (double) scale petalCount: (int) petalCount 
         petalIncrementalAgle: (double) petalIncrementalAngle;
@end

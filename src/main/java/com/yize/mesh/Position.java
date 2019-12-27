package com.yize.mesh;

import static com.yize.mesh.DefaultParams.DEFAULT_RANGE;

public class Position {
    public float x;
    public float y;
    public Position(float x, float y){
        this.x=x;
        this.y=y;
    }

    public static boolean checkIsNeighbor(Position pos1,Position pos2){
        return (float)Math.sqrt(Math.pow(pos1.x-pos2.x,2)+Math.pow(pos1.y-pos2.y,2))<=DEFAULT_RANGE;
    }

}

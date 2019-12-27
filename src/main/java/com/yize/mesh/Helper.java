package com.yize.mesh;

import java.util.Random;

import static com.yize.mesh.DefaultParams.DEFAULT_RRD;

public class Helper {
    public static int getRandomRelayDelay(){
        Random random=new Random();
        return random.nextInt(DEFAULT_RRD);
    }
}

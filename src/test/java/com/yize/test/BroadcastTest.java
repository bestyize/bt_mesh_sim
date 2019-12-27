package com.yize.test;

import com.yize.mesh.Control;
import com.yize.mesh.DefaultParams;
import org.junit.Test;

import static com.yize.mesh.DefaultParams.DEFAULT_RELAY_PROBABILITY;
import static com.yize.mesh.DefaultParams.DEFAULT_TTL_BACKOFF;

public class BroadcastTest {
    @Test
    public void test(){
        DEFAULT_TTL_BACKOFF=2;
        double startValue=0.10;
        double step=0.01;
        double maxValue=1.01;
        while (startValue<=maxValue){
            DEFAULT_RELAY_PROBABILITY=startValue;
            startValue+=step;

            Control.restartTest(5000,148,29,1,20);
        }
//        DEFAULT_RELAY_PROBABILITY=1;
//        Control.restartTest(10000,148,29,1,20);


    }
}

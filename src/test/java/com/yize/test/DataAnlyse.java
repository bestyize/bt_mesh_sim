package com.yize.test;

import com.google.gson.internal.$Gson$Preconditions;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;

public class DataAnlyse {
    class SuccessRate{
        double prob;
        double succRate;

        public SuccessRate(double prob, double succRate) {
            this.prob = prob;
            this.succRate = succRate;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"prob\":\"" + prob + "\"" +
                    ", \"succRate\":\"" + succRate + "\"" +
                    "}";
        }
    }
    private File file=new File("D:\\sim\\testResult\\2019-12-17概率广播测试");

    @Test
    public void anlyseSuccessRate()throws Exception{
        List<SuccessRate> successRateList=new ArrayList<>();
        File []files=file.listFiles();
        System.out.println("开始分析");
        for(File file:files){
            if(!file.isFile()){
                continue;
            }
            int successCount=0;
            String fileName=file.getName();
            String prob=fileName.substring(fileName.lastIndexOf("_")+1,fileName.lastIndexOf("_")+5);

            FileReader reader=new FileReader(file);
           // StringBuilder sb=new StringBuilder();
            String line;
            BufferedReader bf=new BufferedReader(reader);
            while ((line=bf.readLine())!=null){
                if(line.contains("true")){
                    successCount++;
                }
            }
            successRateList.add(new SuccessRate(Double.valueOf(prob),successCount/5000.0));
        }
        System.out.println("结束分析");
        Collections.sort(successRateList, new Comparator<SuccessRate>() {
            @Override
            public int compare(SuccessRate o1, SuccessRate o2) {
                return o1.prob>o2.prob?1:-1;
            }
        });
        System.out.println(successRateList);

        for(int i=0;i<successRateList.size();i++){
            System.out.println(successRateList.get(i).prob+"\t"+successRateList.get(i).succRate);
        }

    }



}

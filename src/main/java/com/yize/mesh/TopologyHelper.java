package com.yize.mesh;

import java.io.*;
import java.util.*;

public class TopologyHelper {
    public static int nodeCount=0;
    public static Map<Float,Float > positionMap = new HashMap<Float, Float>();
    public static Map<Integer, Map<Float,Float >> nodeMap = new HashMap<>();
    public static void createTopologyFile(int num,int x,int y){
        File file=new File("D:\\sim\\topology\\topology_mesh_"+num+"_"+x+"_"+y+".txt");
        Random ran=new Random();
        if(!file.exists()){
            try {
                file.createNewFile();
                FileWriter writer=new FileWriter(file,true);
                for(int i=0;i<num;i++){
                    String msg=i+"\t"+ran.nextInt(x)+"\t"+(ran.nextInt(y))+"\n";
                    writer.write(msg);
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("创建拓扑失败");
            }
        }

    }

    public static Map<Integer, Map<Float,Float >> readTopologyFile(){
        List<TopologyHelper> topologyInfo=new ArrayList<>();
        File file=new File("D:\\sim\\topology\\topology_mesh_200_100_100.txt");
        try {
            BufferedReader reader=new BufferedReader(new FileReader(file));
            String line;
            while ((line=reader.readLine())!=null){
                String[] ss=line.split("\t");
                Map<Float,Float > tempPos = new HashMap<Float, Float>();
                tempPos.put(Float.valueOf(ss[1]),Float.valueOf(ss[2]));
                positionMap.put(Float.valueOf(ss[1]),Float.valueOf(ss[2]));
                nodeMap.put(Integer.valueOf(ss[0]),tempPos);
                nodeCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodeMap;
    }


}
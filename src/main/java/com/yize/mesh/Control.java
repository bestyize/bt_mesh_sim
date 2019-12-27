package com.yize.mesh;

import com.google.gson.internal.bind.util.ISO8601Utils;
import com.yize.tool.TimerHelper;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.omg.CORBA.NO_IMPLEMENT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yize.mesh.DefaultParams.*;
import static com.yize.tool.VisiableTool.saveBoardcastNodePictureWithRange;
import static com.yize.tool.VisiableTool.saveNodePicture;

public class Control {
    public static ArrayList<Node> LIST_OF_NODES = new ArrayList<Node>();
    public static final float MAX_SIM_TIME = 100000*200; // 1 hour in the simulated world
    public static double SYSTEM_CLOCK=0;
    public static EventList eventList=new EventList();

    public static String testResultFolder="D:\\sim\\testResult\\2019-12-17概率广播测试\\";


    public static void main(String[] args) {

    }

    public static void restartTest(int testCount,int srcId,int dstId,int packetNumber,int rate){
       createTestResultFolder();
        System.out.println("System Start!");
        long simStartTime=System.currentTimeMillis();
        //TopologyHelper.createTopologyFile(200,100,100);
        for(int i=0;i<testCount;i++){
            new Control().controlPanel(srcId,dstId,packetNumber,rate);
            LIST_OF_NODES=null;
            LIST_OF_NODES=new ArrayList<>();
            SYSTEM_CLOCK=0;
            eventList=null;
            eventList=new EventList();
        }
        System.out.println("仿真结束，程序运行时间："+(System.currentTimeMillis()-simStartTime)+"ms");
    }

    public void controlPanel(int srcId,int dstId,int packetNumber,int rate){

        TopologyHelper.readTopologyFile();

        buildNodeList();
        //saveNodePicture(LIST_OF_NODES);
        printNeighborOfNode(LIST_OF_NODES.get(dstId));
        printNeighborOfNode(LIST_OF_NODES.get(srcId));
        printNeighborDegree();
        List<Event> packetSendEvents=packetSendEventRegister(srcId,dstId,packetNumber,rate);
        eventList.addEventsFromList(packetSendEvents);

        while (SYSTEM_CLOCK<MAX_SIM_TIME&&!(eventList.theList.size() == 0)){
            SYSTEM_CLOCK=eventList.getFirstEventStartTime();
            eventList.addEventsFromList(eventList.processFirstEvent());
        }
        printNodeThatJoinInRelay();
        testLogger(srcId,dstId);
        //saveBoardcastNodePictureWithRange(LIST_OF_NODES,srcId,dstId);
        //printEveryNodeReceivePacketCount();


    }

    /**
     *
     * 建立节点集
     */
    private static void buildNodeList(){
        for (Map.Entry<Integer, Map<Float,Float >> entry1 : TopologyHelper.nodeMap.entrySet()) {
            Map<Float, Float> tmp = new HashMap<Float, Float>();
            Map<Float, Float> temp1 = new HashMap<Float, Float>();
            tmp.clear();
            temp1.clear();
            temp1 = new HashMap<Float, Float>(entry1.getValue());
            temp1.keySet().removeAll(tmp.keySet());
            tmp.putAll(temp1);
            for (Map.Entry<Float, Float> entry2 : tmp.entrySet()) 	{
                float posX = entry2.getKey();
                float posY = entry2.getValue();
                Position position=new Position(posX,posY);
                int cacheSize = 1000;
                // Initialize nodes: ID=i, position_i, batteryType=0
                // packetsSource=0, lambda=1/60, casheSize=100
                LIST_OF_NODES.add(new Node(entry1.getKey(), position));
            }
        }
    }

    /**
     * 发送一个数据包
     * @param srcId 源节点地址
     * @param dstId 目的节点地址
     * @param seq 数据包的序列号
     * @param time 数据包的发送速率，一秒几个数据包
     * @return
     */
    private static Event pacKetSendEventRegister(int srcId,int dstId,int seq,double time){
        Packet packet=new Packet(srcId,dstId,seq);
        Event event=new Event(time,"EVT_ADV_START",String.valueOf(srcId));
        event.setAdvRecvPayload(packet);
        LIST_OF_NODES.get(srcId).queue.add(packet);//创建数据包发送事件的时候就应该把数据包缓存在节点的待发送列表里了
        return event;
    }

    private static List<Event> packetSendEventRegister(int srcId,int dstId,int num,int rate){
        if(rate>50){
            System.out.println("速率超过允许范围！");
            return null;
        }
        List<Event> list=new ArrayList<>();
        int advInterval=1000/rate;
        int addedTime=0;
        for(int i=0;i<num;i++){
            double time=SYSTEM_CLOCK+Helper.getRandomRelayDelay()+addedTime;
            list.add(pacKetSendEventRegister(srcId,dstId,i,time));
            addedTime+=advInterval;
        }
        return list;
    }

    /**
     * 打印当前节点的
     */
    private static void printNeighborOfNode(Node objNode){
        int nighborCounter=0;
        for(Node node:LIST_OF_NODES){
            if(Position.checkIsNeighbor(objNode.position,node.position)&&(objNode.id!=node.id)){
                //System.out.println(node);
                nighborCounter++;
            }
        }
        //System.out.println("节点："+objNode.id+"的邻居节点数量为："+nighborCounter);
    }

    private static String printPerDegreeOfAllNode(){
        int allCount=0;
        for(int i=0;i<LIST_OF_NODES.size();i++){
            for(int j=0;j<LIST_OF_NODES.size();j++){
                if(j!=i){
                    if(Position.checkIsNeighbor(LIST_OF_NODES.get(i).position,LIST_OF_NODES.get(j).position)){ ;
                        allCount++;
                    }
                }
            }
        }
        //System.out.println("平均节点度为："+allCount/LIST_OF_NODES.size());
        return String.valueOf(allCount/LIST_OF_NODES.size());

    }

    private static void printEveryNodeReceivePacketCount(){
        for(Node node:LIST_OF_NODES){
            System.out.println("节点："+node.id+"接收广播数量为："+node.packetReceiveCount);
        }
    }

    private static void printNeighborDegree(){
        //System.out.println("平均节点度为："+3.1415926*DEFAULT_RANGE*DEFAULT_RANGE*LIST_OF_NODES.size()/100/100);
        printPerDegreeOfAllNode();
    }

    private static String printNodeThatJoinInRelay(){
        StringBuilder sb=new StringBuilder();
        int count=0;
        sb.append("参与转发的节点有：");
        for(Node node:LIST_OF_NODES){
            if(node.broadcastedCount!=0){
                count++;
                //sb.append(node.id+"\t");
            }
        }
        //System.out.println(sb.toString()+count+"个,占总节点数量的"+(float)(100*count+0.0)/(LIST_OF_NODES.size())+"%");
        return String.valueOf(count);
    }


    /**
     * 创建测试文件夹
     */
    private static void createTestResultFolder(){
        File file=new File(testResultFolder);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    private static void testLogger(int srcId,int dstId){
        Node dstNode=LIST_OF_NODES.get(dstId);
        Node srcNode=LIST_OF_NODES.get(srcId);
        StringBuilder sb=new StringBuilder();



       // dstNode.receiveLogger=new StringBuilder();
        try {
            File file=new File(testResultFolder+TimerHelper.getTimeStampToHour()+"_"+LIST_OF_NODES.size()+"_100x100_"+DEFAULT_RANGE+"_"+printPerDegreeOfAllNode()+"_"+DEFAULT_RELAY_PROBABILITY+".txt");
            if(!file.exists()){
                sb.append("发送节点："+srcNode.id+"\n");
                sb.append("目的节点："+dstNode.id+"\n");
                sb.append("概率广播测试结果\n");
                sb.append("********************* 仿真参数如下 ********************************\n");
                sb.append("节点数量："+LIST_OF_NODES.size()+"\n");
                sb.append("网络大小：100mX100m\n");
                sb.append("节点传输半径："+DEFAULT_RANGE+"\n");
                sb.append("该拓扑的平均节点度："+printPerDegreeOfAllNode()+"\n");
                sb.append("节点转发概率："+DEFAULT_RELAY_PROBABILITY+"\n");
                sb.append("TTL退避阶数："+DEFAULT_TTL_BACKOFF+"\n");
                file.createNewFile();
            }
            sb.append("********************* 测试结果如下 ********************************\n");
            sb.append("成功到达目的节点："+(dstNode.receiveLogger.toString().length()>0)+"\n");
            sb.append("网络中广播节点总数："+printNodeThatJoinInRelay()+"\n");
            //sb.append("目标节点收到的消息内容：\n"+dstNode.receiveLogger.toString()+"\n");
            sb.append("仿真时间："+SYSTEM_CLOCK+"ms\n");
            dstNode.receiveLogger=null;
            dstNode.receiveLogger=new StringBuilder();

            FileWriter writer=new FileWriter(file,true);
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}

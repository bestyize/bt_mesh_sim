package com.yize.mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.yize.mesh.Control.LIST_OF_NODES;
import static com.yize.mesh.DefaultParams.*;

public class Node {
    /**
     * 节点的id,唯一标识符
     */
    public int id;
    /**
     * 节点的位置，也就是坐标
     */
    public Position position;
    /**
     * 节点传输半径
     */
    public int range;

    /**
     * 等待发送的数据队列
     */
    public List<Packet> queue=new ArrayList<>();

    /**
     * 已接收消息的缓存
     * 节点对象被创建时就应该初始化缓存大小
     */
    private Cache cache;

    public int packetReceivedCount=0;

    public  List<String> duplicateList = new ArrayList<String>();
    public  int duplicateCounter;

    public int packetReceiveCount=0;
    public int broadcastedCount=0;

    public StringBuilder receiveLogger=new StringBuilder();


    public Node(int id,Position position){
        this.id=id;
        this.position=position;
        this.range=DEFAULT_RANGE;
        cache=new Cache(DEFAULT_CACHE_SIZE);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\"" +
                ", \"position\":\"" + position + "\"" +
                "}";
    }

    /**
     * 广播的时候给邻居节点注册广播接收事件
     * @param eventList
     */
    public List<Event> processAdvStartEvent(Event event,List<Event> eventList){

        boolean probality=Math.random()<=DEFAULT_RELAY_PROBABILITY;

        if(probality==false){
            if(event.getAdvRecvPayload().header.srcId==id||(event.getAdvRecvPayload().header.ttl>DEFAULT_TTL-DEFAULT_TTL_BACKOFF)){
                //假如我是源节点，我必然要以概率1发送,还有一种为了防止第一跳没有转发的情况，规定第一跳转发概率为1
                //System.out.println();
            }else {
                return eventList;
            }

        }
        broadcastedCount++;
        double boardcastTime=Helper.getRandomRelayDelay()+Control.SYSTEM_CLOCK;
        for(Node node:LIST_OF_NODES){
            if(Position.checkIsNeighbor(node.position,position)&&node.id!=id){
                //广播接收事件是发生在这个节点的邻居节点上的，所以这里创建事件是邻居节点事件
                Event newEvent=new Event(boardcastTime,"EVT_ADV_RECV",String.valueOf(node.id));
                newEvent.setAdvRecvPayload(topOfFirstPacketFromQueue());
                eventList.add(newEvent);
            }
        }
        dropFirstPacketFromQueue();//不管有没有邻居节点，这个数据包都广播出去了
        return eventList;

    }

//    /**
//     * 广播的时候给邻居节点注册广播接收事件
//     * @param eventList
//     */
//    public List<Event> processAdvStartEvent(Event event,List<Event> eventList){
//
//        boolean probality=Math.random()<=DEFAULT_RELAY_PROBABILITY;
//
//        if((probality==false&&event.getAdvRecvPayload().header.srcId!=id)){
//            return eventList;
//        }
//        broadcastedCount++;
//        double boardcastTime=Helper.getRandomRelayDelay()+Control.SYSTEM_CLOCK;
//        for(Node node:LIST_OF_NODES){
//            if(Position.checkIsNeighbor(node.position,position)&&node.id!=id){
//                //广播接收事件是发生在这个节点的邻居节点上的，所以这里创建事件是邻居节点事件
//                Event newEvent=new Event(boardcastTime,"EVT_ADV_RECV",String.valueOf(node.id));
//                newEvent.setAdvRecvPayload(topOfFirstPacketFromQueue());
//                eventList.add(newEvent);
//            }
//        }
//        dropFirstPacketFromQueue();//不管有没有邻居节点，这个数据包都广播出去了
//        return eventList;
//
//    }

    /**
     * 处理广播接收事件
     * @param eventList
     * @return
     */
    public List<Event> processAdvRecvEvent(Event event,List<Event> eventList){
        return processPacket(event,eventList);
    }

    /**
     * 如果已经缓存了，那么什么也不做，否则的话把数据包添加到cache,检查节点当前是不是支持处理数据包，如果支持
     * 则检查是不是应该转发这个数据包
     * @param eventList
     */
    private List<Event> processPacket(Event event,List<Event> eventList){
        Packet oldPacket=event.getAdvRecvPayload();
        Packet p = new Packet(oldPacket);
        if(isNodeDestination(p)){
            //System.out.println(" 时间："+Control.SYSTEM_CLOCK+"\t节点："+id+"收到节点："+p.header.srcId+"发送的消息，消息的内容是："+p);
            receiveLogger.append(" 时间："+Control.SYSTEM_CLOCK+"\t节点："+id+"收到节点："+p.header.srcId+"发送的消息，消息的内容是："+p+"\n");
            packetReceivedCount++; //接收数据包的数量++
        }
        if(!cache.isPacketInCache(p)){  					//if it is the first time when the node received the packet
            if(id==40){
                System.out.println("到达节点40");
            }
            addPacketToCache(p);								//add the packet to the cache, and
            if (isNodeDestination(p)){							//1) if the node is packet destination

            }else {
                addPacketToQueue(p);
                eventList=processAdvStartEvent(event,eventList);
            }
        }else if(isNodeDestination(p)){  //统计一下重复数据包数量
            duplicateList.add(p.header.packetId);
            duplicateCounter++;
        }
        packetReceiveCount++;
         return eventList;
    }

    /**
     * 检查是不是发给我自己的
     * @param packet
     * @return
     */
    private boolean isNodeDestination(Packet packet){
        return packet.header.dstId==id;
    }



    /**
     * <pre>
     * 向待转发队列添加数据包.
     * </pre>
     * @param p
     */
    private void addPacketToQueue(Packet p){
        p.header.ttl--;
        queue.add(p);
    }

    /**
     * 向已发送队列中添加数据包的缓存
     * @param p
     */
    private void addPacketToCache(Packet p){
        cache.addPacketToCache(p);
    }

    /**
     * Get the first packet from the queue and remove it from the queue (only one queue per node is assumed)
     * @return First packet from the queue
     */
    private Packet dropFirstPacketFromQueue(){
        Packet p = queue.get(0);
        queue.remove(0);
        return p;
    }

    /**
     * 指向待发送队列的第一个数据包
     * @return
     */
    private Packet topOfFirstPacketFromQueue(){
        Packet p = queue.get(0);
        return p;
    }






}

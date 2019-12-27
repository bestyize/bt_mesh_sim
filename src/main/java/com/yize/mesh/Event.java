package com.yize.mesh;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class Event {
    public double startTime;
    public String type;
    public String metadata;
    private Packet advRecvPayload;
    public Event(double startTime, String type,String metadata) {
        this.startTime = startTime;
        this.type = type;
        this.metadata=metadata;
    }

    public Packet getAdvRecvPayload() {
        return advRecvPayload;
    }

    public void setAdvRecvPayload(Packet advRecvPayload) {
        this.advRecvPayload = advRecvPayload;
    }

    public List<Event> eventHandler(){
       int nodeId=Integer.valueOf(metadata);
       List<Event> newEvents=new ArrayList<>();
       switch (type){
           case "EVT_ADV_START":
               Control.LIST_OF_NODES.get(nodeId).processAdvStartEvent(this,newEvents);
               break;
           case "EVT_ADV_RECV":
                Control.LIST_OF_NODES.get(nodeId).processAdvRecvEvent(this,newEvents);
               break;
           default:
               break;
       }
        newEvents.removeAll(Collections.singleton(null));
       return newEvents;
    }





}

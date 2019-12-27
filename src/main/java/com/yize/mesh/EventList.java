package com.yize.mesh;

import java.util.ArrayList;
import java.util.List;

public class EventList {
    List<Event> theList= new ArrayList<Event>();
    /**
     * We don't initialize nothing
     */
    public EventList(){}
    /**
     * Adds event to the list.
     */
    public void addEvent(Event e){
        int place=binarySearch(e.startTime);	//find proper position
        theList.add(place,e);
    }

    /**
     * 从事件列表添加事件
     * @param list
     */
    public void addEventsFromList(List<Event> list){
        for (Event e : list) addEvent(e);
    }

    /**
     * 移除所有数据包产生事件
     */
    void removeAllPacketGenerations() {
        //printEvents();
        int size=theList.size();
        for (int i = size-1; i>0; i--) {
            Event e = theList.get(i);
            if (e.type.equals("PACKET_GENERATION")) {
                theList.remove(i);
            }
        }
        System.out.println("Packet generation events removed!");
    }
    /**
     * 处理事件列表的第一个事件，并且移除这个事件.
     * @return new triggered events
     */
    public List<Event> processFirstEvent(){
        List<Event> e=theList.get(0).eventHandler();
        theList.remove(0);
        return e;
    }
    /**
     * The function is helpful for Engine object. First event time is current simulation time.
     */
    public double getFirstEventStartTime(){
        return theList.get(0).startTime;
    }
    /**
     * 删除节点中的事件
     * <pre>
     * Removes events that are related with a node (the node probably gets discharged or goes sleep so may not perform any actions)
     *
     * REMARK: The function does not remove the 0 element from the list - it will be removed by performFirstEvent()
     * If the function would remove the 0 element, then next event would become zero element and then would be removed as well (because of performFirstEvent(), theList.remove(0))
     * </pre>
     * @param ID The node ID
     */
    public void removeEventsOfNode(int ID){
        for (int i=theList.size()-1; i>0;i--){
            if (theList.get(i).metadata.equals(Integer.toString(ID)))	theList.remove(i);
        }
    }
    /**
     *
     * 打印当前事件列表
     */
    void printEvents(){
        System.out.println("---Current event list:-----");
        for(int i=0; i<theList.size();i++){
            System.out.println("EVENT :  "+theList.get(i).startTime+ "\t" +theList.get(i).type + "\t ID:"+theList.get(i).metadata);
        }
    }


    /**
     * 根据事件发生的时间向事件列表添加事件，并且返回事件插入的位置
     *
     * @return position in the event list where to place the event
     */
    private int binarySearch(double startTime){
        if (theList.size()==0) return 0; //When there is nothing in the list, the place is equal to 0;
        //When something in the list:
        int firstIndex=0;
        int lastIndex=theList.size()-1;
        while (true){ 										//Repeat until 2 consecutive indexes are founded (index, index+1) or (index, index)
            int midIndex=(firstIndex+lastIndex)/2;			//get the midIndex of your current indexes range
            if (theList.get(midIndex).startTime>startTime){		//if the value at the midIndex is greater than your value, your value position will be not greater than the midIndex
                lastIndex=midIndex;
            }
            if (theList.get(midIndex).startTime<=startTime){	//if the value at the midIndex is not greater than your value, your value position will be not smaller than the midIndex
                firstIndex=midIndex;
            }
            if (lastIndex<=firstIndex+1){						//if you have 2 consecutive indexes
                if (startTime>theList.get(lastIndex).startTime){	//if your value is greater than lastIndex
                    //(it's only possible when the value should take the last place at the list - so below error just for sure)
                    if (lastIndex!=theList.size()-1) System.out.println("ERROR: binary search - you shouldn't see that. It should be only possible when lastIndex == theList.size()-1 ");
                    //put the value at the last place of the list
                    return lastIndex+1;
                }
                else if (startTime<theList.get(firstIndex).startTime){ 	//if your value is smaller than firstIndex
                    //(it's only possible when the value should take the first place at the list - so below error just for sure)
                    if (firstIndex!=0) System.out.println("ERROR: binary search - you shouldn't see that. It should be only possible when firstIndex == 0 ");
                    //put the value at the first place of the list
                    return 0;
                }
                else
                    //In general (except 2 defined above cases) value at firstIndex should be smaller/equal than your value and the value at lastIndex should be equal/greater than your value
                    //e.g: value=5.6, list(firstIndex)=3.4, list(lastIndex)=6.2 -> put at lastIndex
                    //e.g: value=5.6, list(firstIndex)=5.6, list(lastIndex)=5.6 -> put at lastIndex
                    return lastIndex;
            }
        }
    }

}

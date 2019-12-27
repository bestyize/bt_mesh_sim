package com.yize.mesh;

import java.util.ArrayList;
import java.util.List;

public class Cache {
    public int maxSize;
    private List<CachedPacket> cachedPacketList=new ArrayList<>();
    public Cache(int maxSize){
        this.maxSize=maxSize;
    }
    /**
     * 检查数据包是不是在缓存里
     * @param packet
     * @return
     */
    public boolean isPacketInCache(Packet packet){
        boolean response=false;
        for (CachedPacket cp : cachedPacketList){
            if (cp.packetId.equals(packet.header.packetId)) {
                response=true;
            }
        }

        return response;
    }

    /**
     * 向缓存池中添加缓存数据包，一旦超过了最大缓存数量，就清除第一个缓存数据包
     * @param p
     */
    public void addPacketToCache(Packet p){
        if (cachedPacketList.size()==maxSize){
            cachedPacketList.remove(0);
        }
        cachedPacketList.add(new CachedPacket(p));
    }


    private class CachedPacket{
        private String packetId;
        private double cachedTime;
        CachedPacket(Packet p){
            this.cachedTime=Control.SYSTEM_CLOCK;
            this.packetId=p.header.packetId;
        }
    }
}

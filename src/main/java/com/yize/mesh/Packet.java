package com.yize.mesh;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import static com.yize.mesh.DefaultParams.*;

public class Packet {
    public Header header;
    class Header{
        //源地址
        final int srcId;
        //目的地址
        final int dstId;
        //数据包大小
        final int packetSize;
        //TTL值
        int ttl;
        //数据包ID，缓存在别的节点，区分不同数据包的
        final String packetId;
        public Header(int srcId, int dstId,int seq) {
            this.srcId = srcId;
            this.dstId = dstId;
            this.ttl = DEFAULT_TTL;
            this.packetId=srcId+"_"+seq;
            this.packetSize=DEFAULT_PACKET_SIZE;
        }
        public Header(Header h){
            this.srcId=h.srcId;
            this.dstId=h.dstId;
            this.packetSize=h.packetSize;
            this.ttl=h.ttl;
            this.packetId=h.packetId;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"srcId\":\"" + srcId + "\"" +
                    ", \"dstId\":\"" + dstId + "\"" +
                    ", \"packetSize\":\"" + packetSize + "\"" +
                    ", \"ttl\":\"" + ttl + "\"" +
                    ", \"packetId\":\"" + packetId + "\"" +
                    "}";
        }
    }
    public Packet(int srcId,int dstId,int seq){
        header=new Header(srcId,dstId,seq);
    }
    public Packet(Packet p){
        header = new Header(p.header);
    }

    @Override
    public String toString() {
        return "{" +
                "\"header\":\"" + header + "\"" +
                "}";
    }
}

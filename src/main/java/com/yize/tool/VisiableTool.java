package com.yize.tool;

import com.yize.mesh.Node;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.yize.tool.TimerHelper.getTimeStamp;

public class VisiableTool {
    public static int pictureWidth=3000;
    public static int extraSize=pictureWidth/100;
    public static int pictureHeight=pictureWidth;
    public static int dotSize=5;
    public static void saveNodePicture(List<Node> nodes){
        BufferedImage image=new BufferedImage(pictureWidth,pictureHeight,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D=image.createGraphics();
        graphics2D.setStroke(new BasicStroke(5));
        graphics2D.setBackground(Color.WHITE);

        //Node node=nodes.get(124);
        for(Node node:nodes){
            //graphics2D.drawRect((int)node.position.x*10,(int)node.position.y*10,dotSize,dotSize);
            //graphics2D.drawString(String.valueOf(node.id),(int)node.position.x*10,(int)node.position.y*10);
            final int nodePosX=(int)(node.position.x*extraSize);
            final int nodePosY=(int)(node.position.y*extraSize);
            graphics2D.setColor(new Color(0,255,0));
            graphics2D.drawString(String.valueOf(node.id),nodePosX,nodePosY);
            graphics2D.setColor(Color.RED);
            graphics2D.drawOval(nodePosX-node.range*extraSize,nodePosY-node.range*extraSize,node.range*extraSize*2,node.range*extraSize*2);
        }

        graphics2D.dispose();
        try {
            ImageIO.write(image,"png",new File("D:\\sim\\picture\\topology.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveBoardcastNodePictureWithRange(List<Node> nodes,int srcId,int dstId){
        BufferedImage image=new BufferedImage(pictureWidth,pictureHeight,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D=image.createGraphics();
        graphics2D.setStroke(new BasicStroke(5));
        graphics2D.setBackground(Color.WHITE);
        //Node node=nodes.get(124);
        for(Node node:nodes){
            final int nodePosX=(int)(node.position.x*extraSize);
            final int nodePosY=(int)(node.position.y*extraSize);
            graphics2D.setColor(Color.GREEN);
            graphics2D.drawString(String.valueOf(node.id),nodePosX,nodePosY);
            if(node.broadcastedCount!=0){
                graphics2D.setColor(Color.PINK);
                graphics2D.drawOval(nodePosX-node.range*extraSize,nodePosY-node.range*extraSize,node.range*extraSize*2,node.range*extraSize*2);
            }
            if(srcId==node.id){
                graphics2D.setColor(Color.RED);
                graphics2D.drawString("源节点",nodePosX-20,nodePosY-10);
            }else if(dstId==node.id){
                graphics2D.setColor(Color.RED);
                graphics2D.drawString("目的节点",nodePosX-20,nodePosY-10);
            }

        }


        graphics2D.dispose();
        try {
            ImageIO.write(image,"png",new File("D:\\sim\\picture\\topology_"+getTimeStamp()+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

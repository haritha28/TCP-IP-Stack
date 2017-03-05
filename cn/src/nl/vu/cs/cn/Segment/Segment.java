package cn.src.nl.vu.cs.cn.Segment;

import java.nio.ByteBuffer;

import cn.src.nl.vu.cs.cn.IP;
import cn.src.nl.vu.cs.cn.TCP;

/**
 * Created by haritha on 27/12/16.
 */

//Used for creating packets

public class Segment {

    private IP.IpAddress srcAddr;
    private IP.IpAddress destAddr;


    /**
     * TCP Format
     * */

    private short sourcePort;
    private short destPort;
    private long seqno;
    private long ackno;
    private long wnd;
    private short checksum;

    private byte[] data;

    /*
    * Control bits
    * */

    public static final short URG_Bit = 1;
    public static final short ACK_Bit = 1;
    public static final short PSH_Bit = 1;
    public static final short RST_Bit = 1;
    public static final short SYN_Bit = 1;
    public static final short FIN_Bit = 1;

    /**
     * Boolean to check the optional bits
     * */

    private boolean isUrg, isAck, isPsh, isRst, isSyn, isFin;
    private boolean ValidCheckSum;
    private int length = -1; //Specifies the segment length

    /**
     * Get source Address, destAddr
     * */
    public IP.IpAddress getSrcAddr() {

        return srcAddr;
    }

    public IP.IpAddress getDestAddr() {

        return destAddr;
    }

    /**
     * Get Source Port and Dest Port
     * */

    public short getSourcePort() {

        return sourcePort;
    }

    public short getDestPort() {

        return destPort;
    }

    public long getSeqno () {

        return seqno;
    }

    //Get the last sequence number

    public long getLastSeqno () {

        if (getLen() == 0 ){
            return seqno;
        } else {

            return (seqno + getLen());
        }
    }

    public long getAckno () {

        return ackno;
    }

    public long getWnd() {

        return wnd;
    }

    public byte[] getData () {

        return  data;
    }

    public  int getDataLength() {

        if (data == null) {

            return 0;
        } else {
            return data.length;
        }
    }

    public short getChecksum() {

        return checksum;
    }


    /**
     * Set all the Control bits
     * */

    public boolean isUrg () {

        return isUrg;
    }

    public boolean isAck () {

        return isAck;
    }

    public boolean isPsh () {

        return isPsh;
    }

    public boolean isRst () {

        return isRst;
    }

    public boolean isSyn () {

        return isSyn;
    }

    public boolean isFin () {

        return isFin;
    }

    public boolean isValidCheckSum () {

        return ValidCheckSum;
    }

    public int getLen () {

        if (length == -1) {

            length = getDataLength();

            if(isSyn) {
                length = length + 1;
            } else {
                length = length + 1;
            }
        }

        return length;
    }




    //Create a new Segment in TCP layer
    protected Segment (IP.IpAddress srcAddr, IP.IpAddress destAddr, short sourcePort, short destPort, long seqno, short wnd, long ackno) {

        this.srcAddr = srcAddr;
        this.destAddr = destAddr;
        this.sourcePort = sourcePort;
        this.destPort = destPort;
        this.seqno = seqno;
        this.wnd = wnd;

        if (ackno > -1) {
            this.ackno = ackno % Integer.MAX_VALUE;
            isAck = true;
        }

        //every data is set is pushed into the segment here.

        isPsh = true;
        isRst = false;

    }

    //Create a new Segment to process packets from IP Layer

    public Segment(byte[] packet, int srcAddr, int destAddr) {


        this.srcAddr = IP.IpAddress.getAddress(srcAddr);
        this.destAddr = IP.IpAddress.getAddress(destAddr);

        ByteBuffer bb = ByteBuffer.wrap(packet);
        sourcePort = bb.getShort();
        destPort = bb.getShort();

        seqno = bb.getInt();
        ackno = bb.getInt();
       // wnd = bb.getInt();



    }

    //Convert the packets back to string

//    public String toString () {
//
//
//        //appending
//        StringBuilder sb  = new StringBuilder();
//
//
//
//    }


}
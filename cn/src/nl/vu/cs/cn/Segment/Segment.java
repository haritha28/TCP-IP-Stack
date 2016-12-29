package cn.src.nl.vu.cs.cn.Segment;

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

    public long getAckno () {

        return ackno;
    }

    public long getWnd() {

        return wnd;
    }

    public byte[] getData () {

        return  data;
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







    //Create a new Segment
    public Segment (IP.IpAddress srcAddr, IP.IpAddress destAddr, short sourcePort, short destPort, long seqno) {


    }


}

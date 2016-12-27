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
    private short sourcePort;
    private short destPort;

    private long seqno;





    //Create a new Segment
    public Segment (IP.IpAddress srcAddr, IP.IpAddress destAddr, short sourcePort, short destPort, long seqno) {


    }


}

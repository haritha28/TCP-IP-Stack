package cn.src.nl.vu.cs.cn.Segment;

import cn.src.nl.vu.cs.cn.TranmissionControlBlock;

/**
 * Created by haritha on 6/1/17.
 */

public class SegmentUtils {


    public static Segment getSYNPacket (TranmissionControlBlock tcb, long seqno, long ack) {

        Segment  segment = new Segment(
                tcb.getLocalAddr(),
                tcb.getLocalPort(),
                seqno );

        //set the IsSYN packet to true

        return segment;

    }


    public static Segment getSYNACKPacket (TranmissionControlBlock tcb, long seqno, long ack) {

        //get the packet and return the segment is true


    }

    public static Segment getPacket (TranmissionControlBlock tcb, long seqno, long ack) {


    }

    public getFINPacket(TranmissionControlBlock tcb, long seqno, long ack) {


    }


}

package cn.src.nl.vu.cs.cn.Segment;

import cn.src.nl.vu.cs.cn.TranmissionControlBlock;

/**
 * Created by haritha on 6/1/17.
 */

public class SegmentUtils {


    public static Segment getSYNPacket (TranmissionControlBlock tcb, long seqno, long ack) {

        Segment segment = new Segment(
                tcb.getLocalAddr(), tcb.getForeignAddr(),
                tcb.getLocalport(), tcb.getForeignPort(),
                seq, tcb.getSendWindow());

        segment.setIsSyn(true);
        return segment;

    }


    public static Segment getSYNACKPacket (TranmissionControlBlock tcb, long seqno, long ack) {

        Segment segment = getPacket(tcb,seqno, ack);
        segment.getIsSYN(true);
        return segment;
        //get the packet and return the segment is true
    }

    public static Segment getPacket (TranmissionControlBlock tcb, long seqno, long ack) {
            Segment segment = new Segment(
                    tcb.getLocalAddr(),
                    tcb.getLocalPort(),
                    seqno,ack);
        return segment;
    }

    public static  Segment getFINPacket(TranmissionControlBlock tcb, long seqno, long ack) {
        Segment segment = new Segment(
                tcb.getLocalAddr(),
                tcb.getLocalPort(),
                ack);

        segment.setIsFin(true);
        return segment;
    }

    public static boolean inWindow(long left, long seq, long right) {

        boolean inWindow = (left <= right)
                ? (left <= seq && seq < right)
                : !(right <= seq && seq < left);

        return inWindow;
    }

    public static boolean overlap(long left1, long right1, long left2, long right2) {
        boolean overlap = inWindow(left1, left2, right1) || inWindow(left2, left1, right2))
                return overlap;
    }

    public static boolean isAcked(Segment segment, long ack) {
        return SegmentUtils.inWindow(segment.getSeqno(), segment.getLastSeqno(), ack);
    }

    public static boolean isAcked(long seq, long ack, int segmentLen) {
        long lastseq = (segmentLen > 0) ? (seq + segmentLen - 1) % Integer.MAX_VALUE :seq;
        return SegmentUtils.inWindow(seq, lastseq, ack);

    }

}

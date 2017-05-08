package nl.vu.cs.cn.tcp.segment;

import nl.vu.cs.cn.tcp.TransmissionControlBlock;

public abstract class SegmentUtil {

    /**
     * Construct the initial SYN packet needed to start the three-way handshake
     * @param tcb
     * @return
     */
    public static Segment getSYNPacket(TransmissionControlBlock tcb, long seq){
        Segment segment = new Segment(
                tcb.getLocalAddr(), tcb.getForeignAddr(),
                tcb.getLocalport(), tcb.getForeignPort(),
                seq, tcb.getSendWindow());

        segment.setIsSyn(true);
        return segment;
    }

    /**
     * Construct a SYN ACK packet used during the three-way handshake
     * @param tcb
     * @return
     */
    public static Segment getSYNACKPacket(TransmissionControlBlock tcb, long seq, long ack){
        Segment segment = getPacket(tcb, seq, ack);
        segment.setIsSyn(true);
        // isAck is automatically set inside getPacket()

        return segment;
    }

    /**
     * Construct an ACK packet possibly containing data
     * @param tcb
     * @return
     */
    public static Segment getPacket(TransmissionControlBlock tcb, long seq, long ack){
        Segment segment = new Segment(
                tcb.getLocalAddr(), tcb.getForeignAddr(),
                tcb.getLocalport(), tcb.getForeignPort(),
                seq, tcb.getSendWindow(), ack);

        // isAck is automatically set because we passed an ack num to Segment

        return segment;
    }

    /**
     * Construct an FIN packet
     * @param tcb
     * @return
     */
    public static Segment getFINPacket(TransmissionControlBlock tcb, long seq, long ack){
        Segment segment = new Segment(
                tcb.getLocalAddr(), tcb.getForeignAddr(),
                tcb.getLocalport(), tcb.getForeignPort(),
                seq, tcb.getSendWindow(), ack);

        segment.setIsFin(true);
        // isAck is automatically set because we passed an ack num to Segment

        return segment;
    }

    /**
     * Wraparound-safe check if seq is inside the window [left, right).
     * If left <= right, then it is assumed that the sequence numbers have wrapped around.
     *
     * @param left
     * @param seq
     * @param right
     * @return true if and only if seq is contained in the semi-open segment between [left, right).
     */
    public static boolean inWindow(long left, long seq, long right){
        boolean inWindow = (left <= right)
                ? (left <= seq && seq < right)
                : !(right <= seq && seq < left);

        return inWindow;
    }

    /**
     * Wraparound-safe check to see if the windows bounded by [left1, right1) and [left2, right2)
     * overlap each other in at least one point.
     *
     * @param left1
     * @param right1
     * @param left2
     * @param right2
     * @return
     */
    public static boolean overlap(long left1, long right1, long left2, long right2){
        boolean overlap = inWindow(left1, left2, right1) || inWindow(left2, left1, right2);
        return overlap;
    }

    /**
     * Check if the segment is fully ACKed by the given ack
     * @param segment
     * @param ack
     * @return
     */
    public static boolean isAcked(Segment segment, long ack){
        return SegmentUtil.inWindow(segment.getSeq(), segment.getLastSeq(), ack);
    }

    /**
     * Check if the sequence number has been ACKed by the given ack,
     * given the segment lenght was segmentLen.
     *
     * Note: only use this if it is certain what the segment length was.
     *
     * @param seq
     * @param ack
     * @param segmentLen
     * @return
     */
    public static boolean isAcked(long seq, long ack, int segmentLen){
        long lastSeq = (segmentLen > 0) ? (seq + segmentLen - 1) % Integer.MAX_VALUE : seq;
        return SegmentUtil.inWindow(seq, lastSeq, ack);
    }

}
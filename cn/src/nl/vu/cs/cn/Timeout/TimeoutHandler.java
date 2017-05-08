package nl.vu.cs.cn.tcp.timeout;


import java.io.IOException;

import nl.vu.cs.cn.IP;
import nl.vu.cs.cn.IPUtil;
import nl.vu.cs.cn.Log;
import nl.vu.cs.cn.tcp.TransmissionControlBlock;
import nl.vu.cs.cn.tcp.segment.RetransmissionSegment;
import nl.vu.cs.cn.tcp.segment.Segment;
import nl.vu.cs.cn.tcp.segment.SegmentUtil;

public class TimeoutHandler implements OnTimeoutListener {

    private String TAG = "TimeoutHandler";

    private final TransmissionControlBlock tcb;
    private final IP ip;

    public TimeoutHandler(IP ip, TransmissionControlBlock tcb){
        this.ip = ip;
        this.tcb = tcb;
    }

    public void onUserTimeout(){
        /*
         *  Unimplemented for now.
         *
         * TODO:
         * - flush all queus
         * - signal user: "error:  connection aborted due to user timeout" in general
         * - signal user: "error:  connection aborted due to user timeout" in any outstanding calls
         * - enter closed state
         */

        tcb.enterState(TransmissionControlBlock.State.CLOSED);
    }

    public void onRetransmissionTimeout(RetransmissionSegment retransmissionSegment){
        Segment segment = retransmissionSegment.getSegment();
        synchronized (tcb){
            // remove segment from the retransmission queue
            if(!tcb.removeFromRetransmissionQueue(retransmissionSegment)){
                // the segment did not exist in the queue anymore
                return;
            }

            // double check if segment is not acknowledged by now
            if(SegmentUtil.isAcked(segment, tcb.getSendUnacknowledged())){
                // the sequence number has been completely acknowledged by now
                return;
            }
        }

        int retryNum = retransmissionSegment.getRetry();
        if(retryNum >= TransmissionControlBlock.MAX_RETRANSMITS){
            Log.v(getTag(), "Segment " + segment.getSeq() + " was not ACKed. Not retrying");
            // not retrying, so don't add it to the queue again

            switch (tcb.getState()){
                case SYN_SENT:
                    // Client performing three-way handshake, but that failed. Move state to CLOSED
                    tcb.enterState(TransmissionControlBlock.State.CLOSED);
                    break;
                case SYN_RECEIVED:
                    // Server performing three-way handshake, but that failed. Move state to LISTEN
                    tcb.enterState(TransmissionControlBlock.State.LISTEN);
                    break;
            }

            // TODO: according to RFC 1122, we want to close the connection now (since something is obviously wrong)
            tcb.enterState(TransmissionControlBlock.State.CLOSED);
        } else {
            Log.v(getTag(), "Segment " + segment.getSeq() + " was not ACKed. Retry #" + (retryNum+1));

            retransmissionSegment.increaseRetry();

            IP.Packet packet = IPUtil.getPacket(segment);
            try {
                int bytesSent = ip.ip_send(packet);
                Log.v(getTag(), "Resent " + bytesSent + " bytes.");
            } catch (IOException e) {
                // if an error occurs set a timer again and retry afterwards
                Log.w(getTag(), "Error while resending packet", e);
            } finally {
                // retrying, so add segment to the retransmission queue again
                tcb.addToRetransmissionQueue(retransmissionSegment);
            }
        }
    }

    public void onTimeWaitTimeout(){
        tcb.enterState(TransmissionControlBlock.State.CLOSED);
    }

    /**
     * We have to determine the log TAG runtime (we don't know if we're the client or
     * the server up front)
     * @return
     */
    private String getTag(){
        return TAG + ((tcb.isServer()) ? " [server]" : " [client]");
    }
}
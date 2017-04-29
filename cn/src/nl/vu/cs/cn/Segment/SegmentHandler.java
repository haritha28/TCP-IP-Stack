package cn.src.nl.vu.cs.cn.Segment;


import java.io.IOException;

import cn.src.nl.vu.cs.cn.IP;
import cn.src.nl.vu.cs.cn.TranmissionControlBlock;
import cn.src.nl.vu.cs.cn.Log;


/**
 * Created by haritha on 2/1/17.
 * Deals with each layer it passes through
 */

public class SegmentHandler extends SegmentsArriveListener {

    private String TAG = "SegmentHandler";

    private volatile TranmissionControlBlock tcb;

    private volatile IP ip;





    public void onSegmentArrive (Segment segment) {

        Log.v (TAG, "Recieved:" + segment.toString());

        if (!segment.isValidCheckSum()) {

            Log.v(TAG,  "Recieved segment with invalid checksum. Dropping segment");
            return;
        }


        synchronized (tcb) {
            switch (tcb.getState()) {

                case CLOSED:
                    Log.v(TAG, "onSegmentArrive(): segment is dropped. Connection does not exist");
                    return ;

                case LISTEN:

                    SegmentArrivesInListenState(segment);
                    return;

                case SYN_RECIEVED:
                    break;
                case SYN_SENT:

                    SegmentArrivesInSynSentState();
                    return;
                case ESTABLISHED:
                    break;
                case FIN_WAIT_1:
                    break;
                case FIN_WAIT_2:
                    break;
                case TIME_WAIT:
                    break;
                case CLOSE_WAIT:
                    break;
                case LAST_ACK:
                    break;
                case CLOSING:
                    break;
                default:
                        //check if it is an acceptable sequence number
                        if(!acceptableSegment(segment)) {
                            Log.w(TAG, "onSegmentArrive(): unacceptable segment (" + segment.toString() + ") received. Dropping segment");
                            // send ACK <SEQ=SND.NXT><ACK=RCV.NXT><CTL=ACK>
                            Segment outSegment = SegmentUtils.getPacket(tcb, tcb.getSendNext(), tcb.getReceiveNext());
                            IP.Packet packet = IPUtil.getPacket(outSegment);
                            try {
                                Log.v(TAG, "Sending: " + outSegment.toString());
                                ip.ip_send(packet);
                            } catch (IOException e) {
                                Log.e(TAG, "Error while sending ACK. Will be retransmitted...", e);
                            }

                            return;
                        }

                        //no RST bit implementation
                        //no security checking implemented

                        //check SYN bit implementation

                        if(segment.isSyn()){
                            // This is an error, and should responed with RESET (however that's not supported)
                            Log.e(TAG, "onSegmentArrive(): unexpected SYN segment. Ignoring");
                            tcb.enterState(tcb.State.CLOSED);
                            return;
                        }

                        //check the ACK field
                        if (!segment.isAck()) {
                            Log.w(TAG, "onSegmentArrive(): unexpected segment without ACK. Dropping segment");
                            return;
                        } else {
                            if(! handleACKArriveInDefaultState(segment)){ //need to be implemented
                                return;
                            }
                        }

                        //check the URG bit field
                        if(segment.getDataLength() > 0) {
                            handleSegmentText(Segment);
                        }

                        //check if Fin bit
                        if (segment.isFin()) {
                            handleSegmentFIN(Segment);
                        }




            }
        }


    }

    private void handleACKArriveInDefaultState(Segment segment) {

    }

    private void handleSegmentText(Segment segment) {

    }

    private void handleSegmentFIN(Segment segment) {

    }

    public void SegmentArrivesInListenState (Segment segment) {

        //Recieve client sync and sent sync+ACK
        //Server is waiting to recieve a SYN message from the client

        if (segment.isRst()) {

            Log.v(TAG, "onSegmentArrive(RST): state is LISTEN, RST is ignored");

        } else if (segment.isAck()) {
            //recieves an ack which is not necessary
            Log.v(TAG, "Unkown acknowledgement");
            //Implementing the connection reset

        } else if (segment.isSyn()) {
            //recieves a SYN from the client

            //sends back a message with SYN+ACK

            //get the sequence number and add one to it.
            tcb.setRecieveNext(segment.getSeqno() +segment.getLen()% Integer. MAX_VALUE);
            tcb.setIntialSequenceNumber(segment.getSeqno());

            //server moves to SYN recieved state
            tcb.enterState(TranmissionControlBlock.State.SYN_RECIEVED);

            long isn = tcb.getIntialSequenceNumber();
            Segment outgoingSegment =  //have SYN+ACK pack (iss, offset for recieving the next);

            try {

                Log.v(TAG, "Sending: ");
                ip.ip_send(); //sends the packet

            } catch (IOException ) {

                Log.e(TAG, "Error while sending SYN+ACK", e)

            } finally {

                //goes to the transmission Queue and waits there for retransmission
                tcb.addToRetransmissionQueue(new RetransmissionSegment(outgoingSegment));
            }

            tcb.setSendNext(iss +segment.getLen()); //find the correct function

            tcb.setSendUnacknowledged(iss);

        } else {
            Log.w(TAG, "onSegmentArrive(): unexpected state. Not a problem though. Ignoring segment");
        }


    }

    public void SegmentArrivesInSynSentState (Segment segment) {

        if(segment.isAck()) {
            if(!SegmentUtils.inWindow(segment.getAckno(), segment.getAckno(), tcb.getIntialSequenceNumber()+2)) {

                Log.w(TAG, "onSegmentArrive(): unexpected ACK num (segment discarded)");
                return;
            }


        } else if (SegmentUtils.inWindow(tcb.getSendUnacknowledged(), segment.getAck(), tcb.getSendNext()+1)) {
            Log.v(TAG, "onSegmentArrive(): acceptable ACK received");

        } else {
            Log.w(TAG, "onSegmentArrive(): unacceptable ACK received. Ignoring");
            return;

        } else {
            Log.w(TAG, "onSegmentArrive(): excepted SYN-ACK in SYN SENT state. Missing ACK. Ignoring");
            return;
        }

        if(Segment.isSyn()) {

           //set and recieve next sequence number by the length of this segment

            tcb.setRecieveNext(segment.getSeqno() +segment.getLen()% Integer. MAX_VALUE);
            tcb.setIntialSequenceNumber(segment.getSeqno());

            tcb.setSendUnacknowldeged(segment.getAckno());
            tcb.removeFromRetransmissionQueue(segment.getAckno());

            if(SegmentUtils.isAcked(tcb.getIntialSequenceNumber(), segment.getAckno(), 1)){
                // our SYN has been ACKed
                tcb.enterState(tcb.State.ESTABLISHED);

                // TODO: add data that has been queued for transmission here

                // Send ACK segment <SEQ=SND.NXT><ACK=RCV.NXT><CTL=ACK>
                Segment outSegment = SegmentUtils.getPacket(tcb, tcb.getSendNext(), tcb.getReceiveNext());
                IP.Packet packet = IPUtil.getPacket(outSegment);
                try {
                    Log.v(TAG, "Sending: " + outSegment.toString());
                    ip.ip_send(packet);
                } catch (IOException e) {
                    Log.e(TAG, "Error while sending ACK.. will be retried", e);
                }

                tcb.advanceSendNext(outSegment.getLen());

            } else {
                tcb.enterState(tcb.State.SYN_RECEIVED);

                // Send SYN ACK segment <SEQ=ISS><ACK=RCV.NXT><CTL=SYN,ACK>
                Segment outSegment = SegmentUtils.getSYNACKPacket(tcb, tcb.getIntialSequenceNumber(), tcb.getReceiveNext());
                IP.Packet packet = IPUtil.getPacket(outSegment);
                try {
                    Log.v(TAG, "Sending: " + outSegment.toString());
                    ip.ip_send(packet);
                } catch (IOException e) {
                    Log.e(TAG, "Error while sending SYN ACK.. will be retried", e);
                } finally {
                    tcb.addToRetransmissionQueue(new RetransmissionSegment(outSegment));
                }
            }
        } else {
            Log.w(TAG, "onSegmentArrive(): excepted SYN-ACK in SYN SENT state. Missing SYN. Ignoring");
            return;
        }


    }









    }

    public void SegmentArrivesInFinState () {


    }

    public void ACKArrivesInDefaultState () {

    }




}

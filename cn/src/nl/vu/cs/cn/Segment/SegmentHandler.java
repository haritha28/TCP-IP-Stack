package cn.src.nl.vu.cs.cn.Segment;


import cn.src.nl.vu.cs.cn.TCP;
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

                    return ;

                case LISTEN:

                    SegmentArrivesInListenState(segment);
                    return;

                case SYN_SENT:

                    SegmentArrivesInSynSentState();
                    return;
                default:




            }
        }


    }

    public void SegmentArrivesInListenState (Segment segment) {

        //Recieve client sync and sent sync+ACK
        //Server is waiting to recieve a SYN message from the client

        if (segment.isAck()) {
            //recieves an ack which is not necessary
            Log.v(TAG, "Unkown acknowledgement");

        } else if (segment.isSyn()) {
            //recieves a SYN from the client
            //sends back a message with SYN+ACK



            //server moves to SYN recieved state
            tcb.enterState(TranmissionControlBlock.State.SYN_RECIEVED);
        } else {


        }


    }

    public void SegmentArrivesInSynSentState () {

        //Recieve SYN, Send ACK



        //Recieve SYN+ACK , Send ACK



    }

    public void SegmentArrivesInFinState () {


    }

    public void ACKArrivesInDefaultState () {

    }




}

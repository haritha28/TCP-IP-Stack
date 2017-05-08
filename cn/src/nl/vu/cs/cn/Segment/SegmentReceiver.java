package nl.vu.cs.cn.tcp.segment;


import nl.vu.cs.cn.IP;
import nl.vu.cs.cn.Log;


public class SegmentReceiver {

    private static final int RECEIVE_TIMEOUT = 1;
    private final ReceiverRunnable receiver;

    private volatile boolean shouldStop;

    public SegmentReceiver(OnSegmentArriveListener listener, IP ip){
        receiver = new ReceiverRunnable(listener, ip);
    }

    public void stop(){
        shouldStop = true;
    }

    public void run(){
        new Thread(receiver).start();
    }

    private class ReceiverRunnable implements Runnable {

        private final IP ip;
        private final OnSegmentArriveListener listener;
        private final IP.Packet packet;

        private ReceiverRunnable(OnSegmentArriveListener listener, IP ip) {
            this.ip = ip;
            this.listener = listener;
            packet = new IP.Packet();
        }

        @Override
        public void run() {
            while(!shouldStop){
                // reset packet.data before receiving again
                packet.data = null;

                // loop until we receive data
                while(!shouldStop && (packet.data == null || packet.data.length == 0)){
                    try {
                        ip.ip_receive_timeout(packet, RECEIVE_TIMEOUT);
                        if(packet.data != null){
                            Segment segment = new Segment(packet.data, packet.source, packet.destination);
                            Log.d("SegmentRecvr", "[Thread " + Thread.currentThread().getId() + "] Received segment!");
                            if(!shouldStop) {
                                listener.onSegmentArrive(segment);
                            } else {
                                Log.d("SegmentRecvr", "[Thread " + Thread.currentThread().getId() + "] Ignoring segment, stopping!");
                            }
                        } else {
                            Log.d("SegmentRecvr", "[Thread " + Thread.currentThread().getId() + "] Received EMPTY segment!");
                        }
                    } catch (Exception e) {
                        packet.data = null;
                        Log.w("SegmentRecvr", "[Thread " + Thread.currentThread().getId() + "] Exception in ip_receive_timeout()", e);
                    }
                }
            }
        }
    }

}
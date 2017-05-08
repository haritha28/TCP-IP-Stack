package nl.vu.cs.cn.tcp.segment;

/**
 * RetransmissionSegment wraps a Segment and maintains the retry number.
 */
public class RetransmissionSegment {

    private final Segment segment;
    private int retry;

    public RetransmissionSegment(Segment segment){
        this.segment = segment;
        retry = 0;
    }

    public Segment getSegment(){
        return segment;
    }

    public int getRetry(){
        return retry;
    }

    public void increaseRetry(){
        retry++;
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof Segment){
            return segment.equals(o);
        } else if(o instanceof RetransmissionSegment){
            return segment.equals(((RetransmissionSegment)o).segment);
        }

        return false;
    }
}
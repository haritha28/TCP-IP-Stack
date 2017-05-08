package nl.vu.cs.cn.tcp.timeout;

import nl.vu.cs.cn.tcp.segment.RetransmissionSegment;

public interface OnTimeoutListener {

    public void onUserTimeout();
    public void onRetransmissionTimeout(RetransmissionSegment retransmissionSegment);
    public void onTimeWaitTimeout();
}
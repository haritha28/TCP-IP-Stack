

/**
 * Created by haritha on 9/5/17.
 */

package nl.vu.cs.cn;

import nl.vu.cs.cn.tcp.segment.Segment;

public abstract class IPUtil {

    /**
     * Create a new IP packet that wraps the TCP segment and
     * sets the destination address.
     *
     * @param segment
     * @return
     */
    public static IP.Packet getPacket(Segment segment){
        byte[] data = segment.encode();
        IP.Packet packet = new IP.Packet(
                segment.getDestinationAddr().getAddress(),
                IP.TCP_PROTOCOL,
                0,
                data,
                data.length);

        return packet;
    }
}
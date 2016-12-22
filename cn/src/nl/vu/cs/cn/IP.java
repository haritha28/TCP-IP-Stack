package nl.vu.cs.cn;

import java.io.IOException;

/**
 * This class represents a virtual IP stack. A stack can be constructed
 * with addresses 1-254. The virtual IP address will then be 192.168.0.address.
 * <br/>
 * Underlying transport is handled by encapsulating
 * IP packets in UDP packets and sending them to well known port numbers
 * which match the virtual address over the localhost interface.
 * <br/>
 * Note that it is possible to ask this layer to corrupt or loose a certain
 * percentage of packets with random probability by setting System properties
 * PACKET_LOSS and PACKET_CORRUPTION to a percentage 0-100. LOSS and
 * CORRUPTION will be logged each time this layer performs a loss or corruption.
 * <br/>
 * Note that it is very hard to ensure a test case always tests a given behavior based on
 * any random behavior. (hint. hint.)
 * <br/>
 * Finally, note that this IP layer does NOT handle any fragmentation, so packets
 * must be smaller than 8100 bytes to be safely transmitted without truncation.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public class IP {

    /** The protocol number for UDP. */
    public static final int UDP_PROTOCOL = 17;

    /** The protocol number for TCP. */
    public static final int TCP_PROTOCOL = 6;

    /** The local virtual address. */
    private int address;
    /** The local virtual address as an IpAddress. */
    private IpAddress ipAddress;
    /** The underlying UDP port number. */
    private int port;
    /** The native sending socket. */
    private int sending_socket;
    /** The native receiving socket. */
    private int receiving_socket;

    public static class IpAddress {
    	/**
    	 * The address this instance represents.
    	 */
    	private int address;

    	/**
    	 * Construct an ip address object to represent this address.
    	 * @param ipAddress the address.
    	 */
    	private IpAddress(int ipAddress) {
			address = ipAddress;
		}

		/**
         * Note that "host" order in this case is little-endian,
         * despite the fact that Java is always big-endian.
         *
    	 * @return the address as an integer in "host" order.
    	 */
    	public int getAddress() {
    		return address;
    	}

        /**
         * Constructs an IpAddress based on dotted notation.

         * @param ip the ip string to construct an address for.
         * @return the address object
         */
    	public static IpAddress getAddress(String ip) {
    		String[] addrArray = ip.split("\\.");
    		if (addrArray.length != 4) {
    			throw new IllegalArgumentException("Not a valid IP.");
    		}
    		return new IpAddress(Integer.parseInt(addrArray[0]) +
    				(Integer.parseInt(addrArray[1]) << 8) +
    				(Integer.parseInt(addrArray[2]) << 16) +
    				(Integer.parseInt(addrArray[3]) << 24));
    	}

        /**
         * Constructs an IP address from a "host" order address.
         * <br/>
         * Note that "host" order in this case is little-endian,
         * despite the fact that Java is always big-endian.
         *
         * @param ip the ip string to convert
         * @return the integer equivalent address.
         */
    	public static IpAddress getAddress(int ip) {
    		return new IpAddress(ip);
    	}

    	/**
    	 * Converts an to a string.
    	 * <br/>
         * Note that "host" order in this case is little-endian,
         * despite the fact that Java is always big-endian.
         *
    	 * @return the dotted string equivalent of this address.
    	 */
    	public String toString() {
    		return htoa(address);
    	}

    	/**
    	 * Converts an address in "host" order to a string.
    	 * <br/>
         * Note that "host" order in this case is little-endian,
         * despite the fact that Java is always big-endian.
         * @return the string representation of this address.
         */
    	public static String htoa(int address) {
    	    return String.valueOf((address & 0x000000ff)) + "."
    	            + String.valueOf((address & 0x0000ff00) >> 8) + "."
    	            + String.valueOf((address & 0x00ff0000) >> 16) + "."
    	            + String.valueOf(address >> 24 & 0xff);
    	}
    }

    // Load the native library.
    static {
        System.loadLibrary("cnpracticum");
    }

    /**
     * Construct a virtual IP interface with the given address number.
     * The virtual IP address will then be 192.168.0.<address>
     *
     * @param address a virtual address 1-254.
     * @throws IOException if initialization fails.
     */
    public IP(int address) throws IOException {
        if (address < 1 || address > 254) {
            throw new IllegalArgumentException("Invalid address. 1-254 only.");
        }

        ipAddress = IpAddress.getAddress("192.168.0." + address);
        ip_init(address);
    }

    /**
     * @return the virtual IP address for this IP stack.
     */
    public IpAddress getLocalAddress() {
        return ipAddress;
    }

    /**
     * This class represents a packet received by the IP interface.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     *
     */
    public static final class Packet {
    	/** The source address for the packet. Note the value is stored little-endian. */
        int source;
        /** The destination address for the packet. Note the value is stored little-endian. */
        int destination;
        /** The protocol number. */
        int protocol;
        /** The id for the packet. */
        int id;
        /** The data for the packet. May contain garbage beyond length bytes. May be reallocated by the JNI layer. */
        byte[] data;
        /** The length of the packet. Possibly less than data.length. */
        int length;

        public Packet() {
        }

        /** Construct a packet for sending. */
        public Packet(int destination, int protocol, int id,
                byte[] data, int length) {
            this.destination = destination;
            this.protocol = protocol;
            this.id = id;
            this.data = data;
            this.length = length;
        }

        public String toString() {
            return "Source: " + IpAddress.htoa(source) + " Dest: " + IpAddress.htoa(destination) + " Proto: "
                    + protocol + " Id: " + id + " Data: " + arrayString()
                    + " Len: " + length;
        }

        private String arrayString() {
        	StringBuffer dataString = new StringBuffer("[");
        	for (int i = 0; i < length; i++) {
        		if (i > 0) {
            		dataString.append(",");
        		}
        		dataString.append(data[i]);
        	}
        	dataString.append("]");
        	return dataString.toString();
        }
    }

    /**
     * Initializes the underlying native IP layer.
     * @param address the last octet of the virtual IP Address 1-254.
     * @throws IOException if initialization fails.
     */
    private native void ip_init(int address) throws IOException;

    /**
     * Sends the requested packet using the virtual IP stack.
     *
     * @param p the packet to send
     * @return the number of bytes of the data actually sent.
     * @throws IOException if sending fails
     */
    public native int ip_send(Packet p) throws IOException;

    /**
     * Receives a packet. Note that this call takes a packet
     * in order to allow client to reduce allocations, and thus
     * reduce garbage collection pressure. The data field
     * in the packet will be reused, so the data.length should
     * not be used to determine the amount of data in the packet
     * but rather the length field of the packet should be used
     * to determine how much is really stored in the data array.
     * <br>
     * This method is equivalent to calling ip_receive_timeout with
     * a timeout of zero, except that InterruptedException is
     * never thrown.
     *
     * @param p the received packet
     * @throws IOException if receiving fails
     */
    public native void ip_receive(Packet p) throws IOException;

    /**
     * Receives a packet. Note that this call takes a packet
     * in order to allow client to reduce allocations, and thus
     * reduce garbage collection pressure. The data field
     * in the packet will be reused, so the data.length should
     * not be used to determine the amount of data in the packet
     * but rather the length field of the packet should be used
     * to determine how much is really stored in the data array.
     * <br>
     * This call allows a timeout to be set. If the timeout
     * causes a packet not to be received this call throws
     * an InterruptedException. A timeout less than or equal
     * to zero is equivalent to calling ip_receive without
     * a timeout.
     *
     * @param p the received packet
     * @param timeout the timeout in seconds
     * @throws IOException if receiving fails
     * @throws InterruptedException if a timeout occurred
     */
    public native void ip_receive_timeout(Packet p, int timeout)
	throws IOException, InterruptedException;

}

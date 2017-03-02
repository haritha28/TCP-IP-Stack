package cn.src.nl.vu.cs.cn;

import java.io.IOException;

import cn.src.nl.vu.cs.cn.IP.IpAddress;



/**
 * This class represents a TCP stack. It should be built on top of the IP stack
 * which is bound to a given IP address.
 */
public class TCP {

	/** The underlying IP stack for this TCP stack. */
    private IP ip;
    private static final short CLIENT_LOCAL_PORT = 1026;
    protected TranmissionControlBlock tcb;
    private String TAG = "TCP";

    //private TransmissionControlBlock TCB;


    /**
     * Function to sent the data
     * */

    private int send (byte [] buf, int offset, int len) {

        switch(tcb.getState()) {

            case CLOSED:
                Log.e();
                return -1;

            case LISTEN:
                Log.e();
                return -1;

            case SYN_SENT:
                //
                return -1;

            case SYN_RECIEVED:
                Log.e();
                return -1;

            case ESTABLISHED:

                return -1;

            case CLOSE_WAIT:

                return -1;

        }


    }


    /**
     *Fucntion to recieve data
     * */

    private int recieve (byte[] buf, int offset, int maxlen) {

        switch(tcb.getState()) {

            case CLOSED:
                Log.e();
                return -1;

            case LISTEN:
                Log.e();
                return -1;

            case SYN_SENT:
                //
                return -1;

            case SYN_RECIEVED:
                Log.e();
                return -1;

            case ESTABLISHED:

                return -1;

            case CLOSE_WAIT:

                return -1;

        }

    }


    /**
     * This class represents a TCP socket.
     *
     */
    public class Socket {

    	/* Hint: You probably need some socket specific data. */

    	/**
    	 * Construct a client socket.
    	 */
    	private Socket() {

            //Get the port and ip address
            tcb.setLocalSocketInfo(ip.getLocalAddress(), CLIENT_LOCAL_PORT);


    	}

    	/**
    	 * Construct a server socket bound to the given local port.
		 *
    	 * @param port the local port to use
    	 */
        private Socket(int port) {
			// TODO Auto-generated constructor stub
            tcb.setLocalSocketInfo(ip.getLocalAddress(), (short)port);

		}

		/**
         * Connect this socket to the specified destination and port.
         *
         * @param dst the destination to connect to
         * @param port the port to connect to
         * @return true if the connect succeeded.
         */
        public boolean connect(IpAddress dst, int port) {

            // Implement the connection side of the three-way handshake here.

            if(tcb.getState() == TranmissionControlBlock.State.LISTEN) {
                //Send an error message
                Log.e (TAG,"Error in connection");
                return false;

            } else if (tcb.getState() == TranmissionControlBlock.State.CLOSED) {
                Log.e (TAG,"Error in connection");
                return false;

            } else if (dst == null || port == 0){
                Log.e (TAG,"Error in connection");
                return false;

            }


            return false;
        }

        /**
         * Accept a connection on this socket.
         * This call blocks until a connection is made.
         */
        public void accept() {

            // Implement the receive side of the three-way handshake here.
            tcb.enterState(TranmissionControlBlock.State.LISTEN);
            Log.v(TAG, "accept(): waits it get accepted");
            tcb.waitForStates(TranmissionControlBlock.State.ESTABLISHED);

        }

        /**
         * Reads bytes from the socket into the buffer.
         * This call is not required to return maxlen bytes
         * every time it returns.
         *
         * @param buf the buffer to read into
         * @param offset the offset to begin reading data into
         * @param maxlen the maximum number of bytes to read
         * @return the number of bytes read, or -1 if an error occurs.
         */
        public int read(byte[] buf, int offset, int maxlen) {

            // Read from the socket here.

            return recieve (buf, offset, maxlen);
        }

        /**
         * Writes to the socket from the buffer.
         *
         * @param buf the buffer to
         * @param offset the offset to begin writing data from
         * @param len the number of bytes to write
         * @return the number of bytes written or -1 if an error occurs.
         */
        public int write(byte[] buf, int offset, int len) {

            // Write to the socket here.

            return -1;
        }

        /**
         * Closes the connection for this socket.
         * Blocks until the connection is closed.
         *
         * @return true unless no connection was open.
         */
        public boolean close() {

            // Close the socket cleanly here.

            return false;
        }
    }

    /**
     * Constructs a TCP stack for the given virtual address.
     * The virtual address for this TCP stack is then
     * 192.168.0.address.
     *
     * @param address The last octet of the virtual IP address 1-254.
     * @throws IOException if the IP stack fails to initialize.
     */
    public TCP(int address) throws IOException {
        ip = new IP(address);
    }

    /**
     * @return a new socket for this stack
     */
    public Socket socket() {

        return new Socket();
    }

    /**
     * @return a new server socket for this stack bound to the given port
     * @param port the port to bind the socket to.
     */
    public Socket socket(int port) {

        return new Socket(port);
    }

    /**
     * Function for sedn
     */

}

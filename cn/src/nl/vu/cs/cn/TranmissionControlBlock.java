package cn.src.nl.vu.cs.cn;

import java.util.ArrayList;
import java.util.Arrays;


import cn.src.nl.vu.cs.cn.IP;
import cn.src.nl.vu.cs.cn.TCP;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.Random;


public class TranmissionControlBlock {

    /*Mentions all the
    * states in which
    * connection happens
    * */
    public enum  State {
        CLOSED,
        LISTEN,
        SYN_RECIEVED,
        SYN_SENT,
        ESTABLISHED,
        FIN_WAIT_1,
        FIN_WAIT_2,
        TIME_WAIT,
        CLOSE_WAIT,
        LAST_ACK,
        CLOSING

    };
    private State state;
    private IP.IpAddress localAddr;
    private short localPort;
    private final Lock StateLock = new ReentrantLock();

    public String TAG =  "TCB";

    private long iss;
    private long irs;
    public long rcv_next;

    /**
     * Socket Address
     */
    public void setLocalSocketInfo (IP.IpAddress localAddr, short localPort) {

        this.localAddr = localAddr;
        this.localPort =  localPort;

    }

    /*
    * Get the Local address from IP class
    * */
    public IP.IpAddress getLocalAddr () {

        return localAddr;
    }
    /*
    * Get the Local Port
    * */
    public short getLocalPort () {

        return localPort;
    }
    /*
    * GEt the current state
    * where TCP is in
    * */
    public State getState() {
            StateLock.lock();
        try {
            return state;
        } finally {
            StateLock.unlock();
        }
    }

    /*
    * Function to change TCP state
    * */
    public synchronized void enterState (State state) {

        StateLock.lock();

        try {

            Log.v(TAG, "Enter State" + state);
            this.state = state;

            if (state == State.CLOSED) {

            }
        } finally {
            StateLock.unlock();
        }

    }


    /**
     * Function to wait for different states
     * */

    public void waitForStates (TranmissionControlBlock.State state) {

        ArrayList<State> al = new ArrayList();



    }



    public void setIntialSequenceNumber (long irs) {

        this.irs = irs;
    }


    /*
    * Function to get intial sequence number
    * This implementation supports only one client one user
    * */
    public long getIntialSequenceNumber() {

        if( iss == 0 ) {

           //we can use nano time and get the last 32 bits
            iss = System.nanoTime() % Integer.MAX_VALUE;
        }

        return iss;

    }

    public void setRecieveNext (long rcv_next) {

        this.rcv_next = rcv_next % Integer.MAX_VALUE;
    }

    public long getRecieveNext () {
        return rcv_next;
    }



}



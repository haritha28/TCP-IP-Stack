package cn.src.nl.vu.cs.cn;

import java.util.ArrayList;
import java.util.Arrays;

import cn.src.nl.vu.cs.cn.IP;
import cn.src.nl.vu.cs.cn.TCP;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        ESTABLISHED
    };
    private State state;
    private IP.IpAddress localAddr;
    private short localPort;
    private final Lock StateLock = new ReentrantLock();

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


}



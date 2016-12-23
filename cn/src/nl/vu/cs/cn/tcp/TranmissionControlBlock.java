package cn.src.nl.vu.cs.cn.tcp;

import java.util.ArrayList;
import java.util.Arrays;

import cn.src.nl.vu.cs.cn.IP;
import cn.src.nl.vu.cs.cn.TCP;

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
    private IP.IpAddress localAddr;
    private short localPort;

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










}



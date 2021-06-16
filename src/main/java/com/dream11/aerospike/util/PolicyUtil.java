package com.dream11.aerospike.util;

import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Replica;
import com.dream11.aerospike.config.AerospikeConnectOptions;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class PolicyUtil {
    private static final String OS = System.getProperty("os.name");

    public static ClientPolicy getClientPolicy(){
        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.readPolicyDefault.replica = Replica.MASTER_PROLES;
        return clientPolicy;
    }

    public static void setPolicies(ClientPolicy policy, AerospikeConnectOptions config) {
        EventPolicy eventPolicy = new EventPolicy();
        eventPolicy.maxCommandsInProcess = config.getMaxCommandsInProcess();
        EventLoopGroup group = getEventLoopGroup(config.getEventLoopSize());
        policy.eventLoops = new NettyEventLoops(eventPolicy, group);
        policy.maxConnsPerNode = config.getMaxConnsPerNode();
        policy.writePolicyDefault.setTimeout(config.getWriteTimeout());
        policy.readPolicyDefault.setTimeout(config.getReadTimeout());
    }

    private static EventLoopGroup getEventLoopGroup(int size) {
        return OS.contains("linux") || OS.contains("unix") ?
            new EpollEventLoopGroup(size) : new NioEventLoopGroup(size);
    }
}

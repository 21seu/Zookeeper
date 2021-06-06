package com.ftj.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorTest {

    private CuratorFramework client;

    /**
     * 建立连接
     */
    @Before
    public void testConnect() {
        //1、第一种方式
        /**
         * Create a new client
         *
         * @param connectString       list of servers to connect to   zk server地址&端口
         * @param sessionTimeoutMs    session timeout   会话超时时间 单位ms
         * @param connectionTimeoutMs connection timeout   连接超时时间 单位ms
         * @param retryPolicy         retry policy to use   重试策略
         * @return client
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        /*CuratorFramework client = CuratorFrameworkFactory.newClient(
                "localhost:2181",
                60 * 1000,
                15 * 1000,
                retryPolicy);*/

        //第二种方式
        client = CuratorFrameworkFactory.builder().connectString("localhost:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolicy).namespace("21seu").build();

        //开启连接
        client.start();
    }

    /**
     * 创建节点：create 持久/临时/顺序 数据
     * 1、基本创建
     * 2、创建节点 带有数据
     * 3、设置节点的类型
     * 4、创建多级节点 app1/p1
     */
    @Test
    public void testCreate() throws Exception {
        //1、基本创建
        //如果创建节点没有指定数据，则默认将当前客户端的id作为数据存储
        String path = client.create().forPath("/app1");
        System.out.println(path);
    }

    @Test
    public void testCreate2() throws Exception {
        //2、创建节点带有数据
        String path = client.create().forPath("/app2", "hello".getBytes());
        System.out.println(path);
    }

    @Test
    public void testCreate3() throws Exception {
        //3、设置节点类型
        //默认类型：持久化
        String path = client.create().withMode(CreateMode.EPHEMERAL).forPath("/app3");
        System.out.println(path);
    }

    @Test
    public void testCreate4() throws Exception {
        //4、设置多级节点 /app4/p6
        //creatingParentsIfNeeded()：如果父节点不存在则创建父节点
        String path = client.create().creatingParentsIfNeeded().forPath("/app3");
        System.out.println(path);
    }

    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}

package com.ftj.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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

//====================create======================================================================

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

//======================get======================================================================

    /**
     * 查询节点：
     * 1、查询数据：get   getData().forPath("/app1");
     * 2、查询子节点：ls   getChildren().forPath("/app4")
     * 3、查询节点状态信息：ls -s     Stat stat = new Stat();     getData().storingStatIn(stat).forPath("/app1");
     */
    @Test
    public void testGet() throws Exception {
        //1、查询数据：get
        byte[] bytes = client.getData().forPath("/app1");
        System.out.println(new java.lang.String(bytes));
    }

    @Test
    public void testGet2() throws Exception {
        //2、查询子节点：ls
        List<String> list = client.getChildren().forPath("/app4");
        System.out.println(list);
    }

    @Test
    public void testGet3() throws Exception {
        Stat stat = new Stat();
        //3、查询节点状态信息：ls -s
        client.getData().storingStatIn(stat).forPath("/app1");
        System.out.println(stat);
    }


//======================set======================================================================

    /**
     * 修改数据
     * 1、修改数据   setData().forPath();
     * 2、根据版本修改 setData().withVersion(version).forPath();  version是通过查询出来的，目的是为了让其他客户端或线程不干扰我
     *
     * @throws Exception
     */
    @Test
    public void testSet() throws Exception {
        client.setData().forPath("/app1", "fengtj".getBytes());
    }

    @Test
    public void testSetForVersion() throws Exception {
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/app1");
        int version = stat.getVersion();
        System.out.println(version);
        client.setData().withVersion(version).forPath("/app1", "fengtj".getBytes());
    }


    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }
}

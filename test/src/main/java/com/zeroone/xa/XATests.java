package com.zeroone.xa;

import com.google.common.base.Stopwatch;
import com.mysql.jdbc.JDBC4Connection;
import com.mysql.jdbc.jdbc2.optional.MysqlXAConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlXid;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class XATests {


    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public volatile boolean isStop = false;

    private final Logger log = LoggerFactory.getLogger(XATests.class);

    public static void main(String[] args) throws Exception {

        XATests xaTests = new XATests();
        new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                if (xaTests.isStop){
                    xaTests.countDownLatch.countDown();
                    break;
                }
                try {
                    xaTests.xaTransactionMock(999000 + i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Scanner input = new Scanner(System.in);
        System.out.println(input.nextLine());
        xaTests.isStop = true;
        xaTests.countDownLatch.await();

    }

    public void xaTransactionMock(int seq) throws Exception {


        // 是否开启日志
        boolean logXaCommands = false;
        // 获取账户库的 rm（ap做的事情）
        JDBC4Connection con = (JDBC4Connection) DriverManager.getConnection("jdbc:mysql://192.168.127.8:3306/source?useUnicode=true&characterEncoding=utf8&useSSL=false", "root", "123456");
        XAConnection xaConnection = new MysqlXAConnection(con, logXaCommands);
        XAResource xaResource = xaConnection.getXAResource();
        Connection connection = xaConnection.getConnection();
        // XA 事务开始了
        // 全局事务
        byte[] globalId = ("g" + String.format("%04d",seq)).getBytes(StandardCharsets.UTF_8);
        // 就一个标识
        int formatId = 1;
        // 账户的分支事务
        byte[] branch =("b" + String.format("%04d",seq + 1)).getBytes(StandardCharsets.UTF_8);
        Xid xid = new MysqlXid(globalId, branch, formatId);
        try {
            Stopwatch started = Stopwatch.createStarted();
            // 账号事务开始 此时状态：ACTIVE
            xaResource.start(xid, XAResource.TMNOFLAGS);
            // 模拟业务
            String template = "INSERT INTO `source`.`xa-test` (`id`, `name`, `title`, `text`) VALUES (null, 'test', 'record 1mb size test', '%s');";

            Random random = new Random();
            int count = 500;
            for (int i = 0; i < count; i++) {
                int randomSize = random.nextInt(10);
                String s = RandomStringUtils.randomNumeric(randomSize);
                String sql = String.format(template, s);
                PreparedStatement ps1 = connection.prepareStatement(sql);
                ps1.execute();
            }
            xaResource.end(xid, XAResource.TMSUCCESS);
            //此时状态为 idle阶段;
            // 第一阶段：准备提交
            int rm1_prepare = xaResource.prepare(xid);
            // XA 事务 此时状态：PREPARED
            // 第二阶段：TM 根据第一阶段的情况决定是提交还是回滚
            boolean onePhase = false; //TM判断有2个事务分支，所以不能优化为一阶段提交
            if (rm1_prepare == XAResource.XA_OK) {
                xaResource.commit(xid, onePhase);
            } else {
                xaResource.rollback(xid);
            }
           log.info("耗时：{}，写入：{}",started.stop().elapsed(TimeUnit.MILLISECONDS),count);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常，回滚
            xaResource.rollback(xid);
        }

    }
}

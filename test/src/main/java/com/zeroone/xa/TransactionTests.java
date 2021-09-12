package com.zeroone.xa;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionTests {


    private final static Logger log = LoggerFactory.getLogger(TransactionTests.class);


    private static final String jdbcConnectionTemplate = "jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf8&useSSL=false";

    private static final String databaseNameBenchMark = "benchmark";

    private static final String connectionString = "172.16.50.101:16310";

    private static final String normalConnectionString = "172.16.50.106:16315";

    private static final String localDatabaseName = "source";

    private static final String localConnectionString = "192.168.127.8:3306";

    private static final String greatV5ConnectionString = "172.16.110.128:3316";

    private static final String greatV5DatabaseName = "sbtest";

    private static final String testJdbcConnectionString = String.format(jdbcConnectionTemplate,connectionString,databaseNameBenchMark);

    private static final String testNormalJdbcConnectionString = String.format(jdbcConnectionTemplate,normalConnectionString,localDatabaseName);

    private static final String localJdbcConnectionString = String.format(jdbcConnectionTemplate,localConnectionString,localDatabaseName);

    private static final String greatV5JdbcConnectionString = String.format(jdbcConnectionTemplate,greatV5ConnectionString,greatV5DatabaseName);


    static final String useConnectionString = localJdbcConnectionString;

    static final String localPassword = "123456";

    static final String testPassword = "Bgview@2021";

    static final String greatV5Password = "greatdb";

    static final String usePassword = localPassword;

    static final String useNormalConnectionString = testNormalJdbcConnectionString;


    public static void main(String[] args) throws Exception {

        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)p.get(key);
            System.out.println(key + ": " + value);
        }

//        for (int i = 0; i < 300; i++) {
//
//            insertRetlMarkTable(i + 1);
//        }

//        greatDBVersion5Transaction("0001");

//        transaction("0009");

//        mqTransaction("122212");
//        int count = 10;
//        CountDownLatch countDownLatch = new CountDownLatch(count);
//        ExecutorService executorService = Executors.newFixedThreadPool(20);
//        AtomicLong atomicLong = new AtomicLong(0);
//        for (int i = 0; i < count; i++) {
//
//            executorService.execute(() -> {
//                try {
//                    long l = mqTransaction("123123");
//                    atomicLong.addAndGet(l);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                countDownLatch.countDown();
//            });
//        }
//
//        countDownLatch.await();
//        executorService.shutdown();
//        log.info("最终执行结果条目：{}",atomicLong.get());

//        mergeTest();
//        greatDBVersion5Transaction("00003");
//        localLoopBackTransaction("00004");
//        dropTable();
//
//        createTable();
//
//        CountDownLatch countDownLatch = new CountDownLatch(5000);
//        ExecutorService executorService = Executors.newFixedThreadPool(20);
//        for (int i = 0; i < 5000; i++) {
//            final String batchId = "000" + i;
//            executorService.submit(() -> {
//                try {
//
//                    log.info("线程：{}，执行xa数据插入",Thread.currentThread().getId());
//                    transaction(batchId);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                countDownLatch.countDown();
//            });
//
//        }
//        countDownLatch.await();
//        executorService.shutdown();


    }


    public static void insertRetlMarkTable(int i){

        String sql = "INSERT INTO `retl`.`retl_mark` (`ID`, `CHANNEL_ID`, `CHANNEL_INFO`, `MARK`) VALUES (" + i + ", 9, '_SYNC', 0);";
        try {
            Connection con = DriverManager.getConnection(greatV5JdbcConnectionString, greatV5Password, greatV5Password);

            con.setAutoCommit(true);
            PreparedStatement ps1 = con.prepareStatement(sql);
            ps1.execute();
            ps1.close();
            con.close();
        } catch (Exception e) {
            // 出现异常，回滚
            e.printStackTrace();
        }
    }

    public static void dropTable() {

        String dropTable = "DROP TABLE IF EXISTS `benchmark`.`xa01`;";
        try {
            log.info("执行删除表操作...............");
            Connection con = DriverManager.getConnection(useConnectionString, "root", usePassword);

            con.setAutoCommit(true);
            PreparedStatement ps1 = con.prepareStatement(dropTable);
            ps1.execute();
            ps1.close();
            con.close();
        } catch (Exception e) {
            // 出现异常，回滚
            e.printStackTrace();
        }
    }

    public static void createTable() {

        String table = "CREATE TABLE IF NOT EXISTS `benchmark`.`xa01` (\n" +
                "PARTITION KEY ON id, \n" +
                "  PARTITION SCHEME ON hash_type," +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(30) DEFAULT NULL,\n" +
                "  `title` varchar(255) DEFAULT NULL,\n" +
                "  `text` mediumtext,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

        try {
            log.info("执行创建表操作...............");
            Connection con = DriverManager.getConnection(useConnectionString, "root", usePassword);
            con.setAutoCommit(true);
            PreparedStatement ps1 = con.prepareStatement(table);
            ps1.execute();
            ps1.close();
            con.close();
        } catch (Exception e) {
            // 出现异常，回滚
            e.printStackTrace();
        }

    }

    public static void transaction(String batchId) throws Exception {


        Connection con = DriverManager.getConnection(testJdbcConnectionString, "root", testPassword);
        try {

            con.setAutoCommit(false);

            Stopwatch started = Stopwatch.createStarted();
            String template = "INSERT INTO `benchmark`.`xa01` (`id`, `name`, `title`, `text`) VALUES (null, 'test-%06d', 'batch-%s', '%s');";

            log.info("执行插入表操作...............");
            Random random = new Random();
            int count = 500;
            for (int i = 0; i < 10; i++) {
                int randomSize = random.nextInt(10);
                String s = RandomStringUtils.randomNumeric(randomSize);
                String sql = String.format(template, i, batchId, s);
                PreparedStatement ps1 = con.prepareStatement(sql);
                ps1.execute();
            }
            con.commit();
            con.close();

            log.info("耗时：{}，写入：{}", started.stop().elapsed(TimeUnit.MILLISECONDS), count);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常，回滚
            con.rollback();
        }

    }

    public static void mergeTest() throws SQLException {


        String sqls[] = {
                "INSERT INTO `source`.`xa-test` (`id`, `name`, `title`, `text`) VALUES (null, 'test-000000', 'batch-123123', '9657264');",
                "INSERT INTO `source`.`xa-test` (`id`, `name`, `title`, `text`) VALUES (null, 'test-000001', 'batch-123123', '8496');",
                "INSERT INTO `source`.`xa-test` (`id`, `name`, `title`, `text`) VALUES (null, 'test-000002', 'batch-123123', '546117626');",
                "INSERT INTO `source`.`xa-test` (`id`, `name`, `title`, `text`) VALUES (null, 'test-000003', 'batch-123123', '44');",
                "INSERT INTO `source`.`xa-test` (`id`, `name`, `title`, `text`) VALUES (null, null, 'batch-123123', '74906');"
        };

        Connection con = DriverManager.getConnection(localJdbcConnectionString, "root", localPassword);
        try {

            con.setAutoCommit(false);

            Stopwatch started = Stopwatch.createStarted();

            log.info("执行插入表操作...............");
            Long id = null;
            for (String sql : sqls) {
                PreparedStatement ps1 = con.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
                ps1.execute();
                ResultSet generatedKeys = ps1.getGeneratedKeys();
                boolean next = generatedKeys.next();
                id = generatedKeys.getLong(1);
            }
            String updateSql = "UPDATE `source`.`xa-test` SET `name` = 'test-000004-update', `title` = 'batch-123123', `text` = '74906' WHERE `id` = %d;";

            String format = String.format(updateSql, id);
            PreparedStatement ps1 = con.prepareStatement(format);
            ps1.execute();

            con.commit();
            con.close();

            log.info("耗时：{}，写入：{}", started.stop().elapsed(TimeUnit.MILLISECONDS), 10);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常，回滚
            con.rollback();
        }
    }


    public static void greatDBVersion5Transaction(String batchId) throws Exception {


        Connection con = DriverManager.getConnection(greatV5JdbcConnectionString, "greatdb", greatV5Password);
        try {

            con.setAutoCommit(false);

            Stopwatch started = Stopwatch.createStarted();
            String template = "INSERT INTO `sbtest`.`xa01` (`id`, `name`, `title`, `text`) VALUES (null, 'test-%06d', 'batch-%s', '%s');";

            log.info("执行插入表操作...............");

            String retlSql = "INSERT INTO `retl`.`retl_mark` (`ID`, `CHANNEL_ID`, `CHANNEL_INFO`, `MARK`) VALUES (null, 9, '_SYNC', 0);";

            PreparedStatement preparedStatement = con.prepareStatement(retlSql);
            preparedStatement.execute();

            Random random = new Random();
            for (int i = 0; i < 10; i++) {
                int randomSize = random.nextInt(10);
                String s = RandomStringUtils.randomNumeric(randomSize);
                String sql = String.format(template, i, batchId, s);
                PreparedStatement ps1 = con.prepareStatement(sql);
                ps1.execute();
            }
            con.commit();
            con.close();

            log.info("耗时：{}，写入：{}", started.stop().elapsed(TimeUnit.MILLISECONDS), 10);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常，回滚
            con.rollback();
        }

    }

    public static long mqTransaction(String batchId) throws Exception {


        Connection con = DriverManager.getConnection(localJdbcConnectionString, "root", localPassword);
        try {

            int insertCount  = 1;
            con.setAutoCommit(false);

            Stopwatch started = Stopwatch.createStarted();
            String template = "INSERT INTO `source`.`mq-test` (`id`, `name`, `title`, `text`, `amount`, `createTime`, `modifyTime`) VALUES (null, 'test-%d', '%s', '%s', %d, '2021-07-15 10:48:40', '2021-07-15 10:48:43');";
            String updateTemplate = "UPDATE `source`.`mq-test` SET `name` = 'update-9999', `title` = 'update-demo', `text` = 'update-test', `amount` = 12345678, `createTime` = '2021-07-15 10:48:40', `modifyTime` = '2021-07-15 10:48:43' WHERE `id` = %d;";
            log.info("执行插入表操作...............");
            Random random = new Random();
            List<Long> ids = new ArrayList<>();
            for (int i = 0; i < insertCount; i++) {
                int randomSize = random.nextInt(10);
                String s = RandomStringUtils.randomNumeric(randomSize);
                String sql = String.format(template, i, batchId, s,random.nextInt(1000000));
                PreparedStatement ps1 = con.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
                ps1.execute();
                ResultSet generatedKeys = ps1.getGeneratedKeys();
                generatedKeys.next();
                ids.add(generatedKeys.getLong(1));
            }
            int updateSize = random.nextInt(ids.size());

            for (int i = 0; i < updateSize; i++) {
                String sql = String.format(updateTemplate, ids.get(i));
                PreparedStatement ps1 = con.prepareStatement(sql);
                ps1.execute();
            }

            con.commit();
            con.close();


            log.info("耗时：{}，写入：{}", started.stop().elapsed(TimeUnit.MILLISECONDS), (updateSize + insertCount));
            return updateSize + insertCount;
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常，回滚
            con.rollback();
            return 0;
        }

    }

    public static void localTransaction(String batchId) throws Exception {


        Connection con = DriverManager.getConnection(localJdbcConnectionString, "root", localPassword);
        try {

            con.setAutoCommit(false);

            Stopwatch started = Stopwatch.createStarted();
            String template = "INSERT INTO `source`.`xa-test` (`id`, `name`, `title`, `text`) VALUES (null, 'test-%06d', 'batch-%s', '%s');";

            log.info("执行插入表操作...............");
            Random random = new Random();
            for (int i = 0; i < 10000; i++) {
                int randomSize = random.nextInt(10);
                String s = RandomStringUtils.randomNumeric(randomSize);
                String sql = String.format(template, i, batchId, s);
                System.out.println(sql);
                PreparedStatement ps1 = con.prepareStatement(sql);
                ps1.execute();
            }
            con.commit();
            con.close();

            log.info("耗时：{}，写入：{}", started.stop().elapsed(TimeUnit.MILLISECONDS), 10);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常，回滚
            con.rollback();
        }

    }

    public static void localLoopBackTransaction(String batchId) throws Exception {


        Connection con = DriverManager.getConnection(useNormalConnectionString, "root", testPassword);
        try {

            con.setAutoCommit(false);

            Stopwatch started = Stopwatch.createStarted();
            String template = "INSERT INTO `source`.`xa-test` (`id`, `name`, `title`, `text`) VALUES (null, 'test-%06d', 'batch-%s', '%s');";

            log.info("执行插入表操作...............");
            Random random = new Random();
            for (int i = 0; i < 20; i++) {
                int randomSize = random.nextInt(10);
                String s = RandomStringUtils.randomNumeric(randomSize);
                String sql = String.format(template, i, batchId, s);
                PreparedStatement ps1 = con.prepareStatement(sql);
                ps1.execute();
            }
            con.commit();
            con.close();

            log.info("耗时：{}，写入：{}", started.stop().elapsed(TimeUnit.MILLISECONDS), 200);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常，回滚
            con.rollback();
        }

    }
}

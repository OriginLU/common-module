package com.zeroone.xa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class OracleConnectionTests {

    public static void main(String[] args) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");//实例化oracle数据库驱动程序(建立中间件)
            String url = "jdbc://oracle:thin:@172.16.110.69:1521:orcl";//@localhost为服务器名，sjzwish为数据库实例名
            Connection conn = DriverManager.getConnection(url, "c##user01", "abc123");//连接数据库，a代表帐户,a代表密码
            Statement stmt = conn.createStatement();//提交sql语句,创建一个Statement对象来将SQL语句发送到数据库

            //查询数据用executeQuery
            ResultSet  rs = stmt.executeQuery("select * from ruby");//执行查询,(ruby)为表名
            while (rs.next()) {//使当前记录指针定位到记录集的第一条记录
                System.out.println(rs.getString("sid") +" "+ rs.getString("sname"));
            }//1代表当前记录的第一个字段的值，可以写成字段名。
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

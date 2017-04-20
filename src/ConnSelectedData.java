

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class ConnSelectedData {


	public static void getSelectedData() {
		
		ArrayList<String> TitleList = new ArrayList<>();
		 // JDBC 驱动名及数据库 URL
	    final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	    final String DB_URL = "jdbc:mysql://localhost:3306/2868rdb";
	    
	 
	    // 数据库的用户名与密码，需要根据自己的设置
	    final String USER = "root";
	    final String PASS = "liying549464";
		
	    Connection conn = null;
	    Statement stmt = null;
	    
	    
    try{
        // 注册 JDBC 驱动
        Class.forName("com.mysql.jdbc.Driver");
    
        // 打开链接
        System.out.println("连接数据库...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
    
        // 执行查询
        System.out.println(" 实例化Statement对...");
        stmt = conn.createStatement();
        String sql;
        sql = "SELECT id,abstract FROM refs";
        ResultSet rs = stmt.executeQuery(sql);
    
        FileWriter Fwriter1 = new FileWriter("selected_ab.txt");
		BufferedWriter Fbuffer1 = new BufferedWriter(Fwriter1); 

		int rows=0;
        // 展开结果集数据库
        while(rs.next()){
            // 通过字段检索
//            int id  = rs.getInt("id");
//            String author = rs.getString("author");
            //String title = rs.getString("title");
            String abstractS = rs.getString("abstract");

//            
//            TitleList.add(title);
            Fbuffer1.write(abstractS+"\r\n");
            rows++;
        }
        // 完成后关闭
        System.out.println(rows);
        rs.close();
        stmt.close();
        conn.close();
    }catch(SQLException se){
        // 处理 JDBC 错误
        se.printStackTrace();
    }catch(Exception e){
        // 处理 Class.forName 错误
        e.printStackTrace();
    }finally{
        // 关闭资源
        try{
            if(stmt!=null) stmt.close();
        }catch(SQLException se2){
        }// 什么都不做
        try{
            if(conn!=null) conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
		
	}

}



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
		 // JDBC �����������ݿ� URL
	    final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	    final String DB_URL = "jdbc:mysql://localhost:3306/2868rdb";
	    
	 
	    // ���ݿ���û��������룬��Ҫ�����Լ�������
	    final String USER = "root";
	    final String PASS = "liying549464";
		
	    Connection conn = null;
	    Statement stmt = null;
	    
	    
    try{
        // ע�� JDBC ����
        Class.forName("com.mysql.jdbc.Driver");
    
        // ������
        System.out.println("�������ݿ�...");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
    
        // ִ�в�ѯ
        System.out.println(" ʵ����Statement��...");
        stmt = conn.createStatement();
        String sql;
        sql = "SELECT id,abstract FROM refs";
        ResultSet rs = stmt.executeQuery(sql);
    
        FileWriter Fwriter1 = new FileWriter("selected_ab.txt");
		BufferedWriter Fbuffer1 = new BufferedWriter(Fwriter1); 

		int rows=0;
        // չ����������ݿ�
        while(rs.next()){
            // ͨ���ֶμ���
//            int id  = rs.getInt("id");
//            String author = rs.getString("author");
            //String title = rs.getString("title");
            String abstractS = rs.getString("abstract");

//            
//            TitleList.add(title);
            Fbuffer1.write(abstractS+"\r\n");
            rows++;
        }
        // ��ɺ�ر�
        System.out.println(rows);
        rs.close();
        stmt.close();
        conn.close();
    }catch(SQLException se){
        // ���� JDBC ����
        se.printStackTrace();
    }catch(Exception e){
        // ���� Class.forName ����
        e.printStackTrace();
    }finally{
        // �ر���Դ
        try{
            if(stmt!=null) stmt.close();
        }catch(SQLException se2){
        }// ʲô������
        try{
            if(conn!=null) conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
		
	}

}

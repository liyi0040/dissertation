package wholeProcess;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Executable;
import java.nio.file.FileSystems;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexBuilder {
	
	//data from all_data
	public static ArrayList<Integer> IdList = new ArrayList<>();
	public static ArrayList<String> TitleList = new ArrayList<>();
	public static ArrayList<String> AbstractList = new ArrayList<>();
	//title from selected_data 
	public static ArrayList<String> TitleList2 = new ArrayList<>();
	
    public static Document document = new Document();

	public static ArrayList<String> wordsBase = new ArrayList<>();
	static Map<String, Integer> ctTriFre = new HashMap<>();

	static long startTime = System.currentTimeMillis();
	static int nonZero =0;
	
	static String inputFile="triOutputA.txt";
	
	static ArrayList<String> markSe =new ArrayList<>();
	static ArrayList<String> markCl =new ArrayList<>();
	static int count1=0;
/**
 * methods
 * @param args
 * @throws SQLException
 * @throws IOException
 * @throws InterruptedException 
 */
	public static void main(String[] args) throws SQLException, IOException, InterruptedException {

		//connAllData();
		//featureExtration();
		String outputfile =clustering();	
		System.out.println("=====");
    	buildFlag();
    	cluster2(outputfile);
    }


	public static void  connAllData() throws IOException {
		
		 // JDBC 驱动名及数据库 URL
	     final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	     final String DB_URL = "jdbc:mysql://localhost:3306/rdb";

	    // 数据库的用户名与密码，需要根据自己的设置
	     final String USER = "root";
	     final String PASS = "liying549464";
		
		Connection conn1 = null;

        Statement stmt1 = null;
        
        Map<String, String> mark = new  HashMap<>();//mark whether a paper is selected or not

        File flag = new File("flag.txt");
        FileWriter flagWriter = new FileWriter(flag);
    	BufferedWriter flagBuffer = new BufferedWriter(flagWriter); 
    	
        try {
        	FileReader fileReader = new FileReader("selected.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line=bufferedReader.readLine())!= null) {
    			TitleList2.add(line);
    			mark.put(line, null);
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
        try{
            // 注册 JDBC 驱动
            Class.forName("com.mysql.jdbc.Driver");
        
            // 打开链接
            System.out.println("连接数据库...rdb");
            conn1 = DriverManager.getConnection(DB_URL,USER,PASS);

        
            // 执行查询
            System.out.println(" 实例化Statement对...");
            stmt1 = conn1.createStatement();

            String sql1;
//            String sql2;
            sql1 = "SELECT id, author, title,abstract FROM refs";

            ResultSet rs1 = stmt1.executeQuery(sql1);

        

            // 展开结果集数据库
            while(rs1.next()){
                // 通过字段检索
                int id  = rs1.getInt("id");
                String author = rs1.getString("author");
                String title = rs1.getString("title");
                String a_bstract = rs1.getString("abstract");

                if (TitleList2.contains(title)) {
					flagBuffer.write("selected"+"\r\n");
				}else{
					flagBuffer.write("un"+"\r\n");
				}
                if (mark.containsKey(title)) {
					mark.put(title, "selected");
				}else{
					mark.put(title, "unselected");
				}
    
                // 输出数据
                IdList.add(id);
                TitleList.add(title);
                AbstractList.add(a_bstract);

            }
            flagBuffer.flush();
            flagBuffer.close();
            

            rs1.close();
            stmt1.close();
            conn1.close();

        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt1!=null) stmt1.close();
//                if(stmt2!=null) stmt2.close();

            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn1!=null) conn1.close();
//                if(conn2!=null) conn2.close();

            }catch(SQLException se){
                se.printStackTrace();
            }
        }
//    }
//	} 
    
		
	}

	public static void  creatIndex() {

		IndexWriter indexWriter =null;
		try {

            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("E:\\index"));

            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            indexWriter.deleteAll();//清除以前的index
            //要搜索的File路径
//            File[] files = dFile.listFiles();
            for (int i =0;i<IdList.size();i++) {
//          for (int i =0;i<10;i++) {

//               System.out.println("i===="+IdList.get(i));
                document.add(new Field("id", IdList.get(i).toString(),StringField.TYPE_STORED));
                document.add(new Field("title", TitleList.get(i), TextField.TYPE_STORED));
                document.add(new Field("abstract", AbstractList.get(i), TextField.TYPE_STORED));
//                System.out.println(TitleList.get(i));
                indexWriter.addDocument(document);
                
                
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		
	}

	
	
	public static void featureExtration() throws IOException{

		//特征矩阵
		File output = new File(inputFile);
		output.createNewFile();
		FileWriter outputWrite = new FileWriter(output);
        BufferedWriter bufferWritter = new BufferedWriter(outputWrite);

			
     
		for(int a=0;a<IndexBuilder.IdList.size();a++){
//	    for(int a=0;a<3;a++){
	    	System.out.println(a);
//	    	String examples = IndexBuilder.TitleList.get(a)+IndexBuilder.AbstractList.get(a);
	    	String examples = IndexBuilder.AbstractList.get(a);
///***?*/	  	String examples = IndexBuilder.get(2);

	    	ArrayList<String> features = new ArrayList<>();                
	    	features = ngram(examples);                
		}
	    
	    createFeatureSpace();
	   // System.out.println(wordsBase.size());
	    String annotationLine ="21846 "+String.valueOf(wordsBase.size())+" "+String.valueOf(nonZero)+"\r\n"; 
		bufferWritter.write(annotationLine);
		for(int a=0;a<IndexBuilder.IdList.size();a++){
//	    for(int a=0;a<3;a++){
	    	System.out.println(a);
//	    	String examples = IndexBuilder.TitleList.get(a)+IndexBuilder.AbstractList.get(a);
	    	String examples = IndexBuilder.AbstractList.get(a);

///***?*/    	String examples = IndexBuilder.TitleList.get(2);
	    	ArrayList<String> features = new ArrayList<>();                
	    	features = ngram(examples);
	        createMatrix(features, bufferWritter,a);
		}
		bufferWritter.flush();
		bufferWritter.close();


		long endTime = System.currentTimeMillis();
		System.out.println("whole process time is "+(endTime-startTime));
		System.out.println("whole words "+ctTriFre.size());
		System.out.println("whole words "+wordsBase.size());
		System.out.println(nonZero);

	}

	public static ArrayList<String> ngram(String example) throws IOException {
        
        //特征向量
		ArrayList<String> featurevector = new ArrayList<>();

		StringReader stringReader = new StringReader(example);
//    	System.out.println(example);

        Analyzer stAnalyzer = new StandardAnalyzer();
        Analyzer sAnalyzer = new ShingleAnalyzerWrapper(stAnalyzer, 3, 3, " ", false, false, null);
        
        TokenStream input = stAnalyzer.tokenStream("title", stringReader);

        input= new PorterStemFilter(input);

        ShingleFilter sinput = new ShingleFilter(input,3,3);
        sinput.setFillerToken(null);
        sinput.setOutputUnigrams(false);
        sinput.setTokenSeparator(" ");

        OffsetAttribute offsetAttribute = sinput.addAttribute(OffsetAttribute.class);
        sinput.reset();
        int i=0;
        Pattern intNUM = Pattern.compile("^[\\d]*[.]?[\\d]*");
		boolean matchNUM = false;
		
		
		  while (sinput.incrementToken()) {

			  String triToken = offsetAttribute.toString();
			  i++;
			  triToken=triToken.replaceAll(" ", "");
//			  System.out.println(triToken);
			  matchNUM=intNUM.matcher(triToken).matches();
			  
			  if (!ctTriFre.containsKey(triToken)) {
					ctTriFre.put(triToken, 1);
				}else {
					int time= ctTriFre.get(triToken);
					ctTriFre.put(triToken, time+1);
			  }
			  featurevector.add(triToken);
			}
//		System.out.println(featurevector.size()+"in ngram");
		return featurevector;
	}

	public static void createFeatureSpace() throws IOException {
		
	    /**
	     * create feature space
	     * filter low frequency term	
	     */  
		
//特征空间
		File featureSpace = new File("triFeatureSpaceA.txt");
		FileWriter featureSpaceWriter = new FileWriter(featureSpace);
	    BufferedWriter bufferWritter2 = new BufferedWriter(featureSpaceWriter);
	    

	    
		Iterator<Map.Entry<String, Integer>> entries = ctTriFre.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, Integer> entry = entries.next();  				  
			if( entry.getValue()>=2){
				String f = entry.getKey();
//				System.out.println("Key = " +f + ", Value = " + entry.getValue());
				wordsBase.add(f);
			 bufferWritter2.write(f+" ");
			}
		}
		bufferWritter2.flush();
		bufferWritter2.close();
	}
	
	public static void createMatrix(ArrayList<String> features,BufferedWriter bufferWritter, int a) throws IOException {
/**
 * 创建稀疏矩阵
         
		    nub ofrow    colum,                         non-zero
		          21846, 16042---ROW          
		                 13123---AFTER SDNLP STEMMING
		                 12859---AFTER REMOVE NUMBER
 */
//    	     		bufferWritter.write(String.valueOf(a));
//    	     		System.out.println(features.size());
		            for(int b = 0; b<wordsBase.size();b++){
		            	int c=b+1;
		         	   	if(features.contains(wordsBase.get(b))){
//		         	   		System.out.println(wordsBase.get(b));
		             	bufferWritter.write(c+" "+"1"+" ");
		             	nonZero++;
		         	   }
		             }		         
		        	bufferWritter.write("\r\n");



		        
		
	}
	
	public static String clustering() throws IOException, InterruptedException{

			String openApp = "cmd.exe /c  vcluster.exe";
			String parameters ="";
			String output = "-clustfile=2cluster3AT.txt";
			String outputName ="2cluster3AT.txt";
			String ClusterNum = "2";
			String cmd= openApp+""+parameters+" "+output+" "+inputFile+" "+ClusterNum;
			System.out.println(cmd);
			Process process = Runtime.getRuntime().exec(cmd);
			
		    BufferedInputStream in = new BufferedInputStream(process.getInputStream());  
		    BufferedReader inBr = new BufferedReader(new InputStreamReader(in));  
		    String lineStr;  
		    while ((lineStr = inBr.readLine()) != null)  
		    System.out.println(lineStr);
		    
		    inBr.close();  
		    in.close();  
		    process.waitFor();

			return outputName;  




	}
	

    static void cluster2(String clusterRes){


        int count1=0;
        int count2=0;

    	 try {
    	    	FileReader fileReader = new FileReader("flag.txt");
    	        BufferedReader bufferedReader = new BufferedReader(fileReader);
    	        String line = null;
    	        while ((line=bufferedReader.readLine())!= null) {
    	        	count1++;
    				markSe.add(line);
    				
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}

 /*
  * result for 2 cluster
  */
    	 try{
    		 System.out.println(clusterRes);
    		 FileReader resultReader = new FileReader(clusterRes);
 	        BufferedReader bufferedReader = new BufferedReader(resultReader);
 	        String result;
 	        while ((result=bufferedReader.readLine())!= null) {
 				count2++;
 				markCl.add(result);
 			}
    	 } catch (IOException e) {
			// TODO: handle exception
 			e.printStackTrace();
		}    

		 int nn=0;
		 int n0=0;
		 int n1=0;
		 int s0=0;
		 int s1=0;
		 int u0=0;
		 int u1=0;
		 int s2=0;
		 int u2=0;
				 
    	 for(int i2 =0;i2<count2;i2++){
    		String mk = markSe.get(i2);
    		String res = markCl.get(i2);
	        //System.out.println(mk);
    		 if (res.equals("-1")) {
				nn++;
				if (mk.equals("selected")) {
					s2++;
				}else if(mk.equals("un")){
					u2++;
				}
			}else if (res.equals("0")) {
				n0++;
				if (mk.equals("selected")) {
					s0++;
				}else if(mk.equals("un")){
					u0++;
				}
			}else if (res.equals("1")) {
				n1++;
				if (mk.equals("selected")) {
					s1++;
				}else if(mk.equals("un")){
					u1++;
				}
			}
    	 }
    	 System.out.println(nn+" "+n0+" "+n1);
    	 System.out.println("s0: "+s0);
    	 System.out.println("u0: "+u0);
    	 System.out.println("s1: "+s1);
    	 System.out.println("u1: "+u1);
    	 System.out.println(s0);
    	 System.out.println(s1);
    	 System.out.println(u0);
    	 System.out.println(u1);
    	 System.out.println(s2);
    	 System.out.println(u2);
    	 
    	 System.out.println("0: "+  (float)s0/(float)(s0+u0));
    	 System.out.println("1: "+  (float)s1/(float)(s1+u1));
    	 System.out.println("1: "+  (float)s0/(float)(s0+s1));





 	    	   
    	    	     	 
//    	 System.out.println("1: "+count2);
//    	 System.out.println("2: "+markCl.size());

	
    }

    static void cluster3(){
        int count2=0;
 /*
  * result for 3 cluster
  */
    	 try{
    		 FileReader resultReader = new FileReader("rbrAB3");
 	        BufferedReader bufferedReader = new BufferedReader(resultReader);
 	        String result;
 	        while ((result=bufferedReader.readLine())!= null) {
 				count2++;
 				markCl.add(result);
 			}
    	 } catch (IOException e) {
			// TODO: handle exception
 			e.printStackTrace();
		}    

    	 //cluster tag:-1, 0,1,2
		 int nn=0;
		 int n0=0;
		 int n1=0;
		 int n2=0;
		 
		 //count
		 int s0=0;
		 int u0=0;
		 int s1=0;
		 int u1=0;
		 int s2=0;
		 int u2=0;
				 
    	 for(int i2 =0;i2<count2;i2++){
    		String mk = markSe.get(i2);
    		String res = markCl.get(i2);
	        System.out.println(mk);
    		 if (res.equals("-1")) {
				nn++;
			}else if (res.equals("0")) {
				n0++;
				if (mk.equals("selected")) {
					s0++;
				}else if(mk.equals("un")){
					u0++;
				}
			}else if (res.equals("1")) {
				n1++;
				if (mk.equals("selected")) {
					s1++;
				}else if(mk.equals("un")){
					u1++;
				}
			}else if (res.equals("2")) {
				n1++;
				if (mk.equals("selected")) {
					s2++;
				}else if(mk.equals("un")){
					u2++;
				}
			}
    	 }
    	 System.out.println(nn+" "+n0+" "+n1);
//    	 System.out.println("s0: "+s0);
//    	 System.out.println("u0: "+u0);
//    	 System.out.println("s1: "+s1);
//    	 System.out.println("u1: "+u1);
    	 System.out.println(s0);
    	 System.out.println(s1);
    	 System.out.println(s2);
    	 System.out.println(u0);
    	 System.out.println(u1);
    	 System.out.println(u2);
    	 
    	 System.out.println("0: "+  (float)s0/(float)(s0+u0));
    	 System.out.println("1: "+  (float)s1/(float)(s1+u1));
    	 System.out.println("1: "+  (float)s0/(float)(s0+s1));





 	    	   
    	    	     	 
    	 System.out.println("1: "+count2);
//    	 System.out.println("2: "+markCl.size());

	
    }

    static void cluster4(){
        int count2=0;
 /*
  * result for 4 cluster
  */
    	 try{
    		 FileReader resultReader = new FileReader("rbrAh2A4.txt");
 	        BufferedReader bufferedReader = new BufferedReader(resultReader);
 	        String result;
 	        while ((result=bufferedReader.readLine())!= null) {
 				count2++;
 				markCl.add(result);
 			}
    	 } catch (IOException e) {
			// TODO: handle exception
 			e.printStackTrace();
		}    

    	 //cluster tag:-1, 0,1,2,3
		 int nn=0;
		 int n0=0;
		 int n1=0;
		 int n2=0;
		 int n3=0;

		 
		 //count
		 int s0=0;
		 int u0=0;
		 int s1=0;
		 int u1=0;
		 int s2=0;
		 int u2=0;
		 int s3=0;
		 int u3=0;
				 
    	 for(int i2 =0;i2<count2;i2++){
    		String mk = markSe.get(i2);
    		String res = markCl.get(i2);
	        System.out.println(mk);
    		 if (res.equals("-1")) {
				nn++;
			}else if (res.equals("0")) {
				n0++;
				if (mk.equals("selected")) {
					s0++;
				}else if(mk.equals("un")){
					u0++;
				}
			}else if (res.equals("1")) {
				n1++;
				if (mk.equals("selected")) {
					s1++;
				}else if(mk.equals("un")){
					u1++;
				}
			}else if (res.equals("2")) {
				n2++;
				if (mk.equals("selected")) {
					s2++;
				}else if(mk.equals("un")){
					u2++;
				}
			}else if (res.equals("3")) {
				n3++;
				if (mk.equals("selected")) {
					s3++;
				}else if(mk.equals("un")){
					u3++;
				}
			}
    	 }
    	 System.out.println(nn+" "+n0+" "+n1+" "+n2+" "+n3);
//    	 System.out.println("s0: "+s0);
//    	 System.out.println("u0: "+u0);
//    	 System.out.println("s1: "+s1);
//    	 System.out.println("u1: "+u1);
    	 System.out.println(s0);
    	 System.out.println(s1);
    	 System.out.println(s2);
    	 System.out.println(s3);
    	 System.out.println(u0);
    	 System.out.println(u1);
    	 System.out.println(u2);
    	 System.out.println(u3);
    	 
    	 System.out.println("0: "+  (float)s0/(float)(s0+u0));
    	 System.out.println("1: "+  (float)s1/(float)(s1+u1));
    	 System.out.println("1: "+  (float)s0/(float)(s0+s1));





 	    	   
    	    	     	 
    	 System.out.println("1: "+count2);
//    	 System.out.println("2: "+markCl.size());

	
    }

    static void buildFlag(){
      	 try {
   	    	FileReader fileReader = new FileReader("flag.txt");
   	        BufferedReader bufferedReader = new BufferedReader(fileReader);
   	        String line = null;
   	        while ((line=bufferedReader.readLine())!= null) {
   	        	count1++;
   				markSe.add(line);
   				
   			}
   		} catch (IOException e) {
   			e.printStackTrace();
   		}
       }}



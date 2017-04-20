import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

	//static ArrayList<String> wordsBase = new ArrayList<>();
	static long startTime = System.currentTimeMillis();
	static int nonZero =0;
/**
 * methods
 * @param args
 * @throws SQLException
 * @throws IOException
 */
	public static void main(String[] args) throws SQLException, IOException {

		connAllData();
		featureExtration();
	}


	public static void  connAllData() throws IOException {
		
		 // JDBC �����������ݿ� URL
	     final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	     final String DB_URL = "jdbc:mysql://localhost:3306/rdb";

	    // ���ݿ���û��������룬��Ҫ�����Լ�������
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
            // ע�� JDBC ����
            Class.forName("com.mysql.jdbc.Driver");
        
            // ������
            System.out.println("�������ݿ�...rdb");
            conn1 = DriverManager.getConnection(DB_URL,USER,PASS);

        
            // ִ�в�ѯ
            System.out.println(" ʵ����Statement��...");
            stmt1 = conn1.createStatement();

            String sql1;
//            String sql2;
            sql1 = "SELECT id, author, title,abstract FROM refs";

            ResultSet rs1 = stmt1.executeQuery(sql1);

        

            // չ����������ݿ�
            while(rs1.next()){
                // ͨ���ֶμ���
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
    
                // �������
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
            // ���� JDBC ����
            se.printStackTrace();
        }catch(Exception e){
            // ���� Class.forName ����
            e.printStackTrace();
        }finally{
            // �ر���Դ
            try{
                if(stmt1!=null) stmt1.close();
//                if(stmt2!=null) stmt2.close();

            }catch(SQLException se2){
            }// ʲô������
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
            indexWriter.deleteAll();//�����ǰ��index
            //Ҫ������File·��
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

		  //�����ռ�
		File featureSpace = new File("triFeatureSpace.txt");
		FileWriter featureSpaceWriter = new FileWriter(featureSpace);
	    BufferedWriter bufferWritter2 = new BufferedWriter(featureSpaceWriter);
		//��������
		File output = new File("triOutputMatric1.txt");
		output.createNewFile();
		FileWriter outputWrite = new FileWriter(output);
        BufferedWriter bufferWritter = new BufferedWriter(outputWrite);
		bufferWritter.write("21846 12859 219664"+"\r\n");
     
		//for(int a=0;a<IndexBuilder.IdList.size();a++){
	    for(int a=0;a<10;a++){
		System.out.println(a);
		String examples = IndexBuilder.TitleList.get(a);
		System.out.println(examples);
        ArrayList<String> features = new ArrayList<>();
                
        features = ngram(examples,bufferWritter2);
        createMatrix(features, bufferWritter);
                
		}
		bufferWritter.flush();
		bufferWritter.close();
		bufferWritter2.flush();
		bufferWritter2.close();
		long endTime = System.currentTimeMillis();
		System.out.println("whole process time is "+(endTime-startTime));
		System.out.println("whole words "+wordsBase.size());
		System.out.println(nonZero);


	
	}

	public static ArrayList<String> ngram(String example,BufferedWriter bufferWritter2) throws IOException {
        
        //��������
		ArrayList<String> featurevector = new ArrayList<>();

		StringReader stringReader = new StringReader(example);
		
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
		  while (sinput.incrementToken()) {

			  String triToken = offsetAttribute.toString();
			  i++;
			  System.out.println(i+"---"+triToken);
			  
/**
 * ���������ռ�
 */
		        Pattern intNUM = Pattern.compile("^[\\d]*[.]?[\\d]*");
				boolean matchNUM = false;

			  if (!wordsBase.contains(triToken)&&!matchNUM) {
//              	System.out.println(lemma);
					wordsBase.add(triToken);
					bufferWritter2.write(triToken+"  ");
				}

			}
		
		return featurevector;
	}

	public static void createMatrix(ArrayList<String> features, BufferedWriter bufferWritter) throws IOException {

/**
 * ����ϡ�����
 */
		          
//		    nub ofrow    colum,                         non-zero
//		          21846, 16042---ROW          
//		                 13123---AFTER SDNLP STEMMING
//		                 12859---AFTER REMOVE NUMBER
		            for(int b = 0; b<wordsBase.size();b++){
		            	int c=b+1;
		         	   	if(features.contains(wordsBase.get(b))){
		             	bufferWritter.write(c+" "+"1"+" ");
		             	nonZero++;
		         	   }
		             }
		         
		        	bufferWritter.write("\r\n");

		        
		
	}
}


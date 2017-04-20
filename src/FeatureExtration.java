import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class FeatureExtration {
		 
	static ArrayList<String> wordsBase = new ArrayList<>();
	static long startTime = System.currentTimeMillis();
	static int nonZero =0;
   
	public static void featureExtration() throws IOException{

		  //特征空间
		File featureSpace = new File("triFeatureSpace.txt");
		FileWriter featureSpaceWriter = new FileWriter(featureSpace);
	    BufferedWriter bufferWritter2 = new BufferedWriter(featureSpaceWriter);
		//特征矩阵
		File output = new File("triOutputMatric1.txt");
		output.createNewFile();
		FileWriter outputWrite = new FileWriter(output);
        BufferedWriter bufferWritter = new BufferedWriter(outputWrite);
		bufferWritter.write("21846 12859 219664"+"\r\n");
     
		//for(int a=0;a<IndexBuilder.IdList.size();a++){
	    for(int a=0;a<10;a++){
		System.out.println(a);
		String examples = IndexBuilder.TitleList.get(a);
        //for (int j = 0; j < examples.length; j++) {
        ArrayList<String> features = new ArrayList<>();
                
        features = ngram(examples,bufferWritter2);
        createMatrix(features, bufferWritter);
                
        	//}
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
        
        //特征向量
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
 * 创建向量空间
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
 * 创建稀疏矩阵
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

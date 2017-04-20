import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.Iterator;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.stempel.StempelStemmer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

public class TryTry {

	static ArrayList<String> wordsBase = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		inserMap();
	}

	public static void ngram() throws IOException {
		  StringReader stringReader = new StringReader("we used these cars for several times.");

          Analyzer stAnalyzer = new StandardAnalyzer();
          Analyzer sAnalyzer = new ShingleAnalyzerWrapper(stAnalyzer, 3, 3, " ", false, false, null);
          
          TokenStream input = stAnalyzer.tokenStream("title", stringReader);
          input= new PorterStemFilter(input);
          ShingleFilter sinput = new ShingleFilter(input,3,3);
          sinput.setFillerToken(null);
          sinput.setOutputUnigrams(false);
          sinput.setTokenSeparator(" ");

          


          //PorterStemmer porterStemmer = new PorterStemmer();
          //input= new StempelFilter(input, null);
          OffsetAttribute offsetAttribute = sinput.addAttribute(OffsetAttribute.class);
          sinput.reset();
          int i=0;
		  while (sinput.incrementToken()) {

			  i++;
			  System.out.println(i+"---"+offsetAttribute.toString());

			}
	}

	public static void chunk() throws IOException {
		/*
		POSModel model = new POSModelLoader()
				.load(new File("en-pos-maxent.bin"));		
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);
	 
		String input = "Social skills training and computer-assisted cognitive remediation in schizophrenia";
		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new StringReader(input));
	 
		perfMon.start();
		String line;
		String whitespaceTokenizerLine[] = null;
	 
		String[] tags = null;
		while ((line = lineStream.read()) != null) {
			whitespaceTokenizerLine = whitespaceTokenizer
					.tokenize(line);
			tags = tagger.tag(whitespaceTokenizerLine);
	 
			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			System.out.println(sample.toString());
				perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
	 
		// chunker
		InputStream is = new FileInputStream("en-chunker.bin");
		ChunkerModel cModel = new ChunkerModel(is);
	 
		ChunkerME chunkerME = new ChunkerME(cModel);
		String result[] = chunkerME.chunk(whitespaceTokenizerLine, tags);
	 
		for (String s : result)
			System.out.println("s in result:"+s);
	 
		Span[] span = chunkerME.chunkAsSpans(whitespaceTokenizerLine, tags);
		for (Span s : span)
			System.out.println(s.toString());
*/
	}


	public static void featureExtration() throws IOException{
		long startTime = System.currentTimeMillis();
		int nonZero =0;
		
		File output = new File("outputMatric1_ab.txt");
		output.createNewFile();
		FileWriter outputWrite = new FileWriter(output);
        BufferedWriter bufferWritter = new BufferedWriter(outputWrite);
		bufferWritter.write("21846 12859 219664"+"\r\n");
        
        File featureSpace = new File("featureSpace_ab.txt");
        featureSpace.createNewFile();
		FileWriter featureSpaceWriter = new FileWriter(featureSpace);
        BufferedWriter bufferWritter2 = new BufferedWriter(featureSpaceWriter);
        

        Properties props = new Properties(); 
        props.put("annotators", "tokenize, ssplit, pos, lemma"); 
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);

        Pattern intNUM = Pattern.compile("^[\\d]*[.]?[\\d]*");
        //Pattern upCase = Pattern.compile("background");
		boolean matchNUM = false;
		boolean matchUP = false;

        
        
		for(int a=0;a<IndexBuilder.IdList.size();a++){
	//    for(int a=0;a<10;a++){
		System.out.println(a);
		String[] examples = {IndexBuilder.AbstractList.get(a)};
		final Analyzer[] ANALYZERS = new Analyzer[]{new StandardAnalyzer()};
        for (int j = 0; j < examples.length; j++) {
            TokenStream a_bstract = ANALYZERS[0].tokenStream("abstract", examples[j]);
            OffsetAttribute offsetAttribute = a_bstract.addAttribute(OffsetAttribute.class);
            TypeAttribute typeAttribute = a_bstract.addAttribute(TypeAttribute.class);
            a_bstract.reset();
           // System.out.println( examples[j]);
            ArrayList<String> features = new ArrayList<>();
            while (a_bstract.incrementToken()) {
                String s1 = offsetAttribute.toString();
        		//System.out.println(s1);

//                System.out.print(b+"+"+wordsBase.size());
                
//nub of row, colum, non-zero
//         21846, 16042---ROW
//                13123---AFTER SDNLP STEMMING
//                12859---AFTER REMOVE NUMBER
//创建每一句的特征集
                Annotation sentence = pipeline.process(s1);  
                String lemma = null;

                  for(CoreLabel token: sentence.get(TokensAnnotation.class))
                  {       
                      String word = token.get(TextAnnotation.class);
                      lemma = token.get(LemmaAnnotation.class); 
                     // System.out.println("lemmatized:" + lemma);
                  }
                features.add(lemma);//THE FEATURE AFTER STEMMING
                matchNUM =intNUM.matcher(lemma).matches();//is number
               // matchUP =upCase.matcher(lemma).matches();//is all upercase


			
//创建语料库
                if (!wordsBase.contains(lemma)&& !matchNUM) {
//                	System.out.println(lemma);
					wordsBase.add(lemma);
					try {
						bufferWritter2.write(lemma+"  ");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

            }
/*
 * 创建矩阵
 * 稀疏矩阵          
 */
            for(int b = 0; b<wordsBase.size();b++){
            	int c=b+1;
         	   	if(features.contains(wordsBase.get(b))){
             	bufferWritter.write(c+" "+"1"+" ");
             	nonZero++;
         	   }
             }

            a_bstract.end();
            a_bstract.close();
        	bufferWritter.write("\r\n");

        }
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

	public static void inserMap() {
		Map<String, Integer> ctTriFre	=	new HashMap<>();
		String[] strings = {"a","aa","aaa","a","a","aa"};
		for(int i=0;i<strings.length;i++){

			if (!ctTriFre.containsKey(strings[i])) {
				ctTriFre.put(strings[i], 1);
			}else {
				int time= ctTriFre.get(strings[i]);
				ctTriFre.put(strings[i], time+1);
			}

		}

		Iterator<Map.Entry<String, Integer>> entries = ctTriFre.entrySet().iterator();
		while (entries.hasNext()) {
			 Entry<String, Integer> entry = entries.next();  
			  
			 System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  			
		}

		
	}
}

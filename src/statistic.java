import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

public class statistic {

   static ArrayList<String> markSe =new ArrayList<>();
   static ArrayList<String> markCl =new ArrayList<>();
   static int count1=0;

    public static void main(String[] args) {
    	buildFlag();
    	cluster3();
    }
   
    static void cluster2(){


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
    		 FileReader resultReader = new FileReader("rbrAB2");
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
				 
    	 for(int i2 =0;i2<count2;i2++){
    		String mk = markSe.get(i2);
    		String res = markCl.get(i2);
	        //System.out.println(mk);
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
    }
}

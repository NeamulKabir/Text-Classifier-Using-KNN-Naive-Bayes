package textclassifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TfIdf {	
	private class Pair3{
		double d;
		MyDoc doc;
		Pair3(double _d, MyDoc n){
			this.d = _d;
			this.doc = n;
		}
	}
	ArrayList<MyDoc>test;
	ArrayList<MyDoc>train;
	Map<String, Integer> GlobalHashMap;
	public TfIdf(ArrayList<MyDoc> allTrainDocs, ArrayList<MyDoc> allTestDocs) {
		// TODO Auto-generated constructor stub
		test = new ArrayList<MyDoc>();
		train = new ArrayList<MyDoc>();
		test = allTestDocs;
		train = allTrainDocs;
		GlobalHashMap = new HashMap<String, Integer>();
		mergeHashMaps();		
	}
	
	private void mergeHashMaps() {
		// TODO Auto-generated method stub
		for(MyDoc m: this.train){
			for(String s: m.wordMap.keySet()){
				if(GlobalHashMap.containsKey(s)){
					int k = GlobalHashMap.get(s);
					GlobalHashMap.put(s, k + 1);
				}else {
					GlobalHashMap.put(s, 1);
				}
			}
		}
	}

	public double getAccuracy() {
		// TODO Auto-generated method stub
		ArrayList<Pair3>distance;
		
//		int []success = new int[151];
//		int []failure = new int[151];
//		boolean []result = new boolean[151];
//		for(int i=0;i<151;i++){
//			success[i] = 0;
//			failure[i] = 0;
//			result[i] = false;
//		}
		String prevName = "";
		
		int success = 0;
		int failure = 0;
		for(MyDoc m:  this.test){
			
			String originalTopic = m.docTopic;
//			if(!prevName.equals(originalTopic)){
//				if(!prevName.isEmpty()){
////					System.out.println(prevName + " Done");
//				}
//				prevName = originalTopic;
//			}
//			long t = System.currentTimeMillis();
//			
//			System.out.println(System.currentTimeMillis() - t + " ms");
			distance = new ArrayList<>();
			for(MyDoc n: this.train){
//				long t = System.currentTimeMillis();
				double temp = calcDistance(m,n);
//				System.out.println(System.currentTimeMillis() - t + " ms");
				
				
				Pair3 p1 = new Pair3(temp, n);
				distance.add(p1);
			}
			
			Collections.sort(distance, new Comparator<Pair3> (){
				public int compare(Pair3 p1, Pair3 p2){
					return Double.compare(p1.d, p2.d);
				}
			});
			Collections.reverse(distance);
//			System.out.println("done" + m.docTopic);
//			for(Pair3 d: distance){
//				System.out.print(d.d + " ");
//			}
//			System.out.println();
			boolean result = get_K_decision(3, distance, originalTopic);
			if(result){
				success ++;
			}else{
				failure ++;
			}
//			for(int i=1;i<=101;i+=2){
//				
//				if(result[i]==true){
//					success[i] = success[i] + 1;
//				}else{
//					failure[i] = failure[i] + 1;
//				}
//			}
//			System.out.println((System.currentTimeMillis() - t )/1000 +" ms");
		}
//		double maxAcc = -1;
//		int id = -1;
//		for(int i=1;i<=101;i+=2){
//			System.out.println(success[i] + " " + failure[i]);
//			double acc = (double)success[i]/(double)(success[i]+failure[i]);
//			System.out.format("Accuracy for K == %d is %.2f%n", i , acc*100);
//			if(acc>maxAcc){
//				maxAcc = acc;
//				id = i;
//			}
//		}
//		System.out.println(prevName + " Done");
		double acc = (double)success/ (double)(success+failure);
//		System.out.format("Accuracy for K == 3 is %.2f%n", acc*100);
		return acc;
	}
	private boolean get_K_decision(int i, ArrayList<Pair3> distance, String originalTopic) {
		// TODO Auto-generated method stub
		String []s = new String[i];
		ArrayList<Integer>idList=new ArrayList<>();
		for(int k=0;k<i;k++){
			s[k] = distance.get(i).doc.docTopic;
//			System.out.println(s[k]+" ");
		}
//		System.out.println();
		int maxCount = -1;
		for(int k=0;k<s.length;k++){
			int count = 0;
			String ss = s[k];
			for(String sss: s){
				if(sss.equals(ss))count++;
			}
			if(maxCount<count){
				maxCount = count;
				idList.clear();
				idList.add(k);
			}else if(maxCount==count){
				idList.add(k);
			}
		}
		
//		int idx  =-1;
//		double d =  -2;
//		for(int _i: idList){
////			System.out.println(_i);
////			System.out.println(distance.get(_i).d);
//			if(d<distance.get(_i).d){
////				System.out.println("asche");
//				d = distance.get(_i).d;
//				idx = _i;
//			}
//		}
		Random r = new Random(System.currentTimeMillis());
		int k = r.nextInt(s.length);
//		System.out.println(k);
		if(s.length == 1)return s[0].equals(originalTopic);
		else {
			return s[k].equals(originalTopic);
		}
//		return s[r.nextInt()%s.length].equals(originalTopic);
	}
	
	private double calcDistance(MyDoc m, MyDoc n) {
		// TODO Auto-generated method stub
		double sum = 0;
		double len1 = 0;
		double len2 = 0;
		double tf1,tf2,idf1,idf2;
		int k1, k2, totDoc, howManyDocs1, howManyDocs2;
		totDoc = this.train.size();
		int tot1 = m.wordList.length;
		int tot2 = n.wordList.length;
		
		// m is test, n is train
//		long t = System.currentTimeMillis();
		for(String s : m.wordMap.keySet()){
			k1 = (m.wordMap.get(s)==null?0:m.wordMap.get(s));
			tf1 = (double)k1/(double)tot1; //term freq of test 
			howManyDocs1 = (GlobalHashMap.get(s)==null?0:GlobalHashMap.get(s)); // s training set er koyta doc e ache?
			idf1 = Math.log((double)totDoc/(double)(1 + howManyDocs1)); // inverse term freq
			if(idf1-0.0<1e-9)idf1=1e-9;  // if it becomes zero then give a small value
			if(n.wordMap.containsKey(s)){ // if both test and train have the same string
				k2 = (n.wordMap.get(s)==null?0:n.wordMap.get(s)); // term freq of word in train
				tf2 = (double)k2/(double)tot2; // idf of train word
				idf2 = idf1; // cause the same word is in the test doc so its idf will equalise with idf1
				sum += ((tf1*idf1) * (tf2*idf2)); // for dot product
			}else{
				len1 += ((tf1*idf1)*(tf1*idf1)); // no dot product cause one will be zero, update length of test
			}
		}
		
		for(String s : n.wordMap.keySet()){
			if(!m.wordMap.containsKey(s)){
				k1 = (n.wordMap.get(s)==null?0:n.wordMap.get(s));
				tf2 = (double)k1/(double)tot2;
				howManyDocs2 = GlobalHashMap.get(s); // s training set er koyta doc e ache?
				idf2 = Math.log((double)totDoc/(double)(1 + howManyDocs2));
				if(idf2-0.0<1e-9)idf2=1e-9;
				len2 += ((tf2*idf2)*(tf2*idf2));
			}
		}
//		System.out.println(System.currentTimeMillis() - t + " ms");
//		System.out.println(len1);
		return sum/Math.sqrt(len1*len2);
	}

	

	
}

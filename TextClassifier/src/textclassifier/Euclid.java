package textclassifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class Euclid {
	private class Pair2{
		double d;
		MyDoc doc;
		Pair2(double temp, MyDoc n){
			this.d = temp;
			this.doc = n;
		}
	}
	ArrayList<MyDoc> test;
	ArrayList<MyDoc> train;
	public Euclid(ArrayList<MyDoc> allTrainDocs, ArrayList<MyDoc> allTestDocs) {
		// TODO Auto-generated constructor stub
		test = allTestDocs;
		train = allTrainDocs;
	}

	public void getAccuracy() {
		// TODO Auto-generated method stub
		ArrayList<Pair2>distance;
		
		int []success = new int[51];
		int []failure = new int[51];
		boolean []result = new boolean[51];
		for(int i=0;i<51;i++){
			success[i] = 0;
			failure[i] = 0;
			result[i] = false;
		}
		String prevName = "";
		
		for(MyDoc m:  this.test){
			String originalTopic = m.docTopic;
			if(!prevName.equals(originalTopic)){
				if(!prevName.isEmpty()){
					System.out.println(prevName + " Done");
				}
				prevName = originalTopic;
			}
			distance = new ArrayList<>();
			for(MyDoc n: this.train){
				
				double temp = calcDistance(m,n);
				
				
				Pair2 p1 = new Pair2(temp, n);
				distance.add(p1);
			}
			Collections.sort(distance, new Comparator<Pair2> (){
				public int compare(Pair2 p1, Pair2 p2){
					if(p1.d<p2.d)return -1;
					else if(p1.d>p2.d)return 1;
					else return 0;
				}
			});
			for(int i=1;i<=50;i++){
				result[i] = get_K_decision(i, distance, originalTopic);
				if(result[i]==true){
					success[i] = success[i] + 1;
				}else{
					failure[i] = failure[i] + 1;
				}
			}
		}
		double maxAcc = -1;
		int id = -1;
		for(int i=1;i<=50;i++){
			double acc = (double)success[i]/(double)(success[i]+failure[i]);
			System.out.format("Accuracy for K == %d is %.2f%n", i , acc*100);
			if(acc>maxAcc){
				maxAcc = acc;
				id = i;
			}
		}
		System.out.format("Max Accuracy for K == %d is %.2f%n", id , maxAcc*100);
		
	}

	private boolean get_K_decision(int i, ArrayList<Pair2> distance, String originalTopic) {
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
		int idx  =-1;
		double d =  1e9;
		for(int _i: idList){
			if(distance.get(_i).d<d){
				d = distance.get(_i).d;
				idx = _i;
			}
		}
		
		return s[idx].equals(originalTopic);
	}

	private double calcDistance(MyDoc m, MyDoc n) {
		// TODO Auto-generated method stub
		int count = 0;
		int k,l;
		
		for(String s : m.wordMap.keySet()){
			if(!n.wordMap.containsKey(s)){
				k = m.wordMap.get(s);
				count += (k*k);
			}else{
				k = m.wordMap.get(s);
				l = n.wordMap.get(s);
				count += ((k-l)*(k-l));
			}
		}
		
		for(String s: n.wordMap.keySet()){
			if(!m.wordMap.containsKey(s)){
				k = n.wordMap.get(s);
				count += (k*k);
			}
		}
		return Math.sqrt(count);
	}
	

}

package textclassifier;
import java.util.HashMap;
import java.util.Map;

public class MyDoc {
	String[] wordList;
	Map<String,Integer>wordMap;
	String docTopic;
	public MyDoc(String mainString, String topicName){
		this.docTopic = topicName;
		this.wordList = mainString.trim().split(" ");
		this.wordMap = new HashMap<>();
		for(String s : this.wordList){
			if(this.wordMap.containsKey(s)){
				int k = this.wordMap.get(s);
				this.wordMap.put(s,	k+1);
			}
			else this.wordMap.put(s, 1);
		}
	}
	
}

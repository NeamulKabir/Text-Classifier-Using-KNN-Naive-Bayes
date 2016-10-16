package textclassifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Topic {
	ArrayList<MyDoc>allDocs;
	String topicName;
	Map<String, Integer>wordMap;
	public Topic(ArrayList<MyDoc>_alldocs, String _topicName){
		this.allDocs = _alldocs;
		this.topicName = _topicName;
		wordMap = new HashMap<>();
		getAllWords();
	}
	private void getAllWords() {
		// TODO Auto-generated method stub
		for(MyDoc m: allDocs){
			for(String s: m.wordList){
				if(wordMap.containsKey(s)){
					int k = wordMap.get(s);
					wordMap.put(s, k+m.wordMap.get(s));
				}else {
					wordMap.put(s, m.wordMap.get(s));
				}
			}
		}
	}
}

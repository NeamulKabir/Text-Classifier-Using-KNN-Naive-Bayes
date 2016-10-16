package textclassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

public class Parse {

    String xmlFile;
    String mainString;
//	PrintWriter pr = null;
    BufferedReader br = null;
    ArrayList<String> sw;
    ArrayList<MyDoc> documents;
    Topic topic;

    Parse(int rowCount, int startIdxTrain, String string, ArrayList<String> stopwords, String topicName) {
        this.xmlFile = string;
        this.mainString = "";
        this.sw = stopwords;
        this.documents = new ArrayList<MyDoc>();

        try {
//			this.pr = new PrintWriter(new File("out.txt"));
            this.br = new BufferedReader(new FileReader(this.xmlFile));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        removeHTML(rowCount, startIdxTrain, topicName);

    }

    private void removeHTML(int rc, int stIdx, String topicName) {

        // TODO Auto-generated method stub
        String line = "";

        try {
            int rowCount = 0;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                ++rowCount;
                //System.out.println(rowCount + "  " + stIdx);
                if (stIdx != -1) {
                    if (rowCount > stIdx) {
                        sb.append(line);

                        if (rowCount >= rc + stIdx) {
                            break;
                        }
                    }
                } else {
                    sb.append(line);
                    if (rowCount >= rc) {
                        break;
                    }
                }
            }

            this.mainString = sb.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Document doc = Jsoup.parse(this.mainString, "", Parser.xmlParser());

        for (Element e : doc.select("row")) {
            String html = e.attr("Body");
            Document doc2 = Jsoup.parse(html);

            doc2.select("a").unwrap();
            this.mainString = doc2.body().text();

            String[] words = this.mainString.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");
            this.mainString = removeStopWords(words);
            MyDoc tempDoc = new MyDoc(this.mainString, topicName);
            documents.add(tempDoc);
//			pr.println(e.attr("Id")+" " +this.mainString);
        }
//		System.out.println(documents.size() + " ");
        this.topic = new Topic(documents, topicName);

    }

    private String removeStopWords(String[] words) {
        // TODO Auto-generated method stub
        String result = "";
        for (String str : words) {
            if (!str.isEmpty()) {
                if (!isStopWord(str)) {
                    result += (str + " ");
                }
            }
        }
        return result;

    }

    private boolean isStopWord(String word) {
        // TODO Auto-generated method stub
        if (word.length() < 2) {
            return true;
        }
//		if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
        if (word.contains("http") || word.contains("https")) {
            return true;
        }
        if (this.sw.contains(word)) {
            return true;
        } else {
            return false;
        }
    }

}

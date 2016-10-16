/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package textclassifier;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.*;

/**
 *
 * @author Neamul Kabir
 */
public class ParseString {
    String parsed;
    ArrayList<String> stopWords = new ArrayList<>();
    ArrayList<String> finalString = new ArrayList<>();
    
    ParseString(ArrayList<String> sw)
    {
        stopWords = sw;
    }
    
    void parseBuilder(String str)
    {
        
        Document doc = Jsoup.parse(str, "", Parser.xmlParser());
        for (Element e : doc.select("row"))
        {
            String html = e.attr("Body");
            Document doc2 = Jsoup.parse(html);

            doc2.select("a").unwrap();
            parsed = doc2.body().text();
            //System.out.println(parsed);
            String[] words = parsed.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");
            this.parsed = removeStopWords(words);
            finalString.add(parsed);
        }
    }
    
    String removeStopWords(String[] words)
    {
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
    
    boolean isStopWord(String word) {
        // TODO Auto-generated method stub
        if (word.length() < 2) {
            return true;
        }
        if (word.contains("http") || word.contains("https")) {
            return true;
        }
        if (this.stopWords.contains(word)) {
            return true;
        } else {
            return false;
        }
    }
    
    void readStopWords()
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader("stopwords.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                stopWords.add(line);
            }
        } catch (Exception ex) {
            Logger.getLogger(ParseString.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    
}

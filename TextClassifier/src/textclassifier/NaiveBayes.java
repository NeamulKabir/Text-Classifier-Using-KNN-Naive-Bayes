/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package textclassifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Neamul Kabir
 */
public class NaiveBayes {
    ArrayList< HashMap<String, Integer> > trainedData;
    String test;
    int words,classes;
    double alpha;
    
    NaiveBayes(ArrayList< HashMap<String, Integer> > t, int value, int cl, double a)
    {
        trainedData = t;
        words = value;
        classes = cl;
        alpha = a;
    }
    int classifyData(String s)
    {
        test = s;
        int index=classes; double max = -99;double min = 9999;
        for(int i=0;i<classes;i++)
        {
            double prob = getClassProbability(s,i);
            if(prob < min)
            {
                min = prob;
                index = i;
            }
        }
        return index;
    }
    
    double getClassProbability(String s, int index)
    {
        HashMap<String, Integer> map = trainedData.get(index);
        int size = map.size();
        Set<String> wordList = gatherWords(s);
        //String str[] = s.split(" ");
        int w; double prob=0;
        for (String str : wordList) 
        {
            if(map.get(str)!=null)
                  w=map.get(str);
            else    w=0;
            //System.out.println(str+"  "+w);
            double p = ((double)w+alpha)/((double)size+alpha*words);
            
            prob -= Math.log(p);
            //System.out.println(prob);
        }
        //System.out.println(index+"  "+prob);
        return prob;
    }
    
    Set<String> gatherWords(String s)
    {
        Set<String> words = new HashSet<String>();
        String str[] = s.split(" ");
        for(int i=0;i<str.length;i++)
        {
            words.add(str[i]);
        }
        //System.out.println(s);
        //System.out.println(str.length+"     "+words.size());
        return words;
    }
}

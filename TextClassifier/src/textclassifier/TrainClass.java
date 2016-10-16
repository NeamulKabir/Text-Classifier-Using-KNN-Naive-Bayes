/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package textclassifier;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Neamul Kabir
 */
public class TrainClass {
    
    ArrayList<String> documents;
    HashMap<String, Integer> map;
    
    TrainClass(ArrayList<String> data)
    {
        documents = data;
        map = new HashMap<String, Integer>();
    }
    
    HashMap<String, Integer> trainData()
    {
        for(int i=0;i<documents.size();i++)
        {
            String str = documents.get(i);
            String s[] = str.split(" ");
            for(int j=0;j<s.length;j++)
            {
                if(map.get(s[j])==null)
                {
                    map.put(s[j],1);
                }
                else
                    map.put(s[j], map.get(s[j])+1);
            }
        }
        return map;
    }
}

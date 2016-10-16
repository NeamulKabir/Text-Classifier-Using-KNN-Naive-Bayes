/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textclassifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Neamul Kabir
 */
public class TextClassifier {

    /**
     * @param args the command line arguments
     */
    static int d = 17;
    static double dof,t,kavg,navg,kstd,nstd;
    static int trainCount = 100;
    static int testCount = 50;
    static int startIdxTrain = 2;
    ////
    static ArrayList< ArrayList<String>> classes;
    static ArrayList< ArrayList<String>> testData;
    static ArrayList< HashMap<String, Integer>> training;
    static ArrayList<String> stopWords = new ArrayList<>();
    static ArrayList<Double> NB,KNN;
    static int startIndex = 0;
    static int dataLimit = 100;
    static int testLimit = 50;
    static int totalClasses;
    static int totalWords = 0;
    static double alpha = 0.0015;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        ArrayList<String> fileName = new ArrayList<>();
        BufferedReader brTopics = new BufferedReader(new FileReader("topics.txt"));
        String files;
        while ((files = brTopics.readLine()) != null) {
            //System.out.println(files);
            fileName.add(files);
        }
        totalClasses = fileName.size();
        System.out.println("Classes = " + fileName.size());
        BufferedReader brS = new BufferedReader(new FileReader("stopwords.txt"));
        String stW;
        while ((stW = brS.readLine()) != null) {
             stopWords.add(stW);
        }
        testData = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < fileName.size(); i++) {
            //System.out.println("Test/"+fileName.get(i)+".xml");
            BufferedReader br = new BufferedReader(new FileReader("Test/" + fileName.get(i) + ".xml"));
            String line;
            int count = 0;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                count++;
                if(count>startIndex)
                {
                    sb.append(line);
                    if(count >= (startIndex + testLimit))
                        break;
                }
            }
            ParseString p = new ParseString(stopWords);
            p.parseBuilder(sb.toString());
            ArrayList<String> documents = p.finalString;
            testData.add(documents);
        }
        double testDataCount = (double) testLimit * (double) totalClasses;
        //////////////////////////////////////////////////////////////////////
        ArrayList<String> stopwords = new ArrayList<String>();
        stopwords = getStopWords();

//		ArrayList <Topic> topics = new ArrayList<Topic>();
//		topics = getTrainingData("Training", stopwords,152);
//		System.out.println(topics.size());
        ArrayList<MyDoc> allTestDocs = new ArrayList<MyDoc>();
        allTestDocs = getData("Test", stopwords, testCount);
        ArrayList<Double> accuracy = new ArrayList<Double>();
        /////////////////////////////////////////////////////////////////////
        NB = new ArrayList<>();
        KNN = new ArrayList<>();
        BufferedWriter writer = null;
        File file = new File("KNN_NB_comparison.txt");
        file.createNewFile();
        writer = new BufferedWriter(new FileWriter(file));
        writer.write("Iteration \tNB\tKNN\n");
        for (int run = 0; run < 50; run++) {
            classes = new ArrayList<ArrayList<String>>();

            training = new ArrayList< HashMap<String, Integer>>();
            for (int i = 0; i < fileName.size(); i++) {
                //System.out.println("Training/"+fileName.get(i)+".xml");
                BufferedReader br = new BufferedReader(new FileReader("Training/" + fileName.get(i) + ".xml"));
                String line;
                int count = 0;
                ArrayList<String> documents = new ArrayList<String>();
               StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    count++;
                    if(count>startIndex)
                    {
                        sb.append(line);
                        if(count >= (startIndex + dataLimit))
                            break;
                    }

                }
                ParseString p = new ParseString(stopWords);
                p.parseBuilder(sb.toString());
                documents = p.finalString;
                classes.add(documents);
            }

            for (int i = 0; i < totalClasses; i++) {
                ArrayList<String> temp = classes.get(i);
                TrainClass tob = new TrainClass(temp);
                HashMap<String, Integer> trainMap = tob.trainData();
                training.add(trainMap);
            }
            totalWords = calculateWords(training);

            int success = 0;
            NaiveBayes nb = new NaiveBayes(training, totalWords, totalClasses, alpha);
            for (int i = 0; i < testData.size(); i++) {
                ArrayList<String> temp = testData.get(i);
                for (int j = 0; j < temp.size(); j++) {
                    String t = temp.get(j);
                    int index = nb.classifyData(t);
                    if (i == index) {
                        success++;
                    }

                }

            }
            writer.write(" "+(run+1)+" : \t");
            double ac = (double) success * 100.0 / testDataCount;
            System.out.println("For iteration " + (run + 1) + " data\t\tAccuracy = " + ac);
            NB.add(ac);
            writer.write(ac+"\t");
            ////////////////////////////////////////////////
            ArrayList<MyDoc> allTrainDocs = new ArrayList<MyDoc>();

            allTrainDocs = getData("Training", stopwords, trainCount);

//			System.out.println("TfIdf starts...");
            TfIdf tfidf = new TfIdf(allTrainDocs, allTestDocs);
            double acc = tfidf.getAccuracy();
            acc *= 100.0;
            accuracy.add(acc);
            System.out.println("Iteration " + (run + 1) + ": " + " \tAccuracy(K=3): " + accuracy.get(run));
            KNN.add(acc);
            writer.write(acc+"\n");
            startIdxTrain += 100;
            ////////////////////////////////////////////////////
            startIndex += 100;

        }
        calculateT_Test();
        writer.write("KNN:\tMean = "+kavg+"\tStd = "+kstd);
        writer.write("NB:\tMean = "+navg+"\tStd = "+nstd);
        writer.write("T-value : \t"+t+"\n"+"D.O.F : \t"+dof);
        writer.close();


    }

    static void calculateT_Test()
    {
        double n = NB.size();
        double nsum=0,ksum=0;
        nstd=0;kstd=0;
        for(int i=0;i<NB.size();i++)
        {
            nsum += NB.get(i);
            ksum += KNN.get(i);
        }
        navg = nsum/n;
        kavg = ksum/n;
        for(int i=0;i<NB.size();i++)
        {
            nstd += (NB.get(i)-navg)*(NB.get(i)-navg);
            kstd += (KNN.get(i)-kavg)*(KNN.get(i)-kavg);
        }
        nstd = (nstd/(n-1));
        kstd = (kstd/(n-1));
        System.out.println("Nmean : "+navg+"\tKmean : "+kavg+"\nNstd : "+nstd+"\tKstd : "+kstd);
        double sqr = (nstd/n)+(kstd/n);
        t = (navg - kavg - d)/Math.sqrt(sqr);
        dof = sqr * sqr;
        dof = (n*n*(n-1)) * ( dof / (nstd*nstd+kstd*kstd));
        System.out.println("T-value : "+ t+"\t "+dof);
    }

    static int calculateWords(ArrayList< HashMap<String, Integer>> data) {
        int words = 0;
        HashMap<String, Integer> total = new HashMap<String, Integer>();
        for (int i = 0; i < data.size(); i++) {
            HashMap<String, Integer> temp = training.get(i);
            for (Map.Entry<String, Integer> entry : temp.entrySet()) {
                String key = entry.getKey();
                if (total.get(key) == null) {
                    total.put(key, 1);
                }
            }
            //words += temp.size();
        }
        words = total.size();
        return words;
    }

    ///////////////////////////////////////////////////////////////////////
    private static ArrayList<MyDoc> getData(String string, ArrayList<String> stopwords, int row) {
        // TODO Auto-generated method stub
        String Directory = "../Data";
        String sep = System.getProperty("file.separator");
//		System.out.println(sep);
        ArrayList<MyDoc> m = new ArrayList<MyDoc>();

        ArrayList<String> files = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("../Data/topics.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                files.add(line);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < files.size(); i++) {
//			System.out.println(i + " " + files.get(i) + " is now parsing...");
            String s = Directory + sep + string + sep + files.get(i) + ".xml";
            Parse ps = null;
            if (string.equals("Training")) {

                ps = new Parse(row, startIdxTrain, s, stopwords, files.get(i));
            } else {
                ps = new Parse(row, -1, s, stopwords, files.get(i));
            }
            m.addAll(ps.documents);
        }

        return m;
    }


    private static ArrayList<String> getStopWords() {
        // TODO Auto-generated method stub
        ArrayList<String> stpwrds = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("stopwords.txt"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                stpwrds.add(line);
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return stpwrds;
    }

}

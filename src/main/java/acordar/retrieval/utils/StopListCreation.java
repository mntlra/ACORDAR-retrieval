package acordar.retrieval.utils;

import org.apache.lucene.analysis.CharArraySet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class StopListCreation {
    /**
     * Creates a CharArraySet containing the stoplist file passed as parameter.
     *
     * @param filename: path to the stoplist file.
     * @return
     */
    public static CharArraySet getStopListFromFile(String filename){

        ArrayList<String> stopList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (true) {
                String word = bufferedReader.readLine();
                if (word != null) {
                    stopList.add(word);
                }else{
                    bufferedReader.close();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CharArraySet cas_stoplist = new CharArraySet(0,true);
        cas_stoplist.addAll(stopList);
        return cas_stoplist;
    }
}

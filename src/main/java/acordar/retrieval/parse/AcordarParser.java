package acordar.retrieval.parse;

import org.apache.lucene.benchmark.quality.QualityQuery;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Represents a specific {@code TopicParser} that parses queries encoded in TXT files.
 */
public class AcordarParser extends TopicParser {

    private final Scanner sc;
    private QualityQuery parsedQuery;
    private boolean queryPending;

    public AcordarParser(Reader in) {
        super(in);
        this.sc = new Scanner(in);
    }

    /**
     * Parses each query, extracting its id and text.
     *
     * @return QualityQuery
     */
    @Override
    protected QualityQuery parse() {
        if (queryPending) {
            queryPending = false;
            return parsedQuery;
        }
        Map<String, String> fields = new HashMap<>();
        String number = "";

        try {
            String line = sc.nextLine();
            String[] toks = line.split("\t");

            number = toks[0];
            fields.put(AcordarFields.TEXT, toks[1]);

            parsedQuery =  new QualityQuery(number, fields);
            return parsedQuery;

        }catch (NoSuchElementException e){
            // end of file
            return null;
        }
    }

    @Override
    public boolean hasNext() {
        if (queryPending) return true;
        if (parse() == null) return false;
        queryPending = true;
        return true;
    }
}

package acordar.retrieval;

import acordar.retrieval.index.DatasetsIndexer;
import acordar.retrieval.parse.AcordarParser;
import acordar.retrieval.parse.DatasetsParser;
import acordar.retrieval.search.AcordarSearcher;
import acordar.retrieval.utils.StopListCreation;
import acordar.retrieval.utils.SimilarityFunction;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.*;

import java.util.Arrays;


/**
 * Runs the ACORDAR Retrieval. For each query, returns the top-10 datasets based on the similarity given as input parameter.
 */
public class AcordarRetrieval {

    private static final String INDEX_DESCRIPTOR = "lucene-lucenetoken-nltklucenestop-nostem";
    private static final int MAX_DOCS_RETRIEVED = 10;

    public static void main(String[] args) throws Exception {

        if(args.length != 7)
            throw new IllegalArgumentException("USAGE: inputDataset, similarity, mode, contentPath, topicsPath, outputDir, boostWeights");

        if (!Arrays.asList("TFIDF", "BM25", "FSDM", "LMD").contains(args[1]))
            throw new IllegalArgumentException("Accepted similarity functions: \"TFIDF\", \"BM25\", \"FSDM\", \"LMD\"");

        if (!Arrays.asList("Metadata", "Content", "Full").contains(args[2]))
            throw new IllegalArgumentException("Accepted modalities: \"Metadata\", \"Content\", \"Full\"");

        String corpusPath = args[0];
        String sim = args[1];
        String mode = args[2];
        String contentPath = args[3];
        String topicsPath = args[4];

        String runPath = args[5] + "/" + mode;
        boolean boost = args[6].equals("boost");

        final String RUN_DESCRIPTOR = mode + "-" + INDEX_DESCRIPTOR;
        final String INDEX_PATH = "experiment/indexes/"+sim+mode+"-index-" + RUN_DESCRIPTOR;
        final String RUN_ID = sim + "_" + mode;

        // Create the analyzer
        Analyzer analyzer = new StandardAnalyzer(StopListCreation.getStopListFromFile("resources/nltk_en_stopwords.txt"));
        // Set the similarity
        Similarity similarity = SimilarityFunction.getSimFunction(sim, mode);

        System.out.println("--- ACORDAR RETRIEVAL, mode: " + mode + ", similarity function: "+ sim +" ---");

        System.out.println("\n--------------- INDEXING ---------------\n");
        DatasetsIndexer indexer = new DatasetsIndexer(analyzer, similarity, INDEX_PATH, corpusPath, mode, contentPath, DatasetsParser.class);
        indexer.index(true);
        System.out.println("---------------- INDEXING SUCCESSFULLY COMPLETED -----------\n");
        System.out.println(("Indexed " + indexer.getIndexedDatasetsCount() + " Documents"));

        System.out.println("--------------- SEARCHING ---------------\n");
        AcordarSearcher searcher = new AcordarSearcher(analyzer, similarity, boost, sim, mode, INDEX_PATH, topicsPath, RUN_ID, runPath, MAX_DOCS_RETRIEVED, AcordarParser.class, RUN_ID + ".txt");
        searcher.search(true);

        System.out.println("--------------- FINISHED ACORDAR RETRIEVAL ---------------\n");

    }

}


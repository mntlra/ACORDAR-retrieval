package acordar.retrieval.search;

import acordar.retrieval.similarity.FSDMRanker;
import acordar.retrieval.utils.Weights;
import javafx.util.Pair;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import acordar.retrieval.parse.DatasetFields;
import acordar.retrieval.parse.TopicParser;
import acordar.retrieval.parse.AcordarFields;

/**
 * Searches among the indexed datasets and return the top-10 relevant dataset for a given query, based on the considered
 * similarity function.
 */
public class AcordarSearcher {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String TOPICS_FILE_EXTENSION = ".txt";

    private final String runId;
    private final String runFileName;
    private final PrintWriter writer;
    private final IndexReader reader;
    private final IndexSearcher searcher;
    private final QueryParser queryParser;
    private final int maxDocsRetrieved;
    private final Class<? extends TopicParser> tpCls;
    private final String topicsPath;
    private final String sim_label;
    private FSDMRanker ranker;

    public AcordarSearcher(Analyzer analyzer, Similarity similarity, Boolean boost, String sim_label, String mode, String indexPath, String topicsPath, String runId,
                           String runPath, int maxDocsRetrieved, Class<? extends TopicParser> tpCls, String runFileName) {

        if (analyzer == null) throw new IllegalArgumentException("Analyzer cannot be null.");
        if (similarity == null) throw new IllegalArgumentException("Similarity cannot be null.");
        if (indexPath == null) throw new IllegalArgumentException("Index path cannot be null.");
        if (topicsPath == null) throw new IllegalArgumentException("Topics path cannot be null.");
        if (runId == null) throw new IllegalArgumentException("Run identifier cannot be null.");
        if (runPath == null) throw new IllegalArgumentException("Run path cannot be null.");
        if (tpCls == null) throw new IllegalArgumentException("Topic parser class cannot be null.");
        if (runFileName == null) throw new IllegalArgumentException("Run file name cannot be null.");
        if (indexPath.isEmpty()) throw new IllegalArgumentException("Index path cannot be empty.");
        if (topicsPath.isEmpty()) throw new IllegalArgumentException("Topics path cannot be empty.");
        if (runId.isEmpty()) throw new IllegalArgumentException("Run identifier cannot be empty.");
        if (runPath.isEmpty()) throw new IllegalArgumentException("Run path cannot be empty.");
        if (runFileName.isEmpty()) throw new IllegalArgumentException("Run file name cannot be empty.");
        if (maxDocsRetrieved <= 0)
            throw new IllegalArgumentException("The maximum number of documents to be retrieved cannot be less than or equal to zero.");

        Path indexDir = Paths.get(indexPath);
        Path runDir = Paths.get(runPath);
        Path runFile = runDir.resolve(runFileName);

        if (!Files.isReadable(indexDir))
            throw new IllegalArgumentException("Index directory \"" + indexDir.toAbsolutePath() + "\" cannot be read.");

        if (!Files.isDirectory(indexDir))
            throw new IllegalArgumentException("\"" + indexDir.toAbsolutePath() + "\" expected to be a directory where to search the index.");

        if (!Files.isWritable(runDir))
            throw new IllegalArgumentException("Run directory \"" + runDir.toAbsolutePath() + "\" cannot be written.");

        if (!Files.isDirectory(runDir))
            throw new IllegalArgumentException("\"" + runDir.toAbsolutePath() + "\" expected to be a directory where to write the run.");

        // Open index directory to read and load the index
        try {
            this.reader = DirectoryReader.open(FSDirectory.open(indexDir));
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to create the index reader for directory \"" + indexDir.toAbsolutePath() + "\"");
        }

        try {
            this.writer = new PrintWriter(Files.newBufferedWriter(runFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE));
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to open run file \"" + runFile.toAbsolutePath() + "\".");
        }
        // Read index and load it
        this.searcher = new IndexSearcher(reader);
        this.searcher.setSimilarity(similarity);
        // Set query fields based on the configuration
        String[] queryFields;
        if(mode.equals("Metadata")){
            queryFields = new String[]{DatasetFields.TITLE, DatasetFields.DESCRIPTION, DatasetFields.TAGS, DatasetFields.AUTHOR};
        }else if(mode.equals("Content")){
            queryFields = new String[]{DatasetFields.CLASSES, DatasetFields.PROPERTIES, DatasetFields.ENTITIES, DatasetFields.LITERALS};
        }else{
            queryFields = new String[]{DatasetFields.TITLE, DatasetFields.DESCRIPTION, DatasetFields.TAGS, DatasetFields.AUTHOR,
                    DatasetFields.CLASSES, DatasetFields.PROPERTIES, DatasetFields.ENTITIES, DatasetFields.LITERALS};
        }

        if (boost) {
            // Add boost weights
            this.queryParser = new MultiFieldQueryParser(queryFields, analyzer, Weights.getBoostWeights(sim_label, mode));
        }else {
            // Do not use boost weights
            this.queryParser = new MultiFieldQueryParser(queryFields, analyzer);
        }

        this.runId = runId;
        this.maxDocsRetrieved = maxDocsRetrieved;
        this.tpCls = tpCls;
        this.topicsPath = topicsPath;
        this.runFileName = runFileName;
        this.sim_label = sim_label;
        if(sim_label.equals("FSDM")){
            this.ranker = new FSDMRanker(indexPath, analyzer, similarity, maxDocsRetrieved, Weights.getBoostWeights(sim_label, mode));
        }
    }

    public void search(boolean verbose) throws IOException, ParseException {
        long startTime = System.currentTimeMillis();
        if (!topicsPath.endsWith(TOPICS_FILE_EXTENSION)) {
            throw new IllegalArgumentException("Topic file must be .txt");
        }
        TopicParser tp = TopicParser.create(tpCls, Files.newBufferedReader(Paths.get(topicsPath), CHARSET));
        int searchedTopicsCount = 0;
        if(sim_label.equals("FSDM")){
            // Re Ranking based on FSDM
            for (QualityQuery t : tp) {
                String textQuery = t.getValue(AcordarFields.TEXT);
                if (verbose) System.out.println("Searching topic (" + t.getQueryID() + ") : " + textQuery);
                List<Pair<Integer, Double>> rankedList = ranker.getFSDMRankingList(textQuery);
                //print results
                for(int i=0; i<rankedList.size(); i++){
                    writer.printf(Locale.ENGLISH, "%s\tQ0\t%s\t%d\t%.6f\t%s%n", t.getQueryID(), rankedList.get(i).getKey(), i, rankedList.get(i).getValue(), runId);
                    writer.flush();
                }
                searchedTopicsCount++;
            }
        }
        else{
            // Standard search pipeline provided by Lucene
            for (QualityQuery t : tp) {
                if (verbose) System.out.println("Searching topic (" + t.getQueryID() + ") : " + t.getValue(AcordarFields.TEXT));
                BooleanQuery.Builder builder = new BooleanQuery.Builder();
                builder.add(queryParser.parse(QueryParserBase.escape(t.getValue(AcordarFields.TEXT))), BooleanClause.Occur.SHOULD);
                Query query = builder.build();
                TopDocs topDocs = searcher.search(query, maxDocsRetrieved);
                ScoreDoc[] scoreDocs = topDocs.scoreDocs;
                if (verbose) System.out.println("Saving " + scoreDocs.length + " documents resulting for topic id " + t.getQueryID());
                for (int i = 0; i < scoreDocs.length; i++) {
                    Set<String> idField = Collections.singleton(DatasetFields.ID);
                    String docId = reader.document(scoreDocs[i].doc, idField).get(DatasetFields.ID);
                    writer.printf(Locale.ENGLISH, "%s Q0 %s %d %.6f %s%n", t.getQueryID(), docId, i, scoreDocs[i].score, runId);
                }
                writer.flush();
                searchedTopicsCount++;
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        if (verbose) {
            String timeFormat = ((elapsedTime / 1000) / 60 < 1 ? "" : "m'min' ") + (elapsedTime / 1000 < 1 ? "" : "s'sec' ") + "S'ms'";
            String duration = DurationFormatUtils.formatDuration(endTime - startTime, timeFormat);
            System.out.println("\nSearching completed in: " + duration);
            System.out.println("Total searched topics: " + searchedTopicsCount);
            System.out.println("Created run file: \"" + runFileName + "\"");
        }
        writer.close();
        reader.close();
    }
}

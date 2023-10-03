package acordar.retrieval.index;

import acordar.retrieval.parse.DatasetFields;
import acordar.retrieval.parse.DocumentParser;
import acordar.retrieval.parse.ParsedDataset;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;



/**
 * Indexes the datasets processing the ACORDAR Test Collection.
 */
public class DatasetsIndexer {

    private static final String DATASETS_FILES_EXTENSION = ".json";
    private static final int RAM_BUFFER_SIZE = 256;

    private final IndexWriter writer;
    private final Class<? extends DocumentParser> dpCls;
    private final Path corpusFile;
    private final String corpusPath;
    private final String indexPath;
    private int indexedDatasetsCount;

    /**
     *
     * @param analyzer: analyzer used to build the index.
     * @param similarity: similarity function.
     * @param indexPath: saving path for the index.
     * @param corpusPath: path to the ACORDAR Test Collection.
     * @param dpCls: class used to parse the corpus.
     */
    public DatasetsIndexer(Analyzer analyzer, Similarity similarity, String indexPath, String corpusPath, Class<? extends DocumentParser> dpCls) {
        if (dpCls == null) throw new IllegalArgumentException("Document parser class cannot be null.");
        if (analyzer == null) throw new IllegalArgumentException("Analyzer cannot be null.");
        if (similarity == null) throw new IllegalArgumentException("Similarity cannot be null.");
        if (indexPath == null) throw new IllegalArgumentException("Index path cannot be null.");
        if (corpusPath == null) throw new IllegalArgumentException("Corpus path cannot be null.");
        if (indexPath.isEmpty()) throw new IllegalArgumentException("Index path cannot be empty.");
        if (corpusPath.isEmpty()) throw new IllegalArgumentException("Corpus path cannot be empty.");

        this.indexPath = indexPath;
        Path indexDir = Paths.get(indexPath);
        Path corpusFile = Paths.get(corpusPath);

        if (Files.notExists(indexDir)) {
            try {
                Files.createDirectories(indexDir);
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to create directory \"" + indexDir.toAbsolutePath() + "\".", e);
            }
        }

        if (!Files.isWritable(indexDir))
            throw new IllegalArgumentException("Index directory \"" + indexDir.toAbsolutePath() + "\" cannot be written.");

        if (!Files.isDirectory(indexDir))
            throw new IllegalArgumentException("\"" + indexDir.toAbsolutePath() + "\" expected to be a directory where to write the index.");

        if (!Files.isReadable(corpusFile))
            throw new IllegalArgumentException("Documents path \"" + corpusFile.toAbsolutePath() + "\" cannot be read.");

        // Set the index writer configuration
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setSimilarity(similarity);
        iwc.setRAMBufferSizeMB(RAM_BUFFER_SIZE);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setCommitOnClose(true);

        try {
            writer = new IndexWriter(FSDirectory.open(indexDir), iwc);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to create the index writer in directory \"" + indexDir.toAbsolutePath() + "\".", e);
        }

        this.dpCls = dpCls;
        this.corpusFile = corpusFile;
        this.corpusPath = corpusPath;
    }

    /**
     * Indexes the parsed corpus.
     *
     * @param mode: considered configuration. (Metadata, Content, or Full)
     * @param verbose: if set to True, print diagnostics.
     * @throws IOException
     */
    public void index(String mode, boolean verbose) throws IOException {

        long startTime = System.currentTimeMillis();

        if (corpusFile.getFileName().toString().endsWith(DATASETS_FILES_EXTENSION)) {
            if (verbose) System.out.println("Indexing file: " + corpusFile.getFileName());
            DocumentParser dp = DocumentParser.create(dpCls, new BufferedInputStream(new FileInputStream(corpusPath)));

            Document doc;
            Set<String> ids = new HashSet<>();
            indexedDatasetsCount = 0;
            for (ParsedDataset d : dp) {
                if (!ids.contains(d.getId())) {
                    doc = new Document();
                    doc.add(new StringField(DatasetFields.ID, d.getId(), Field.Store.YES));
                    if(mode.equals("Metadata") || mode.equals("Full")) {
                        // metadata fields
                        doc.add(new DescriptionField(d.getDescription()));
                        doc.add(new TitleField(d.getTitle()));
                        doc.add(new AuthorField(d.getAuthor()));
                        doc.add(new TagsField(d.getTags()));
                    }
                    if(mode.equals("Content") || mode.equals("Full")) {
                        // data fields
                        doc.add(new ClassesField(d.getClasses()));
                        doc.add(new PropertiesField(d.getProperties()));
                        doc.add(new EntitiesField(d.getEntities()));
                        doc.add(new LiteralsField(d.getLiterals()));
                    }
                    ids.add(d.getId());
                    writer.addDocument(doc);
                    indexedDatasetsCount++;
                }
                if (verbose && indexedDatasetsCount % 10000 == 0)
                    System.out.println("Indexed documents: " + indexedDatasetsCount + " (partial)");
            }
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            if (verbose) {
                String timeFormat = ((elapsedTime / 1000) / 60 < 1 ? "" : "m'min' ") + (elapsedTime / 1000 < 1 ? "" : "s'sec' ") + "S'ms'";
                String duration = DurationFormatUtils.formatDuration(endTime - startTime, timeFormat);
                System.out.println("Indexing completed in: " + duration);
                System.out.println("Total indexed documents: " + indexedDatasetsCount + "\n");
            }
            writer.commit();
            writer.close();
            if (verbose){
                System.out.println("-- Index saved in directory: " + indexPath);
                System.out.println("-- Indexer successfully closed" + "\n");
            }
        }
    }

    /**
     * @return the number of indexed datasets
     */
    public int getIndexedDatasetsCount() {
        return indexedDatasetsCount;
    }
}


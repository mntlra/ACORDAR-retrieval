package acordar.retrieval.utils;
import org.apache.lucene.search.similarities.*;

import java.util.HashMap;

/**
 * Return the correct similarity function, given the name of the function
 */
public class SimilarityFunction {

    private static final float K1 = 1.2f;

    public static Similarity getSimFunction(String name, String mode){

        if (name.equals("TFIDF")){
            return new ClassicSimilarity();
        }
        else if(name.equals("LMD")){
            return new LMDirichletSimilarity();
        }
        else if(name.equals("BM25")){
                //|| (name.equals("FSDM"))){
            if(mode.equals("Metadata")) {
                return new PerFieldSimilarityWrapper() {
                    @Override
                    public Similarity get(String field) {
                        return getBM25FSimilarity("Metadata", field);
                    }
                };
            }else if(mode.equals("Content")) {
                return new PerFieldSimilarityWrapper() {
                    @Override
                    public Similarity get(String field) {
                        return getBM25FSimilarity("Content", field);
                    }
                };
            }else {
                return new PerFieldSimilarityWrapper() {
                    @Override
                    public Similarity get(String field) {
                        return getBM25FSimilarity("Full", field);
                    }
                };
            }
        }
        else if(name.equals("FSDM")){
            // FSDM is a re-rankering function, we use BM25 as similarity
            return new BM25Similarity();
        }
        else {
            // default BM25 similarity
            return new BM25Similarity();
        }
    }

    /**
     * Return the BM25Similarity function with the correct boost weight.
     *
     * @param mode: the run configuration (Metadata, Content, Full).
     * @param field: the considered field.
     *
     * @return BM25Similarity with the correct boost weight.
     */
    private static Similarity getBM25FSimilarity(String mode, String field){
        HashMap<String, Float> boostWeights = Weights.getBoostWeights("BM25", mode);
        return new BM25Similarity(K1, boostWeights.get(field));
    }

}

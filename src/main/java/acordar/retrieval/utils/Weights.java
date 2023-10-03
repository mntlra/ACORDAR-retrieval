package acordar.retrieval.utils;

import java.util.HashMap;

/**
 * Class to store the boost weights and return the requested weights given the similarity function and configuration.
 */
public class Weights {
    // weights follows the format:
    // {"title", "description", "author", "tags", "entity", "literal", "class", "property"};

    // pooling weights -- from ACORDAR-2 GitHub repository
    public static float[] BM25BoostWeights = {1.0f, 0.9f, 0.9f, 0.6f, 0.2f, 0.3f, 0.1f, 0.1f};
    public static float[] TFIDFBoostWeights = {1.0f, 0.7f, 0.9f, 0.9f, 0.8f, 0.5f, 0.1f, 0.4f};
    public static float[] LMDBoostWeights = {1.0f, 0.9f, 0.1f, 1.0f, 0.2f, 0.3f, 0.2f, 0.1f};
    public static float[] FSDMBoostWeights = {1.0f, 0.1f, 0.5f, 0.9f, 0.1f, 0.1f, 0.4f, 0.6f};

    public static float[] BM25MetadataBoostWeights = {0.5f, 0.3f, 0.2f, 0.2f};
    public static float[] TFIDFMetadataBoostWeights = {1.0f, 0.6f, 0.4f, 0.5f};
    public static float[] LMDMetadataBoostWeights = {1.0f, 0.8f, 0.9f, 0.7f};
    public static float[] FSDMMetadataBoostWeights = {1.0f, 0.1f, 0.2f, 0.6f};

    public static float[] BM25ContentBoostWeights = {0.1f, 0.7f, 0.2f, 0.2f};
    public static float[] TFIDFContentBoostWeights = {0.3f, 1.0f, 0.6f, 0.3f};
    public static float[] LMDContentBoostWeights = {0.3f, 1.0f, 0.1f, 0.6f};
    public static float[] FSDMContentBoostWeights = {1.0f, 0.6f, 0.1f, 0.1f};

    private static HashMap<String, float[]> model2weights = new HashMap<>();

    private static void populateMaps(HashMap<String, float[]> model2weights){

        // Mapping between models and weights
        model2weights.put("BM25", BM25BoostWeights);
        model2weights.put("TFIDF", TFIDFBoostWeights);
        model2weights.put("LMD", LMDBoostWeights);
        model2weights.put("FSDM", FSDMBoostWeights);
        model2weights.put("BM25Metadata", BM25MetadataBoostWeights);
        model2weights.put("TFIDFMetadata", TFIDFMetadataBoostWeights);
        model2weights.put("LMDMetadata", LMDMetadataBoostWeights);
        model2weights.put("FSDMMetadata", FSDMMetadataBoostWeights);
        model2weights.put("BM25Content", BM25ContentBoostWeights);
        model2weights.put("TFIDFContent", TFIDFContentBoostWeights);
        model2weights.put("LMDContent", LMDContentBoostWeights);
        model2weights.put("FSDMContent", FSDMContentBoostWeights);
    }

    public static void init(){
        Weights.populateMaps(model2weights);
    }

    /**
     * Filter the boost weights based on the given similarity and configuration.
     *
     * @param similarity: name of the considered similarity function.
     * @param mode: name of the considered configuration.
     *
     * @return HashMap with the boost weights of the given similarity and configuration.
     */
    public static HashMap<String, Float> getBoostWeights(String similarity, String mode) {

        Weights.init();

        HashMap<String, Float> boostWeights = new HashMap<>();
        String keyword = similarity;
        if(!mode.equals("both")){
            keyword = similarity+mode;
        }
        float[] weights = model2weights.get(keyword);
        if (mode.equals("Metadata") || mode.equals("Full")){
            boostWeights.put("title", weights[0]);
            boostWeights.put("description", weights[1]);
            boostWeights.put("author", weights[2]);
            boostWeights.put("tags", weights[3]);
            if (mode.equals("Full")){
                boostWeights.put("entities", weights[4]);
                boostWeights.put("literals", weights[5]);
                boostWeights.put("classes", weights[6]);
                boostWeights.put("properties", weights[7]);
            }
        }
        else if (mode.equals("Content")){
            boostWeights.put("entities", weights[0]);
            boostWeights.put("literals", weights[1]);
            boostWeights.put("classes", weights[2]);
            boostWeights.put("properties", weights[3]);
        }
        return boostWeights;
    }
}

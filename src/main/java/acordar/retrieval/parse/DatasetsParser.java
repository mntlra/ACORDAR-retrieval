package acordar.retrieval.parse;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Represents a specific {@code DocumentParser} that parses debates encoded in JSON files.
 */
public class DatasetsParser extends DocumentParser {

    private static final JsonFactory jsonFactory = new JsonFactory().enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());
    private final JsonParser jsonParser;
    private ParsedDataset parsedDataset;
    private boolean documentPending;

    public DatasetsParser(BufferedInputStream in, String mode, String contentPath) throws IOException {
        super(in, mode, contentPath);
        this.jsonParser = jsonFactory.createParser(in);
        // Check the first token
        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new IllegalStateException("Expected content to be an array");
        }
        else{
            // skip to start of the first object (i.e. START_OBJECT)
            jsonParser.nextToken();
        }
    }

    /**
     * Parses each dataset and extract the relevant fields.
     *
     * @return ParsedDataset object.
     */
    @Override
    protected ParsedDataset parse() {
        if (documentPending) {
            documentPending = false;
            return parsedDataset;
        }
        try {
            // Check the first token
            if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
                throw new IllegalStateException("Expected content to be an object");
            }

            String id = "";
            String title = "";
            String description = "";
            String size = "";
            String license = "";
            String created = "";
            String updated = "";
            String tags = "";
            String version = "";
            String author = "";

            //loop through the JsonTokens to extract the metadata fields
            while(jsonParser.nextToken() != JsonToken.END_OBJECT){

                String property = jsonParser.getCurrentName();
                jsonParser.nextToken();
                String nextTokText = (jsonParser.getText()).equals("None") ? "" : jsonParser.getText();

                switch(property){
                    case DatasetFields.ID:
                        id = nextTokText;
                        break;
                    case DatasetFields.TITLE:
                        title = nextTokText;
                        break;
                    case DatasetFields.DESCRIPTION:
                        description = nextTokText;
                        break;
                    case DatasetFields.SIZE:
                        size = nextTokText;
                        break;
                    case DatasetFields.LICENSE:
                        license = nextTokText;
                        break;
                    case DatasetFields.CREATED:
                        created = nextTokText;
                        break;
                    case DatasetFields.UPDATED:
                        updated = nextTokText;
                        break;
                    case DatasetFields.TAGS:
                        tags = nextTokText;
                        break;
                    case DatasetFields.VERSION:
                        version = nextTokText;
                        break;
                    case DatasetFields.AUTHOR:
                        author = nextTokText;
                        break;
                    case DatasetFields.DOWNLOAD:
                        while(jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            // skip "download" field
                            String tmp = jsonParser.getText();
                        }
                        break;
                }
            }
            if(mode.equals("Metadata")){
                // Return the parsed dataset with only metadata fields
                parsedDataset =  new ParsedDataset(id, title, description, size, license, created, updated, tags, version, author);
            }
            else{
                // Extract the data fields
                DatasetContent datasetContent = new DatasetContent(id, contentDir);
                // Return the parsed dataset
                parsedDataset =  new ParsedDataset(id, title, description, size, license, created, updated, tags, version, author, datasetContent.getClasses(), datasetContent.getProperties(), datasetContent.getEntities(),
                        datasetContent.getLiterals());
            }

            return parsedDataset;

        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse the document.", e);
        }
    }

    @Override
    public boolean hasNext(){
        if (documentPending) return true;
        // Check positioning in the file
        if (jsonParser.currentToken() == JsonToken.START_OBJECT) {
            // start of new dataset
            documentPending = false;
            return true;
        }
        else if (jsonParser.currentToken() == JsonToken.END_OBJECT) {
            try {
                // check next token
                JsonToken nextToken = jsonParser.nextToken();
                if (nextToken == JsonToken.START_OBJECT) {
                    // start of new dataset
                    documentPending = false;
                    return true;
                } else if (nextToken == JsonToken.END_ARRAY) {
                    // end of file
                    return false;
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            documentPending = true;
            return true;
        }
        documentPending = false;
        return true;
    }
}

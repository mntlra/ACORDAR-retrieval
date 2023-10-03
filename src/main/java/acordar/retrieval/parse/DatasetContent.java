package acordar.retrieval.parse;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Represents a parsed dataset to be indexed.
 */
public class DatasetContent {

    private String id;
    private String classes;
    private String properties;
    private String entities;
    private String literals;

    private static final JsonFactory jsonFactory = new JsonFactory().enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());
    private final JsonParser jsonParser;

    /**
     * Store the data fields for each dataset.
     *
     * @param id: the id of the considered dataset.
     * @param contentPath: the directory where the indexable content is stored.
     *
     * @throws IOException
     */
    public DatasetContent(String id, String contentPath) throws IOException {

        if (id == null) throw new IllegalArgumentException("Document id cannot be null.");

        this.id = id;
        String filepath = contentPath+"dataset_"+id+".json";
        this.jsonParser = jsonFactory.createParser(new BufferedInputStream(new FileInputStream(filepath)));
        // Check the first token
        if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Expected content to be a JSON-like file");
        }
        while(jsonParser.nextToken() != JsonToken.END_OBJECT){

            String property = jsonParser.getCurrentName();
            jsonParser.nextToken();
            String nextTokText = (jsonParser.getText()).equals("None") ? "" : jsonParser.getText();

            switch(property){
                case DatasetFields.CLASSES:
                    this.classes = nextTokText;
                    break;
                case DatasetFields.PROPERTIES:
                    this.properties = nextTokText;
                    break;
                case DatasetFields.ENTITIES:
                    this.entities = nextTokText;
                    break;
                case DatasetFields.LITERALS:
                    this.literals = nextTokText;
                    break;
            }
        }
    }

    public void setId(String id) {this.id = id;}

    public void setClasses(String classes) { this.classes = classes; }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public void setEntities(String entities) { this.entities = entities; }

    public void setLiterals(String literals) {
        this.literals = literals;
    }

    public String getId() {
        return id;
    }

    public String getClasses() {
        return classes;
    }

    public String getProperties() {
        return properties;
    }

    public String getEntities() { return entities; }

    public String getLiterals() {
        return literals;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("classes", classes.isEmpty()  ? "<empty>" : classes)
                .append("properties", properties.isEmpty()  ? "<empty>" : properties)
                .append("entities", entities.isEmpty()  ? "<empty>" : entities)
                .append("literals", literals.isEmpty()  ? "<empty>" : literals);
        return tsb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof ParsedDataset) && id.equals(((ParsedDataset) obj).getId()));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


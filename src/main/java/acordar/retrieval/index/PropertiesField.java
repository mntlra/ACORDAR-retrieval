package acordar.retrieval.index;

import acordar.retrieval.parse.DatasetFields;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * Represents a {@link Field} for containing the properties of a dataset.
 *
 */
public class PropertiesField extends Field {

    private static final FieldType PROPERTIES_TYPE = new FieldType();

    static {
        PROPERTIES_TYPE.setStored(true);
        PROPERTIES_TYPE.setTokenized(true);
        PROPERTIES_TYPE.setStoreTermVectors(true);
        PROPERTIES_TYPE.setStoreTermVectorPositions(true);
        PROPERTIES_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    }

    public PropertiesField(String value) {
        super(DatasetFields.PROPERTIES, value, PROPERTIES_TYPE);
    }
}


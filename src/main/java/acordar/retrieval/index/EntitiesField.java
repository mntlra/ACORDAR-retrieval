package acordar.retrieval.index;

import acordar.retrieval.parse.DatasetFields;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * Represents a {@link Field} for containing the entities of a dataset.
 *
 */
public class EntitiesField extends Field {

    private static final FieldType ENTITIES_TYPE = new FieldType();

    static {
        ENTITIES_TYPE.setStored(true);
        ENTITIES_TYPE.setTokenized(true);
        ENTITIES_TYPE.setStoreTermVectors(true);
        ENTITIES_TYPE.setStoreTermVectorPositions(true);
        ENTITIES_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    }

    public EntitiesField(String value) {
        super(DatasetFields.ENTITIES, value, ENTITIES_TYPE);
    }
}


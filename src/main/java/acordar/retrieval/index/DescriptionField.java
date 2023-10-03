package acordar.retrieval.index;

import acordar.retrieval.parse.DatasetFields;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * Represents a {@link Field} for containing the description of a dataset.
 *
 */
public class DescriptionField extends Field {

    private static final FieldType DESCRIPTION_TYPE = new FieldType();

    static {
        DESCRIPTION_TYPE.setStored(true);
        DESCRIPTION_TYPE.setTokenized(true);
        DESCRIPTION_TYPE.setStoreTermVectors(true);
        DESCRIPTION_TYPE.setStoreTermVectorPositions(true);
        DESCRIPTION_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    }

    public DescriptionField(String value) {
        super(DatasetFields.DESCRIPTION, value, DESCRIPTION_TYPE);
    }
}

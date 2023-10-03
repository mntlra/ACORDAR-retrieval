package acordar.retrieval.index;

import acordar.retrieval.parse.DatasetFields;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * Represents a {@link Field} for containing the literals of a dataset.
 *
 */
public class LiteralsField extends Field {

    private static final FieldType LITERALS_TYPE = new FieldType();

    static {
        LITERALS_TYPE.setStored(true);
        LITERALS_TYPE.setTokenized(true);
        LITERALS_TYPE.setStoreTermVectors(true);
        LITERALS_TYPE.setStoreTermVectorPositions(true);
        LITERALS_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    }

    public LiteralsField(String value) {
        super(DatasetFields.LITERALS, value, LITERALS_TYPE);
    }
}


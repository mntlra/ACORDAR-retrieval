package acordar.retrieval.index;

import acordar.retrieval.parse.DatasetFields;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * Represents a {@link Field} for containing the authors of a dataset.
 *
 */
public class AuthorField extends Field {

    private static final FieldType AUTHOR_TYPE = new FieldType();

    static {
        AUTHOR_TYPE.setStored(true);
        AUTHOR_TYPE.setTokenized(true);
        AUTHOR_TYPE.setStoreTermVectors(true);
        AUTHOR_TYPE.setStoreTermVectorPositions(true);
        AUTHOR_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    }

    public AuthorField(String value) {
        super(DatasetFields.AUTHOR, value, AUTHOR_TYPE);
    }
}

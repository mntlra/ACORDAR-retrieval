package acordar.retrieval.index;

import acordar.retrieval.parse.DatasetFields;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * Represents a {@link Field} for containing the classes of a dataset.
 *
 */
public class ClassesField extends Field {

    private static final FieldType CLASSES_TYPE = new FieldType();

    static {
        CLASSES_TYPE.setStored(true);
        CLASSES_TYPE.setTokenized(true);
        CLASSES_TYPE.setStoreTermVectors(true);
        CLASSES_TYPE.setStoreTermVectorPositions(true);
        CLASSES_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    }

    public ClassesField(String value) {
        super(DatasetFields.CLASSES, value, CLASSES_TYPE);
    }
}


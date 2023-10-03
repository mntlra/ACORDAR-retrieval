package acordar.retrieval.index;

import acordar.retrieval.parse.DatasetFields;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * Represents a {@link Field} for containing the title of a dataset.
 * <p>
 * It is a tokenized field, not stored, keeping only document ids and term frequencies (see {@link
 * IndexOptions#DOCS_AND_FREQS} in order to minimize the space occupation.
 */
public class TitleField extends Field {

    private static final FieldType TITLE_TYPE = new FieldType();

    static {
        TITLE_TYPE.setStored(true);
        TITLE_TYPE.setTokenized(true);
        TITLE_TYPE.setStoreTermVectors(true);
        TITLE_TYPE.setStoreTermVectorPositions(true);
        TITLE_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    }

    public TitleField(String value) {
        super(DatasetFields.TITLE, value, TITLE_TYPE);
    }
}

package acordar.retrieval.index;

import acordar.retrieval.parse.DatasetFields;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * Represents a {@link Field} for containing the tags of a dataset.
 *
 */
public class TagsField extends Field {

    private static final FieldType TAGS_TYPE = new FieldType();

    static {
        TAGS_TYPE.setStored(true);
        TAGS_TYPE.setTokenized(true);
        TAGS_TYPE.setStoreTermVectors(true);
        TAGS_TYPE.setStoreTermVectorPositions(true);
        TAGS_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    }

    public TagsField(String value) {
        super(DatasetFields.TAGS, value, TAGS_TYPE);
    }
}

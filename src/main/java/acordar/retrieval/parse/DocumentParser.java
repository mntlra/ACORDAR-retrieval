package acordar.retrieval.parse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents an abstract document parser.
 */
public abstract class DocumentParser implements Iterator<ParsedDataset>, Iterable<ParsedDataset>{


    protected final BufferedInputStream in;

    protected DocumentParser(BufferedInputStream in) {
        if (in == null) throw new IllegalArgumentException("Reader cannot be null.");
        this.in = in;
    }

    public static DocumentParser create(Class<? extends DocumentParser> cls, BufferedInputStream in) {
        if (cls == null) throw new IllegalArgumentException("Document parser class cannot be null.");
        if (in == null) throw new IllegalArgumentException("BufferedInputStream cannot be null.");

        try {
            return cls.getConstructor(BufferedInputStream.class).newInstance(in);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to instantiate document parser \"" + cls.getName() + "\".", e);
        }
    }

    @Override
    public Iterator<ParsedDataset> iterator() {
        return this;
    }

    @Override
    public ParsedDataset next() {
        if (hasNext()) return parse();
        try {
            in.close();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to close the BufferedInputStream.", e);
        }
        throw new NoSuchElementException("No more datasets to parse.");
    }

    protected abstract ParsedDataset parse();

}


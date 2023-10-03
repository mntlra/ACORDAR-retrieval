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
    protected final String mode;
    protected final String contentDir;

    protected DocumentParser(BufferedInputStream in, String mode, String contentDir) {
        if (in == null) throw new IllegalArgumentException("Reader cannot be null.");
        this.in = in;
        this.mode = mode;
        this.contentDir = contentDir;

    }

    public static DocumentParser create(Class<? extends DocumentParser> cls, BufferedInputStream in, String mode, String contentPath) {
        if (cls == null) throw new IllegalArgumentException("Document parser class cannot be null.");
        if (in == null) throw new IllegalArgumentException("BufferedInputStream cannot be null.");

        try {
            return cls.getConstructor(BufferedInputStream.class, String.class, String.class).newInstance(in, mode, contentPath);
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


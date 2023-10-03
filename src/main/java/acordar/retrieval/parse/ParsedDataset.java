package acordar.retrieval.parse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a parsed dataset to be indexed.
 */
public class ParsedDataset {

    private String id;
    private String title;
    private String description;
    private String size;
    private String license;
    private String created;
    private String updated;
    private String tags;
    private String version;
    private String author;
    private String classes;
    private String properties;
    private String entities;
    private String literals;


    public ParsedDataset(String id, String title, String description, String size, String license, String created, String updated, String tags, String version, String author,
                         String classes, String properties, String entities, String literals) {
        if (id == null) throw new IllegalArgumentException("Document id cannot be null.");
        if (title == null) throw new IllegalArgumentException("Document title cannot be null.");
        if (id.isEmpty()) throw new IllegalArgumentException("Document id cannot be empty.");
        if (title.isEmpty()) throw new IllegalArgumentException("Document title cannot be empty.");

        this.id = id;
        this.title = title;
        this.description = description;
        this.size = size;
        this.license = license;
        this.created = created;
        this.updated = updated;
        this.tags = tags;
        this.version = version;
        this.author = author;
        this.classes = classes;
        this.properties = properties;
        this.entities = entities;
        this.literals = literals;
    }

    public ParsedDataset(String id, String title, String description, String size, String license, String created, String updated, String tags, String version, String author) {
        if (id == null) throw new IllegalArgumentException("Document id cannot be null.");
        if (title == null) throw new IllegalArgumentException("Document title cannot be null.");
        if (id.isEmpty()) throw new IllegalArgumentException("Document id cannot be empty.");
        if (title.isEmpty()) throw new IllegalArgumentException("Document title cannot be empty.");

        this.id = id;
        this.title = title;
        this.description = description;
        this.size = size;
        this.license = license;
        this.created = created;
        this.updated = updated;
        this.tags = tags;
        this.version = version;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreated() {
        return created;
    }

    public String getSize() {
        return size;
    }

    public String getLicense() {
        return license;
    }

    public String getUpdated() {
        return updated;
    }

    public String getTags() {
        return tags;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getClasses() {
        return classes;
    }

    public String getProperties() {
        return properties;
    }

    public String getEntities() { return entities; }

    public String getLiterals() {
        return literals;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("title", title)
                .append("description", description.isEmpty() ? "<empty>" : description)
                .append("created", created.isEmpty() ? "<empty>" : created)
                .append("size", size.isEmpty() ? "<empty>" : size)
                .append("license", license.isEmpty() ? "<empty>" : license)
                .append("updated", updated.isEmpty() ? "<empty>" : updated)
                .append("tags", tags.isEmpty() ? "<empty>" : tags)
                .append("version", version.isEmpty() ? "<empty>" : version)
                .append("author", author.isEmpty() ? "<empty>" : author)
                .append("classes", classes.isEmpty()  ? "<empty>" : classes)
                .append("properties", properties.isEmpty()  ? "<empty>" : properties)
                .append("entities", entities.isEmpty()  ? "<empty>" : entities)
                .append("literals", literals.isEmpty()  ? "<empty>" : literals);
        return tsb.toString();
    }

}


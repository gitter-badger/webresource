package com.github.t1.webresource.codec;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

import lombok.Data;

import com.github.t1.webresource.meta.*;

/**
 * This is the super class and context of the various html writers. Does two things, but the code turns out nicer than
 * when passing a context around... maybe we should use CDI instead?
 */
public class AbstractHtmlWriter {
    @Data
    protected static class Attribute {
        private final String name;
        private final String value;
    }

    protected class Tag implements AutoCloseable {
        private final String name;

        public Tag(String name, Attribute... attributes) throws IOException {
            this.name = name;
            out.append('<').append(name);
            for (Attribute attribute : attributes)
                out.append(' ').append(attribute.name).append("='").append(attribute.value).append('\'');
            out.append(">");
        }

        @Override
        public void close() throws IOException {
            out.append("</").append(name).append(">\n");
        }
    }

    private final Writer out;
    private final URI baseUri;
    private final Map<String, Integer> ids;

    /** Constructs the initial or root context. */
    public AbstractHtmlWriter(Writer out, URI baseUri) {
        this.out = out;
        this.baseUri = baseUri;
        this.ids = new HashMap<String, Integer>();
    }

    /** Constructs a sub-context by copying reusable fields. */
    public AbstractHtmlWriter(AbstractHtmlWriter context) {
        this.out = context.out;
        this.baseUri = context.baseUri;
        this.ids = context.ids;
    }

    public void writeHead(Item item) throws IOException {
        new HtmlHeadWriter(this, item).write();
    }

    public void writeBody(Item item) throws IOException {
        new HtmlBodyWriter(this, item).write();
    }

    public void writeForm(Item item) throws IOException {
        new HtmlFormWriter(this, item).write();
    }

    public void writeList(Item listItem) throws IOException {
        new HtmlListWriter(this, listItem).write();
    }

    public void writeField(Item item, Trait trait, String id) throws IOException {
        new HtmlFieldWriter(this, item, trait, id).write();
    }

    public void writeLink(Item item, String id) throws IOException {
        new HtmlLinkWriter(this, item, id).write();
    }

    public void writeTable(List<Item> list, Collection<Trait> traits) throws IOException {
        new HtmlTableWriter(this, list, traits).write();
    }

    protected void write(String text) {
        try {
            out.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void write(Object object) {
        if (object != null) {
            write(object.toString());
        }
    }

    protected void write(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String line = readLine(reader);
            if (line == null)
                break;
            write(line);
            nl();
        }
    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void write(Exception e) {
        e.printStackTrace(new PrintWriter(out));
    }

    protected Writer escaped() {
        return new HtmlEscapeWriter(out);
    }

    protected void nl() {
        write("\n");
    }

    /**
     * The path of the JAX-RS base-uri contains the resource base (often 'rest'), but we need the application base,
     * which is the first path element.
     */
    public Path applicationPath() {
        return Paths.get(baseUri.getPath()).getName(0);
    }

    /**
     * Resolve the given URI against the application base, i.e.
     * <ul>
     * <li>if the given uri is absolut (contains a protocol like http), use that
     * <li>if the given uri path starts with a slash, use the same host, but nothing from the application path, or
     * <li>if the given uri does not start with a slash, resolve within the application.
     * </ul>
     * 
     * @see HtmlStyleSheet
     */
    public URI resolveApp(URI uri) {
        if (uri.isAbsolute())
            return uri;
        if (uri.getPath() == null)
            throw new IllegalArgumentException("the given uri has no path: " + uri);
        if (uri.getPath().startsWith("/")) {
            return baseUri.resolve(uri.getPath());
        } else {
            Path path = Paths.get(baseUri.getPath()).subpath(0, 1).resolve(uri.getPath());
            return baseUri.resolve("/" + path);
        }
    }

    /**
     * Resolve the given path against the base uri (i.e. including the application and the 'rest' or 'resource' path
     * elements)
     */
    public URI resolveBase(String path) {
        return URI.create(baseUri + path);
    }

    protected String name(Trait trait) {
        if (trait.is(HtmlFieldName.class))
            return trait.get(HtmlFieldName.class).value();
        return trait.name();
    }

    protected String id(String name) {
        Integer i = ids.get(name);
        if (i == null)
            i = 0;
        ids.put(name, i + 1);
        return name + "-" + i;
    }
}

package com.github.t1.webresource.codec;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

import lombok.*;

import com.github.t1.webresource.meta.*;

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

    @Delegate
    private final Writer out;
    private final URI baseUri;
    private final Map<String, Integer> ids;

    public AbstractHtmlWriter(Writer out, URI baseUri) {
        this.out = out;
        this.baseUri = baseUri;
        this.ids = new HashMap<String, Integer>();
    }

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

    public void writeList(List<Item> list, Trait trait) throws IOException {
        new HtmlListWriter(this, list, trait).write();
    }

    public void writeField(Item item, Trait trait, String id) throws IOException {
        new HtmlFieldWriter(this, item, trait, id).write();
    }

    public void writeTable(List<Item> list, List<Trait> traits) throws IOException {
        new HtmlTableWriter(this, list, traits).write();
    }

    protected void write(Exception e) {
        e.printStackTrace(new PrintWriter(out));
    }

    protected Writer escaped() {
        return new HtmlEscapeWriter(out);
    }

    protected void nl() throws IOException {
        out.append('\n');
    }

    /**
     * The path of the JAX-RS base-uri starts with the resource base (often 'rest'), but we need the application base,
     * which is the first path element.
     */
    public Path applicationPath() {
        return Paths.get(baseUri.getPath()).getName(0);
    }

    /**
     * Resolve the given URI.
     * 
     * @see HtmlStyleSheet
     */
    public URI resolve(URI uri) {
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

    protected String id(String name) {
        Integer i = ids.get(name);
        if (i == null)
            i = 0;
        ids.put(name, i + 1);
        return name + "-" + i;
    }
}

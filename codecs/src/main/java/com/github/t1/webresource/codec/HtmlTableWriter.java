package com.github.t1.webresource.codec;

import static com.github.t1.webresource.meta.SimpleTrait.*;

import java.io.IOException;
import java.util.List;

import com.github.t1.webresource.meta.*;

public class HtmlTableWriter extends AbstractHtmlWriter {
    private final List<Item> list;
    private final List<Trait> traits;

    public HtmlTableWriter(AbstractHtmlWriter context, List<Item> list, List<Trait> traits) {
        super(context);
        this.list = list;
        this.traits = traits;
    }


    public void write() throws IOException {
        try (Tag table = new Tag("table")) {
            try (Tag thead = new Tag("thead")) {
                writeTableHead(traits);
            }
            try (Tag tbody = new Tag("tbody")) {
                for (Item element : list) {
                    writeTableRow(element, traits);
                }
            }
        }
    }

    private void writeTableHead(List<Trait> traits) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Trait trait : traits) {
                try (Tag th = new Tag("th")) {
                    escaped().append(trait.name());
                }
            }
        }
    }

    // this duplicates a lot of writeTableHead... closures would be nice, here ;-)
    private void writeTableRow(Item rowItem, List<Trait> traits) throws IOException {
        try (Tag tr = new Tag("tr")) {
            for (Trait trait : traits) {
                try (Tag td = new Tag("td")) {
                    Item cellItem = rowItem.get(trait);
                    String name = trait.name();
                    String id = id(name);
                    if (cellItem.isSimple()) {
                        writeField(cellItem, SIMPLE, id);
                    } else if (cellItem.isList()) {
                        writeList(cellItem.getList(), SIMPLE);
                    } else {
                        writeLink(cellItem, id, "");
                    }
                }
            }
        }
    }
}

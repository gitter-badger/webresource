package com.github.t1.webresource.codec;

import javax.inject.Inject;

import com.github.t1.webresource.codec.HtmlOut.Attribute;
import com.github.t1.webresource.codec.HtmlOut.Tag;
import com.github.t1.webresource.meta.*;

public class HtmlFormWriter {

    @Inject
    HtmlOut out;
    @Inject
    UriResolver uriResolver;
    @Inject
    IdGenerator ids;
    @Inject
    HtmlListWriter listWriter;
    @Inject
    HtmlFieldWriter fieldWriter;
    @Inject
    HtmlLinkWriter linkWriter;

    public void write(Item item) {
        try (Tag form = out.tag("form", //
                new IdAttribute(item, "form"), //
                new ActionAttribute(uriResolver, item), //
                new Attribute("method", "post") //
                )) {
            Trait idTrait = HtmlId.of(item).trait();
            if (idTrait != null) {
                out.nl();
                out.write("<input name=\"" + idTrait.name() + "\" type=\"hidden\" value=\"" + item.read(idTrait)
                        + "\"/>");
            }
            out.nl();
            for (Trait trait : item.traits()) {
                writeFormDiv(item, trait);
            }
            out.write("<input type=\"submit\" value=\"submit\"/>");
            out.nl();
        }
    }

    private void writeFormDiv(Item item, Trait trait) {
        String id = ids.get(trait);
        try (Tag div = out.tag("div" /* TODO , new Attribute("class", name + "-item") */)) {
            try (Tag label = out.tag("label", new Attribute("for", id), new ClassAttribute(trait, "label"))) {
                out.writeEscapedObject(new FieldName(trait));
            }
            writeItem(item, trait, id);
        }
    }

    private void writeItem(Item item, Trait trait, String id) {
        Item value = item.read(trait);
        if (value.isSimple()) {
            fieldWriter.write(item, trait, id);
        } else if (value.isList()) {
            listWriter.write(value);
        } else {
            linkWriter.write(value, id);
        }
    }
}

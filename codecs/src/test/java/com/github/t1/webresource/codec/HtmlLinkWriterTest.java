package com.github.t1.webresource.codec;

import static org.junit.Assert.*;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;

import org.junit.Test;

import com.github.t1.webresource.WebResourceKey;
import com.github.t1.webresource.meta.Items;

public class HtmlLinkWriterTest extends AbstractHtmlWriterTest {
    private void write(Object pojo) {
        HtmlLinkWriter writer = new HtmlLinkWriter();
        writer.out = out;
        writer.uriResolver = uriResolver;
        writer.titleWriter = new HtmlTitleWriter();
        writer.write(Items.newItem(pojo), "id");
    }

    @Getter
    @AllArgsConstructor
    public static class SimplePojo {
        String str1;
        String str2;

        @Override
        public String toString() {
            return str1 + "-" + str2;
        }
    }

    @Test
    public void shouldWriteSimplePojo() throws Exception {
        SimplePojo pojo = new SimplePojo("one", "two");

        write(pojo);

        assertEquals("<a href='" + BASE_URI + "simplepojos/" + pojo + ".html' id='id-href' class='simplepojos'>" + pojo
                + "</a>", result());
    }

    @Getter
    @AllArgsConstructor
    public static class OneLinkFieldPojo {
        @HtmlTitle
        String str1;
        String str2;

        @Override
        public String toString() {
            return str1 + "-" + str2;
        }
    }

    @Test
    public void shouldWriteOneFieldTextLink() throws Exception {
        OneLinkFieldPojo pojo = new OneLinkFieldPojo("one", "two");

        write(pojo);

        assertEquals("<a href='" + BASE_URI + "onelinkfieldpojos/" + pojo
                + ".html' id='id-href' class='onelinkfieldpojos'>one</a>", result());
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @XmlRootElement
    public static class TextLinkWebResourceKeyPojo {
        @WebResourceKey
        String str1;
    }

    @Test
    public void shouldWriteWebResourceKeyLink() throws Exception {
        TextLinkWebResourceKeyPojo pojo = new TextLinkWebResourceKeyPojo("one");

        write(pojo);

        assertEquals("<a href='" + BASE_URI + "textlinkwebresourcekeypojos/one.html' "
                + "id='id-href' class='textlinkwebresourcekeypojos'>one</a>", result());
    }


    @Getter
    @AllArgsConstructor
    @XmlRootElement
    @HtmlTitle("${str1}-${str2}")
    public static class TextLinkVariablePojo {
        String str1;
        String str2;

        @Override
        public String toString() {
            return str1 + "-" + str2;
        }
    }

    @Test
    public void shouldWriteVariableLink() throws Exception {
        TextLinkVariablePojo pojo = new TextLinkVariablePojo("one", "two");

        write(pojo);

        assertEquals("<a href='" + BASE_URI + "textlinkvariablepojos/" + pojo
                + ".html' id='id-href' class='textlinkvariablepojos'>one-two</a>", result());
    }
}

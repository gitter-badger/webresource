package com.github.t1.webresource.codec;

import static org.junit.Assert.*;

import java.util.*;

import lombok.*;

import org.junit.*;

import com.github.t1.webresource.meta.Items;

public class HtmlHeadWriterTest extends AbstractHtmlWriterTest {
    private static final String TITLE = "<title>TITLE</title>";

    private void write(Object t) {
        HtmlHeadWriter writer = new HtmlHeadWriter();
        writer.out = out;
        writer.uriResolver = uriResolver;
        writer.titleWriter = new HtmlTitleWriter();
        writer.write(Items.newItem(t));
    }

    @AllArgsConstructor
    private static class PojoWithOneHtmlTitle {
        @HtmlTitle
        public String str;
        public Integer i;
    }

    @Test
    public void shouldWritePojoWithOneHtmlTitle() {
        PojoWithOneHtmlTitle pojo = new PojoWithOneHtmlTitle("dummy", 123);
        assertEquals(123, (int) pojo.i); // cover

        write(pojo);

        assertEquals("<title>dummy</title>", result());
    }

    @AllArgsConstructor
    private static class PojoWithTwoHtmlTitles {
        @HtmlTitle
        public String str0;
        @HtmlTitle
        public String str1;
    }

    @Test
    public void shouldWritePojoWithTwoHtmlTitle() {
        PojoWithTwoHtmlTitles pojo = new PojoWithTwoHtmlTitles("dummy0", "dummy1");

        write(pojo);

        assertEquals("<title>dummy0 dummy1</title>", result());
    }

    @Data
    @HtmlTitle("TITLE")
    @AllArgsConstructor
    @HtmlStyleSheet("/root-path")
    private static class PojoWithRootPathCss {
        private String str;
    }

    @Test
    public void shouldLinkRootPathCssStyleSheet() {
        PojoWithRootPathCss pojo = new PojoWithRootPathCss("dummy");

        write(pojo);

        assertEquals(TITLE + stylesheet("/root-path"), result());
    }

    private String stylesheet(String href) {
        return "<link rel='stylesheet' href='" + href + "' type='text/css'/>";
    }

    @Test
    public void shouldWritePojoListWithRootPathCssStyleSheet() {
        PojoWithRootPathCss pojo1 = new PojoWithRootPathCss("a");
        PojoWithRootPathCss pojo2 = new PojoWithRootPathCss("b");
        List<PojoWithRootPathCss> list = Arrays.asList(pojo1, pojo2);

        write(list);

        assertEquals("<title>List of pojowithrootpathcsss</title>" + stylesheet("/root-path"), result());
    }

    @Data
    @HtmlTitle("TITLE")
    @AllArgsConstructor
    @HtmlStyleSheet(value = "file:src/test/resources/testfile.txt", inline = true)
    private static class PojoWithInlineFileCss {
        private String str;
    }

    @Test
    public void shouldInlineFileCssStyleSheet() {
        PojoWithInlineFileCss pojo = new PojoWithInlineFileCss("dummy");

        write(pojo);

        assertEquals(TITLE + "<style>test-file-contents</style>", result());
    }

    @Data
    @HtmlTitle("TITLE")
    @AllArgsConstructor
    @HtmlStyleSheet(value = "/stylesheets/main.css", inline = true)
    private static class PojoWithInlineRootResourceCss {
        private String str;
    }

    @Test
    @Ignore("needs a running service and I have no idea how to map that to a file-url")
    public void shouldInlineRootResourceCssStyleSheet() {
        PojoWithInlineRootResourceCss pojo = new PojoWithInlineRootResourceCss("dummy");

        write(pojo);

        assertEquals(TITLE + "<style>test-file-contents</style>", result());
    }

    @Data
    @HtmlTitle("TITLE")
    @AllArgsConstructor
    @HtmlStyleSheet(value = "stylesheets/main.css", inline = true)
    private static class PojoWithInlineLocalResourceCss {
        private String str;
    }

    @Test
    @Ignore("needs a running service and I have no idea how to map that to a file-url")
    public void shouldInlineLocalResourceCssStyleSheet() {
        PojoWithInlineLocalResourceCss pojo = new PojoWithInlineLocalResourceCss("dummy");

        write(pojo);

        assertEquals(TITLE + "<style>test-file-contents</style>", result());
    }

    @Data
    @HtmlTitle("TITLE")
    @AllArgsConstructor
    @HtmlStyleSheet("local-path")
    private static class PojoWithLocalCss {
        private String str;
    }

    @Test
    public void shouldLinkLocalCssStyleSheet() {
        PojoWithLocalCss pojo = new PojoWithLocalCss("dummy");

        write(pojo);

        assertEquals(TITLE + stylesheet("/demo/local-path"), result());
    }

    @Data
    @HtmlTitle("TITLE")
    @AllArgsConstructor
    @HtmlStyleSheets({ @HtmlStyleSheet("/root-path"), @HtmlStyleSheet("local-path") })
    private static class PojoWithTwoCss {
        private String str;
    }

    @Test
    public void shouldLinkTwoCssStyleSheets() {
        PojoWithTwoCss pojo = new PojoWithTwoCss("dummy");

        write(pojo);

        assertEquals(TITLE + stylesheet("/root-path") + stylesheet("/demo/local-path"), result());
    }
}

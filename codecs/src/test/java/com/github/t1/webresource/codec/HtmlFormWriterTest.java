package com.github.t1.webresource.codec;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.*;

import javax.persistence.Id;

import lombok.*;

import org.junit.Test;

import com.google.common.collect.*;

public class HtmlFormWriterTest extends AbstractHtmlWriterTest {
    private static String wrappedForm(String string) {
        return wrapped("<form>" + string + "</form>");
    }

    @Test
    public void shouldEncodeEmptyMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();

        writer(map).write();

        assertEquals(wrappedForm(""), result());
    }

    @Test
    public void shouldEncodeOneKeyMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");

        writer(map).write();

        assertEquals(wrappedForm(field("one", "111")), result());
    }

    @Test
    public void shouldEncodeMap() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");

        writer(map).write();

        assertEquals(wrappedForm(field("one", "111") + field("two", "222") + field("three", "333")), result());
    }

    private String field(String name, String value) {
        return field(name, value, 0);
    }

    private String field(String name, String value, int id) {
        return field(name, value, id, "string", "text");
    }

    private String field(String name, String value, int id, String cssClass, String type) {
        return div(label(name, id) //
                + "<input id='" + name + "-" + id + "' class='" + cssClass + "' type='" + type + "' value='"
                + value
                + "' readonly/>" //
        );
    }

    private String label(String name) {
        return label(name, 0);
    }

    private String label(String name, int id) {
        return "<label for='" + name + "-" + id + "' class='" + name + "-label'>" + name + "</label>";
    }

    @Test
    public void shouldEncodeOneStringPojoWithoutKey() throws Exception {
        OneStringPojo pojo = new OneStringPojo("str");

        writer(pojo).write();

        assertEquals(wrappedForm(field("string", "str")), result());
    }

    @Test
    public void shouldEncodeOneStringPojoNullValue() throws Exception {
        OneStringPojo pojo = new OneStringPojo(null);

        writer(pojo).write();

        assertEquals(wrappedForm(field("string", "")), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoWithoutKey() throws Exception {
        OneStringInputNamedPojo pojo = new OneStringInputNamedPojo("str");

        writer(pojo).write();

        assertEquals(wrappedForm(field("foo", "str", 0, "string", "text")), result());
    }

    @Test
    public void shouldEncodeOneStringInputNamedPojoNullValue() throws Exception {
        OneStringInputNamedPojo pojo = new OneStringInputNamedPojo(null);

        writer(pojo).write();

        assertEquals(wrappedForm(field("foo", "", 0, "string", "text")), result());
    }

    @Data
    @AllArgsConstructor
    private static class OneStringInputTypedPojo {
        @HtmlInputType("test")
        private String string;
    }

    @Test
    public void shouldEncodeOneStringInputTypedPojoWithoutKey() throws Exception {
        OneStringInputTypedPojo pojo = new OneStringInputTypedPojo("str");

        writer(pojo).write();

        assertEquals(wrappedForm(field("string", "str", 0, "string", "test")), result());
    }

    @Test
    public void shouldEncodeTwoFieldPojoAsSequenceOfDivsWithLabelsAndReadonlyInputs() throws Exception {
        TwoFieldPojo pojo = new TwoFieldPojo("dummy", 123);

        writer(pojo).write();

        assertThat(result(), containsString(field("i", "123", 0, "number", "text")));
        assertThat(result(), containsString(field("str", "dummy")));
    }

    @Data
    @AllArgsConstructor
    private static class TwoFieldsOneBooleanPojo {
        private boolean b;
        private String str;
    }

    @Test
    public void shouldEncodeOneBooleanPojoWithoutKey() throws Exception {
        TwoFieldsOneBooleanPojo pojo = new TwoFieldsOneBooleanPojo(true, "dummy");

        writer(pojo).write();

        assertThat(result(), containsString(field("b", "true", 0, "boolean", "checkbox")));
        assertThat(result(), containsString(field("str", "dummy")));
    }

    @Data
    @AllArgsConstructor
    private static class SetPojo {
        private String str;
        private Set<String> set;
    }

    @Test
    public void shouldEncodeSetPojo() throws Exception {
        SetPojo pojo = new SetPojo("dummy", ImmutableSet.of("one", "two", "three"));

        writer(pojo).write();

        assertThat(result(), containsString(field("str", "dummy")));
        assertThat(result(), containsString(div(label("set", 0) + ul("strings", "one", "two", "three"))));
    }

    @Test
    public void shouldEncodeListPojo() throws Exception {
        ListPojo pojo = new ListPojo("dummy", ImmutableList.of("one", "two", "three"));

        writer(pojo).write();

        assertThat(result(), containsString(field("str", "dummy")));
        assertThat(result(), containsString(div(label("list") + ul("strings", "one", "two", "three"))));
    }

    @Test
    public void shouldEncodeNestedPojo() throws Exception {
        ContainerPojo pojo = new ContainerPojo("dummy", new NestedPojo("foo", 123));

        writer(pojo).write();

        assertThat(
                result(),
                containsString(div(label("nested")
                        + a("href='" + BASE_URI + "nestedpojos/123.html' id='nested-0-href' class='nestedpojos'", "foo"))));
        assertThat(result(), containsString(field("str", "dummy")));
    }

    @Data
    @AllArgsConstructor
    private static class LinkNestedPojo {
        @Id
        public String ref;
        @HtmlLinkText
        public String body;
    }

    @Data
    @AllArgsConstructor
    private static class LinkContainerPojo {
        private LinkNestedPojo nested;
    }

    @Test
    public void shouldEncodeLinkNestedPojo() throws Exception {
        LinkContainerPojo pojo = new LinkContainerPojo(new LinkNestedPojo("foo", "bar"));

        writer(pojo).write();

        assertThat(
                result(),
                containsString(div(label("nested")
                        + a("href='" + BASE_URI
                                + "linknestedpojos/foo.html' id='nested-0-href' class='linknestedpojos'", "bar"))));
    }
}

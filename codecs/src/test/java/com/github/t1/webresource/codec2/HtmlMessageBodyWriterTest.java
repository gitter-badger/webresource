package com.github.t1.webresource.codec2;

import static com.github.t1.webresource.codec2.UriInfoMockProducer.*;
import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.*;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Value;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.t1.webresource.html.Html;
import com.github.t1.webresource.meta2.*;

@RunWith(Arquillian.class)
public class HtmlMessageBodyWriterTest {
    @Deployment
    public static JavaArchive loggingInterceptorDeployment() {
        return ShrinkWrap.create(JavaArchive.class) //
                .addPackage(HtmlMessageBodyWriter.class.getPackage()) //
                .addPackage(Accessors.class.getPackage()) //
                .addPackage(Html.class.getPackage()) //
                .addPackage(UriInfo.class.getPackage()) //
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml") //
        ;
    }

    @Inject
    private HtmlMessageBodyWriter writer;
    @Inject
    private MetaDataStore meta;

    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    private String html(String title, String body) {
        return "<html>\n" //
                + "<head><title>" + title + "</title>\n" //
                + "</head>\n" //
                + "<body>\n" //
                + "<h1>" + title + "</h1>\n" //
                + body //
                + "</body>\n" //
                + "</html>\n";
    }

    @Test
    public void shouldProduceHtmlFromLink() {
        URI uri = URI.create("http://example.com/");
        when(uriInfo.getBaseUri()).thenReturn(uri);
        meta.put(uri, new UriMetaData("some link"));

        writer.writeTo(uri, URI.class, null, null, null, null, stream);

        assertEquals(html("some link", "<a href=\"http://example.com/\">some link</a>\n"), stream.toString());
    }

    @Test
    public void shouldProduceHtmlFromList() {
        List<String> list = asList("one", "two", "three");
        meta.put(list, new ListMetaData("list-of-strings"));

        writer.writeTo(list, List.class, null, null, null, null, stream);

        assertEquals(html("list-of-strings", "<ul>\n" //
                + "<li>one</li>\n" //
                + "<li>two</li>\n" //
                + "<li>three</li>\n" //
                + "</ul>\n" //
        ), stream.toString());
    }

    @Test
    public void shouldProduceHtmlFromListInList() {
        List<String> list1 = asList("one", "two");
        List<String> list2 = asList("three", "four");
        List<List<String>> list = asList(list1, list2);
        meta.put(list, new ListMetaData("list-of-lists"));

        writer.writeTo(list, List.class, null, null, null, null, stream);

        assertEquals(html("list-of-lists", "<ul>\n" //
                + "<li>" //
                + "<ul>\n" //
                + "<li>one</li>\n" //
                + "<li>two</li>\n" //
                + "</ul>\n" //
                + "</li>\n" //
                //
                + "<li>" //
                + "<ul>\n" //
                + "<li>three</li>\n" //
                + "<li>four</li>\n" //
                + "</ul>\n" //
                + "</li>\n" //
                //
                + "</ul>\n" //
        ), stream.toString());
    }

    @Test
    public void shouldProduceHtmlFromMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("one", "111");
        map.put("two", "222");
        map.put("three", "333");
        meta.put(map, new MapMetaData("some map", "some key", "some value"));

        writer.writeTo(map, Map.class, null, null, null, null, stream);

        assertEquals(html("some map", "<table>\n" //
                + "<tr>\n" //
                + "<td>some key</td>\n" //
                + "<td>some value</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>one</td>\n" //
                + "<td>111</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>two</td>\n" //
                + "<td>222</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>three</td>\n" //
                + "<td>333</td>\n" //
                + "</tr>\n" //
                + "</table>\n" //
        ), stream.toString());
    }

    @Value
    public static class Pojo {
        String one, two, three;

        @Override
        public String toString() {
            return "Pojo[" + one + "]";
        }
    }

    @Test
    public void shouldProduceHtmlFromPojo() {
        Pojo pojo = new Pojo("111", "222", "333");
        // meta.put(map, new MapMetaData("some map", "some key", "some value"));

        writer.writeTo(pojo, Pojo.class, null, null, null, null, stream);

        assertEquals(html("Pojo[111]", "<table>\n" //
                + "<tr>\n" //
                + "<td>one</td>\n" //
                + "<td>111</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>two</td>\n" //
                + "<td>222</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>three</td>\n" //
                + "<td>333</td>\n" //
                + "</tr>\n" //
                + "</table>\n" //
        ), stream.toString());
    }

    @Test
    public void shouldProduceHtmlFromMapInMap() {
        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("one", "111");
        meta.put(map1, new MapMetaData("map1", "key1", "value1"));

        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("two", "222");
        meta.put(map2, new MapMetaData("map2", "key2", "value2"));

        Map<String, Map<String, String>> map = new LinkedHashMap<>();
        map.put("foo", map1);
        map.put("bar", map2);
        meta.put(map, new MapMetaData("map", "key", "value"));

        writer.writeTo(map, Map.class, null, null, null, null, stream);

        assertEquals(html("map", "<table>\n" //
                + "<tr>\n" //
                + "<td>key</td>\n" //
                + "<td>value</td>\n" //
                + "</tr>\n" //
                //
                + "<tr>\n" //
                + "<td>foo</td>\n" //
                + "<td>" //
                //
                + "<table>\n" //
                + "<tr>\n" //
                + "<td>key1</td>\n" //
                + "<td>value1</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>one</td>\n" //
                + "<td>111</td>\n" //
                + "</tr>\n" //
                + "</table>\n" //
                //
                + "</td>\n" //
                + "</tr>\n" //
                //
                + "<tr>\n" //
                + "<td>bar</td>\n" //
                + "<td>" //
                //
                + "<table>\n" //
                + "<tr>\n" //
                + "<td>key2</td>\n" //
                + "<td>value2</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>two</td>\n" //
                + "<td>222</td>\n" //
                + "</tr>\n" //
                + "</table>\n" //
                //
                + "</td>\n" //
                + "</tr>\n" //
                //
                + "</table>\n" //
        ), stream.toString());
    }

    @Test
    public void shouldProduceHtmlFromListOfPojo() {
        Pojo pojo0 = new Pojo("01", "02", "03");
        Pojo pojo1 = new Pojo("11", "12", "13");
        Pojo pojo2 = new Pojo("21", "22", "23");
        List<Pojo> list = asList(pojo0, pojo1, pojo2);
        meta.put(list, new ListMetaData("list-of-pojos"));

        writer.writeTo(list, List.class, null, null, null, null, stream);

        assertEquals(html("list-of-pojos", "<table>\n" //
                + "<tr>\n" //
                + "<td>one</td>\n" //
                + "<td>two</td>\n" //
                + "<td>three</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>01</td>\n" //
                + "<td>02</td>\n" //
                + "<td>03</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>11</td>\n" //
                + "<td>12</td>\n" //
                + "<td>13</td>\n" //
                + "</tr>\n" //
                + "<tr>\n" //
                + "<td>21</td>\n" //
                + "<td>22</td>\n" //
                + "<td>23</td>\n" //
                + "</tr>\n" //
                + "</table>\n" //
        ), stream.toString());
    }

    @Value
    public static class TransientPojo {
        String one;
        transient String two;
        @XmlTransient
        String three;

        @Override
        public String toString() {
            return "TransientPojo[" + one + "]";
        }
    }

    @Test
    public void shouldProduceHtmlFromTransientPojo() {
        TransientPojo pojo = new TransientPojo("111", "222", "333");

        writer.writeTo(pojo, TransientPojo.class, null, null, null, null, stream);

        assertEquals(html("TransientPojo[111]", "<table>\n" //
                + "<tr>\n" //
                + "<td>one</td>\n" //
                + "<td>111</td>\n" //
                + "</tr>\n" //
                + "</table>\n" //
        ), stream.toString());
    }
}

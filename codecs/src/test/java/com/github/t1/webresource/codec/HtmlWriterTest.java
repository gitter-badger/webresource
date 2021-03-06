package com.github.t1.webresource.codec;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import javax.enterprise.inject.Instance;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.t1.webresource.meta.*;

@RunWith(MockitoJUnitRunner.class)
public class HtmlWriterTest extends AbstractHtmlWriterTest {
    private static final String EXCEPTION_COMMENT = //
            "<!-- ............................................................" //
                    + "java.lang.RuntimeException" //
                    + "............................................................ -->";

    @Mock
    HtmlBodyWriter bodyWriter;
    @Mock
    HtmlHeadWriter headWriter;
    @Mock
    Instance<HtmlDecorator> decorators;
    @InjectMocks
    HtmlWriter writer;

    @Before
    public void before() {
        givenDecorators();
        writer.out = out;
    }

    private void givenDecorators(HtmlDecorator... decoratorArray) {
        when(decorators.iterator()).thenReturn(Arrays.asList(decoratorArray).iterator());
    }

    private void write(Object t) {
        writer.write(Items.newItem(t));
    }

    @Test
    public void shouldWriteBody() {
        doAnswer(writeAnswer("body")).when(bodyWriter).write(any(Item.class));

        write("dummy");

        assertEquals("<html><head></head><body>{body:dummy}</body></html>", result());
    }

    @Test
    public void shouldWriteHead() {
        doAnswer(writeAnswer("head")).when(headWriter).write(any(Item.class));

        write("dummy");

        assertEquals("<html><head>{head:dummy}</head><body></body></html>", result());
    }

    @Test
    public void shouldWriteExceptionInBody() {
        doThrow(RuntimeException.class).when(bodyWriter).write(any(Item.class));

        try {
            write("dummy");
            fail("expected RuntimeException");
        } catch (RuntimeException e) {
            // we don't check the exception but the html output
        }

        // TODO the error text should be IN the body
        assertEquals("<html><head></head><body></body>error writing body</html>" + EXCEPTION_COMMENT, result());
    }

    @Test
    public void shouldWriteExceptionInHead() {
        doThrow(RuntimeException.class).when(headWriter).write(any(Item.class));

        try {
            write("dummy");
            fail("expected RuntimeException");
        } catch (RuntimeException e) {
            // we don't check the exception but the html output
        }

        // TODO the error text should be IN the head
        assertEquals("<html><head></head>error writing head</html>" + EXCEPTION_COMMENT, result());
    }

    class Decorator implements HtmlDecorator {
        private final String decoration;

        public Decorator(String decoration) {
            this.decoration = decoration;
        }

        @Override
        public void decorate(Item item) {
            out.write(decoration);
        }
    }

    @Test
    public void shouldDecorateOne() {
        givenDecorators(new Decorator("decoration"));
        doAnswer(writeAnswer("body")).when(bodyWriter).write(any(Item.class));

        write("dummy");

        assertEquals("<html><head></head><body>{body:dummy}decoration</body></html>", result());
    }

    @Test
    public void shouldDecorateTwo() {
        givenDecorators(new Decorator("foo"), new Decorator("bar"));
        doAnswer(writeAnswer("body")).when(bodyWriter).write(any(Item.class));

        write("dummy");

        assertEquals("<html><head></head><body>{body:dummy}foobar</body></html>", result());
    }
}

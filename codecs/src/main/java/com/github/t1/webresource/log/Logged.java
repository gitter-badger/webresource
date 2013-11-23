package com.github.t1.webresource.log;

import static com.github.t1.webresource.log.LogLevel.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import javax.interceptor.InterceptorBinding;

/**
 * Logs the method invocation (the name of the method and the parameter values) and eventually the return value resp.
 * exception thrown. There are helpful defaults for the {@link #level() log-level}, the {@link #logger()}, and even the
 * {@link #value() message}.
 * <p>
 * Note that an interceptor is not called, when you call a method locally (not to mention calling a private method)
 * <p>
 * TODO find out and document how to call through the interceptor stack on self
 */
@InterceptorBinding
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface Logged {
    /**
     * The level of detail to log at.
     * 
     * @see org.slf4j.Logger the logging methods for those levels
     */
    public LogLevel level() default DEBUG;

    /**
     * The class used to create the logger. Defaults to the top level class containing the method being logged (i.e.
     * nested, inner, local, or anonymous classes are unwrapped).
     * 
     * @see Class#getEnclosingClass() the comment <i>in</i> <code>Class#getEnclosingClass</code>
     */
    public Class<?> logger() default void.class;

    /**
     * The format of the message to log. Defaults to a camel-case-to-space-separated string of the method name with the
     * space separated arguments appended. If you do provide a format, make sure to include enough placeholders ("{}")
     * for the arguments.
     */
    public String value() default "";
}

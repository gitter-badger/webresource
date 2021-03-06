package com.github.t1.webresource;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.Id;

/**
 * By default the {@link Id primary key} will be used for the REST boundary, but sometimes a different, business key
 * will be more suitable.
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface WebResourceKey {
    //
}

package com.github.t1.webresource.log;

import lombok.experimental.Value;

/**
 * A variable that can be added to the {@link org.slf4j.MDC}. If a producers returns <code>null</code>, nothing will be
 * added.
 */
@Value
public class LogContextVariable {
    String key, value;
}

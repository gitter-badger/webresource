package com.github.t1.webresource.log;

import java.util.Objects;

public class ToStringLogContextConverter implements LogContextConverter<Object> {
    @Override
    public String convert(Object object) {
        return Objects.toString(object);
    }
}

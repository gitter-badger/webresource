package com.github.t1.webresource.meta;

import static com.github.t1.webresource.meta.Converter.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.github.t1.stereotypes.Annotations;

public class PojoFieldTrait extends PojoTrait {

    private final Field field;

    public PojoFieldTrait(Field field, String name, Predicate<PojoTrait> visible) {
        super(Annotations.on(field), name, visible);
        this.field = field;
    }

    @Override
    protected Field member() {
        return field;
    }

    @Override
    Object read(Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't get " + name() + " of " + object, e);
        }
    }

    @Override
    void write(Object object, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, to(field.getType()).convert(value));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("can't set " + name() + " of " + object + " to " + value, e);
        }
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("field ").append(name());
        out.append(" of ").append(field.getDeclaringClass().getSimpleName());
        if (annotations.getAnnotations().length > 0) {
            out.append(": ");
            for (Annotation annotation : annotations.getAnnotations()) {
                out.append(annotation).append(" ");
            }
        }
        return out.toString();
    }

    @Override
    protected Class<?> typeClass() {
        return field.getType();
    }
}

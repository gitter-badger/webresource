package com.github.t1.webresource.meta;

import java.util.*;

public class Items {
    public static Item newItem(Object object) {
        return new Items(object).create();
    }

    private final Object object;

    public Items(Object object) {
        this.object = object;
    }

    private Item create() {
        if (isType())
            return new TypeItem(object);
        if (isSimple())
            return new SimplePojoItem(object);
        if (isMap())
            return new MapItem(object);
        if (isList())
            return new ListItem(object);
        return new PojoItem(object);
    }

    private boolean isType() {
        return Class.class.isInstance(object);
    }

    private boolean isSimple() {
        return isNull() || object instanceof String || object instanceof Number || object instanceof Boolean
                || object instanceof Character || object.getClass().isPrimitive() || object instanceof Date;
    }

    private boolean isNull() {
        return object == null;
    }

    private boolean isMap() {
        return object instanceof Map;
    }

    private boolean isList() {
        return object instanceof Collection;
    }
}

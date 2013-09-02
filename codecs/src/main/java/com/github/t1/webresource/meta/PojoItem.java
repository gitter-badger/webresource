package com.github.t1.webresource.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

import com.github.t1.stereotypes.Annotations;

public class PojoItem implements Item {
    private static final List<Trait> SIMPLE_TRAITS = Collections.<Trait> singletonList(SimpleTrait.SIMPLE);

    private final Object object;
    private final Class<?> type;
    private List<Trait> traits = null;
    private final AnnotatedElement annotations;

    public <T> PojoItem(Class<T> type, T object) {
        this.type = type;
        this.object = object;
        this.annotations = annotations(type, object);
    }

    private static <T> boolean isList(Class<T> type) {
        return List.class.isAssignableFrom(type);
    }

    private static <T> AnnotatedElement annotations(Class<T> type, T object) {
        if (type == null)
            return null;
        if (isMap(type))
            return null;
        if (isList(type)) {
            if (((List<?>) object).isEmpty()) {
                return new NullAnnotatedElement();
            } else {
                return Annotations.on(((List<?>) object).get(0).getClass());
            }
        }
        return Annotations.on(type);
    }

    private static boolean isMap(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    @Override
    public Object target() {
        return object;
    }

    @Override
    public boolean isList() {
        return isList(type);
    }

    @Override
    public List<Item> getList() {
        List<Item> result = new ArrayList<>();
        for (Object element : ((List<?>) object)) {
            result.add(Items.newItem(element));
        }
        return result;
    }

    @Override
    public List<Trait> traits() {
        if (traits == null) {
            if (isSimple()) {
                this.traits = SIMPLE_TRAITS;
            } else if (isMap(type)) {
                this.traits = mapTraits();
            } else if (isList(type)) {
                this.traits = new PojoTraits(type);
            } else {
                this.traits = new PojoTraits(type);
            }
        }
        return traits;
    }

    @Override
    public boolean isSimple() {
        return isNull() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class
                || type.isPrimitive();
    }

    @Override
    public boolean isNull() {
        return object == null;
    }

    private List<Trait> mapTraits() {
        List<Trait> traits = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>) object;
        for (String key : map.keySet()) {
            traits.add(new MapTrait(key));
        }
        return traits;
    }

    @Override
    public Object get(Trait trait) {
        return trait.of(this.object);
    }

    @Override
    public <A extends Annotation> boolean is(Class<A> type) {
        return (annotations == null) ? false : annotations.isAnnotationPresent(type);
    }

    @Override
    public <A extends Annotation> A get(Class<A> type) {
        return (annotations == null) ? null : annotations.getAnnotation(type);
    }

    @Override
    public Trait trait(String traitName) {
        for (Trait trait : traits()) {
            if (traitName.equals(trait.getName())) {
                return trait;
            }
        }
        throw new IllegalArgumentException("no trait " + traitName + " in " + type);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + type.getName() + "]";
    }
}

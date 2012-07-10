package com.github.t1.webresource;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;

import net.java.messageapi.processor.TemplateGenerator;

public class WebResourceGenerator extends TemplateGenerator {

    public WebResourceGenerator(Messager messager, ProcessingEnvironment env) {
        super(messager, env, "/WebResource.template");
    }

    @Override
    protected String getTargetTypeName() {
        return type.getQualifiedName() + "WebResource";
    }

    @Override
    protected String replaceVariable(String variable) {
        if ("simple".equals(variable)) {
            return getSimple();
        } else if ("lower".equals(variable)) {
            return getSimple().toLowerCase();
        } else if ("pkg".equals(variable)) {
            return getPackage();
        } else {
            throw new RuntimeException("invalid variable name " + variable);
        }
    }

    private String getSimple() {
        return type.getSimpleName().toString();
    }

    private String getPackage() {
        for (Element e = type; e != null; e = e.getEnclosingElement()) {
            if (e.getKind() == ElementKind.PACKAGE) {
                return ((PackageElement) e).getQualifiedName().toString();
            }
        }
        throw new IllegalStateException("no package for " + type);
    }
}

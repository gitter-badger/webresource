package com.github.t1.webresource.html;

public class UL extends Tag {
    public UL(Tag container) {
        super(container, "ul", true);
    }

    public LI li() {
        return new LI(this);
    }
}

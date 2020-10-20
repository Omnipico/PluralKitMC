package com.omnipico.pluralkitmc;

public class PluralKitProxy {
    String prefix = "";
    String suffix = "";

    public PluralKitProxy(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
        if (prefix == null) {
            this.prefix = "";
        }
        if (suffix == null) {
            this.suffix = "";
        }
    }

    public String getPrefix() {
        return prefix == null ? "" : prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix == null ? "" : suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}

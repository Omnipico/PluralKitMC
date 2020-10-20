package com.omnipico.pluralkitmc;

import java.util.List;

public class PluralKitMember {
    String id;
    String name;
    String color;
    String avatar_url;
    String birthday;
    String pronouns;
    String description;
    List<PluralKitProxy> proxy_tags;
    boolean keep_proxy;
    String created;

    public PluralKitMember(String id, String name, String color, String avatar_url, String birthday, String pronouns, String description, List<PluralKitProxy> proxy_tags, boolean keep_proxy, String created) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.avatar_url = avatar_url;
        this.birthday = birthday;
        this.pronouns = pronouns;
        this.description = description;
        this.proxy_tags = proxy_tags;
        this.keep_proxy = keep_proxy;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PluralKitProxy> getProxy_tags() {
        return proxy_tags;
    }

    public void setProxy_tags(List<PluralKitProxy> proxy_tags) {
        this.proxy_tags = proxy_tags;
    }

    public boolean isKeep_proxy() {
        return keep_proxy;
    }

    public void setKeep_proxy(boolean keep_proxy) {
        this.keep_proxy = keep_proxy;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

package com.omnipico.pluralkitmc;

import java.util.List;

public class PluralKitMember {
    String id;
    String uuid;
    String name;
    String display_name;
    String color;
    String birthday;
    String pronouns;
    String avatar_url;
    String banner;
    String description;
    String created;
    List<PluralKitProxy> proxy_tags;
    boolean keep_proxy;
    //PluralKitPrivacy privacy;

    public PluralKitMember( String id, String uuid, String name, String display_name, String color, String birthday,
            String pronouns, String avatar_url, String banner, String description, String created,
            List<PluralKitProxy> proxy_tags, boolean keep_proxy) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.display_name = display_name;
        this.color = color;
        this.pronouns = pronouns;
        this.birthday = birthday;
        this.avatar_url = avatar_url;
        this.banner = banner;
        this.description = description;
        this.created = created;
        this.proxy_tags = proxy_tags;
        this.keep_proxy = keep_proxy;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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
}

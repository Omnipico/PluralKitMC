package com.omnipico.pluralkitmc;

public class PluralKitSystem {
    String id;
    String uuid;
    String name;
    String description;
    String tag;
    String pronouns;
    String avatar_url;
    String banner;
    String color;
    String created;
    // PluralKitPrivacy privacy;

    public PluralKitSystem(String id, String uuid, String name, String description, String tag, String pronouns, String avatar_url, String banner, String color, String created) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.tag = tag;
        this.pronouns = pronouns;
        this.avatar_url = avatar_url;
        this.banner = banner;
        this.color = color;
        this.created = created;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

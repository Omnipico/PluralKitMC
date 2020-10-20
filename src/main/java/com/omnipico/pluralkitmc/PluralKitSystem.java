package com.omnipico.pluralkitmc;

public class PluralKitSystem {
    String id;
    String name;
    String description;
    String tag;
    String avatar_url;
    String tz;
    String created;

    public PluralKitSystem(String id, String name, String description, String tag, String avatar_url, String tz, String created) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tag = tag;
        this.avatar_url = avatar_url;
        this.tz = tz;
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

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

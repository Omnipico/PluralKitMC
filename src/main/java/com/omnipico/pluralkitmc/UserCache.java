package com.omnipico.pluralkitmc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class UserCache {
    UUID uuid;
    String systemId = "";
    String token = "";
    List<PluralKitMember> members = new ArrayList<>();
    PluralKitSystem system = null;
    long lastUpdated = 0;
    String autoProxyMode = "off";
    String autoProxyMember = null;
    String lastProxied = null;
    List<PluralKitMember> fronters = new ArrayList<>();
    JavaPlugin plugin;
    public UserCache(UUID uuid, String systemId, JavaPlugin plugin) {
        this.uuid = uuid;
        this.systemId = systemId;
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Update();
            }
        });
    }

    public UserCache(UUID uuid, String systemId, String token, JavaPlugin plugin) {
        this.uuid = uuid;
        this.systemId = systemId;
        this.token = token;
        this.plugin = plugin;
    }

    public void Update() {
        Date date = new Date();
        lastUpdated = date.getTime();
        updateSystem();
        updateMembers();
    }

    private void updateSystem() {
        URL url = null;
        try {
            url = new URL("https://api.pluralkit.me/v1/s/" + systemId);
        } catch (MalformedURLException e) {
            system = null;
        }
        InputStreamReader reader = null;
        try {
            assert url != null;
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            if (token != null) {
                conn.setRequestProperty("Authorization", token);
            }
            conn.addRequestProperty("User-Agent", "PluralKitMC");
            reader = new InputStreamReader(conn.getInputStream());
        } catch (IOException e) {
            system = null;
        }
        if (reader != null) {
            system = new Gson().fromJson(reader, PluralKitSystem.class);
        } else {
            system = null;
        }
    }

    private void updateMembers() {
        URL url = null;
        try {
            url = new URL("https://api.pluralkit.me/v1/s/" + systemId + "/members");
        } catch (MalformedURLException e) {
            members = new ArrayList<PluralKitMember>();
        }
        InputStreamReader reader = null;
        try {
            assert url != null;
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            if (token != null) {
                conn.setRequestProperty("Authorization", token);
            }
            conn.addRequestProperty("User-Agent", "PluralKitMC");
            reader = new InputStreamReader(conn.getInputStream());
        } catch (IOException e) {
            members = new ArrayList<PluralKitMember>();
        }
        Type listType = new TypeToken<List<PluralKitMember>>(){}.getType();
        if (reader != null) {
            members = new Gson().fromJson(reader, listType);
        } else {
            members = new ArrayList<PluralKitMember>();
        }
    }

    void UpdateIfNeeded(long updateFrequency) {
        Date date = new Date();
        long now = date.getTime();
        if (now >= (lastUpdated + updateFrequency)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    Update();
                }
            });
        }
    }

    public boolean verifyToken(String token) {
        URL url = null;
        try {
            url = new URL("https://api.pluralkit.me/v1/s/");
        } catch (MalformedURLException e) {
            return false;
        }
        InputStreamReader reader = null;
        try {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            if (token != null) {
                conn.setRequestProperty("Authorization", token);
            }
            conn.addRequestProperty("User-Agent", "PluralKitMC");
            reader = new InputStreamReader(conn.getInputStream());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public List<PluralKitMember> getMembers() {
        return members;
    }

    public PluralKitMember getMemberById(String id) {
        for (int i = 0; i < members.size(); i++) {
            PluralKitMember member = members.get(i);
            if (member.id.toLowerCase().equals(id.toLowerCase())) {
                return member;
            }
        }
        return null;
    }

    public PluralKitMember getMemberByIdOrName(String id) {
        for (int i = 0; i < members.size(); i++) {
            PluralKitMember member = members.get(i);
            if (member.id.toLowerCase().equals(id.toLowerCase()) || member.name.toLowerCase().equals(id.toLowerCase())) {
                return member;
            }
        }
        return null;
    }

    public PluralKitMember getRandomMember() {
        if (members != null && members.size() > 0)
        {
            return members.get(new Random().nextInt(members.size()));
        }
        return null;
    }

    public List<PluralKitMember> searchMembers(String search) {
        List<PluralKitMember> result = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            PluralKitMember member = members.get(i);
            if (member.name.toLowerCase().contains(search.toLowerCase())) {
                result.add(member);
            }
        }
        return result;
    }

    public PluralKitSystem getSystem() {
        return system;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAutoProxyMode() {
        return autoProxyMode;
    }

    public void setAutoProxyMode(String autoProxyMode) {
        this.autoProxyMode = autoProxyMode;
    }

    public String getAutoProxyMember() {
        return autoProxyMember;
    }

    public void setAutoProxyMember(String autoProxyMember) {
        this.autoProxyMember = autoProxyMember;
    }

    public List<PluralKitMember> getFronters() {
        return fronters;
    }

    public void setFronters(List<PluralKitMember> fronters) {
        this.fronters = fronters;
        if (this.token != null) {
            //TODO: Register switch with PluralKit API
        }
    }

    public PluralKitMember getFirstFronter() {
        if (fronters.size() > 0) {
            return fronters.get(0);
        } else {
            return null;
        }
    }

    public String getLastProxied() {
        return lastProxied;
    }

    public void setLastProxied(String lastProxied) {
        this.lastProxied = lastProxied;
    }
}

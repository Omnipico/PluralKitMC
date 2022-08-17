package com.omnipico.pluralkitmc;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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
    boolean updateMembersToggle = false;
    String autoProxyMode = "off";
    String autoProxyMember = null;
    String lastProxied = null;
    List<PluralKitMember> fronters = new ArrayList<>();
    PluralKitMC plugin;
    FileConfiguration config;

    public UserCache(UUID uuid, String systemId, PluralKitMC plugin, boolean blocking) {
        this.uuid = uuid;
        this.systemId = systemId;
        this.plugin = plugin;
        config = plugin.getConfig();
        loadFromConfig();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                update(blocking);
            }
        });
    }

    public UserCache(UUID uuid, String systemId, String token, PluralKitMC plugin, boolean blocking) {
        this.uuid = uuid;
        this.systemId = systemId;
        this.token = token;
        this.plugin = plugin;
        config = plugin.getConfig();
        loadFromConfig();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                update(blocking);
            }
        });
    }

    public void update(boolean blocking) {
        Date date = new Date();
        lastUpdated = date.getTime();
        // Alternate between updating members and system if this is not blocking (to avoid hitting rate limits)
        if (blocking) {
            updateSystem(true);
            updateMembers(true);
        } else if (updateMembersToggle) {
            updateMembers(false);
            updateMembersToggle = !updateMembersToggle;
        } else {
            updateSystem(false);
            updateMembersToggle = !updateMembersToggle;
        }
        if (config.getBoolean("cache_data", false)) {
            Gson gson = new Gson();
            config.set("players." + uuid.toString() + ".system_cache", gson.toJson(system));
            config.set("players." + uuid.toString() + ".members_cache", gson.toJson(members));
            plugin.saveConfig();
        }
    }

    private void loadFromConfig() {
        autoProxyMode = config.getString("players." + uuid.toString() + ".ap_mode", "off");
        autoProxyMember = config.getString("players." + uuid.toString() + ".ap_member", "");
        if (config.getBoolean("cache_data", false)) {
            if (config.contains("players." + uuid.toString() + ".system_cache")) {
                system = new Gson().fromJson(config.getString("players." + uuid.toString() + ".system_cache"), PluralKitSystem.class);
            }
            if (config.contains("players." + uuid.toString() + ".members_cache")) {
                Type listType = new TypeToken<List<PluralKitMember>>() {
                }.getType();
                members = new Gson().fromJson(config.getString("players." + uuid.toString() + ".members_cache"), listType);
            }
        }
    }

    private void updateSystem(boolean blocking) {
        if (blocking) {
            try {
                plugin.apiSemaphore.acquire();
            } catch (InterruptedException e) {
                plugin.getLogger().warning("System update was interrupted!");
                return;
            }
        } else {
            boolean acquired = plugin.apiSemaphore.tryAcquire();
            if (!acquired) {
                return;
            }
        }
        URL url = null;
        try {
            url = new URL("https://api.pluralkit.me/v2/systems/" + systemId);
        } catch (MalformedURLException e) {
            system = null;
        }
        InputStreamReader reader;
        HttpsURLConnection conn = null;
        try {
            assert url != null;
            conn = (HttpsURLConnection) url.openConnection();
            if (token != null) {
                conn.setRequestProperty("Authorization", token);
            }
            conn.addRequestProperty("User-Agent", "PluralKitMC");
            reader = new InputStreamReader(conn.getInputStream());
        } catch (IOException e) {
            if (conn != null && conn.getErrorStream() != null) {
                InputStreamReader errorReader = new InputStreamReader(conn.getErrorStream());
                PluralKitError pkError = new Gson().fromJson(errorReader, PluralKitError.class);
                if (pkError.retryAfter != null) {
                    // Hit a rate limit
                    // Unfortunately, we can't really disable the semaphore
                    plugin.getLogger().severe("Hit a rate limit while fetching system info for " + systemId);
                }
            } else {
                plugin.getLogger().warning("Failed to get system info for " + systemId);
            }
            if (members == null) {
                members = new ArrayList<>();
            }
            return;
        }
        system = new Gson().fromJson(reader, PluralKitSystem.class);
    }

    private void updateMembers(boolean blocking) {
        if (blocking) {
            try {
                plugin.apiSemaphore.acquire();
            } catch (InterruptedException e) {
                plugin.getLogger().warning("Member update was interrupted!");
                return;
            }
        } else {
            boolean acquired = plugin.apiSemaphore.tryAcquire();
            if (!acquired) {
                return;
            }
        }
        URL url = null;
        try {
            url = new URL("https://api.pluralkit.me/v2/systems/" + systemId + "/members");
        } catch (MalformedURLException e) {
            members = new ArrayList<>();
        }
        InputStreamReader reader;
        HttpsURLConnection conn = null;
        try {
            assert url != null;
            conn = (HttpsURLConnection) url.openConnection();
            if (token != null) {
                conn.setRequestProperty("Authorization", token);
            }
            conn.addRequestProperty("User-Agent", "PluralKitMC");
            reader = new InputStreamReader(conn.getInputStream());
        } catch (IOException e) {
            if (conn != null) {
                InputStreamReader errorReader = new InputStreamReader(conn.getErrorStream());
                PluralKitError pkError = new Gson().fromJson(errorReader, PluralKitError.class);
                if (pkError.retryAfter != null) {
                    // Hit a rate limit
                    // Unfortunately, we can't really disable the semaphore
                    plugin.getLogger().severe("Hit a rate limit while fetching system members for " + systemId);
                }
            } else {
                plugin.getLogger().warning("Failed to get system members for " + systemId);
            }
            if (members == null) {
                members = new ArrayList<>();
            }
            return;
        }
        Type listType = new TypeToken<List<PluralKitMember>>(){}.getType();
        members = new Gson().fromJson(reader, listType);
    }

    void updateIfNeeded(long updateFrequency, boolean blocking) {
        Date date = new Date();
        long now = date.getTime();
        if (now >= (lastUpdated + updateFrequency)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    update(blocking);
                }
            });
        }
    }

    public static String verifyToken(PluralKitMC plugin, String token) {
        try {
            plugin.apiSemaphore.acquire();
        } catch (InterruptedException e) {
            plugin.getLogger().warning("Token verification was interrupted!");
            return null;
        }
        URL url = null;
        try {
            url = new URL("https://api.pluralkit.me/v2/systems/@me");
        } catch (MalformedURLException e) {
            return null;
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
            return null;
        }
        PluralKitSystem system = new Gson().fromJson(reader, PluralKitSystem.class);
        return system.id;
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

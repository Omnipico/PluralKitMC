package com.omnipico.pluralkitmc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class PluralKitData {
    FileConfiguration config;
    JavaPlugin plugin;
    Map<UUID, UserCache> userCache = new HashMap<>();
    long cacheUpdateFrequency;
    public PluralKitData(FileConfiguration config, JavaPlugin plugin) {
        this.config = config;
        this.plugin = plugin;
        cacheUpdateFrequency = config.getLong("cache_update_frequency");
    }

    String getSystemId(UUID uuid) {
        if (userCache.containsKey(uuid)) {
            return userCache.get(uuid).systemId;
        } else {
            if (this.config.contains("players." + uuid.toString() + ".system")) {
                return this.config.getString("players." + uuid.toString() + ".system");
            } else {
                return null;
            }
        }
    }

    String getToken(UUID uuid) {
        if (userCache.containsKey(uuid)) {
            return userCache.get(uuid).token;
        } else {
            if (this.config.contains("players." + uuid.toString() + ".token")) {
                return this.config.getString("players." + uuid.toString() + ".token");
            } else {
                return null;
            }
        }
    }

    void setSystemId(UUID uuid, String systemId) {
        config.set("players." + uuid.toString() + ".system", systemId);
        plugin.saveConfig();
        String token = null;
        if (userCache.containsKey(uuid)) {
            token = userCache.get(uuid).getToken();
        }
        userCache.remove(uuid);
        UserCache user = new UserCache(uuid, systemId, token, plugin);
        userCache.put(uuid, user);
    }

    UserCache getCacheOrCreate(UUID uuid) {
        if (userCache.containsKey(uuid)) {
            UserCache user = userCache.get(uuid);
            user.UpdateIfNeeded(cacheUpdateFrequency);
            return user;
        } else {
            String systemId = getSystemId(uuid);
            String token = getToken(uuid);
            if (systemId != null) {
                UserCache user = new UserCache(uuid, systemId, token, plugin);
                userCache.put(uuid, user);
                return user;
            } else {
                return null;
            }
        }
    }

    boolean setToken(UUID uuid, String token) {
        config.set("players." + uuid.toString() + ".token", token);
        plugin.saveConfig();
        UserCache user = getCacheOrCreate(uuid);
        if (user.verifyToken(token)) {
            user.setToken(token);
            user.Update();
            plugin.saveConfig();
            return true;
        }
        return false;
    }

    void updateAutoProxyMode(UUID uuid, String mode) {
        UserCache userCache = getCacheOrCreate(uuid);
        if (userCache != null) {
            userCache.setAutoProxyMode(mode);
        }
        config.set("players." + uuid.toString() + ".ap_mode", mode);
        plugin.saveConfig();
    }

    boolean updateAutoProxyMember(UUID uuid, String memberName) {
        UserCache userCache = getCacheOrCreate(uuid);
        memberName = memberName.toLowerCase();
        if (userCache != null) {
            for (int i = 0; i < userCache.members.size(); i++) {
                PluralKitMember member = userCache.members.get(i);
                if (member.id.equals(memberName) || member.name.toLowerCase().equals(memberName)) {
                    userCache.setAutoProxyMember(member.id);
                    config.set("players." + uuid.toString() + ".ap_member", member.id);
                    plugin.saveConfig();
                    return true;
                }
            }
        }
        return false;
    }

    void updateFronters(UUID uuid, List<String> fronterNames) {
        UserCache userCache = getCacheOrCreate(uuid);
        if (userCache != null) {
            List<PluralKitMember> members = new ArrayList<>();
            for (int i = 0; i < fronterNames.size(); i++) {
                String fronterName = fronterNames.get(i);
                PluralKitMember member = userCache.getMemberByIdOrName(fronterName);
                if (member != null) {
                    members.add(member);
                }
            }
            userCache.setFronters(members);
        }
    }

    void updateCache(UUID uuid) {
        UserCache user = getCacheOrCreate(uuid);
        user.Update();
    }

    void clearCache(UUID uuid) {
        userCache.remove(uuid);
    }

    String getSystemTag(UUID uuid) {
        UserCache user = getCacheOrCreate(uuid);
        if (user != null) {
            return user.system.tag;
        } else {
            return null;
        }
    }

    List<PluralKitMember> getMembers(UUID uuid) {
        UserCache user = getCacheOrCreate(uuid);
        if (user != null) {
            return user.members;
        } else {
            return new ArrayList<PluralKitMember>();
        }
    }

    PluralKitSystem getSystem(UUID uuid) {
        UserCache user = getCacheOrCreate(uuid);
        if (user != null) {
            return user.system;
        } else {
            return null;
        }
    }

    PluralKitMember getRandomMember(UUID uuid) {
        UserCache user = getCacheOrCreate(uuid);
        if (user != null) {
            return user.getRandomMember();
        } else {
            return null;
        }
    }

    List<PluralKitMember> searchMembers(UUID uuid, String search) {
        UserCache user = getCacheOrCreate(uuid);
        if (user != null) {
            return user.searchMembers(search);
        } else {
            return new ArrayList<>();
        }
    }

    PluralKitMember getMemberByName(UUID uuid, String search) {
        UserCache user = getCacheOrCreate(uuid);
        if (user != null) {
            return user.getMemberByIdOrName(search);
        } else {
            return null;
        }
    }

    PluralKitMember getProxiedUser(UUID uuid, String message) {
        UserCache user = getCacheOrCreate(uuid);
        List<PluralKitMember> members = getMembers(uuid);
        int fitStrength = 0;
        PluralKitMember bestFit = null;
        for (int i = 0; i < members.size(); i++) {
            PluralKitMember member = members.get(i);
            for (int j = 0; j < member.proxy_tags.size(); j++) {
                PluralKitProxy proxy = member.proxy_tags.get(j);
                int proxyLength = 0;
                if (proxy.getPrefix() != null) {
                    proxyLength += proxy.getPrefix().length();
                }
                if (proxy.getSuffix() != null) {
                    proxyLength += proxy.getSuffix().length();
                }
                if (message.length() > proxyLength && (proxy.getPrefix() == null || message.substring(0, proxy.getPrefix().length()).equals(proxy.getPrefix()))
                        && (proxy.getSuffix() == null || message.substring(message.length()-proxy.getSuffix().length()).equals(proxy.getSuffix()))
                        && (proxy.getPrefix() != null || proxy.getSuffix() != null)) {
                    if (proxyLength > fitStrength) {
                        fitStrength = proxyLength;
                        bestFit = member;
                    }
                }
            }
        }
        if (bestFit == null && user != null) {
            String apMode = user.getAutoProxyMode();
            if (apMode.equals("member")) {
                bestFit = user.getMemberById(user.getAutoProxyMember());
            } else if (apMode.equals("latch")) {
                bestFit = user.getMemberById(user.getLastProxied());
            } else if (apMode.equals("front")) {
                bestFit = user.getFirstFronter();
            }
        }
        if (bestFit != null) {
            user.setLastProxied(bestFit.id);
        }
        return bestFit;
    }

    PluralKitProxy getProxy(UUID uuid, String message) {
        UserCache user = getCacheOrCreate(uuid);
        List<PluralKitMember> members = getMembers(uuid);
        int fitStrength = 0;
        PluralKitProxy bestFit = null;
        for (int i = 0; i < members.size(); i++) {
            PluralKitMember member = members.get(i);
            for (int j = 0; j < member.proxy_tags.size(); j++) {
                PluralKitProxy proxy = member.proxy_tags.get(j);
                int proxyLength = 0;
                if (proxy.getPrefix() != null) {
                    proxyLength += proxy.getPrefix().length();
                }
                if (proxy.getSuffix() != null) {
                    proxyLength += proxy.getSuffix().length();
                }
                if (message.length() > proxyLength && (proxy.getPrefix() == null || message.substring(0, proxy.getPrefix().length()).equals(proxy.getPrefix()))
                        && (proxy.getSuffix() == null || message.substring(message.length()-proxy.getSuffix().length()).equals(proxy.getSuffix()))
                        && (proxy.getPrefix() != null || proxy.getSuffix() != null)) {
                    if (proxyLength > fitStrength) {
                        fitStrength = proxyLength;
                        bestFit = proxy;
                    }
                }
            }
        }
        if (bestFit == null) {
            bestFit = new PluralKitProxy("","");
        }
        return bestFit;
    }
}

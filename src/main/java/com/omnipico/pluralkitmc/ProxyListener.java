package com.omnipico.pluralkitmc;


import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class ProxyListener implements Listener {
    //TODO: Make this a config option
    FileConfiguration config;
    String format;
    PluralKitData data;
    String defaultNameColor;
    Chat chat;

    public ProxyListener(PluralKitData data, FileConfiguration config, Chat chat) {
        this.data = data;
        this.config = config;
        this.chat = chat;
        defaultNameColor = ChatUtils.replaceColor(config.getString("default_name_color","&b"));
        if (config.contains("message_format")) {
            format = ChatUtils.replaceColor(Objects.requireNonNull(config.getString("message_format")).replace("%message%","%2$s"));
        } else {
            format = ChatColor.WHITE.toString() + "[PK] " + ChatColor.AQUA.toString() + "%member% " + ChatColor.DARK_GRAY + "> " + ChatColor.WHITE + "%2$s";
        }

    }

    @EventHandler // Listening for the event.
    public void onChat(AsyncPlayerChatEvent event) {
        // Called when a player sends a chat message.
        Player player = event.getPlayer(); // Getting the player who sent the message.
        String message = event.getMessage();
        PluralKitMember proxiedMember = data.getProxiedUser(player.getUniqueId(), message);
        PluralKitProxy pluralKitProxy = data.getProxy(player.getUniqueId(), message);
        if (proxiedMember != null) {
            String memberName = proxiedMember.name + " " + data.getSystemTag(player.getUniqueId());
            int prefixLength = pluralKitProxy.prefix != null ? pluralKitProxy.prefix.length() : 0;
            int suffixLength = pluralKitProxy.suffix != null ? pluralKitProxy.suffix.length() : 0;
            event.setMessage(message.substring(prefixLength, message.length()-suffixLength));
            String ourFormat = format;
            ourFormat = ourFormat.replace("%member%", memberName.replace("%2$s","%%2$s"));
            String nameColor = proxiedMember.color == null ? defaultNameColor : ChatUtils.replaceColor("&#" + proxiedMember.color);
            ourFormat = ourFormat.replace("%name_color%", nameColor);
            ourFormat = ourFormat.replace("%prefix%", ChatUtils.replaceColor(chat.getPlayerPrefix(player)));
            ourFormat = ourFormat.replace("%suffix%", ChatUtils.replaceColor(chat.getPlayerSuffix(player)));
            //Bukkit.getLogger().info("format: " + ourFormat);
            event.setFormat(ourFormat.replaceAll("%","%%").replace("%%2$s","%2$s"));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        data.getCacheOrCreate(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        data.clearCache(event.getPlayer().getUniqueId());
    }

}
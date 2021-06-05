package com.omnipico.pluralkitmc;


import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
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

    @EventHandler(priority = EventPriority.HIGHEST) // Listening for the event.
    public void onChat(AsyncPlayerChatEvent event) {
        // Called when a player sends a chat message.
        Player player = event.getPlayer(); // Getting the player who sent the message.
        String message = event.getMessage();
        PluralKitSystem system = data.getSystem(player.getUniqueId());
        PluralKitMember proxiedMember = data.getProxiedUser(player.getUniqueId(), message);
        PluralKitProxy pluralKitProxy = data.getProxy(player.getUniqueId(), message);
        if (proxiedMember != null && system != null) {
            String memberName = proxiedMember.name + " " + data.getSystemTag(player.getUniqueId());
            int prefixLength = pluralKitProxy.prefix != null ? pluralKitProxy.prefix.length() : 0;
            int suffixLength = pluralKitProxy.suffix != null ? pluralKitProxy.suffix.length() : 0;
            event.setMessage(message.substring(prefixLength, message.length()-suffixLength));
            String ourFormat = format;
            String nameColor = proxiedMember.color == null ? defaultNameColor : ChatUtils.replaceColor("&#" + proxiedMember.color);
            //ourFormat = ourFormat.replace("%member%", nameColor + memberName.replace("%2$s","%%2$s"));
            ourFormat = ourFormat.replace("%member%", nameColor + "%member%");
            String prefix = "";
            String suffix = "";
            if (chat != null) {
                prefix = ChatUtils.replaceColor(chat.getPlayerPrefix(player));
                suffix = ChatUtils.replaceColor(chat.getPlayerSuffix(player));
            }
            ourFormat = ourFormat.replace("%prefix%", prefix);
            ourFormat = ourFormat.replace("%suffix%", suffix);
            //Bukkit.getLogger().info("format: " + ourFormat);
            if (!config.getBoolean("hover_text", false)) {
                ourFormat = ourFormat.replace("%member%", memberName);
            }
            event.setFormat(ourFormat.replaceAll("%","%%").replace("%%2$s","%2$s"));
            if (config.getBoolean("hover_text", false)) {
                String resultMessage = String.format(event.getFormat(), player.getDisplayName(), event.getMessage());
                BaseComponent[] resultComponents = TextComponent.fromLegacyText(resultMessage);
                ArrayList<BaseComponent> components = new ArrayList<>();
                for (BaseComponent component : resultComponents) {
                    if (component.toPlainText().startsWith("%member%")) {
                        ComponentBuilder hoverTextBuilder = new ComponentBuilder("User: ").color(ChatColor.GREEN)
                                .append(TextComponent.fromLegacyText(player.getDisplayName())).append("\nSystem: ").color(ChatColor.GREEN);
                        if (system.getName() != null && system.getName().length() > 0) {
                            hoverTextBuilder.append(system.getName()).color(ChatColor.AQUA);
                        } else {
                            hoverTextBuilder.append(system.getId()).color(ChatColor.GRAY);
                        }
                        Text hoverText = new Text(hoverTextBuilder.create());
                        components.addAll(Arrays.asList(new ComponentBuilder(memberName)
                                .color(component.getColor())
                                .bold(component.isBold())
                                .italic(component.isItalic())
                                .obfuscated(component.isObfuscated())
                                .strikethrough(component.isStrikethrough())
                                .underlined(component.isUnderlined())
                                .font(component.getFont())
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        hoverText
                                ))
                                .create()));
                        config.set("test", player.getDisplayName());
                        if (component.toPlainText().length() > 8) {
                            BaseComponent[] post = new ComponentBuilder(component.toPlainText().substring(8))
                                    .color(component.getColor())
                                    .bold(component.isBold())
                                    .italic(component.isItalic())
                                    .obfuscated(component.isObfuscated())
                                    .strikethrough(component.isStrikethrough())
                                    .underlined(component.isUnderlined())
                                    .font(component.getFont())
                                    .create();
                            components.addAll(Arrays.asList(post));
                        }
                    } else {
                        components.add(component);
                    }
                }
                BaseComponent[] sendable = components.toArray(new BaseComponent[0]);
                for (Player p : event.getRecipients()) {
                    p.spigot().sendMessage(sendable);
                }
                event.setCancelled(true);
            }
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
package com.omnipico.pluralkitmc;


import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.PlaceholderAPI;
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
    DiscordSRV discord;
    boolean usePlaceholderAPI;

    public ProxyListener(PluralKitData data, FileConfiguration config, Chat chat, DiscordSRV discord, boolean usePlaceholderAPI) {
        this.data = data;
        this.chat = chat;
        this.discord = discord;
        this.usePlaceholderAPI = usePlaceholderAPI;


        setConfig(config);
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
        defaultNameColor = ChatUtils.replaceColor(config.getString("default_name_color","&b"));
        if (config.contains("message_format")) {
            format = ChatUtils.replaceColor(Objects.requireNonNull(config.getString("message_format")).replace("%message%","%2$s"));
        } else {
            format = ChatColor.WHITE.toString() + "[PK] " + ChatColor.AQUA.toString() + "%member% " + ChatColor.DARK_GRAY + "> " + ChatColor.WHITE + "%2$s";
        }
    }

    private BaseComponent[] getOutputComponent(BaseComponent[] resultComponents, Player player, PluralKitSystem system, String memberName) {
        return getOutputComponent(resultComponents, player, system, memberName, false);
    }

    private BaseComponent[] getOutputComponent(BaseComponent[] resultComponents, Player player, PluralKitSystem system, String memberName, Boolean logUsername) {
        ArrayList<BaseComponent> components = new ArrayList<>();
        boolean convertedMember = false;
        if (logUsername) {
            components.add(new TextComponent("(" + player.getName() + ") "));
        }
        for (BaseComponent component : resultComponents) {
            if (!convertedMember && component.toPlainText().startsWith("%member%")) {
                convertedMember = true;
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
        return components.toArray(new BaseComponent[0]);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Listening for the event.
    public void onChat(AsyncPlayerChatEvent event) {
        // Called when a player sends a chat message.
        Player player = event.getPlayer(); // Getting the player who sent the message.
        String originalFormat = event.getFormat();
        String message = event.getMessage();
        PluralKitSystem system = data.getSystem(player.getUniqueId());
        PluralKitMember proxiedMember = data.getProxiedUser(player.getUniqueId(), message);
        PluralKitProxy pluralKitProxy = data.getProxy(player.getUniqueId(), message);
        if (proxiedMember != null && system != null) {
            String systemTag = data.getSystemTag(player.getUniqueId());
            String memberName;
            String fullMemberName;
            if (proxiedMember.display_name != null && proxiedMember.display_name.length() > 0) {
                memberName = proxiedMember.display_name;
            } else {
                memberName = proxiedMember.name;
            }
            if (systemTag != null && systemTag.length() > 0) {
                fullMemberName = memberName + " " + systemTag;
            } else {
                fullMemberName = memberName;
            }
            int prefixLength = pluralKitProxy.prefix != null ? pluralKitProxy.prefix.length() : 0;
            int suffixLength = pluralKitProxy.suffix != null ? pluralKitProxy.suffix.length() : 0;
            message = message.substring(prefixLength, message.length()-suffixLength);
            event.setMessage(message);
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
                ourFormat = ourFormat.replace("%member%", fullMemberName);
            }
            if (usePlaceholderAPI) {
                ourFormat = PlaceholderAPI.setPlaceholders(player, ourFormat);
            }
            event.setFormat(ourFormat.replaceAll("%","%%").replace("%%2$s","%2$s"));
            if (config.getBoolean("hover_text", false)) {
                String resultMessage = String.format(event.getFormat(), player.getDisplayName(), event.getMessage());
                BaseComponent[] resultComponents = TextComponent.fromLegacyText(resultMessage);
                BaseComponent[] sendable = getOutputComponent(resultComponents, player, system, fullMemberName);
                for (Player p : event.getRecipients()) {
                    p.spigot().sendMessage(sendable);
                }
                if (discord != null && config.getBoolean("discordsrv_compatibility", true)) {
                    if (config.getBoolean("discordsrv_use_member_names", true)) {
                        String oldDisplayName = player.getDisplayName();
                        player.setDisplayName(fullMemberName);
                        discord.processChatMessage(player, message, "global", false);
                        player.setDisplayName(oldDisplayName);
                    } else {
                        discord.processChatMessage(player, message, "global", false);
                    }
                }
                if (config.getBoolean("keep_original_message_event", false)) {
                    /*
                    Don't cancel the original message event
                    Just get rid of recipients, if possible,
                    this means the console will not see the member name
                     */
                    try {
                        event.setFormat(originalFormat);
                        event.getRecipients().clear();
                    } catch (UnsupportedOperationException e) {
                        event.setCancelled(true);
                    }
                } else {
                    if (config.getBoolean("log_username", true)) {
                        BaseComponent[] loggable = getOutputComponent(resultComponents, player, system, fullMemberName, true);
                        Bukkit.getConsoleSender().spigot().sendMessage(loggable);
                    } else {
                        Bukkit.getConsoleSender().spigot().sendMessage(sendable);
                    }
                    event.setCancelled(true);
                }

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
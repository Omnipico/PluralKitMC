package com.omnipico.pluralkitmc;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    final static BaseComponent[] pluginComponent = new ComponentBuilder("Plural").color(ChatColor.WHITE).append("Kit").color(ChatColor.GRAY).append("MC").color(ChatColor.AQUA).create();
    final static BaseComponent[] pluginTag = new ComponentBuilder("[").color(ChatColor.WHITE).append(pluginComponent).append("]").color(ChatColor.WHITE).create();
    final static BaseComponent[] helpMessage = new ComponentBuilder()
            .append(pluginTag)
            .append(" Help").color(ChatColor.GREEN)
            .append("\nCommands: ").color(ChatColor.GREEN)
            .append("\n/pk help ").color(ChatColor.AQUA)
            .append("-- Lists the ").color(ChatColor.GREEN).append(pluginComponent).append(" commands").color(ChatColor.GREEN)
            .append("\n/pk load <system id> ").color(ChatColor.AQUA)
            .append("-- Links you to the given system id").color(ChatColor.GREEN)
            .append("\n/pk update ").color(ChatColor.AQUA)
            .append("-- Forces your system information to refresh").color(ChatColor.GREEN)
            .append("\n/pk link <token> ").color(ChatColor.AQUA)
            .append("-- Links your account to the token from pk;token").color(ChatColor.GREEN)
            .append("\n/pk unlink ").color(ChatColor.AQUA)
            .append("-- Removes the attached token").color(ChatColor.GREEN)
            .append("\n/pk autoproxy <off/front/latch/member>").color(ChatColor.AQUA)
            .append("-- Configures your autoproxy settings").color(ChatColor.GREEN)
            .append("\n/pk switch [out/member...]").color(ChatColor.AQUA)
            .append("-- Switch out or to one or more member").color(ChatColor.GREEN)
            .append("\n/pk find <search term>").color(ChatColor.AQUA)
            .append("-- Searches for a member by name").color(ChatColor.GREEN)
            .append("\n/pk random").color(ChatColor.AQUA)
            .append("-- Lists a random member from your system").color(ChatColor.GREEN)
            .append("\n/pk member <member>").color(ChatColor.AQUA)
            .append("-- Display information regarding a user in your system").color(ChatColor.GREEN)
            .append("\n/pk system [list] [full]").color(ChatColor.AQUA)
            .append("-- Display information regarding your system, or list its members").color(ChatColor.GREEN)
            .create();
    static Pattern REPLACE_ALL_RGB_PATTERN = Pattern.compile("(&)?&(#[0-9a-fA-F]{6})");

    static String replaceColor(String input) {
        input = ChatColor.translateAlternateColorCodes('&', input);
        StringBuffer rgbBuilder = new StringBuffer();
        Matcher rgbMatcher = REPLACE_ALL_RGB_PATTERN.matcher(input);
        while (rgbMatcher.find()) {
            boolean isEscaped = rgbMatcher.group(1) != null;
            if (!isEscaped) {
                try {
                    final String hexCode = rgbMatcher.group(2);
                    rgbMatcher.appendReplacement(rgbBuilder, ChatColor.of(hexCode).toString());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        rgbMatcher.appendTail(rgbBuilder);
        return rgbBuilder.toString();
    }

    static public BaseComponent[] displayMemberInfo(PluralKitMember member, PluralKitSystem system) {
        ChatColor color = ChatColor.AQUA;
        if (member.color != null) {
            color = ChatColor.of("#" + member.color);
        }
        ComponentBuilder memberInfoBuilder = new ComponentBuilder()
                .append(pluginTag)
                .append(" Member information for ").color(ChatColor.GREEN);
        if (system.name != null && system.name.length() > 0) {
            memberInfoBuilder.append(member.name + " (" + system.name + ")").color(color);
        } else {
            memberInfoBuilder.append(member.name).color(color);
        }
        memberInfoBuilder.append("\nDisplay Name: ").color(ChatColor.GREEN).append(member.name).color(color);
        if (member.getBirthday() != null) {
            memberInfoBuilder.append("\nBirthday: ").color(ChatColor.GREEN).append(member.getBirthday()).color(ChatColor.AQUA);
        }
        if (member.getPronouns() != null) {
            memberInfoBuilder.append("\nPronouns: ").color(ChatColor.GREEN).append(member.getPronouns()).color(ChatColor.AQUA);
        }
        if (member.getColor() != null) {
            memberInfoBuilder.append("\nColor: ").color(ChatColor.GREEN).append("#" + member.getColor()).color(color);
        }
        if (member.getProxy_tags() != null) {
            List<PluralKitProxy> proxyTags = member.getProxy_tags();
            if (proxyTags.size() > 0) {
                memberInfoBuilder.append("\nProxy Tags: ").color(ChatColor.GREEN);
                for (int i = 0; i < proxyTags.size(); i++) {
                    PluralKitProxy proxyTag = proxyTags.get(i);
                    if (i > 0) {
                        memberInfoBuilder.append(", ").color(ChatColor.GREEN);
                    }
                    memberInfoBuilder.append(proxyTag.getPrefix() + "text" + proxyTag.getSuffix()).color(ChatColor.GRAY);
                }
            }
        }
        memberInfoBuilder.append("\nCreated: ").color(ChatColor.GREEN).append(member.getCreated()).color(ChatColor.AQUA);
        memberInfoBuilder.append("\nSystem ID: ").color(ChatColor.GREEN).append(system.getId()).color(ChatColor.GRAY);
        memberInfoBuilder.append("\nMember ID: ").color(ChatColor.GREEN).append(member.getId()).color(ChatColor.GRAY);
        return memberInfoBuilder.create();
    }

    static public BaseComponent[] displaySystemInfo(PluralKitSystem system) {
        ComponentBuilder systemInfoBuilder = new ComponentBuilder()
                .append(pluginTag)
                .append(" System information for ").color(ChatColor.GREEN);
        if (system.name != null && system.name.length() > 0) {
            systemInfoBuilder.append(system.name).color(ChatColor.AQUA)
                    .append(" (").color(ChatColor.GREEN)
                    .append(system.id).color(ChatColor.GRAY)
                    .append(")").color(ChatColor.GREEN);
        } else {
            systemInfoBuilder.append(system.id).color(ChatColor.GRAY);
        }
        if (system.tag != null) {
            systemInfoBuilder.append("\nTag: ").color(ChatColor.GREEN).append(system.tag).color(ChatColor.AQUA);
        }
        return systemInfoBuilder.create();
    }

    static public BaseComponent[] displayMemberListForm(PluralKitMember member) {
        ChatColor color = ChatColor.AQUA;
        if (member.color != null) {
            color = ChatColor.of("#" + member.color);
        }
        ComponentBuilder memberInfoBuilder = new ComponentBuilder()
                .append("[").color(ChatColor.WHITE)
                .append(member.id).color(ChatColor.GRAY)
                .append("] ").color(ChatColor.WHITE)
                .append(member.name).color(color);
        if (member.getProxy_tags() != null) {
            List<PluralKitProxy> proxyTags = member.getProxy_tags();
            if (proxyTags.size() > 0) {
                memberInfoBuilder.append(" (").color(ChatColor.WHITE);
                for (int i = 0; i < proxyTags.size(); i++) {
                    PluralKitProxy proxyTag = proxyTags.get(i);
                    if (i > 0) {
                        memberInfoBuilder.append(", ").color(ChatColor.WHITE);
                    }
                    memberInfoBuilder.append(proxyTag.getPrefix() + "text" + proxyTag.getSuffix()).color(ChatColor.GRAY);
                }
                memberInfoBuilder.append(")").color(ChatColor.WHITE);
            }
        }
        return memberInfoBuilder.create();
    }

    static public BaseComponent[] displayMemberList(List<PluralKitMember> members, PluralKitSystem system) {
        ComponentBuilder memberListBuilder = new ComponentBuilder()
                .append(pluginTag)
                .append(" Members of ").color(ChatColor.GREEN);
        if (system.name != null && system.name.length() > 0) {
            memberListBuilder.append(system.name).color(ChatColor.AQUA)
                    .append(" (").color(ChatColor.GREEN)
                    .append(system.id).color(ChatColor.GRAY)
                    .append(")").color(ChatColor.GREEN);
        } else {
            memberListBuilder.append(system.id).color(ChatColor.GRAY);
        }
        for (int i = 0; i < members.size(); i++) {
            PluralKitMember member = members.get(i);
            memberListBuilder.append("\n")
                    .append(displayMemberListForm(member));

        }
        return memberListBuilder.create();
    }

    static public BaseComponent[] displayMemberSearch(List<PluralKitMember> members, PluralKitSystem system, String search) {
        ComponentBuilder memberListBuilder = new ComponentBuilder()
                .append(pluginTag)
                .append(" Members of ").color(ChatColor.GREEN);
        if (system.name != null && system.name.length() > 0) {
            memberListBuilder.append(system.name).color(ChatColor.AQUA)
                    .append(" (").color(ChatColor.GREEN)
                    .append(system.id).color(ChatColor.GRAY)
                    .append(")").color(ChatColor.GREEN);
        } else {
            memberListBuilder.append(system.id).color(ChatColor.GRAY);
        }
        memberListBuilder.append(" matching ").color(ChatColor.GREEN)
                .append(search).color(ChatColor.AQUA);
        for (int i = 0; i < members.size(); i++) {
            PluralKitMember member = members.get(i);
            memberListBuilder.append("\n")
                    .append(displayMemberListForm(member));

        }
        return memberListBuilder.create();
    }
}

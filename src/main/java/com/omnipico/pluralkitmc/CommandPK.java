package com.omnipico.pluralkitmc;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandPK implements CommandExecutor, TabCompleter {
    PluralKitData data;
    PluralKitMC plugin;

    public CommandPK(PluralKitData data, PluralKitMC plugin) {
        this.data = data;
        this.plugin = plugin;
    }

    public List<String> getMemberList(CommandSender sender) {
        List<String> memberNames = new ArrayList<>();
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            List<PluralKitMember> members = data.getMembers(player.getUniqueId());
            for (int i = 0; i < members.size(); i++) {
                memberNames.add(members.get(i).name);
            }
        }
        return memberNames;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 1) {
                if (args[0].toLowerCase().equals("help") || args[0].toLowerCase().equals("h")) {
                    player.spigot().sendMessage(ChatUtils.helpMessage);
                } else if (args[0].toLowerCase().equals("update") || args[0].toLowerCase().equals("u")) {
                    if (player.hasPermission("pluralkitmc.update") || player.hasPermission("pluralkitmc.*") || player.hasPermission("*")) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            data.updateCache(player.getUniqueId(), true);
                        });
                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Your system is being updated and will be active momentarily.").color(ChatColor.GREEN).create());
                    } else {
                        player.spigot().sendMessage( new ComponentBuilder("You do not have permission for this command.").color(ChatColor.RED).create());
                    }
                } else if (args[0].toLowerCase().equals("load") || args[0].toLowerCase().equals("l")) {
                    if (args.length == 2) {
                        if (args[1].length() == 5) {
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                                data.setSystemId(player.getUniqueId(), args[1].toLowerCase());
                            });
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag)
                                    .append(" Set system id to ").color(ChatColor.GREEN)
                                    .append(args[1]).color(ChatColor.AQUA)
                                    .append(", it will be active momentarily").color(ChatColor.GREEN)
                                    .create());
                        } else {
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" System ids must be 5 characters long.").color(ChatColor.RED).create());
                        }
                    } else {
                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Usage: /pk load <system id>").color(ChatColor.RED).create());
                    }
                } else if (args[0].toLowerCase().equals("link") || args[0].toLowerCase().equals("token") || args[0].toLowerCase().equals("t")) {
                    if (player.hasPermission("pluralkitmc.update") || player.hasPermission("pluralkitmc.*") || player.hasPermission("*")) {
                        if (args.length == 2) {
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag)
                                    .append(" Linking pluralkit...").color(ChatColor.GREEN)
                                    .create());
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    if (data.setToken(player.getUniqueId(), args[1])) {
                                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag)
                                                .append(" Your pluralkit has been linked.").color(ChatColor.GREEN)
                                                .create());
                                    } else {
                                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag)
                                                .append(" Your token was invalid, try refreshing it with pk;token refresh.").color(ChatColor.RED)
                                                .create());
                                    }
                                }
                            });
                        } else {
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Usage: /pk link <token from pk;token>").color(ChatColor.RED).create());
                        }
                    } else {
                        player.spigot().sendMessage( new ComponentBuilder("You do not have permission for this command.").color(ChatColor.RED).create());
                    }
                } else if (args[0].toLowerCase().equals("system") || args[0].toLowerCase().equals("s")) {
                    if (args.length >= 2) {
                        if (args[1].toLowerCase().equals("list") || args[1].toLowerCase().equals("l")) {
                            if (args.length == 3) {
                                if (args[2].toLowerCase().equals("full") || args[2].toLowerCase().equals("f")) {
                                    PluralKitSystem system = data.getSystem(player.getUniqueId());
                                    List<PluralKitMember> members = data.getMembers(player.getUniqueId());
                                    if (system != null && members.size() > 0) {
                                        player.spigot().sendMessage(ChatUtils.displayMemberList(members, system));
                                    } else {
                                        player.spigot().sendMessage(new ComponentBuilder().append(ChatUtils.pluginTag).append(" System/Members not found :(").color(ChatColor.RED).create());
                                    }
                                } else {
                                    player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Usage: /pk system [list] [full]").color(ChatColor.RED).create());
                                }
                            } else {
                                PluralKitSystem system = data.getSystem(player.getUniqueId());
                                List<PluralKitMember> members = data.getMembers(player.getUniqueId());
                                if (system != null && members.size() > 0) {
                                    player.spigot().sendMessage(ChatUtils.displayMemberList(members, system));
                                } else {
                                    player.spigot().sendMessage(new ComponentBuilder().append(ChatUtils.pluginTag).append(" System/Members not found :(").color(ChatColor.RED).create());
                                }
                            }
                        } else if (args[1].toLowerCase().equals("f")) {
                            //TODO: Show fronter
                            /*List<PluralKitMember> members = data.getFronters(player.getUniqueId());
                            player.spigot().sendMessage(new ComponentBuilder().append(ChatUtils.pluginTag)
                                    .append(" Current Fronter(s): ").color(ChatColor.GREEN)

                                    .create());*/
                        } else {
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Usage: /pk system [list] [full]").color(ChatColor.RED).create());
                        }
                    } else {
                        PluralKitSystem system = data.getSystem(player.getUniqueId());
                        if (system != null) {
                            player.spigot().sendMessage(ChatUtils.displaySystemInfo(system));
                        } else {
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" System not found :(").color(ChatColor.RED).create());
                        }
                    }
                } else if (args[0].toLowerCase().equals("find") || args[0].toLowerCase().equals("f")) {
                    if (args.length == 2) {
                        PluralKitSystem system = data.getSystem(player.getUniqueId());
                        List<PluralKitMember> members = data.searchMembers(player.getUniqueId(), args[1]);
                        if (system != null && members.size() > 0)
                        {
                            player.spigot().sendMessage(ChatUtils.displayMemberSearch(members, system, args[1]));
                        } else if (system != null) {
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" No members found :(").color(ChatColor.RED).create());
                        } else {
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Your system is not currently loaded!").color(ChatColor.RED).create());
                        }
                    } else {
                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Usage: /pk find <search term>").color(ChatColor.RED).create());
                    }
                } else if (args[0].toLowerCase().equals("autoproxy") || args[0].toLowerCase().equals("ap")) {
                    if (args.length == 2) {
                        String apMode = args[1].toLowerCase();
                        if (apMode.equals("off") || apMode.equals("front") || apMode.equals("latch")) {
                            data.updateAutoProxyMode(player.getUniqueId(), apMode);
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag)
                                    .append(" Auto proxy mode set to ").color(ChatColor.GREEN)
                                    .append(apMode).color(ChatColor.AQUA)
                                    .create());
                        } else {
                            apMode = "member";
                            data.updateAutoProxyMode(player.getUniqueId(), apMode);
                            if (data.updateAutoProxyMember(player.getUniqueId(), args[1].toLowerCase())) {
                                player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag)
                                        .append(" Auto proxy set to ").color(ChatColor.GREEN)
                                        .append(args[1]).color(ChatColor.AQUA)
                                        .create());
                            } else {
                                player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag)
                                        .append(" Could not find system member ").color(ChatColor.RED)
                                        .append(apMode).color(ChatColor.AQUA)
                                        .create());
                            }
                        }
                    } else {
                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Usage: /pk autoproxy <off/front/latch/member>").color(ChatColor.RED).create());
                    }
                } else if (args[0].toLowerCase().equals("member") || args[0].toLowerCase().equals("m")) {
                    if (args.length == 2) {
                        PluralKitSystem system = data.getSystem(player.getUniqueId());
                        PluralKitMember member = data.getMemberByName(player.getUniqueId(), args[1]);
                        if (system != null && member != null) {
                            player.spigot().sendMessage(ChatUtils.displayMemberInfo(member, system));
                        } else {
                            player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Member not found :(").color(ChatColor.RED).create());
                        }
                    } else {
                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Usage: /pk member <member>").color(ChatColor.RED).create());
                    }
                } else if (args[0].toLowerCase().equals("random") || args[0].toLowerCase().equals("r")) {
                    PluralKitSystem system = data.getSystem(player.getUniqueId());
                    PluralKitMember member = data.getRandomMember(player.getUniqueId());
                    if (system != null && member != null) {
                        player.spigot().sendMessage(ChatUtils.displayMemberInfo(member, system));
                    } else {
                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Could not find any members :(").color(ChatColor.RED).create());
                    }
                } else if (args[0].toLowerCase().equals("reload")) {
                    if (player.hasPermission("pluralkitmc.reload") || player.hasPermission("pluralkitmc.*") || player.hasPermission("*")) {
                        plugin.reloadConfigData();
                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Config reloaded.").color(ChatColor.GREEN).create());
                    } else {
                        player.spigot().sendMessage( new ComponentBuilder("You do not have permission for this command.").color(ChatColor.RED).create());
                    }
                } else if (args[0].toLowerCase().equals("switch") || args[0].toLowerCase().equals("sw")) {
                    if (args.length >= 2) {
                        List<String> newFronters = new ArrayList<>();
                        for (int i = 1; i < args.length; i++) {
                            String memberName = args[i].toLowerCase();
                            if (!memberName.equals("out")) {
                                newFronters.add(memberName);
                            }
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                            @Override
                            public void run() {
                                data.updateFronters(player.getUniqueId(), newFronters);
                                player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag)
                                        .append(" Updated fronters.").color(ChatColor.GREEN)
                                        .create());
                            }
                        });
                    } else {
                        player.spigot().sendMessage( new ComponentBuilder().append(ChatUtils.pluginTag).append(" Usage: /pk switch [out/member...]").color(ChatColor.RED).create());
                    }
                }
            } else {
                player.spigot().sendMessage(ChatUtils.helpMessage);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subCommands = new ArrayList<String>();
        //PKMC specific commands
        subCommands.add("help");
        subCommands.add("update");
        subCommands.add("load");
        subCommands.add("link");
        subCommands.add("unlink");
        //Copies of PluralKit's commands
        subCommands.add("system");
        subCommands.add("find");
        subCommands.add("autoproxy");
        subCommands.add("member");
        subCommands.add("random");
        subCommands.add("switch");
        subCommands.add("reload");
        if (args.length == 1) {
            return subCommands;
        } else {
            String subCommand = args[0].toLowerCase();
            // These are tab completion for clones of PluralKit commands
            if (subCommand.equals("system") || subCommand.equals("s")) {
                List<String> specificSystemCommands = new ArrayList<>();
                specificSystemCommands.add("list");
                specificSystemCommands.add("fronter");
                //specificSystemCommands.add("fronthistory");
                //specificSystemCommands.add("frontpercent");
                //specificSystemCommands.add("find");
                if (args.length == 2) {
                    List<String> systemCommands = new ArrayList<>();
                    //System subcommands
                    //systemCommands.add("new");
                    //systemCommands.add("rename");
                    //systemCommands.add("description");
                    //systemCommands.add("avatar");
                    //systemCommands.add("tag");
                    //systemCommands.add("timezone");
                    //systemCommands.add("proxy");
                    systemCommands.addAll(specificSystemCommands);
                    return systemCommands;
                } else {
                    String systemSub = args[1].toLowerCase();
                    if (args.length == 3 && systemSub.equals("proxy")) {
                        List<String> proxyCommands = new ArrayList<>();
                        proxyCommands.add("on");
                        proxyCommands.add("off");
                        return proxyCommands;
                    } else if (args.length == 3 && systemSub.length() == 5) {
                        return specificSystemCommands;
                    }
                }
            } else if (subCommand.equals("autoproxy") || subCommand.equals("ap")) {
                if (args.length == 2) {
                    List<String> autoProxyCommands = new ArrayList<>();
                    autoProxyCommands.add("off");
                    autoProxyCommands.add("front");
                    autoProxyCommands.add("latch");
                    autoProxyCommands.addAll(getMemberList(sender));
                    return autoProxyCommands;
                }
            } else if (subCommand.equals("member") || subCommand.equals("m")) {
                if (args.length == 2) {
                    List<String> subMember = getMemberList(sender);
                    //subMember.add("new");
                    return subMember;
                } else if (args.length == 3 && args[1].toLowerCase().equals("new")) {
                    return null;
                } else if (args.length == 3 && args[1].toLowerCase().equals("member")) {
                    List<String> memberCommands = new ArrayList<>();
                    //memberCommands.add("rename");
                    //memberCommands.add("displayname");
                    //memberCommands.add("servername");
                    //memberCommands.add("description");
                    //memberCommands.add("avatar");
                    //memberCommands.add("proxy");
                    //memberCommands.add("keepproxy");
                    //memberCommands.add("color");
                    //memberCommands.add("birthdate");
                    return memberCommands;
                }
            } else if (subCommand.equals("switch") || subCommand.equals("sw")) {
                List<String> subMember = getMemberList(sender);
                subMember.add("out");
                return subMember;
            }
        }
        return null;
    }
}

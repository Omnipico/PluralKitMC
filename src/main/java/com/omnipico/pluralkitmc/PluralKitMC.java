package com.omnipico.pluralkitmc;
import github.scarsz.discordsrv.DiscordSRV;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluralKitMC extends JavaPlugin {
    Chat chat;
    PluralKitData data;
    ProxyListener proxyListener;
    DiscordSRV discord;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null){
            chat = getServer().getServicesManager().load(Chat.class);
        }
        if(Bukkit.getServer().getPluginManager().getPlugin("DiscordSRV") != null){
            discord = DiscordSRV.getPlugin();
        }
        FileConfiguration config = this.getConfig();
        this.data = new PluralKitData(config, this);
        //Fired when the server enables the plugin
        CommandPK commandPK = new CommandPK(data, this);
        this.getCommand("pk").setExecutor(commandPK);
        this.getCommand("pk").setTabCompleter(commandPK);
        proxyListener = new ProxyListener(data, config, chat, discord);
        getServer().getPluginManager().registerEvents(proxyListener, this);
    }

    @Override
    public void onDisable() {
        //Fired when the server stops and disables all plugins
    }

    public void reloadConfigData() {
        this.reloadConfig();
        FileConfiguration config = this.getConfig();
        proxyListener.setConfig(config);
        data.setConfig(config);
    }

}

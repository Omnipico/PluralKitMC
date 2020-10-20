package com.omnipico.pluralkitmc;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluralKitMC extends JavaPlugin {
    Chat chat;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        chat = getServer().getServicesManager().load(Chat.class);
        FileConfiguration config = this.getConfig();
        PluralKitData data = new PluralKitData(config, this);
        //Fired when the server enables the plugin
        CommandPK commandPK = new CommandPK(data, this);
        this.getCommand("pk").setExecutor(commandPK);
        this.getCommand("pk").setTabCompleter(commandPK);
        getServer().getPluginManager().registerEvents(new ProxyListener(data, config, chat), this);
    }

    @Override
    public void onDisable() {
        //Fired when the server stops and disables all plugins
    }
}

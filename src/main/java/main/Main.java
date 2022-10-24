package main;

import main.cmdhandler.CMDHandler;
import main.eventhandler.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {
    public static final String INDEX = "§5[§cMurder§5]§f ";
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a플러그인이 활성화되었습니다.");
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        this.getDescription().getCommands().keySet().forEach(s -> {
            Objects.requireNonNull(getCommand(s)).setExecutor(new CMDHandler());
            Objects.requireNonNull(getCommand(s)).setTabCompleter(new CMDHandler());
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§c플러그인이 비활성화되었습니다.");
    }
}

package main.gamehandler;

import main.stringhandler.TextFormatter;
import main.stringhandler.TranslateHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NotifyHandler {
    public static void sendTitle(Player p, String key, String key1, int i, int i1, int i2) {
        p.sendTitle(TranslateHandler.getString(key, p), TranslateHandler.getString(key1, p), i, i1, i2);
    }

    public static void sendTitle(Player p, TextFormatter key, TextFormatter key1, int i, int i1, int i2) {
        p.sendTitle(TranslateHandler.getText(key, p).toString(), TranslateHandler.getText(key1, p).toString(), i, i1, i2);
    }

    public static void sendTitle(String key, String key1, int i, int i1, int i2) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendTitle(p, key, key1, i, i1, i2);
        }
    }

    public static void sendTitle(TextFormatter key, TextFormatter key1, int i, int i1, int i2) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendTitle(p, key, key1, i, i1, i2);
        }
    }

    public static void sendMessage(Player p, String key) {
        p.sendMessage(TranslateHandler.getString(key, p));
    }

    public static void sendMessage(Player p, TextFormatter key) {
        p.sendMessage(TranslateHandler.getText(key, p).toString());
    }
}

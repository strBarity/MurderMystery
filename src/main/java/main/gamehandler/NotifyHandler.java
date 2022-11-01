package main.gamehandler;

import main.stringhandler.TextFormatter;
import main.stringhandler.TranslateHandler;
import org.bukkit.entity.Player;

public class NotifyHandler {
    public static void sendTitle(Player p, String key, String key1, int i, int i1, int i2)
    {
        p.sendTitle(TranslateHandler.getString(key, p), TranslateHandler.getString(key1, p), i, i1, i2);
    }

    public static void sendTitle(Player p, TextFormatter key, TextFormatter key1, int i, int i1, int i2)
    {

    }
}

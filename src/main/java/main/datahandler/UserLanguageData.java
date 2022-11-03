package main.datahandler;

import main.stringhandler.TranslateHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static main.Main.*;

public class UserLanguageData {
    private static FileConfiguration userLanguageData;
    private static final File userLang = new File("plugins/Murder/userLanguageData.yml");

    public static void loadData() {
        userLanguageData = YamlConfiguration.loadConfiguration(userLang);
        try {
            if (!userLang.exists()) {
                userLanguageData.save(userLang);
            }
        } catch (IOException e) {
            printException(getClassName(), getMethodName(), e);
        }
    }
    public static void saveData() {
        try {
            userLanguageData.save(userLang);
        } catch (IOException e) {
            printException(getClassName(), getMethodName(), e);
        }
    }

    public static TranslateHandler.MurderLanguage getUserLanguage(Player p)
    {
        if (userLanguageData.getString("userLang." + p.getUniqueId()) == null) {
            setUserLanguage(p, TranslateHandler.MurderLanguage.English);
            return TranslateHandler.MurderLanguage.English;
        }
        return TranslateHandler.MurderLanguage.valueOf(userLanguageData.getString("userLang." + p.getUniqueId()));
    }

    public static void setUserLanguage(Player p, TranslateHandler.MurderLanguage lang)
    {
        userLanguageData.set("userLang." + p.getUniqueId(), lang.name());
        saveData();
    }
}

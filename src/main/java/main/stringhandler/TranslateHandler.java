package main.stringhandler;

import main.datahandler.UserLanguageData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TranslateHandler {
    public enum MurderLanguage {Korean, English}

    public static Map<String, String> KoreanMessage = new HashMap<>();
    public static Map<String, String> EnglishMessage = new HashMap<>();

    public static void initialize() {
        setString("murder.key.asdf", "ㅁㄴㅇㄹ", "asdf");
    }

    public static void setString(String key, String kr, String en) {
        KoreanMessage.put(key, kr);
        EnglishMessage.put(key, en);
    }

    public static String getString(String key, MurderLanguage lang) {
        switch (lang) {
            case Korean:
                String ko = KoreanMessage.get(key);
                if (ko == null) ko = key;
                return ko;
            default:
                String en = EnglishMessage.get(key);
                if (en == null) en = key;
                return en;
        }
    }

    public static String getString(String key, Player p) {
        return getString(key, UserLanguageData.getUserLanguage(p));
    }

    public static TextFormatter getText(TextFormatter text, MurderLanguage lang) {
        TextFormatter formatter = new TextFormatter("", text.getPreColor(), text.getPreStyle(), text.getPostColor(), text.getPostStyle());
        formatter.string = getString(text.string, lang);
        return formatter;
    }

    public static TextFormatter getText(TextFormatter text, Player p) {
        return getText(text, UserLanguageData.getUserLanguage(p));
    }
}

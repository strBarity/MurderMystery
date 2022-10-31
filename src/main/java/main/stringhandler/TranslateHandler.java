package main.stringhandler;

import java.util.HashMap;
import java.util.Map;

public class TranslateHandler {
    public enum MurderLanguage {Korean, English}

    public static Map<String, String> KoreanMessage = new HashMap<>();
    public static Map<String, String> EnglishMessage = new HashMap<>();

    public static void initialize() {
        setString("murder.key.ggt", "얻'얻'습니다", "g'g't");
    }

    public static void setString(String key, String kr, String en) {
        KoreanMessage.put(key, kr);
        EnglishMessage.put(key, en);
    }

    public static String getString(String key, MurderLanguage lang) {
        switch (lang) {
            case Korean:
                try {
                    return KoreanMessage.get(key);
                }
                catch(Exception ex) {
                    return "Missing Key(KR)" + key;
                }
            default:
                try {
                    return EnglishMessage.get(key);
                }
                catch(Exception ex) {
                    return "Missing Key(EN)" + key;
                }
        }
    }
}
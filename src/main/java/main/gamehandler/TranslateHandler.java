package main.gamehandler;

import java.util.HashMap;
import java.util.Map;

public class TranslateHandler {
    public enum MurderLanguage { Korean, English }

    public static Map<String, String> KoreanMessage = new HashMap<>();
    public static Map<String, String> EnglishMessage = new HashMap<>();


    public static void initialize() {
        KoreanMessage.put("murder.key.onDeath","얻'얻'습니다");
        EnglishMessage.put("murder.key.onDeath","g'g't");
    }

    public static void setString(String key, String kr, String en) {
        KoreanMessage.put(key, kr);
        EnglishMessage.put(key, en);
    }


    public static String getString(String key, MurderLanguage lang) {
        switch (lang) {
            case Korean:
                return KoreanMessage.get(key);
            default:
                return EnglishMessage.get(key);
        }
    }
}

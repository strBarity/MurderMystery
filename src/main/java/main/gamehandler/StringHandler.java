package main.gamehandler;

import java.util.HashMap;
import java.util.Map;

public class StringHandler {
    public enum MurderLanguage {Korean, English}

    public enum StringColor {
        Black(0), Dark_Blue(1), Dark_Green(2), Dark_Aqua(3), Dark_Red(4), Dark_Purple(5),
        Gold(6), Gray(7), Dark_Gray(8), Blue(9), Green(0xa), Aqua(0xb), Red(0xc), Light_Purple(0xd),
        Yellow(0xe), White(0xf);

        public final int value;

        StringColor(int value) {
            this.value = value;
        }
    }

    public enum StringStyle {
        Obfuscated('k'), Bold('l'), Strikethrough('m'), Underlined('n'), Italic('o'), None('r');

        public final char value;

        StringStyle(char value) {
            this.value = value;
        }
    }

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
                return KoreanMessage.get(key);
            default:
                return EnglishMessage.get(key);
        }
    }

    public static String applyColor(String str, StringColor color) {
        return applyColor(str, color, color);
    }

    public static String applyColor(String str, StringColor color, StringColor reset) {
        StringBuilder sb = new StringBuilder();
        sb.append("§").append(Integer.toHexString(color.value)).append(str).append("§").append(Integer.toHexString(reset.value));
        return sb.toString();
    }

    public static String applyStyle(String str, StringStyle style)
    {
        return applyStyle(str, style, false);
    }

    public static String applyStyle(String str, StringStyle style, boolean reset) {
        StringBuilder sb = new StringBuilder();
        sb.append("§").append(style.value).append(str);
        if (reset) sb.append("§").append(StringStyle.None.value);
        return sb.toString();
    }
}

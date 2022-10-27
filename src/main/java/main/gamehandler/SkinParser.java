package main.gamehandler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class SkinParser {
    public static String getValue(UUID uuid) {
        final String u = uuid.toString().replace("-", "");
        try {
            URL mojang = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + u + "?unsigned=false");
            InputStreamReader reader = new InputStreamReader(mojang.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            if (textureProperty.get("value").getAsString() != null) return textureProperty.get("value").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        } throw new RuntimeException("스킨 값을 불러오는 데 실패하였습니다.");
    } public static String getSignature(UUID uuid) {
        final String u = uuid.toString().replace("-", "");
        try {
            URL mojang = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + u + "?unsigned=false");
            InputStreamReader reader = new InputStreamReader(mojang.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            if (textureProperty.get("signature").getAsString() != null) return textureProperty.get("signature").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        } throw new RuntimeException("스킨 시그니쳐를 불러오는 데 실패하였습니다.");
    }
}

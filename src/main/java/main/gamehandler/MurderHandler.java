package main.gamehandler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MurderHandler {
    public static String heroName = null;
    public static int bowType = 0;
    // bowType = 0: 탐정 생존, 1: 활 떨어짐, 2: 활 떨어지지 않음
    public static int innocentAlive = 0;
    public static int murderKills = 0;
    public static boolean gameStarted = false;
    public static final HashMap<Player, String> roleType = new HashMap<>();
    public static void startGame(@NotNull World w) {
        gameStarted = true;
        for (int z = 166; z <= 187; z++) for (int x = 103; x <= 134; x++) for (int y = 80; y <= 98; y++) if (w.getBlockAt(x, y, z).getType().equals(Material.GOLD_BLOCK)) w.getBlockAt(x, y, z).setType(Material.STRING);
        for (int x = 107; x <= 117; x++) for (int y = 88; y <= 95; y++) if (w.getBlockAt(x, y, 196).getType().equals(Material.IRON_FENCE)) w.getBlockAt(x, y, 196).setType(Material.AIR);
        for (int x = 107; x <= 117; x++) for (int y = 88; y <= 95; y++) if (w.getBlockAt(x, y, 156).getType().equals(Material.IRON_FENCE)) w.getBlockAt(x, y, 156).setType(Material.AIR);
        for (int z = 175; z <= 177; z++) for (int x = 98; x <= 99; x++) w.getBlockAt(x, 97, z).setType(Material.AIR);
        for (int z = 174; z <= 178; z++) for (int y = 90; y <= 95; y++) w.getBlockAt(96, y, z).setType(Material.AIR);
    }
    public static void stopGame(@NotNull World w, @NotNull Boolean finished, @NotNull Boolean innocentWin) {
        gameStarted = false;
        for (int z = 166; z <= 187; z++) for (int x = 103; x <= 134; x++) for (int y = 80; y <= 98; y++) if (w.getBlockAt(x, y, z).getType().equals(Material.STRING)) w.getBlockAt(x, y, z).setType(Material.GOLD_BLOCK);
        for (int x = 107; x <= 117; x++) for (int y = 88; y <= 95; y++) if (w.getBlockAt(x, y, 196).getType().equals(Material.AIR)) w.getBlockAt(x, y, 196).setType(Material.IRON_FENCE);
        for (int x = 107; x <= 117; x++) for (int y = 88; y <= 95; y++) if (w.getBlockAt(x, y, 156).getType().equals(Material.AIR)) w.getBlockAt(x, y, 156).setType(Material.IRON_FENCE);
        for (int z = 175; z <= 177; z++) for (int x = 98; x <= 99; x++) w.getBlockAt(x, 97, z).setType(Material.EMERALD_BLOCK);
        for (int z = 174; z <= 178; z++) for (int y = 90; y <= 95; y++) w.getBlockAt(96, y, z).setType(Material.IRON_FENCE);
        w.getBlockAt(98, 98, 176).setType(Material.CAKE_BLOCK);
        if (finished) {
            String boarder = "§a--------------------------------------------------------------------------------";
            Bukkit.broadcastMessage(boarder + "\n                                   §f§l머더 미스터리");
            if (innocentWin) Bukkit.broadcastMessage("\n                                   §f§l승자: §a플레이어");
            else Bukkit.broadcastMessage("\n                                   §f§l승자: §c살인자");
            if (bowType == 0) Bukkit.broadcastMessage("\n                                    §7탐정: §f제작중");
            else Bukkit.broadcastMessage("\n                                    §7탐정: §f§m제작중");
            if (innocentWin) Bukkit.broadcastMessage("                                §7살인자: §f§m제작중§7 (§6" + murderKills + "§7 킬)");
            else Bukkit.broadcastMessage("                                     §7살인자: §f제작중§7 (§6" + murderKills + "§7 킬)");
            if (heroName != null) Bukkit.broadcastMessage("                                    §7영웅: §f제작중");
            Bukkit.broadcastMessage("\n" + boarder);
        }
    }
}

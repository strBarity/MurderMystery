package main.gamehandler;

import main.Main;
import main.datahandler.SpawnLocationData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static main.Main.*;

public class MurderHandler {
    public static String heroName = null;
    public static int bowType = 0;
    // bowType = 0: 탐정 생존, 1: 활 떨어짐, 2: 활 떨어지지 않음
    public static int innocentAlive = 0;
    public static int murderKills = 0;
    public static boolean gameStarted = false;
    public static final HashMap<Player, String> roleType = new HashMap<>();
    private static final List<Location> savedGoldBlock = new ArrayList<>();
    public static void startGame(@NotNull World w) {
        try {
            gameStarted = true;
            for (int z = 166; z <= 187; z++)
                for (int x = 103; x <= 134; x++)
                    for (int y = 80; y <= 98; y++)
                        if (w.getBlockAt(x, y, z).getType().equals(Material.GOLD_BLOCK)) {
                            w.getBlockAt(x, y, z).setType(Material.AIR);
                            savedGoldBlock.add(new Location(Main.CURRENTMAP, x, y, z));
                        }
            for (int x = 107; x <= 117; x++)
                for (int y = 88; y <= 95; y++)
                    if (w.getBlockAt(x, y, 196).getType().equals(Material.IRON_FENCE))
                        w.getBlockAt(x, y, 196).setType(Material.AIR);
            for (int x = 107; x <= 117; x++)
                for (int y = 88; y <= 95; y++)
                    if (w.getBlockAt(x, y, 156).getType().equals(Material.IRON_FENCE))
                        w.getBlockAt(x, y, 156).setType(Material.AIR);
            for (int z = 175; z <= 177; z++)
                for (int x = 98; x <= 99; x++) w.getBlockAt(x, 97, z).setType(Material.AIR);
            for (int z = 174; z <= 178; z++)
                for (int y = 90; y <= 95; y++) w.getBlockAt(96, y, z).setType(Material.AIR);
            if (SpawnLocationData.getSpawnLocation(Main.CURRENTMAP.getName()).isEmpty()) throw new RuntimeException("저장된 스폰 위치가 존재하지 않습니다");
            for (Player p : Bukkit.getOnlinePlayers()) {
                double r = Math.random();
                int l = 0;
                String m = Main.CURRENTMAP.getName();
                Location[] locations = new Location[SpawnLocationData.getSpawnLocation(m).size()];
                for (String s : SpawnLocationData.getSpawnLocation(m)) {
                    final String[] parts = s.split(",");
                    locations[l] = new Location(Main.CURRENTMAP, Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    l++;
                }
                double t = 1D / locations.length;
                l = 0;
                for (Location l2 : locations) {
                    l++;
                    if (r >= t * l - 1 && r < t * l) p.teleport(l2);
                }
            }
        } catch (Exception e) {
            printException(getClassName(), getMethodName(), e);
        }
    }
    public static void stopGame(@NotNull World w, @NotNull Boolean finished, @NotNull Boolean innocentWin) {
        try {
            gameStarted = false;
            for (Location l : savedGoldBlock) w.getBlockAt(l).setType(Material.GOLD_BLOCK);
            for (int x = 107; x <= 117; x++)
                for (int y = 88; y <= 95; y++)
                    if (w.getBlockAt(x, y, 196).getType().equals(Material.AIR))
                        w.getBlockAt(x, y, 196).setType(Material.IRON_FENCE);
            for (int x = 107; x <= 117; x++)
                for (int y = 88; y <= 95; y++)
                    if (w.getBlockAt(x, y, 156).getType().equals(Material.AIR))
                        w.getBlockAt(x, y, 156).setType(Material.IRON_FENCE);
            for (int z = 175; z <= 177; z++)
                for (int x = 98; x <= 99; x++) w.getBlockAt(x, 97, z).setType(Material.EMERALD_BLOCK);
            for (int z = 174; z <= 178; z++)
                for (int y = 90; y <= 95; y++) w.getBlockAt(96, y, z).setType(Material.IRON_FENCE);
            w.getBlockAt(98, 98, 176).setType(Material.CAKE_BLOCK);
            if (finished) {
                String boarder = "§a--------------------------------------------------------------------------------";
                Bukkit.broadcastMessage(boarder + "\n                                   §f§l머더 미스터리");
                if (innocentWin) Bukkit.broadcastMessage("\n                                   §f§l승자: §a플레이어");
                else Bukkit.broadcastMessage("\n                                   §f§l승자: §c살인자");
                if (bowType == 0) Bukkit.broadcastMessage("\n                                    §7탐정: §f제작중");
                else Bukkit.broadcastMessage("\n                                    §7탐정: §f§m제작중");
                if (innocentWin)
                    Bukkit.broadcastMessage("                                §7살인자: §f§m제작중§7 (§6" + murderKills + "§7 킬)");
                else
                    Bukkit.broadcastMessage("                                     §7살인자: §f제작중§7 (§6" + murderKills + "§7 킬)");
                if (heroName != null) Bukkit.broadcastMessage("                                    §7영웅: §f제작중");
                Bukkit.broadcastMessage("\n" + boarder);
            }
        } catch (Exception e) {
            printException(getClassName(), getMethodName(), e);
        }
    }
}

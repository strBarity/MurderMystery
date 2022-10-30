package main.gamehandler;

import main.Main;
import main.datahandler.SpawnLocationData;
import main.eventhandler.EventListener;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static main.Main.*;

public class MurderHandler {
    public enum BowType { DectectiveAlive, BowDrop, BowNotDrop }
    public static Player murderer = null;
    public static Player detective = null;
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
            if (Bukkit.getOnlinePlayers().size() < 2) throw new RuntimeException("플레이어 수가 너무 적습니다");
            gameStarted = true;
            innocentAlive = Bukkit.getOnlinePlayers().size() - 1;
            for (int z = 166; z <= 187; z++)
                for (int x = 103; x <= 134; x++)
                    for (int y = 80; y <= 98; y++) if (w.getBlockAt(x, y, z).getType().equals(Material.GOLD_BLOCK)) {
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
                p.getInventory().clear();
                players[n] = p;
                p.teleport(locations[n % locations.length]);
                n++;
            }

            for (int i = 0; i < players.length; i++) {
                int r = random.nextInt(players.length - 1);
                Player tmp = players[0];
                players[0] = players[r];
                players[r] = tmp;
            } roleType.put(players[0], "§c살인자");
            murderer = players[0];
            players[0].sendTitle("§c역할: 살인자", "§e모든 플레이어를 죽이세요!", 20, 100, 20);
            roleType.put(players[1], "§b탐정");
            detective = players[1];
            players[1].sendTitle("§b역할: 탐정", "§e살인자를 찾아 처치하세요!", 20, 100, 20);
            for (int i = 2; i < players.length; i++) {
                roleType.put(players[i], "§a시민");
                players[i].sendTitle("§a역할: 시민", "§e최대한 오래 살아남으세요!", 20, 100, 20);
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
            String boarder = "§a--------------------------------------------------------------------------------";
            Bukkit.broadcastMessage(boarder + "\n                                   §f§l머더 미스터리");
            if (innocentWin) Bukkit.broadcastMessage("\n                                   §f§l승자: §a플레이어");
            else Bukkit.broadcastMessage("\n                                   §f§l승자: §c살인자");
            if (bowType == BowType.DectectiveAlive) Bukkit.broadcastMessage("\n                                    §7탐정: " + EventListener.rankColor.get(detective) + detective.getName());
            else Bukkit.broadcastMessage("\n                                    §7탐정: " + EventListener.rankColor.get(detective) + "§m" + detective.getName());
            if (innocentWin) Bukkit.broadcastMessage("                                §7살인자: " + EventListener.rankColor.get(murderer) + murderer.getName() + " §7(§6" + murderKills + "§7 킬)");
            else Bukkit.broadcastMessage("                                     §7살인자: " + EventListener.rankColor.get(murderer) + "§m" + murderer.getName() + " §7(§6" + murderKills + "§7 킬)");
            if (heroName != null) Bukkit.broadcastMessage("                                    §7영웅: §f제작중");
            Bukkit.broadcastMessage("\n" + boarder);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                murderer = null;
                detective = null;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    roleType.remove(p);
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.teleport(new Location(p.getWorld(), 104.5, 88.0, 176.5, 90F, 0F));
                    p.setGameMode(GameMode.ADVENTURE);
                }
            }, 200L);
        } catch (Exception e) {
            printException(getClassName(), getMethodName(), e);
        }
    }
}

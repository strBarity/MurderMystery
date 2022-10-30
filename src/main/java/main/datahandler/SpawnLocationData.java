package main.datahandler;

import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SpawnLocationData {
    private static FileConfiguration spawnLocationData;
    private static final File spawnLocation = new File("plugins/Murder/spawnLocationData.yml");
    public static final HashMap<Player, Integer> slWandId = new HashMap<>();
    public static void registerSLWand(Player p) {
        int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getItemMeta() != null && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName() != null && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("스폰 위치 설정 도구")) {
                for (String s : getSpawnLocation(Main.CURRENTMAP.getName())) {
                    final String[] parts = s.split(",");
                    final int x = Integer.parseInt(parts[0]);
                    final int y = Integer.parseInt(parts[1]);
                    final int z = Integer.parseInt(parts[2]);
                    Main.CURRENTMAP.spawnParticle(Particle.REDSTONE, new Location(Main.CURRENTMAP, x+0.5, y+0.5, z+0.5), 25, 0.125, 0.125, 0.125, 0.0);
                }
            }
        }, 0, 5L);
        slWandId.put(p, i);
    }
    public static void loadData() {
        spawnLocationData = YamlConfiguration.loadConfiguration(spawnLocation);
        try {
            if (!spawnLocation.exists()) {
                spawnLocationData.save(spawnLocation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveData() {
        try {
            spawnLocationData.save(spawnLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> getSpawnLocation(String mapName) {
        if (spawnLocationData.getList("spawnLocation." + mapName) == null) return new ArrayList<>();
        return spawnLocationData.getStringList("spawnLocation." + mapName);
    }
    public static void addSpawnLocation(String mapName, Location location) {
        List<String> spawnLocationList = getSpawnLocation(mapName);
        if (spawnLocationList.size() > 100) throw new RuntimeException("저장된 데이터가 너무 많습니다");
        spawnLocationList.add(location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
        spawnLocationData.set("spawnLocation." + mapName, spawnLocationList);
        saveData();
    }
    public static void removeSpawnLocation(String mapName, Location location) {
        List<String> spawnLocationList = getSpawnLocation(mapName);
        if (spawnLocationList.contains(location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ())) {
            spawnLocationList.remove(location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
            spawnLocationData.set("spawnLocation." + mapName, spawnLocationList);
            saveData();
        }
    } public static void removeAllSpawnLocation(String mapName) {
        spawnLocationData.set("spawnLocation." + mapName, Collections.emptyList());
        saveData();
    }
}

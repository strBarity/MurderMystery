package main.gamehandler;

import main.Main;
import main.datahandler.SpawnLocationData;
import main.eventhandler.EventListener;
import main.timerhandler.CountdownTimer;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static main.Main.*;

public class MurderHandler {
    public enum BowType { DectectiveAlive, BowDrop, BowNotDrop }
    public enum WinType { INNOCENT_ALL_DIED, MURDER_DIED, TIMED_OUT, STOPPED }
    public static Player murderer = null;
    public static Player detective = null;
    public static String heroName = null;
    public static BowType bowType = BowType.DectectiveAlive;
    public static int innocentAlive = 0;
    public static int murderKills = 0;
    public static boolean gameStarted = false;
    public static final HashMap<Player, String> roleType = new HashMap<>();
    public static final List<Location> savedGoldBlock = new ArrayList<>();
    public static void startGame(@NotNull World w) {
        try {
            if (Bukkit.getOnlinePlayers().size() < 2) {
                SERVER.broadcastMessage(INDEX + "§c플레이어 수가 너무 적어 게임이 시작되지 않았습니다.");
                return;
            } else if (SpawnLocationData.getSpawnLocation(Main.CURRENTMAP.getName()).isEmpty()) {
                SERVER.broadcastMessage(INDEX + "§c지정된 스폰 위치가 없어 게임이 시작되지 않았습니다.");
                return;
            } gameStarted = true;
            innocentAlive = Bukkit.getOnlinePlayers().size() - 1;
            for (int z = 166; z <= 187; z++)
                for (int x = 103; x <= 134; x++)
                    for (int y = 80; y <= 98; y++) if (w.getBlockAt(x, y, z).getType().equals(Material.GOLD_BLOCK)) {
                            w.getBlockAt(x, y, z).setType(Material.AIR);
                            savedGoldBlock.add(new Location(Main.CURRENTMAP, x, y, z));
                        }
            for (int x = 107; x <= 117; x++)
                for (int y = 88; y <= 95; y++) w.getBlockAt(x, y, 196).setType(Material.AIR);
            for (int x = 107; x <= 117; x++)
                for (int y = 88; y <= 95; y++) w.getBlockAt(x, y, 156).setType(Material.AIR);
            for (int z = 175; z <= 177; z++)
                for (int x = 98; x <= 99; x++) w.getBlockAt(x, 97, z).setType(Material.AIR);
            for (int z = 174; z <= 178; z++)
                for (int y = 90; y <= 95; y++) w.getBlockAt(96, y, z).setType(Material.AIR);
            Location[] locations = new Location[SpawnLocationData.getSpawnLocation(Main.CURRENTMAP.getName()).size()];
            int l=0;
            for (String s : SpawnLocationData.getSpawnLocation(Main.CURRENTMAP.getName())) {
                final String[] parts = s.split(",");
                locations[l] = new Location(Main.CURRENTMAP, Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                l++;
            } Random random = new Random();
            for (int i = 0; i < locations.length; i++) {
                int r = random.nextInt(locations.length - 1);
                Location tmp = locations[0];
                locations[0] = locations[r];
                locations[r] = tmp;
            } int n = 0;
            Player[] players = new Player[Bukkit.getOnlinePlayers().size()];
            ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), "");
            team.setCollisionRule(ScoreboardTeamBase.EnumTeamPush.NEVER);
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
            team.setCanSeeFriendlyInvisibles(false);
            ArrayList<String> playerToAdd = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) playerToAdd.add(p.getName());
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
                connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
                connection.sendPacket(new PacketPlayOutScoreboardTeam(team, playerToAdd, 3));
                p.removePotionEffect(PotionEffectType.INVISIBILITY);
                p.getInventory().clear();
                players[n] = p;
                p.teleport(locations[n % locations.length]);
                n++;
            } int nn = random.nextInt(3);
            for (int i = 0; i < players.length + nn; i++) {
                int r = random.nextInt(players.length - 1);
                Player tmp = players[0];
                players[0] = players[r];
                players[r] = tmp;
            } roleType.put(players[0], "§c살인자");
            murderer = players[0];
            players[0].sendTitle("§c역할: 살인자", "§e모든 플레이어를 죽이세요!", 0, 100, 20);
            roleType.put(players[1], "§b탐정");
            detective = players[1];
            players[1].sendTitle("§b역할: 탐정", "§e살인자를 찾아 처치하세요!", 0, 100, 20);
            ItemStack knife = new ItemStack(Material.IRON_SWORD);
            ItemMeta knifeM = knife.getItemMeta();
            knifeM.setDisplayName("§c칼");
            knifeM.setUnbreakable(true);
            knifeM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            knife.setItemMeta(knifeM);
            ItemStack bow = new ItemStack(Material.BOW);
            ItemMeta bowM = bow.getItemMeta();
            bowM.setDisplayName("§b활");
            bowM.setUnbreakable(true);
            bowM.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            bow.setItemMeta(bowM);
            SCHEDULER.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                Bukkit.broadcastMessage(INDEX + "§e살인자가 §b5§e초 후에 칼을 얻습니다!");
                for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
            }, 100L);
            SCHEDULER.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                Bukkit.broadcastMessage(INDEX + "§e살인자가 §b4§e초 후에 칼을 얻습니다!");
                for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
            }, 120L);
            SCHEDULER.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                Bukkit.broadcastMessage(INDEX + "§e살인자가 §b3§e초 후에 칼을 얻습니다!");
                for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
            }, 140L);
            SCHEDULER.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                Bukkit.broadcastMessage(INDEX + "§e살인자가 §b2§e초 후에 칼을 얻습니다!");
                for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
            }, 160L);
            SCHEDULER.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                Bukkit.broadcastMessage(INDEX + "§e살인자가 §b1§e초 후에 칼을 얻습니다!");
                for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
            }, 180L);
            SCHEDULER.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                players[0].getInventory().setItem(1, knife);
                players[1].getInventory().setItem(1, bow);
                players[1].getInventory().setItem(9, new ItemStack(Material.ARROW));
                Bukkit.broadcastMessage(INDEX + "§e살인자가 칼을 얻었습니다!");
                for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
            }, 200L);
            for (int i = 2; i < players.length; i++) {
                roleType.put(players[i], "§a시민");
                players[i].sendTitle("§a역할: 시민", "§e최대한 오래 살아남으세요!", 0, 100, 20);
            }
        } catch (Exception e) {
            printException(e);
        }
    }
    public static void stopGame(@NotNull World w, @NotNull Boolean innocentWin, @NotNull WinType winType, @Nullable EventListener.DeathCause murderDeathCause) {
        try {
            gameStarted = false;
            for (Location l : savedGoldBlock) w.getBlockAt(l).setType(Material.GOLD_BLOCK);
            for (int x = 107; x <= 117; x++)
                for (int y = 88; y <= 95; y++) w.getBlockAt(x, y, 196).setType(Material.IRON_FENCE);
            for (int x = 107; x <= 117; x++) 
                for (int y = 88; y <= 95; y++) w.getBlockAt(x, y, 156).setType(Material.IRON_FENCE);
            for (int z = 175; z <= 177; z++)
                for (int x = 98; x <= 99; x++) w.getBlockAt(x, 97, z).setType(Material.EMERALD_BLOCK);
            for (int z = 174; z <= 178; z++)
                for (int y = 90; y <= 95; y++) w.getBlockAt(96, y, z).setType(Material.IRON_FENCE);
            w.getBlockAt(98, 98, 176).setType(Material.CAKE_BLOCK);
            String boarder = "§a--------------------------------------------------------------------------------";
            Bukkit.broadcastMessage(boarder + "\n                                   §f§l머더 미스터리");
            if (innocentWin) {
                Bukkit.broadcastMessage("\n                                   §f§l승자: §a플레이어");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p == murderer) {
                        if (winType.equals(WinType.MURDER_DIED)) {
                            if (murderDeathCause == null) p.sendTitle("§c패배했습니다!", "§e지정되지 않은 사망 사유입니다.", 0, 200, 0);
                            else if (murderDeathCause.equals(EventListener.DeathCause.INNOCENT_SHOOT)) p.sendTitle("§c패배했습니다!", "§e당신은 사망했습니다!", 0, 200, 0);
                            else if (murderDeathCause.equals(EventListener.DeathCause.DROWNED)) p.sendTitle("§c패배했습니다!", "§e당신은 익사했습니다!", 0, 200, 0);
                            else if (murderDeathCause.equals(EventListener.DeathCause.PORTAL)) p.sendTitle("§c패배했습니다!", "§e당신은 포탈에 빠졌습니다!", 0, 200, 0);
                        }
                        else if (winType.equals(WinType.TIMED_OUT)) p.sendTitle("§c패배했습니다!", "§e시간이 다 되었습니다!", 0, 200, 0);
                    } else {
                        p.sendTitle("§a승리했습니다!", "§e살인자가 멈췄습니다!", 0, 200, 0);
                    }
                }
            } else {
                Bukkit.broadcastMessage("\n                                   §f§l승자: §c살인자");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p == murderer) p.sendTitle("§a승리했습니다!", "§e모든 플레이어를 처치했습니다!", 0, 200, 0);
                    else p.sendTitle("§c패배했습니다!", "§e모든 시민이 사망했습니다!", 0, 200, 0);
                }
            } if (bowType == BowType.DectectiveAlive) Bukkit.broadcastMessage(String.format("\n                                    §7탐정: %s%s", EventListener.rankColor.get(detective), detective.getName()));
            else Bukkit.broadcastMessage(String.format("\n                                    §7탐정: %s§m%s", EventListener.rankColor.get(detective), detective.getName()));
            if (innocentWin) Bukkit.broadcastMessage(String.format("                                §7살인자: %s%s§7 (§6%d§7 킬)", EventListener.rankColor.get(murderer), murderer.getName(), murderKills));
            else Bukkit.broadcastMessage(String.format("                                     §7살인자: %s§m%s§7 (§6%d§7 킬)", EventListener.rankColor.get(murderer), murderer.getName(), murderKills));
            if (heroName != null) Bukkit.broadcastMessage("                                    §7영웅: §f제작중");
            Bukkit.broadcastMessage("\n" + boarder);
            CountdownTimer.setStartCountdown(70L);
            SCHEDULER.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                murderer = null;
                detective = null;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    for (int i : EventListener.summonedNpcsId) ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(i));
                    roleType.remove(p);
                    p.getInventory().clear();
                    p.setAllowFlight(false);
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.teleport(new Location(p.getWorld(), 104.5, 88.0, 176.5, 90F, 0F));
                    p.setGameMode(GameMode.ADVENTURE);
                    ItemStack i = new ItemStack(Material.BED, 1, (short) 14);
                    ItemMeta im = i.getItemMeta();
                    im.setDisplayName("§c게임 나가기 §7(우클릭)");
                    im.setLore(Arrays.asList("§a우클릭 시 3초 후 로비로 돌아갑니다.", "§7다시 우클릭을 누르면 취소됩니다.", "", "§e클릭해서 로비로 돌아가기"));
                    i.setItemMeta(im);
                    i.setData(new MaterialData(Material.BED));
                    p.getInventory().setItem(8, i);
                    if (!EventListener.spinStandId.isEmpty()) {
                        for (Map.Entry<ArmorStand, Integer> entry : EventListener.spinStandId.entrySet()) {
                            entry.getKey().remove();
                            SCHEDULER.cancelTask(entry.getValue());
                        }
                    }
                }
            }, 200L);
        } catch (Exception e) {
            printException(e);
        }
    }
}

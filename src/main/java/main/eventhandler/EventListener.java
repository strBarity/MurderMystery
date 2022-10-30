package main.eventhandler;

import main.Main;
import main.datahandler.SpawnLocationData;
import main.gamehandler.MurderHandler;
import main.timerhandler.CountdownTimer;
import main.timerhandler.ExitTimer;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static main.Main.*;

public class EventListener implements Listener {
    public static final int startPlayerCount = 4;
    public static final HashMap<Player, Integer> boardId = new HashMap<>();
    public static final HashMap<Player, Integer> antiOutMapId = new HashMap<>();
    public static final HashMap<Player, String> rankType = new HashMap<>();
    public static final HashMap<Player, ChatColor> rankColor = new HashMap<>();
    public static void registerAntiOutMap(Player p) {
        int i = s.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            Location l = p.getLocation();
            if (l.getX() < 29 || l.getX() > 219 || l.getY() < 24 || l.getY() > 125 || l.getZ() < 22 || l.getZ() > 330) {
                if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) p.teleport(new Location(p.getWorld(), 104.5, 88.0, 176.5, 90F, 0F));
                else {
                    p.kickPlayer(Main.INDEX + "§4비정상적인 맵 탈출이 감지되었습니다.");
                }
                if (Bukkit.getOnlinePlayers() != null) for (Player o : Bukkit.getOnlinePlayers()) {
                    if (o.isOp()) o.sendMessage(Main.INDEX + p.getName() + "§c님이 §6" + p.getGameMode().toString() + " §c모드에서 맵 탈출을 시도했습니다.");
                }
            }
        }, 0, 1L);
        antiOutMapId.put(p, i);
    } public static void registerBoard(Player p) {
        int i = s.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            final Objective objective = board.registerNewObjective("§e§l머더 미스터리", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score t = objective.getScore("§7" + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + "§8 Murder");
            if (MurderHandler.gameStarted) {
                t.setScore(11);
                Score b1 = objective.getScore(" ");
                b1.setScore(10);
                Score r = objective.getScore("§f역할: " + MurderHandler.roleType.get(p));
                r.setScore(9);
                Score b2 = objective.getScore("  ");
                b2.setScore(8);
                Score in = objective.getScore("§f남은 시민: §a" + MurderHandler.innocentAlive + "§f명");
                in.setScore(7);
                Score ti = objective.getScore("§f남은 시간: §a" + CountdownTimer.getGameCountdownMin() + ":" + CountdownTimer.getGameCountdownSec());
                if (CountdownTimer.getGameCountdownSec() < 10) ti = objective.getScore("§f남은 시간: §a" + CountdownTimer.getGameCountdownMin() + ":0" + CountdownTimer.getGameCountdownSec());
                ti.setScore(6);
                Score b3 = objective.getScore("   ");
                b3.setScore(5);
                Score b = objective.getScore("§f탐정: §a생존");
                if (MurderHandler.bowType == MurderHandler.BowType.BowDrop) {
                    b = objective.getScore("§f활: §c떨어짐");
                } else if (MurderHandler.bowType == MurderHandler.BowType.BowNotDrop) {
                    b = objective.getScore("§f활: §a떨어지지 않음");
                } b.setScore(4);
                Score b4 = objective.getScore("     ");
                b4.setScore(3);
                Score m = objective.getScore("§f맵: §a" + p.getWorld().getName());
                m.setScore(2);
            } else {
                t.setScore(9);
                Score b1 = objective.getScore(" ");
                b1.setScore(8);
                Score m = objective.getScore("§f맵: §a" + p.getWorld().getName());
                m.setScore(7);
                Score l = objective.getScore("§f플레이어: §a" + s.getOnlinePlayers().size() + "/32");
                l.setScore(6);
                Score b2 = objective.getScore("  ");
                b2.setScore(5);
                if (Bukkit.getOnlinePlayers().size() >= startPlayerCount) {
                    Score s = objective.getScore("§a" + CountdownTimer.startCountdown + "초 §f후 시작");
                    s.setScore(4);
                } else {
                    Score s = objective.getScore("§f플레이어를 기다리는 중...");
                    s.setScore(4);
                }
                Score b3 = objective.getScore("   ");
                b3.setScore(3);
                Score mo = objective.getScore("§f모드: §a일반");
                mo.setScore(2);
            } Score b4 = objective.getScore("    ");
            b4.setScore(1);
            Score a = objective.getScore("§eChoco24h");
            a.setScore(0);
            p.setScoreboard(board);
        }, 0, 20L);
        boardId.put(p, i);
    } @EventHandler(priority=EventPriority.HIGHEST)
    public void onAttack(@NotNull EntityDamageByEntityEvent e) {
        try {
            e.setCancelled(true);
            if (!e.getDamager().getType().equals(EntityType.PLAYER) || !e.getEntity().getType().equals(EntityType.PLAYER)) return;
            Player attacker = (Player) e.getDamager();
            Player victim = (Player) e.getEntity();
            if (attacker.getInventory().getItemInMainHand() == null || attacker.getInventory().getItemInMainHand().getItemMeta() == null || attacker.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null) return;
            if (MurderHandler.murderer == e.getDamager() && attacker.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("칼")) {
                MurderHandler.roleType.put(victim, "§7사망");
                victim.sendMessage(INDEX + "§c죽었습니다! §e이제부터 관전자 상태입니다.");
                victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3, 0), true);
                victim.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
                victim.setAllowFlight(true);
                for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, SoundCategory.MASTER, 100F, 1F);
            }
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    } @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() == null) return;
            else if (e.getCurrentItem().getItemMeta() == null) return;
            else if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            if (name.contains("게임 나가기") || name.contains("칼") || name.equals("활")) {
                e.setCancelled(true);
                if (name.contains("게임 나가기")) {
                    if (ExitTimer.getExitTimer().containsKey(p)) {
                        ExitTimer.getExitTimer().remove(p);
                        p.sendMessage(Main.INDEX + "로비로 이동이 취소되었습니다.");
                    } else {
                        ExitTimer.getExitTimer().put(p, 60);
                        p.sendMessage(Main.INDEX + "§e3초 후에 로비로 이동합니다. 취소하려면 다시 우클릭하세요.");
                    }
                }
            }
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    } @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    } @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent e) {
        try {
            Player p = e.getPlayer();
            if ((p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getItemMeta() != null && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName() != null && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("활"))) return;
            e.setCancelled(true);
            if (p.getInventory().getItemInMainHand() == null || p.getInventory().getItemInMainHand().getItemMeta() == null || p.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null) return;
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType().equals(Material.CAKE_BLOCK)) {
                    p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, SoundCategory.MASTER, 100, 1);
                    p.getWorld().spawnParticle(Particle.CRIT, 98.5, 98.5, 176.5, 10, 0.125, 0.125, 0.125, 3);
                }
            } if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("게임 나가기")) {
                if (ExitTimer.getExitTimer().containsKey(p)) {
                    ExitTimer.getExitTimer().remove(p);
                    p.sendMessage(Main.INDEX + "로비로 이동이 취소되었습니다.");
                } else {
                    ExitTimer.getExitTimer().put(p, 60);
                    p.sendMessage(Main.INDEX + "§e3초 후에 로비로 이동합니다. 취소하려면 다시 우클릭하세요.");
                }
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("스폰 위치 설정 도구")) {
                if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    SpawnLocationData.addSpawnLocation(p.getWorld().getName(), p.getLocation());
                    p.sendMessage(Main.INDEX + p.getWorld().getName() + " §a맵에서 §2(§a" + p.getLocation().getBlockX() + "§2, §a" + p.getLocation().getBlockY() + "§2, §a" + p.getLocation().getBlockZ() + "§2)§a를 스폰 위치에 추가했습니다.");
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.75F, 1);
                } if (e.getAction().equals(Action.LEFT_CLICK_AIR)) {
                    boolean notRemoved = true;
                    for (int x = p.getLocation().getBlockX() - 2; x <= p.getLocation().getBlockX() + 2; x++) for (int y = p.getLocation().getBlockY() - 2; y <= p.getLocation().getBlockY() + 2; y++) for (int z = p.getLocation().getBlockZ() - 2; z <= p.getLocation().getBlockZ() + 2; z++) {
                        for (String s : SpawnLocationData.getSpawnLocation(p.getWorld().getName())) {
                            final String[] parts = s.split(",");
                            final int x2 = Integer.parseInt(parts[0]);
                            final int y2 = Integer.parseInt(parts[1]);
                            final int z2 = Integer.parseInt(parts[2]);
                            if (x == x2 && y == y2 && z == z2) {
                                notRemoved = false;
                                SpawnLocationData.removeSpawnLocation(p.getWorld().getName(), new Location(p.getWorld(), x2, y2, z2));
                                p.sendMessage(Main.INDEX + p.getWorld().getName() + " §e맵에서 §2(§a" + x2 + "§2, §a" + y2 + "§2, §a" + z2 + "§2)§e에 있는 스폰 위치를 제거했습니다.");
                                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.75F, 0);
                            }
                        }
                    } if (notRemoved) p.sendMessage(Main.INDEX + "§c제거할 스폰 위치가 근처에 없습니다.");
                }
            }
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    } @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    } @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        try {
            Player p = e.getPlayer();
            p.removePotionEffect(PotionEffectType.INVISIBILITY);
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().clear();
            p.teleport(new Location(p.getWorld(), 104.5, 88.0, 176.5, 90F, 0F));
            if (p.getUniqueId().toString().equals("604d2144-5577-4330-a2b4-dbe04e3b9cc3")) {
                rankType.put(p, "§b[MVP§c+§b] ");
                rankColor.put(p, ChatColor.AQUA);
                p.setPlayerListName("§b[MVP§c+§b] " + p.getName() + " ");
                e.setJoinMessage(Main.INDEX + "§b누군가...?§e가 참여했습니다. (§b" + Bukkit.getOnlinePlayers().size() + "§e/§b32§e)");
                for (Player o : Bukkit.getOnlinePlayers()) o.playSound(o.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 100, 1);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    s.broadcastMessage(Main.INDEX + "§c키가 너무 작아서 이름이 안보여요... 죄송합니다");
                    for (Player o : Bukkit.getOnlinePlayers()) o.playSound(o.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 100, 1);
                }, 60L);
            } else {
                if (p.isOp()) {
                    rankType.put(p, "§c[ADMIN] ");
                    rankColor.put(p, ChatColor.RED);
                    p.setPlayerListName("§c[ADMIN] " + p.getName() + " ");
                } else {
                    rankType.put(p, "§a[VIP] ");
                    rankColor.put(p, ChatColor.GREEN);
                    p.setPlayerListName("§a[VIP] " + p.getName() + " ");
                }
            } if (MurderHandler.gameStarted) {
                e.setJoinMessage(null);
                p.setAllowFlight(true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
                MurderHandler.roleType.put(p, "§8관전자");
            } else {
                ItemStack i = new ItemStack(Material.BED, 1, (short) 14);
                ItemMeta im = i.getItemMeta();
                im.setDisplayName("§c게임 나가기 §7(우클릭)");
                im.setLore(Arrays.asList("§a우클릭 시 3초 후 로비로 돌아갑니다.", "§7다시 우클릭을 누르면 취소됩니다.", "", "§e클릭해서 로비로 돌아가기"));
                i.setItemMeta(im);
                i.setData(new MaterialData(Material.BED));
                p.getInventory().setItem(8, i);
                if (!p.getUniqueId().toString().equals("604d2144-5577-4330-a2b4-dbe04e3b9cc3")) e.setJoinMessage(Main.INDEX + rankColor.get(p) + p.getName() + "§e님이 참여했습니다! (§b" + Bukkit.getOnlinePlayers().size() + "§e/§b32§e)");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100, 1);
            } p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(Double.MAX_VALUE);
            registerBoard(p);
            registerAntiOutMap(p);
            SpawnLocationData.registerSLWand(p);
            ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), p.getName());
            team.setCollisionRule(ScoreboardTeamBase.EnumTeamPush.NEVER);
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
            team.setCanSeeFriendlyInvisibles(false);
            ArrayList<String> playerToAdd = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) playerToAdd.add(player.getName());
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
                connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
                connection.sendPacket(new PacketPlayOutScoreboardTeam(team, playerToAdd, 3));
            }
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    } @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        try {
            Player p = e.getPlayer();
            if (MurderHandler.gameStarted) e.setQuitMessage(null);
            else e.setQuitMessage(Main.INDEX + rankColor.get(p) + p.getName() + "§e님이 나갔습니다! (§b" + (Bukkit.getOnlinePlayers().size() - 1) + "§e/§b32§e)");
            Bukkit.getScheduler().cancelTask(boardId.get(e.getPlayer()));
            Bukkit.getScheduler().cancelTask(antiOutMapId.get(e.getPlayer()));
            Bukkit.getScheduler().cancelTask(SpawnLocationData.slWandId.get(e.getPlayer()));
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    } @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent e) {
        try {
            e.setFormat(rankType.get(e.getPlayer()) + e.getPlayer().getName() + "§7: §f" + e.getMessage());
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    } @EventHandler
    public void onDamage(@NotNull EntityDamageEvent e) {
        try {
            if (e.getEntity().getType().equals(EntityType.PLAYER)) {
                e.setCancelled(true);
                Player p = (Player) e.getEntity();
                p.setHealth(p.getHealthScale());
                if (p.getFireTicks() > 0) p.setFireTicks(0);
            }
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    } @EventHandler
    public void onFood(@NotNull FoodLevelChangeEvent e) {
        try {
            e.setCancelled(true);
            Player p = (Player) e.getEntity();
            p.setFoodLevel(20);
        } catch (Exception exception) {
            printException(getClassName(), getMethodName(), exception);
        }
    }
}

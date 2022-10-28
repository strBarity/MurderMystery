package main.eventhandler;

import main.Main;
import main.timerhandler.CountdownTimer;
import main.timerhandler.ExitTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

import static main.Main.s;

public class EventListener implements Listener {
    public static void registerBoard(Player p) {
        int i = s.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            final Objective objective = board.registerNewObjective("§e§l머더 미스터리", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score t = objective.getScore("§7" + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + "§8 Murder");
            t.setScore(9);
            Score b1 = objective.getScore(" ");
            b1.setScore(8);
            Score m = objective.getScore("§f맵: §a" + p.getWorld().getName());
            m.setScore(7);
            Score l = objective.getScore("§f플레이어: §a" + s.getOnlinePlayers().size() + "/32");
            l.setScore(6);
            Score b2 = objective.getScore("  ");
            b2.setScore(5);
            if (Bukkit.getOnlinePlayers().size() > 2) {
                Score s = objective.getScore("§a" + CountdownTimer.getStartCountdown() + "초 §f후 시작");
                s.setScore(4);
            } else {
                Score s = objective.getScore("§f플레이어를 기다리는 중...");
                s.setScore(4);
            }
            Score b3 = objective.getScore("   ");
            b3.setScore(3);
            Score mo = objective.getScore("§f모드: §a일반");
            mo.setScore(2);
            Score b4 = objective.getScore("    ");
            b4.setScore(1);
            Score a = objective.getScore("§eChoco24h");
            a.setScore(0);
            p.setScoreboard(board);
        }, 0, 20L);
        taskId.put(p, i);
    }
    public static final HashMap<Player, Integer> taskId = new HashMap<>();
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onAttack(@NotNull EntityDamageByEntityEvent e) {
        try {
            e.setCancelled(true);
            if (e.getDamager().getType().equals(EntityType.PLAYER) && e.getEntity().getType().equals(EntityType.PLAYER)) {
                e.getDamager().sendMessage(Main.INDEX + "너는 방금 사람을 찔렀다!!");
            }
        } catch (Exception exception) {
            s.broadcastMessage(Main.INDEX + "§6onAttack§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
        }
    } @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem().getType().equals(Material.BED) && e.getCurrentItem().getItemMeta().getDisplayName().contains("게임 나가기")) {
                e.setCancelled(true);
                if (ExitTimer.getExitTimer().containsKey(p)) {
                    ExitTimer.getExitTimer().remove(p);
                    p.sendMessage(Main.INDEX + "로비로 이동이 취소되었습니다.");
                } else {
                    ExitTimer.getExitTimer().put(p, 60);
                    p.sendMessage(Main.INDEX + "§e3초 후에 로비로 이동합니다. 취소하려면 다시 우클릭하세요.");
                }
            }
        } catch (Exception exception) {
            s.broadcastMessage(Main.INDEX + "§6onInventoryClick§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
        }
    } @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception exception) {
            s.broadcastMessage(Main.INDEX + "§6onSwap§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
        }
    } @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent e) {
        try {
            Player p = e.getPlayer();
            if (p.getInventory().getItemInMainHand().getType().equals(Material.BED) && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("게임 나가기")) {
                e.setCancelled(true);
                if (ExitTimer.getExitTimer().containsKey(p)) {
                    ExitTimer.getExitTimer().remove(p);
                    p.sendMessage(Main.INDEX + "로비로 이동이 취소되었습니다.");
                } else {
                    ExitTimer.getExitTimer().put(p, 60);
                    p.sendMessage(Main.INDEX + "§e3초 후에 로비로 이동합니다. 취소하려면 다시 우클릭하세요.");
                }
                return;
            }
            if (e.getClickedBlock() != null) e.setCancelled(true);
        } catch (Exception exception) {
            s.broadcastMessage(Main.INDEX + "§6onInteract§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
        }
    } @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception exception) {
            s.broadcastMessage(Main.INDEX + "§6onDrop§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
        }
    }
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        try {
            e.getPlayer().getInventory().clear();
            e.getPlayer().teleport(new Location(e.getPlayer().getWorld(), 104.5, 88.0, 176.5, 90F, 0F));
            if (e.getPlayer().getUniqueId().toString().equals("604d2144-5577-4330-a2b4-dbe04e3b9cc3")) {
                e.setJoinMessage(Main.INDEX + "누군가...?가 접속했습니다.");
                for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100F, 1F);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    s.broadcastMessage(Main.INDEX + "§e키가 너무 작아서 이름이 안보여요... 죄송합니다");
                    for (Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 100F, 1F);
                }, 60L);
            } else e.setJoinMessage(Main.INDEX + e.getPlayer().getName() + "님이 접속했습니다.");
            ItemStack i = new ItemStack(Material.BED);
            ItemMeta im = i.getItemMeta();
            im.setDisplayName("§c게임 나가기 §7(우클릭)");
            im.setLore(Arrays.asList("§a우클릭 시 3초 후 로비로 돌아갑니다.", "§7다시 우클릭을 누르면 취소됩니다.", "", "§e클릭해서 로비로 돌아가기"));
            i.setItemMeta(im);
            e.getPlayer().getInventory().setItem(8, i);
            registerBoard(e.getPlayer());
        } catch (Exception exception) {
            s.broadcastMessage(Main.INDEX + "§6onJoin§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
        }
    } @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        try {
            e.setQuitMessage(Main.INDEX + e.getPlayer().getName() + "님이 퇴장했습니다.");
            Bukkit.getScheduler().cancelTask(taskId.get(e.getPlayer()));
        } catch (NullPointerException n) {
            for (Player p : Bukkit.getOnlinePlayers()) if (p.isOp()) p.sendMessage(Main.INDEX + "§7" + e.getPlayer().getName() + "에게 할당된 스코어보드 작업이 없어 NullPointerException이 처리되었습니다.");
        } catch (Exception exception) {
            s.broadcastMessage(Main.INDEX + "§6onQuit§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
        }
    } @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent e) {
        try {
            e.setCancelled(true);
            s.broadcastMessage("| " + e.getPlayer().getName() + ": " + e.getMessage());
        } catch (Exception exception) {
            s.broadcastMessage(Main.INDEX + "§6onChat§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
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
            s.broadcastMessage(Main.INDEX + "§6onDamage§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
        }
    } @EventHandler
    public void onFood(@NotNull FoodLevelChangeEvent e) {
        try {
            e.setCancelled(true);
            Player p = (Player) e.getEntity();
            p.setFoodLevel(20);
        } catch (Exception exception) {
            s.broadcastMessage(Main.INDEX + "§6onFood§c에서 오류가 발생했습니다: §4" + exception.getClass().getSimpleName());
            exception.printStackTrace();
        }
    }
}

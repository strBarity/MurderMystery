package main.eventhandler;

import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class EventListener implements Listener {
    private static final HashMap<Player, Integer> taskId = new HashMap<>();
    @EventHandler (priority=EventPriority.HIGH)
    public void onAttack(@NotNull EntityDamageByEntityEvent e) {
        e.setCancelled(true);
        if (e.getDamager().getType().equals(EntityType.PLAYER) && e.getEntity().getType().equals(EntityType.PLAYER)) {
            e.getDamager().sendMessage(Main.INDEX + "너는 방금 사람을 찔렀다!!");
        }
    } @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        e.setJoinMessage(Main.INDEX + e.getPlayer().getName() + "님이 접속했습니다.");
        int id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            final Scoreboard board = manager.getNewScoreboard();
            final Objective objective = board.registerNewObjective("§e§l머더 미스터리", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score score1 = objective.getScore(" ");
            score1.setScore(2);
            Score nickname = objective.getScore("닉네임: " + e.getPlayer().getName());
            nickname.setScore(1);
            Score score2 = objective.getScore("  ");
            score2.setScore(0);
            e.getPlayer().setScoreboard(board);
        }, 0, 20L);
        taskId.put(e.getPlayer(), id);
    } @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        Bukkit.getScheduler().cancelTask(taskId.get(e.getPlayer()));
        e.setQuitMessage(Main.INDEX + e.getPlayer().getName() + "님이 퇴장했습니다.");
    } @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Bukkit.getServer().broadcastMessage("| " + e.getPlayer().getName() + ": " + e.getMessage());
    } @EventHandler
    public void onDamage(@NotNull EntityDamageEvent e) {
        if (e.getEntity().getType().equals(EntityType.PLAYER)) {
            e.setCancelled(true);
            Player p = (Player) e.getEntity();
            p.setHealth(p.getHealthScale());
        }
    } @EventHandler
    public void onHunger(@NotNull FoodLevelChangeEvent e) {
        Player p = (Player) e.getEntity();
        p.setFoodLevel(20);
    }
}

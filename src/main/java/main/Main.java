package main;

import main.cmdhandler.CMDHandler;
import main.datahandler.SpawnLocationData;
import main.eventhandler.EventListener;
import main.timerhandler.CMDCooldownTimer;
import main.timerhandler.CountdownTimer;
import main.timerhandler.ExitTimer;
import net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.ScoreboardTeam;
import net.minecraft.server.v1_12_R1.ScoreboardTeamBase;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public final class Main extends JavaPlugin {
    public static final World CURRENTMAP = Bukkit.getWorld("archives");

    public static final Server s = Bukkit.getServer();
    public static final String INDEX = "§f[§cMurder§f] ";
    @Override
    public void onEnable() {
        try {
            SpawnLocationData.loadData();
            Bukkit.getPluginManager().registerEvents(new EventListener(), this); /* 이벤트 리스너 등록 */
            Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a플러그인이 활성화되었습니다."); /* 플러그인 활성화 메시지 전송 */
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new CountdownTimer(), 0L, 20L); /* 타이머 등록 & 시작 */
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ExitTimer(), 0L, 1L); /* 타이머 등록 & 시작 */
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new CMDCooldownTimer(), 0L, 20L); /* 타이머 등록 & 시작 */
            this.getDescription().getCommands().keySet().forEach(s -> { /* 커맨드 & 탭컴플리터 등록 */
                Objects.requireNonNull(getCommand(s)).setExecutor(new CMDHandler()); /* 커맨드 처리 클래스 등록 */
                Objects.requireNonNull(getCommand(s)).setTabCompleter(new CMDHandler()); /* 탭컴플리터(커맨드 제안) 등록 */
            });
            if (Bukkit.getOnlinePlayers() != null) {
                ArrayList<String> playerToAdd = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) playerToAdd.add(p.getName());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.setGameMode(GameMode.ADVENTURE);
                    ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), p.getName());
                    team.setCollisionRule(ScoreboardTeamBase.EnumTeamPush.NEVER);
                    team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
                    team.setCanSeeFriendlyInvisibles(false);
                    EventListener.registerBoard(p);
                    EventListener.registerAntiOutMap(p);
                    SpawnLocationData.registerSLWand(p);
                    PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                    connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
                    connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
                    connection.sendPacket(new PacketPlayOutScoreboardTeam(team, playerToAdd, 3));
                    if (p.getUniqueId().toString().equals("604d2144-5577-4330-a2b4-dbe04e3b9cc3")) {
                        EventListener.rankType.put(p, "§b[MVP§c+§b] ");
                        EventListener.rankColor.put(p, ChatColor.AQUA);
                        p.setPlayerListName("§b[MVP§c+§b] " + p.getName() + " ");
                    } else if (p.isOp()) {
                        EventListener.rankType.put(p, "§c[ADMIN] ");
                        EventListener.rankColor.put(p, ChatColor.RED);
                        p.setPlayerListName("§c[ADMIN] " + p.getName() + " ");
                    } else {
                        EventListener.rankType.put(p, "§a[VIP] ");
                        EventListener.rankColor.put(p, ChatColor.GREEN);
                        p.setPlayerListName("§a[VIP] " + p.getName() + " ");
                    }
                }
            }
        } catch (Exception e) {
            printException(getClassName(), getMethodName(), e);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§c플러그인이 비활성화되었습니다."); /* 플러그인 비활성화 메시지 전송 */
    }
    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }
    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
    public static void printException(@NotNull String className, @NotNull String methodName, @NotNull Exception e) {
        s.broadcastMessage(Main.INDEX + "§6" + className + "." + methodName + "()§c에서 오류가 발생했습니다.");
        if (e.getMessage() != null) s.broadcastMessage(Main.INDEX + "§4" + e.getClass().getName() + ": §c" + e.getMessage());
        else s.broadcastMessage(Main.INDEX + "§4" + e.getClass().getName() + ": §c알 수 없는 오류");
        e.printStackTrace();
    }
}

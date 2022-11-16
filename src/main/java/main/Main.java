package main;

import main.cmdhandler.CMDHandler;
import main.datahandler.SpawnLocationData;
import main.datahandler.UserLanguageData;
import main.eventhandler.EventListener;
import main.gamehandler.MurderHandler;
import main.stringhandler.TranslateHandler;
import main.timerhandler.CMDCooldownTimer;
import main.timerhandler.CountdownTimer;
import main.timerhandler.ExitTimer;
import main.timerhandler.ItemCooldownTimer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.ScoreboardTeam;
import net.minecraft.server.v1_12_R1.ScoreboardTeamBase;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static main.eventhandler.EventListener.*;

public final class Main extends JavaPlugin {
    public static final Server SERVER = Bukkit.getServer();
    public static final World CURRENTMAP = SERVER.getWorld("archives");
    public static final BukkitScheduler SCHEDULER = SERVER.getScheduler();
    public static final ConsoleCommandSender LOGGER = SERVER.getConsoleSender();
    public static final String INDEX = "§f[§cMurder§f] ";
    public static final List<String> EXCEPTIONS = new ArrayList<>();

    @Override
    public void onEnable() {
        try {
            SpawnLocationData.loadData();
            UserLanguageData.loadData();
            TranslateHandler.initialize();
            Bukkit.getPluginManager().registerEvents(new EventListener(), this); /* 이벤트 리스너 등록 */
            LOGGER.sendMessage(INDEX + "§a플러그인이 활성화되었습니다."); /* 플러그인 활성화 메시지 전송 */
            SCHEDULER.scheduleSyncRepeatingTask(this, new CountdownTimer(), 0L, 20L); /* 타이머 등록 & 시작 */
            SCHEDULER.scheduleSyncRepeatingTask(this, new ExitTimer(), 0L, 1L); /* 타이머 등록 & 시작 */
            SCHEDULER.scheduleSyncRepeatingTask(this, new CMDCooldownTimer(), 0L, 20L); /* 타이머 등록 & 시작 */
            SCHEDULER.scheduleSyncRepeatingTask(this, new ItemCooldownTimer(), 0L, 2L); /* 타이머 등록 & 시작 */
            this.getDescription().getCommands().keySet().forEach(s -> { /* 커맨드 & 탭컴플리터 등록 */
                Objects.requireNonNull(getCommand(s)).setExecutor(new CMDHandler()); /* 커맨드 처리 클래스 등록 */
                Objects.requireNonNull(getCommand(s)).setTabCompleter(new CMDHandler()); /* 탭컴플리터(커맨드 제안) 등록 */
            });
            if (SERVER.getOnlinePlayers() != null) {
                for (Player p : SERVER.getOnlinePlayers()) {
                    onlineNameList.add(p.getName());
                    p.setGameMode(GameMode.ADVENTURE);
                    ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), p.getName());
                    team.setCollisionRule(ScoreboardTeamBase.EnumTeamPush.NEVER);
                    team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
                    team.setCanSeeFriendlyInvisibles(false);
                    registerBoard(p);
                    registerAntiOutMap(p);
                    mainScoreboardSet(p);
                    SpawnLocationData.registerSLWand(p);
                    if (p.getUniqueId().toString().equals("604d2144-5577-4330-a2b4-dbe04e3b9cc3")) {
                        rankType.put(p, "§b[MVP§c+§b] ");
                        rankColor.put(p, ChatColor.AQUA);
                        p.setPlayerListName("§b[MVP§c+§b] " + p.getName() + " ");
                    } else if (p.isOp()) {
                        rankType.put(p, "§c[ADMIN] ");
                        rankColor.put(p, ChatColor.RED);
                        p.setPlayerListName("§c[ADMIN] " + p.getName() + " ");
                    } else {
                        rankType.put(p, "§a[VIP] ");
                        rankColor.put(p, ChatColor.GREEN);
                        p.setPlayerListName("§a[VIP] " + p.getName() + " ");
                    }
                }
            }
        } catch (Exception e) {
            printException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            if (MurderHandler.gameStarted) {
                for (Location l : MurderHandler.savedGoldBlock)
                    CURRENTMAP.getBlockAt(l).setType(Material.GOLD_BLOCK);
                for (int x = 107; x <= 117; x++)
                    for (int y = 88; y <= 95; y++) CURRENTMAP.getBlockAt(x, y, 196).setType(Material.IRON_FENCE);
                for (int x = 107; x <= 117; x++)
                    for (int y = 88; y <= 95; y++) CURRENTMAP.getBlockAt(x, y, 156).setType(Material.IRON_FENCE);
                for (int z = 175; z <= 177; z++)
                    for (int x = 98; x <= 99; x++) CURRENTMAP.getBlockAt(x, 97, z).setType(Material.EMERALD_BLOCK);
                for (int z = 174; z <= 178; z++)
                    for (int y = 90; y <= 95; y++) CURRENTMAP.getBlockAt(96, y, z).setType(Material.IRON_FENCE);
                CURRENTMAP.getBlockAt(98, 98, 176).setType(Material.CAKE_BLOCK);
            }
            if (!summonedNpcsId.isEmpty())
                for (Player p : SERVER.getOnlinePlayers())
                    for (int i : summonedNpcsId)
                        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(i));
            LOGGER.sendMessage(INDEX + "§c플러그인이 비활성화되었습니다."); /* 플러그인 비활성화 메시지 전송 */
        } catch (Exception e) {
            printException(e);
        }
    }
    public static void printException(@NotNull Exception e) {
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String errorName = e.getClass().getName();
        String errorMessage = e.getMessage();
        SERVER.broadcastMessage(format("%s§6%s.%s()§c에서 오류가 발생했습니다.", INDEX, className, methodName));
        if (e.getMessage() != null) {
            EXCEPTIONS.add(format("§4%s: §c%s\n%s§c> §6%s.%s() §4(§c%tT§4)", errorName, errorMessage, INDEX, className, methodName, new Date()));
            SERVER.broadcastMessage(format("%s§4%s: §c%s", INDEX, errorName, errorMessage));
        } else {
            EXCEPTIONS.add(format("§4%s: §c알 수 없는 오류 §7(오류 메시지 없음)\n%s§c> §6%s.%s() §4(§c%tT§4)", errorName, INDEX, className, methodName, new Date()));
            SERVER.broadcastMessage(format("%s§4%s: §c알 수 없는 오류", INDEX, errorName));
        }
        e.printStackTrace();
    }
}

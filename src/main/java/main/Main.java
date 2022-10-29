package main;

import main.cmdhandler.CMDHandler;
import main.datahandler.SpawnLocationData;
import main.eventhandler.EventListener;
import main.timerhandler.CMDCooldownTimer;
import main.timerhandler.CountdownTimer;
import main.timerhandler.ExitTimer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
            if (Bukkit.getOnlinePlayers() != null) for (Player p : Bukkit.getOnlinePlayers()) {
                EventListener.registerBoard(p);
                EventListener.registerAntiOutMap(p);
                SpawnLocationData.registerSLWand(p);
            }
        } catch (Exception e) {
            s.broadcastMessage(Main.INDEX + "§6onEnable§c에서 오류가 발생했습니다: §4" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§c플러그인이 비활성화되었습니다."); /* 플러그인 비활성화 메시지 전송 */
    }
}

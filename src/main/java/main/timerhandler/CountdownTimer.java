package main.timerhandler;

import main.Main;
import main.gamehandler.MurderHandler;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import static main.Main.*;
import static main.eventhandler.EventListener.startPlayerCount;

public class CountdownTimer implements Runnable {
    public static Long startCountdown = 60L;
    private static Long gameCountdownSec = 30L;
    private static Long gameCountdownMin = 4L;
    @Override
    public void run() {
        try {
            if (Bukkit.getOnlinePlayers().size() >= startPlayerCount && !MurderHandler.gameStarted) {
                if (startCountdown == 60L) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
                    Bukkit.broadcastMessage(Main.INDEX + "§e게임이 §b60§e초 후에 시작합니다!");
                }
                startCountdown--;
                if (startCountdown == 30L) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
                    Bukkit.broadcastMessage(Main.INDEX + "§e게임이 §b30§e초 후에 시작합니다!");
                }
                if (startCountdown == 10L) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
                    Bukkit.broadcastMessage(Main.INDEX + "§e게임이 §b10§e초 후에 시작합니다!");
                }
                if (startCountdown == 5L) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
                    Bukkit.broadcastMessage(Main.INDEX + "§e게임이 §b5§e초 후에 시작합니다!");
                }
                if (startCountdown == 4L) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
                    Bukkit.broadcastMessage(Main.INDEX + "§e게임이 §b4§e초 후에 시작합니다!");
                }
                if (startCountdown == 3L) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
                    Bukkit.broadcastMessage(Main.INDEX + "§e게임이 §b3§e초 후에 시작합니다!");
                }
                if (startCountdown == 2L) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
                    Bukkit.broadcastMessage(Main.INDEX + "§e게임이 §b2§e초 후에 시작합니다!");
                }
                if (startCountdown == 1L) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100F, 1F);
                    Bukkit.broadcastMessage(Main.INDEX + "§e게임이 §b1§e초 후에 시작합니다!");
                }
                if (startCountdown == 0L) MurderHandler.startGame(Main.CURRENTMAP);
            } else startCountdown = 60L;
            if (MurderHandler.gameStarted) {
                if (gameCountdownSec <= 0L && gameCountdownMin > 0L) {
                    gameCountdownSec = 59L;
                    gameCountdownMin--;
                } else if (gameCountdownSec > 0L) gameCountdownSec--;
                if (gameCountdownSec == 0L && gameCountdownMin == 0L)
                    MurderHandler.stopGame(Main.CURRENTMAP, true, MurderHandler.WinType.TIMED_OUT, null);
            } else {
                gameCountdownSec = 30L;
                gameCountdownMin = 4L;
            }
        } catch (Exception e) {
            printException(e);
        }
    } public static Long getGameCountdownSec() {
        return gameCountdownSec;
    } public static Long getGameCountdownMin() {
        return gameCountdownMin;
    }
}

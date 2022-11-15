package main.timerhandler;

import main.Main;
import main.gamehandler.MurderHandler;
import org.bukkit.entity.Player;

import static main.Main.SERVER;
import static main.Main.printException;
import static main.eventhandler.EventListener.startPlayerCount;
import static org.bukkit.Sound.BLOCK_NOTE_HAT;
import static org.bukkit.SoundCategory.MASTER;

public class CountdownTimer implements Runnable {
    private static Long startCountdown = 60L;
    private static Long gameCountdownSec = 30L;
    private static Long gameCountdownMin = 4L;

    public static Long getStartCountdown() {
        return startCountdown;
    }
    public static void setStartCountdown(Long startCountdown) {
        CountdownTimer.startCountdown = startCountdown;
    }
    public static Long getGameCountdownSec() {
        return gameCountdownSec;
    }
    public static Long getGameCountdownMin() {
        return gameCountdownMin;
    }

    @Override
    public void run() {
        try {
            if (SERVER.getOnlinePlayers().size() >= startPlayerCount && !MurderHandler.gameStarted) {
                if (startCountdown == 60L) {
                    for (Player p : SERVER.getOnlinePlayers())
                        p.playSound(p.getLocation(), BLOCK_NOTE_HAT, MASTER, 100F, 1F);
                    SERVER.broadcastMessage(Main.INDEX + "§e게임이 §b60§e초 후에 시작합니다!");
                }
                startCountdown--;
                if (startCountdown == 30L) {
                    for (Player p : SERVER.getOnlinePlayers())
                        p.playSound(p.getLocation(), BLOCK_NOTE_HAT, MASTER, 100F, 1F);
                    SERVER.broadcastMessage(Main.INDEX + "§e게임이 §b30§e초 후에 시작합니다!");
                }
                if (startCountdown == 10L) {
                    for (Player p : SERVER.getOnlinePlayers())
                        p.playSound(p.getLocation(), BLOCK_NOTE_HAT, MASTER, 100F, 1F);
                    SERVER.broadcastMessage(Main.INDEX + "§e게임이 §b10§e초 후에 시작합니다!");
                }
                if (startCountdown == 5L) {
                    for (Player p : SERVER.getOnlinePlayers())
                        p.playSound(p.getLocation(), BLOCK_NOTE_HAT, MASTER, 100F, 1F);
                    SERVER.broadcastMessage(Main.INDEX + "§e게임이 §b5§e초 후에 시작합니다!");
                }
                if (startCountdown == 4L) {
                    for (Player p : SERVER.getOnlinePlayers())
                        p.playSound(p.getLocation(), BLOCK_NOTE_HAT, MASTER, 100F, 1F);
                    SERVER.broadcastMessage(Main.INDEX + "§e게임이 §b4§e초 후에 시작합니다!");
                }
                if (startCountdown == 3L) {
                    for (Player p : SERVER.getOnlinePlayers())
                        p.playSound(p.getLocation(), BLOCK_NOTE_HAT, MASTER, 100F, 1F);
                    SERVER.broadcastMessage(Main.INDEX + "§e게임이 §b3§e초 후에 시작합니다!");
                }
                if (startCountdown == 2L) {
                    for (Player p : SERVER.getOnlinePlayers())
                        p.playSound(p.getLocation(), BLOCK_NOTE_HAT, MASTER, 100F, 1F);
                    SERVER.broadcastMessage(Main.INDEX + "§e게임이 §b2§e초 후에 시작합니다!");
                }
                if (startCountdown == 1L) {
                    for (Player p : SERVER.getOnlinePlayers())
                        p.playSound(p.getLocation(), BLOCK_NOTE_HAT, MASTER, 100F, 1F);
                    SERVER.broadcastMessage(Main.INDEX + "§e게임이 §b1§e초 후에 시작합니다!");
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
    }
}

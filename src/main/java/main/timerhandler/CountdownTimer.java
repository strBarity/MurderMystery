package main.timerhandler;

import main.Main;
import main.gamehandler.MurderHandler;
import org.bukkit.Bukkit;

public class CountdownTimer implements Runnable {
    private static Long startCountdown = 60L;
    private static Long gameCountdownSec = 30L;
    private static Long gameCountdownMin = 4L;
    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() > 2 && !MurderHandler.gameStarted) {
            startCountdown--;
            if (startCountdown == 0L) {
                MurderHandler.startGame(Bukkit.getWorld("world"));
            }
        } else startCountdown = 60L;
        if (MurderHandler.gameStarted) {
            if (gameCountdownSec <= 0L && gameCountdownMin > 0L) {
                gameCountdownSec = 59L;
                gameCountdownMin--;
            } else gameCountdownSec--;
            if (gameCountdownSec == 0L && gameCountdownMin == 0L) {
                MurderHandler.stopGame(Main.CURRENTMAP, true, true);
            }
        } else gameCountdownSec = 30L;
    } public static Long getStartCountdown() {
        return startCountdown;
    } public static Long getGameCountdownSec() {
        return gameCountdownSec;
    } public static Long getGameCountdownMin() {
        return gameCountdownMin;
    }
}

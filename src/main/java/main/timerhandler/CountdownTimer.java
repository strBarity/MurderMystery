package main.timerhandler;

import main.Main;
import main.gamehandler.MurderHandler;
import org.bukkit.Bukkit;

public class CountdownTimer implements Runnable {
    public static Long startCountdown = 60L;
    private static Long gameCountdownSec = 30L;
    private static Long gameCountdownMin = 4L;
    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() > 2 && !MurderHandler.gameStarted) {
            startCountdown--;
            if (startCountdown == 0L) {
                MurderHandler.startGame(Main.CURRENTMAP);
            }
        } else startCountdown = 60L;
        if (MurderHandler.gameStarted) {
            if (gameCountdownSec <= 0L && gameCountdownMin > 0L) {
                gameCountdownSec = 59L;
                gameCountdownMin--;
            } else if (gameCountdownSec > 0L) gameCountdownSec--;
            if (gameCountdownSec == 0L && gameCountdownMin == 0L) MurderHandler.stopGame(Main.CURRENTMAP, true, true);
        } else {
            gameCountdownSec = 30L;
            gameCountdownMin = 4L;
        }
    } public static Long getGameCountdownSec() {
        return gameCountdownSec;
    } public static Long getGameCountdownMin() {
        return gameCountdownMin;
    }
}

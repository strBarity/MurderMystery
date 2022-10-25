package main.timerhandler;

import org.bukkit.Bukkit;

public class CountdownTimer implements Runnable {
    private static Long startCountdown = 60L;
    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() > 1) startCountdown--;
        else startCountdown = 60L;
    } public static Long getStartCountdown() {
        return startCountdown;
    }
}

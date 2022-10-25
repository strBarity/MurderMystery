package main.cmdhandler;

import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MurderHandler {
    public static boolean gameStarted = false;
    public static void onCommand(CommandSender commandSender, String @NotNull [] args) {
        Player p = (Player) commandSender;
        final String w = Main.INDEX + "§c올바르지 않은 사용법입니다!";
        final String notOp = Main.INDEX + "§c이 명령어를 사용할 권한이 없습니다!";
        if (args.length < 2) {
            p.sendMessage(w);
            return;
        } switch (args[0]) {
            case "task":
                if (p.isOp() && args[1].equals("cancel")) {
                    Bukkit.getServer().getScheduler().cancelTasks(Main.getPlugin(Main.class));
                    p.sendMessage(Main.INDEX + "모든 작업을 취소했습니다.");
                } break;
            case "game":
                if (!p.isOp()) {
                    p.sendMessage(notOp);
                    break;
                } else if (args[1].equals("start")) {
                    if (!gameStarted) {
                        gameStarted = true;
                        Bukkit.getServer().broadcastMessage(Main.INDEX + "§b관리자가 게임을 시작시켰습니다.");
                    } else p.sendMessage(Main.INDEX + "게임이 이미 진행 중입니다.");
                } else if (args[1].equals("stop")) {
                    if (gameStarted) {
                        gameStarted = false;
                        Bukkit.getServer().broadcastMessage(Main.INDEX + "§c관리자가 게임을 중지시켰습니다.");
                    } else p.sendMessage(Main.INDEX + "게임이 진행 중이 아닙니다.");
                } break;
            default: p.sendMessage(w);
        }
    }
}

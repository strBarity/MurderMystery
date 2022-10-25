package main.cmdhandler;

import main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CMDHandler implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            if (s.equals("murder")) MurderHandler.onCommand(commandSender, strings);
        } catch (Exception e) {
            Bukkit.getServer().broadcastMessage(Main.INDEX + "§6onCommand§c에서 오류가 발생했습니다: §4" + e.getClass().getSimpleName());
            e.printStackTrace();
        } return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            if (s.equals("murder") || commandSender.isOp()) {
                if (strings.length == 1) return Arrays.asList("task", "game");
                if (strings.length == 2) {
                    if (strings[0].equals("task")) return Collections.singletonList("cancel");
                    else if (strings[0].equals("game")) return Arrays.asList("start", "stop");
                }
            }
        } catch (Exception e) {
            Bukkit.getServer().broadcastMessage(Main.INDEX + "§6onTabComplete§c에서 오류가 발생했습니다: §4" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return null;
    }
}


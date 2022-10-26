package main.cmdhandler;

import main.Main;
import main.timerhandler.CMDCooldownTimer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CMDHandler implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            if (CMDCooldownTimer.getCMDClickStack().containsKey((Player) commandSender)) {
                for (Map.Entry<Player, Integer> e : CMDCooldownTimer.getCMDClickStack().entrySet()) {
                    if (e.getKey().equals(commandSender) && e.getValue() >= 4) {
                        if (e.getValue() < 5) e.setValue(e.getValue()+1);
                        commandSender.sendMessage(Main.INDEX + "§c커맨드 사용이 너무 빠릅니다. 잠시 후에 다시 시도해주세요.");
                        return true;
                    } else {
                        e.setValue(e.getValue()+1);
                        break;
                    }
                }
            } else CMDCooldownTimer.getCMDClickStack().put((Player) commandSender, 1);
            if (s.equals("murder")) MurderTHandler.onCommand(commandSender, strings);
        } catch (Exception e) {
            Main.s.broadcastMessage(Main.INDEX + "§6onCommand§c에서 오류가 발생했습니다: §4" + e.getClass().getSimpleName());
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
            Main.s.broadcastMessage(Main.INDEX + "§6onTabComplete§c에서 오류가 발생했습니다: §4" + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return null;
    }
}


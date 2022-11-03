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

import static main.Main.*;

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
            if ("murder".equals(s)) {
                MurderTHandler.onCommand(commandSender, strings);
            }
        } catch (Exception e) {
            printException(getClassName(), getMethodName(), e);
        } return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            if (commandSender.isOp()) {
                if ("murder".equals(s)) {
                    if (strings.length == 1) return Arrays.asList("task", "game", "spawn");
                    else if (strings.length == 2) {
                        switch (strings[0]) {
                            case "task": return Collections.singletonList("cancel");
                            case "game": return Arrays.asList("start", "stop", "shorten");
                            case "spawn": return Arrays.asList("wand", "list", "remove");
                        }
                    }
                }
            }
        } catch (Exception e) {
            printException(getClassName(), getMethodName(), e);
        } return null;
    }
}


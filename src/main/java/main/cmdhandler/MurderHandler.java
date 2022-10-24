package main.cmdhandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MurderHandler {
    public static void onCommand(CommandSender commandSender, String[] args) {
        Player p = (Player) commandSender;
        p.sendMessage("ㅎㅇ");
    }
}

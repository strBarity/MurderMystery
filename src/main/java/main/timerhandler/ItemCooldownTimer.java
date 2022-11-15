package main.timerhandler;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static main.Main.printException;
import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;

public class ItemCooldownTimer implements Runnable {
    private static final HashMap<Player, Double> bowCooldown = new HashMap<>();
    public static void setBowCooldown(Player player, Double cooldown) {
        bowCooldown.put(player, cooldown);
    }
    @Override
    public void run() {
        try {
            if (!bowCooldown.isEmpty()) {
                for (Map.Entry<Player, Double> entry : bowCooldown.entrySet()) {
                    double v = (double) Math.round(entry.getValue() * 10) / 10;
                    bowCooldown.put(entry.getKey(), entry.getValue() - 0.1);
                    final Player.Spigot s = entry.getKey().spigot();
                    if (v >= 4.5)
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■§c■■■■■■■■■§8]").create());
                    else if (v >= 4.0)
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■■§c■■■■■■■■§8]").create());
                    else if (v >= 3.5)
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■■■§c■■■■■■■§8]").create());
                    else if (v >= 3.0)
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■■■■§c■■■■■■§8]").create());
                    else if (v >= 2.5)
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■■■■■§c■■■■■§8]").create());
                    else if (v >= 2.0)
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■■■■■■§c■■■■§8]").create());
                    else if (v >= 1.5)
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■■■■■■■§c■■■§8]").create());
                    else if (v >= 1.0)
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■■■■■■■■§c■■§8]").create());
                    else if (v >= 0.5)
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■■■■■■■■■§c■§8]").create());
                    else
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("§e" + v + "s §8[§a■■■■■■■■■■§8]").create());
                    if (entry.getValue() <= 0) {
                        s.sendMessage(ACTION_BAR, new ComponentBuilder("").create());
                        entry.getKey().getInventory().setItem(9, new ItemStack(Material.ARROW));
                        bowCooldown.remove(entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            printException(e);
        }
    }
}

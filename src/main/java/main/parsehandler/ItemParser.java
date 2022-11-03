package main.parsehandler;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class ItemParser {
    public static boolean isNotCustom(@Nullable ItemStack i) {
        return i == null || i.getItemMeta() == null || i.getItemMeta().getDisplayName() == null;
    }
}

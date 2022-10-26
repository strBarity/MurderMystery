package main.cmdhandler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import main.Main;
import main.gamehandler.MurderHandler;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static main.gamehandler.MurderHandler.gameStarted;

public class MurderTHandler {
    public static void onCommand(CommandSender commandSender, String @NotNull [] args) {
        Player p = (Player) commandSender;
        final String w = Main.INDEX + "§c올바르지 않은 사용법입니다!";
        final String notOp = Main.INDEX + "§c이 명령어를 사용할 권한이 없습니다!";
        if (args.length < 2) {
            p.sendMessage(w);
            return;
        } else if (!p.isOp()) {
            p.sendMessage(notOp);
            return;
        } switch (args[0]) {
            case "npc":
                p.sendMessage(Main.INDEX + "§eNPC 생성 중...");
                MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
                EntityPlayer npc = new EntityPlayer(server, world, new GameProfile(UUID.randomUUID(), "TestNPC"), new PlayerInteractManager(world));
                Location l = p.getLocation();
                npc.setLocation(l.getX(), l.getY(), l.getZ(), 0F, 0F);
                String texture = "ewogICJ0aW1lc3RhbXAiIDogMTY2Njc5NjA5NTAzOSwKICAicHJvZmlsZUlkIiA6ICI2MDRkMjE0NDU1Nzc0MzMwYTJiNGRiZTA0ZTNiOWNjMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJCbHVQZW4iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDUxZjZjZmUyZWFhNThjN2I4NzNlZDhlOGNkNjk1Yjc0NDgyYmE4ZTYzZmQxNTQyMzNiZDY3YmZkMzk1MmYxZiIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=";
                String signature = "fRG0ccYF8keDG2I6DSUeWIdMwN8YwnYM6sToXVKcwlJaalcgslqC+Hur08ZyoXW/uA4AIKN7U7hb1u4wQYv6wrufHbij9NXsi6CL5wXXcKr7zhu0tdN12VQdffgszLpdDLWeFZASidwv8NZ3XP/+PRFO/qztvKa0K1OnZ8XdffXJKWMsSvluOw8g9zVywSmWcOhTd4k8lglr11Fu8W6d6Ri0wRoR+N17tUZmk65d2fSnVI/xFkblccbOV52Y6rKCeQ5DD6f5tqKD0wYNkI4aFal+Fs+1rPo34vH5PM6L5sKS6VFB6mJTlh6d1wlehC/tw5Tn0peTnVO6VLgHhTbycJgbJLmv31R0Efus8WVmvtgF3+KNR9KTgANSsNUpqU28I0BBda5AjO2L6gvyGdWWlhkPLhJh+7ee5aauYQ2KnP/4qCjf/4H6Bq3B6qhDgjpfSyE+iByxfeuFgx+8UWD9UXgoxzaTOb/FgFWBv8JznvF/6UxVKfNoaO/a1p1uNAZzL8bN6GCoRg17+OlbkJ2FUU0oQRmadF6I3VU6lQTdPBmuPghnYnH64u/N/98firOoa4r9482jBk0Wl9/osvrJba2uKOsyjANUP4I/ZhaYFkpVI/Po5Vp7Q/hvLWwNlS9vdKK1pQe3o6rM0c7HkqKravWnVAJbSnlJap0At0hPaI0=";
                npc.getProfile().getProperties().put("textures", new Property("textures", texture, signature));
                PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc)), 10L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc)), 20L);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
                    p.sendMessage(Main.INDEX + "§bNPC 생성 완료.");
                }, 30L);
                break;
            case "task":
                if (args[1].equals("cancel")) {
                    Bukkit.getServer().getScheduler().cancelTasks(Main.getPlugin(Main.class));
                    p.sendMessage(Main.INDEX + "모든 작업을 취소했습니다.");
                } break;
            case "game":
                if (args[1].equals("start")) {
                    if (!gameStarted) {
                        gameStarted = true;
                        Bukkit.getServer().broadcastMessage(Main.INDEX + "§b관리자가 게임을 시작시켰습니다.");
                        MurderHandler.startGame();
                    } else p.sendMessage(Main.INDEX + "게임이 이미 진행 중입니다.");
                } else if (args[1].equals("stop")) {
                    if (gameStarted) {
                        gameStarted = false;
                        Bukkit.getServer().broadcastMessage(Main.INDEX + "§c관리자가 게임을 중지시켰습니다.");
                        MurderHandler.stopGame();
                    } else p.sendMessage(Main.INDEX + "게임이 진행 중이 아닙니다.");
                } break;
            default: p.sendMessage(w);
        }
    }
}

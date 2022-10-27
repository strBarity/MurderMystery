package main.cmdhandler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import main.Main;
import main.gamehandler.MurderHandler;
import main.gamehandler.SkinParser;
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
                if (Bukkit.getPlayer(args[1]) == null) {
                    p.sendMessage(w);
                    break;
                } Player a = Bukkit.getPlayer(args[1]);
                p.sendMessage(Main.INDEX + "§eNPC 생성 중...");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
                    EntityPlayer npc = new EntityPlayer(server, world, new GameProfile(UUID.randomUUID(), "TestNPC"), new PlayerInteractManager(world));
                    Location l = p.getLocation();
                    npc.setLocation(l.getX(), l.getY(), l.getZ(), 0F, 0F);
                    npc.getProfile().getProperties().removeAll("textures");
                    npc.getProfile().getProperties().put("textures", new Property("textures", SkinParser.getValue(a.getUniqueId()), SkinParser.getSignature(a.getUniqueId())));
                    npc.getDataWatcher().set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc)), 5L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc)), 10L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256F / 360F))), 15L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true)), 20L);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc)), 25L);
                }
                p.sendMessage(Main.INDEX + "§bNPC 생성 완료.");
                break;
            case "task":
                if (args[1].equals("cancel")) {
                    Bukkit.getServer().getScheduler().cancelTasks(Main.getPlugin(Main.class));
                    p.sendMessage(Main.INDEX + "모든 작업을 취소했습니다.");
                } break;
            case "game":
                if (args[1].equals("start")) {
                    if (!gameStarted) {
                        Bukkit.getServer().broadcastMessage(Main.INDEX + "§b관리자가 게임을 시작시켰습니다.");
                        MurderHandler.startGame();
                    } else p.sendMessage(Main.INDEX + "게임이 이미 진행 중입니다.");
                } else if (args[1].equals("stop")) {
                    if (gameStarted) {
                        Bukkit.getServer().broadcastMessage(Main.INDEX + "§c관리자가 게임을 중지시켰습니다.");
                        MurderHandler.stopGame();
                    } else p.sendMessage(Main.INDEX + "게임이 진행 중이 아닙니다.");
                } break;
            default: p.sendMessage(w);
        }
    }
}

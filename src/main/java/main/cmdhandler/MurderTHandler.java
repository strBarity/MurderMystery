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
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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
                } p.sendMessage(Main.INDEX + "§eNPC 생성 중...");
                int i1 = 0; int i2 = 0;
                final float yaw = p.getLocation().getYaw();
                if (yaw >= -45 && yaw < 45) {
                    i1 = 104; i2 = 175;
                } else if (yaw >= 45 && yaw < 135) {
                    i1 = 105; i2 = 176;
                } else if ((yaw >= 135 && yaw <= 180) || (yaw >= -180 && yaw < -135)) {
                    i1 = 104; i2 = 177;
                } else if (yaw >= -135 && yaw < -45) {
                    i1 = 103; i2 = 176;
                } if (i1 == 0) {
                    double r = Math.random();
                    if (r <= 0.25) {
                        i1 = 104; i2 = 175;
                    } else if (r <= 0.5) {
                        i1 = 105; i2 = 176;
                    } else if (r <= 0.75) {
                        i1 = 104; i2 = 177;
                    } else {
                        i1 = 103; i2 = 176;
                    }
                } final int x = i1; final int z = i2;
                Location l = p.getLocation();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    CraftPlayer c = (CraftPlayer) Bukkit.getPlayer(args[1]);
                    Property property = c.getProfile().getProperties().get("textures").iterator().next();
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
                    EntityPlayer npc = new EntityPlayer(server, world, new GameProfile(UUID.randomUUID(), ""), new PlayerInteractManager(world));
                    ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), p.getName());
                    team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
                    npc.getProfile().getProperties().removeAll("textures");
                    npc.getProfile().getProperties().put("textures", new Property("textures", property.getValue(), property.getSignature()));
                    npc.getDataWatcher().set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);
                    npc.setLocation(l.getX(), l.getY(), l.getZ(), 0F, 0F);
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
                    connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                        connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
                        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
                        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) 64));
                        connection.sendPacket(new PacketPlayOutBlockChange(npc.world, new BlockPosition(npc.locX, npc.locY, npc.locZ)));
                        connection.sendPacket(new PacketPlayOutBed(npc, new BlockPosition(x, 79, z)));
                        connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
                        connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
                        connection.sendPacket(new PacketPlayOutScoreboardTeam(team, Collections.singletonList(npc.getName()), 3));
                        npc.teleportTo(new Location(p.getWorld(), l.getX(), l.getY(), l.getZ(), 90F, 0F), false);
                        connection.sendPacket(new PacketPlayOutEntityTeleport(npc));
                    }, 1L);
                } Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> p.sendMessage(Main.INDEX + "§bNPC 생성 완료."), 1L);
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

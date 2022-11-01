package main.cmdhandler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import main.Main;
import main.datahandler.SpawnLocationData;
import main.gamehandler.MurderHandler;
import main.timerhandler.CountdownTimer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static main.Main.sc;
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
            case "exception":
                throw new RuntimeException(args[1]);
            case "spawn":
                switch (args[1]) {
                    case "wand":
                        ItemStack slWand = new ItemStack(Material.BLAZE_ROD);
                        ItemMeta slWandMeta = slWand.getItemMeta();
                        slWandMeta.setDisplayName("§e스폰 위치 설정 도구");
                        slWandMeta.setLore(Arrays.asList("§f우클릭 - §e자신의 위치에 스폰 위치 생성", "§f좌클릭 - §e근처 스폰 위치 삭제"));
                        slWand.setItemMeta(slWandMeta);
                        p.getInventory().addItem(slWand);
                        p.sendMessage(Main.INDEX + "§a우클릭해서 자신의 위치에 스폰 위치를 생성합니다.\n" + Main.INDEX + "§a좌클릭해서 근처의 스폰 위치를 제거합니다.");
                        break;
                    case "list":
                        TextComponent m = new TextComponent(Main.INDEX + "§9------------[ §b현재 맵 스폰 위치 목록 §9]------------\n" + Main.INDEX + "§9맵: §b" + p.getWorld().getName() + " §f| §9스폰 위치 갯수: §b" + SpawnLocationData.getSpawnLocation(p.getWorld().getName()).size() + "/100§9개 §f| ");
                        TextComponent e = new TextComponent("§4모두 삭제 ");
                        e.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§4클릭해서 모든 스폰 위치 삭제하기 ").create()));
                        e.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/murder spawn remove all"));
                        m.addExtra(e);
                        p.spigot().sendMessage(m);
                        int loopnum = 0;
                        for (String s : SpawnLocationData.getSpawnLocation(p.getWorld().getName())) {
                            loopnum++;
                            final String[] parts = s.split(",");
                            final int x = Integer.parseInt(parts[0]);
                            final int y = Integer.parseInt(parts[1]);
                            final int z = Integer.parseInt(parts[2]);
                            TextComponent msg = new TextComponent(Main.INDEX + "§e" + loopnum + " §f| " + "§2(§a" + s.replace(",", "§2, §a") + "§2) §f| ");
                            TextComponent t = new TextComponent("§e텔레포트 ");
                            t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§e클릭해서 텔레포트하기 ").create()));
                            t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + x + " " + y + " " + z));
                            msg.addExtra(t);
                            msg.addExtra("§f| ");
                            TextComponent r = new TextComponent("§c삭제 ");
                            ComponentBuilder cb2 = new ComponentBuilder("§c클릭해서 삭제하기 ");
                            r.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb2.create()));
                            r.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/murder spawn remove " + s));
                            msg.addExtra(r);
                            p.spigot().sendMessage(msg);
                        } if (SpawnLocationData.getSpawnLocation(p.getWorld().getName()).isEmpty()) p.sendMessage(Main.INDEX + "§7저장된 스폰 위치가 없습니다.");
                        p.sendMessage(Main.INDEX + "§9---------------------------------------------------");
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.75F, 1);
                        break;
                    case "remove":
                        if (args.length == 2) p.sendMessage(Main.INDEX + "§c삭제할 스폰 위치를 x,y,z 형식으로 입력해주세요.");
                        else if (args.length == 3) {
                            if (args[2].equals("all")) {
                                if (SpawnLocationData.getSpawnLocation(p.getWorld().getName()).isEmpty()) {
                                    p.sendMessage(Main.INDEX + "§c등록된 스폰 위치가 없습니다.");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 0.5F, 1);
                                } else {
                                    TextComponent msg = new TextComponent(Main.INDEX + "§6정말 §4모든§6 스폰 위치를 삭제하려면 ");
                                    TextComponent t = new TextComponent("§b여기§6를");
                                    t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§4클릭해서 모두 삭제하기 ").create()));
                                    t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/murder spawn remove all confirm"));
                                    msg.addExtra(t);
                                    msg.addExtra(" §6클릭하세요.");
                                    p.spigot().sendMessage(msg);
                                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.75F, 1);
                                }
                            } else if (SpawnLocationData.getSpawnLocation(p.getWorld().getName()).contains(args[2])) {
                                TextComponent msg = new TextComponent(Main.INDEX + "§e정말 §2(§a" + args[2].replace(",", "§2, §a") + "§2)§e에 있는 스폰 위치를 삭제하려면 ");
                                TextComponent t = new TextComponent("§b여기§e를");
                                t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c클릭해서 삭제하기 ").create()));
                                t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/murder spawn remove " + args[2] + " confirm"));
                                msg.addExtra(t);
                                msg.addExtra(" §e클릭하세요.");
                                p.spigot().sendMessage(msg);
                                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.75F, 1);
                            } else {
                                p.sendMessage(Main.INDEX + "§c등록되지 않은 스폰 위치입니다.");
                                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 0.5F, 1);
                            }
                        } else {
                            if (args[2].equals("all")) {
                                SpawnLocationData.removeAllSpawnLocation(p.getWorld().getName());
                                p.sendMessage(Main.INDEX + "§a성공적으로 모든 스폰 위치가 삭제되었습니다.");
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 0.75F, 1);
                            }  else if (SpawnLocationData.getSpawnLocation(p.getWorld().getName()).contains(args[2])) {
                                String[] s = args[2].split(",");
                                SpawnLocationData.removeSpawnLocation(p.getWorld().getName(), new Location(p.getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));
                                p.sendMessage(Main.INDEX + "§a성공적으로 스폰 위치가 삭제되었습니다.");
                                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 0.75F, 1);
                            } else {
                                p.sendMessage(Main.INDEX + "§c등록되지 않은 스폰 위치입니다.");
                                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 0.5F, 1);
                            }
                        } break;
                } break;
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
                    CraftPlayer c = (CraftPlayer) Bukkit.getPlayerExact(args[1]);
                    Property property = c.getProfile().getProperties().get("textures").iterator().next();
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
                    EntityPlayer npc = new EntityPlayer(server, world, new GameProfile(UUID.randomUUID(), ""), new PlayerInteractManager(world));
                    ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), p.getName());
                    team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
                    team.setCollisionRule(ScoreboardTeamBase.EnumTeamPush.NEVER);
                    npc.getProfile().getProperties().removeAll("textures");
                    npc.getProfile().getProperties().put("textures", new Property("textures", property.getValue(), property.getSignature()));
                    npc.getDataWatcher().set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);
                    npc.setLocation(l.getX(), l.getY(), l.getZ(), 0F, 0F);
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
                    connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
                    sc.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
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
                } sc.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> p.sendMessage(Main.INDEX + "§bNPC 생성 완료."), 1L);
                break;
            case "task":
                if (args[1].equals("cancel")) {
                    Bukkit.getServer().getScheduler().cancelTasks(Main.getPlugin(Main.class));
                    p.sendMessage(Main.INDEX + "모든 작업을 취소했습니다.");
                } break;
            case "game":
                switch (args[1]) {
                    case "shorten":
                        CountdownTimer.startCountdown = 5L;
                        Bukkit.getServer().broadcastMessage(Main.INDEX + "§b관리자가 시작 시간을 단축시켰습니다.");
                        break;
                    case "start":
                        if (!gameStarted) {
                            Bukkit.getServer().broadcastMessage(Main.INDEX + "§b관리자가 게임을 시작시켰습니다.");
                            MurderHandler.startGame(p.getWorld());
                        } else p.sendMessage(Main.INDEX + "게임이 이미 진행 중입니다.");
                        break;
                    case "stop":
                        if (gameStarted) {
                            Bukkit.getServer().broadcastMessage(Main.INDEX + "§c관리자가 게임을 중지시켰습니다.");
                            MurderHandler.stopGame(p.getWorld(), true, MurderHandler.WinType.STOPPED, null);
                        } else p.sendMessage(Main.INDEX + "게임이 진행 중이 아닙니다.");
                        break;
                } break;
            default: p.sendMessage(w);
        }
    }
}

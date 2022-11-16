package main.cmdhandler;

import main.Main;
import main.timerhandler.CountdownTimer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.lang.String.format;
import static main.Main.*;
import static main.datahandler.SpawnLocationData.*;
import static main.gamehandler.MurderHandler.*;
import static net.md_5.bungee.api.chat.ClickEvent.Action;
import static net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT;
import static org.bukkit.Sound.*;
import static org.bukkit.SoundCategory.MASTER;

public class MurderTHandler {
    public static void onCommand(CommandSender commandSender, String @NotNull [] args) {
        try {
            Player p = (Player) commandSender;
            final String w = INDEX + "§c올바르지 않은 사용법입니다!";
            final String notOp = INDEX + "§c이 명령어를 사용할 권한이 없습니다!";
            if (args.length < 2) {
                p.sendMessage(w);
                return;
            } else if (!p.isOp()) {
                p.sendMessage(notOp);
                return;
            }
            switch (args[0]) {
                case "exception":
                    switch (args[1]) {
                        case "create":
                            if (args.length == 2) throw new RuntimeException();
                            else throw new RuntimeException(args[2]);
                        case "list":
                            p.sendMessage(INDEX + "§c----------------[ §6최근 오류 목록 §c]----------------");
                            int l = 0;
                            if (EXCEPTIONS.isEmpty()) p.sendMessage(INDEX + "§7서버 시작 후 기록된 오류가 없습니다.");
                            else {
                                for (String s : EXCEPTIONS) {
                                    l++;
                                    p.sendMessage(String.format("%s§e%d §6| %s", INDEX, l, s));
                                }
                            } p.sendMessage(INDEX + "§c--------------------------------------------------");
                            break;
                        case "clear":
                            EXCEPTIONS.clear();
                            p.sendMessage(INDEX + "§a모든 오류 기록을 제거했습니다.");
                            break;
                        default:
                            p.sendMessage(w);
                    } break;
                case "spawn":
                    switch (args[1]) {
                        case "wand":
                            ItemStack slWand = new ItemStack(Material.BLAZE_ROD);
                            ItemMeta slWandMeta = slWand.getItemMeta();
                            slWandMeta.setDisplayName("§e스폰 위치 설정 도구");
                            slWandMeta.setLore(Arrays.asList("§f우클릭 - §e자신의 위치에 스폰 위치 생성", "§f좌클릭 - §e근처 스폰 위치 삭제"));
                            slWand.setItemMeta(slWandMeta);
                            p.getInventory().addItem(slWand);
                            p.sendMessage(INDEX + "§a우클릭해서 자신의 위치에 스폰 위치를 생성합니다.\n" + INDEX + "§a좌클릭해서 근처의 스폰 위치를 제거합니다.");
                            break;
                        case "list":
                            TextComponent m = new TextComponent(format("%s§9------------[ §b현재 맵 스폰 위치 목록 §9]------------\n%s§9맵: §b%s §f| §9스폰 위치 갯수: §b%d/100§9개 §f| ", INDEX, INDEX, p.getWorld().getName(), getSpawnLocation(p.getWorld().getName()).size()));
                            TextComponent e = new TextComponent("§4모두 삭제 ");
                            e.setHoverEvent(new HoverEvent(SHOW_TEXT, new ComponentBuilder("§4클릭해서 모든 스폰 위치 삭제하기 ").create()));
                            e.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/murder spawn remove all"));
                            m.addExtra(e);
                            p.spigot().sendMessage(m);
                            int loopnum = 0;
                            for (String s : getSpawnLocation(p.getWorld().getName())) {
                                loopnum++;
                                final String[] parts = s.split(",");
                                final int x = Integer.parseInt(parts[0]);
                                final int y = Integer.parseInt(parts[1]);
                                final int z = Integer.parseInt(parts[2]);
                                TextComponent msg = new TextComponent(format("%s§e%d §f| §2(§a%s§2) §f| ", INDEX, loopnum, s.replace(",", "§2, §a")));
                                TextComponent t = new TextComponent("§e텔레포트 ");
                                t.setHoverEvent(new HoverEvent(SHOW_TEXT, new ComponentBuilder("§e클릭해서 텔레포트하기 ").create()));
                                t.setClickEvent(new ClickEvent(Action.RUN_COMMAND, format("/tp %d %d %d", x, y, z)));
                                msg.addExtra(t);
                                msg.addExtra("§f| ");
                                TextComponent r = new TextComponent("§c삭제 ");
                                ComponentBuilder cb2 = new ComponentBuilder("§c클릭해서 삭제하기 ");
                                r.setHoverEvent(new HoverEvent(SHOW_TEXT, cb2.create()));
                                r.setClickEvent(new ClickEvent(Action.RUN_COMMAND, format("/murder spawn remove %s", s)));
                                msg.addExtra(r);
                                p.spigot().sendMessage(msg);
                            }
                            if (getSpawnLocation(p.getWorld().getName()).isEmpty())
                                p.sendMessage(INDEX + "§7저장된 스폰 위치가 없습니다.");
                            p.sendMessage(INDEX + "§9---------------------------------------------------");
                            p.playSound(p.getLocation(), ENTITY_EXPERIENCE_ORB_PICKUP, MASTER, 0.75F, 1);
                            break;
                        case "remove":
                            if (args.length == 2) p.sendMessage(INDEX + "§c삭제할 스폰 위치를 x,y,z 형식으로 입력해주세요.");
                            else if (args.length == 3) {
                                if (args[2].equals("all")) {
                                    if (getSpawnLocation(p.getWorld().getName()).isEmpty()) {
                                        p.sendMessage(INDEX + "§c등록된 스폰 위치가 없습니다.");
                                        p.playSound(p.getLocation(), BLOCK_ANVIL_LAND, MASTER, 0.5F, 1);
                                    } else {
                                        TextComponent msg = new TextComponent(INDEX + "§6정말 §4모든§6 스폰 위치를 삭제하려면 ");
                                        TextComponent t = new TextComponent("§b여기§6를");
                                        t.setHoverEvent(new HoverEvent(SHOW_TEXT, new ComponentBuilder("§4클릭해서 모두 삭제하기 ").create()));
                                        t.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/murder spawn remove all confirm"));
                                        msg.addExtra(t);
                                        msg.addExtra(" §6클릭하세요.");
                                        p.spigot().sendMessage(msg);
                                        p.playSound(p.getLocation(), ENTITY_EXPERIENCE_ORB_PICKUP, MASTER, 0.75F, 1);
                                    }
                                } else if (getSpawnLocation(p.getWorld().getName()).contains(args[2])) {
                                    TextComponent msg = new TextComponent(format("%s§e정말 §2(§a%s§2)§e에 있는 스폰 위치를 삭제하려면 ", INDEX, args[2].replace(",", "§2, §a")));
                                    TextComponent t = new TextComponent("§b여기§e를");
                                    t.setHoverEvent(new HoverEvent(SHOW_TEXT, new ComponentBuilder("§c클릭해서 삭제하기 ").create()));
                                    t.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/murder spawn remove " + args[2] + " confirm"));
                                    msg.addExtra(t);
                                    msg.addExtra(" §e클릭하세요.");
                                    p.spigot().sendMessage(msg);
                                    p.playSound(p.getLocation(), ENTITY_EXPERIENCE_ORB_PICKUP, MASTER, 0.75F, 1);
                                } else {
                                    p.sendMessage(INDEX + "§c등록되지 않은 스폰 위치입니다.");
                                    p.playSound(p.getLocation(), BLOCK_ANVIL_LAND, MASTER, 0.5F, 1);
                                }
                            } else {
                                if (args[2].equals("all")) {
                                    removeAllSpawnLocation(p.getWorld().getName());
                                    p.sendMessage(INDEX + "§a성공적으로 모든 스폰 위치가 삭제되었습니다.");
                                    p.playSound(p.getLocation(), ENTITY_PLAYER_LEVELUP, MASTER, 0.75F, 1);
                                } else if (getSpawnLocation(p.getWorld().getName()).contains(args[2])) {
                                    String[] s = args[2].split(",");
                                    removeSpawnLocation(p.getWorld().getName(), new Location(p.getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));
                                    p.sendMessage(INDEX + "§a성공적으로 스폰 위치가 삭제되었습니다.");
                                    p.playSound(p.getLocation(), ENTITY_PLAYER_LEVELUP, MASTER, 0.75F, 1);
                                } else {
                                    p.sendMessage(INDEX + "§c등록되지 않은 스폰 위치입니다.");
                                    p.playSound(p.getLocation(), BLOCK_ANVIL_LAND, MASTER, 0.5F, 1);
                                }
                            }
                            break;
                    }
                    break;
                case "task":
                    if (args[1].equals("cancel")) {
                        SCHEDULER.cancelTasks(Main.getPlugin(Main.class));
                        p.sendMessage(INDEX + "모든 작업을 취소했습니다.");
                    }
                    break;
                case "game":
                    switch (args[1]) {
                        case "shorten":
                            CountdownTimer.setStartCountdown(5L);
                            SERVER.broadcastMessage(INDEX + "§b관리자가 시작 시간을 단축시켰습니다.");
                            break;
                        case "start":
                            if (!gameStarted) {
                                SERVER.broadcastMessage(INDEX + "§b관리자가 게임을 시작시켰습니다.");
                                startGame(p.getWorld());
                            } else p.sendMessage(INDEX + "게임이 이미 진행 중입니다.");
                            break;
                        case "stop":
                            if (gameStarted) {
                                SERVER.broadcastMessage(INDEX + "§c관리자가 게임을 중지시켰습니다.");
                                stopGame(p.getWorld(), true, WinType.STOPPED, null);
                            } else p.sendMessage(INDEX + "게임이 진행 중이 아닙니다.");
                            break;
                    }
                    break;
                default:
                    p.sendMessage(w);
            }
        } catch (Exception e) {
            printException(e);
        }
    }
}

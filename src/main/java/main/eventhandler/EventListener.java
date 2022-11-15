package main.eventhandler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import main.Main;
import main.datahandler.SpawnLocationData;
import main.gamehandler.MurderHandler;
import main.parsehandler.ItemParser;
import main.timerhandler.CountdownTimer;
import main.timerhandler.ExitTimer;
import main.timerhandler.ItemCooldownTimer;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static main.Main.*;
import static main.gamehandler.MurderHandler.*;

public class EventListener implements Listener {
    public enum DeathCause {MURDER_KNIFE, MURDER_THROW, MURDER_SNIPE, INNOCENT_SNIPE, INNOCENT_SHOOT, DROWNED, PORTAL}
    public static ArrayList<String> onlineNameList = new ArrayList<>();
    public static final int startPlayerCount = 4;
    public static final List<Integer> summonedNpcsId = new ArrayList<>();
    public static final HashMap<Player, Integer> boardId = new HashMap<>();
    public static final HashMap<Player, Integer> antiOutMapId = new HashMap<>();
    public static final HashMap<ArmorStand, Integer> spinStandId = new HashMap<>();
    public static final HashMap<Player, String> rankType = new HashMap<>();
    public static final HashMap<Player, ChatColor> rankColor = new HashMap<>();
    public static void registerArmorstandSpin(ArmorStand a) {
        try {
            int i = SCHEDULER.scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                Location l = a.getLocation();
                a.teleport(new Location(a.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw() + 7.5F, l.getPitch()));
            }, 0, 1L);
            spinStandId.put(a, i);
        } catch (Exception e) {
            printException(e);
        }
    }
    public static void registerAntiOutMap(Player p) {
        try {
            int i = SERVER.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                Location l = p.getLocation();
                if (l.getX() < 29 || l.getX() > 219 || l.getY() < 24 || l.getY() > 125 || l.getZ() < 22 || l.getZ() > 330) {
                    if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
                        p.teleport(new Location(p.getWorld(), 104.5, 88.0, 176.5, 90F, 0F));
                    else {
                        p.kickPlayer(INDEX + "§4비정상적인 맵 탈출이 감지되었습니다.");
                    }
                    if (Bukkit.getOnlinePlayers() != null) for (Player o : Bukkit.getOnlinePlayers()) {
                        if (o.isOp())
                            o.sendMessage(String.format("%s%s§c님이 §6%s §c모드에서 맵 탈출을 시도했습니다.", INDEX, p.getName(), p.getGameMode().toString()));
                    }
                }
            }, 0, 1L);
            antiOutMapId.put(p, i);
        } catch (Exception e) {
            printException(e);
        }
    } public static void registerBoard(Player p) {
        try {
            int i = SERVER.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                final Objective objective = board.registerNewObjective("§e§l머더 미스터리", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                Score t = objective.getScore("§7" + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy")) + "§8 Murder");
                if (MurderHandler.gameStarted) {
                    t.setScore(11);
                    Score b1 = objective.getScore(" ");
                    b1.setScore(10);
                    Score r = objective.getScore("§f역할: " + MurderHandler.roleType.get(p));
                    r.setScore(9);
                    Score b2 = objective.getScore("  ");
                    b2.setScore(8);
                    Score in = objective.getScore(String.format("§f남은 시민: §a%d§f명", MurderHandler.innocentAlive));
                    in.setScore(7);
                    Score ti = objective.getScore(String.format("§f남은 시간: §a%d:%d", CountdownTimer.getGameCountdownMin(), CountdownTimer.getGameCountdownSec()));
                    if (CountdownTimer.getGameCountdownSec() < 10)
                        ti = objective.getScore(String.format("§f남은 시간: §a%d:0%d", CountdownTimer.getGameCountdownMin(), CountdownTimer.getGameCountdownSec()));
                    ti.setScore(6);
                    Score b3 = objective.getScore("   ");
                    b3.setScore(5);
                    Score b = objective.getScore("§f탐정: §a생존");
                    if (MurderHandler.bowType == MurderHandler.BowType.BowDrop) {
                        b = objective.getScore("§f활: §c떨어짐");
                    } else if (MurderHandler.bowType == MurderHandler.BowType.BowNotDrop) {
                        b = objective.getScore("§f활: §a떨어지지 않음");
                    }
                    b.setScore(4);
                    Score b4 = objective.getScore("     ");
                    b4.setScore(3);
                    Score m = objective.getScore("§f맵: §a" + p.getWorld().getName());
                    m.setScore(2);
                } else {
                    t.setScore(9);
                    Score b1 = objective.getScore(" ");
                    b1.setScore(8);
                    Score m = objective.getScore("§f맵: §a" + p.getWorld().getName());
                    m.setScore(7);
                    Score l = objective.getScore(String.format("§f플레이어: §a%d/32", SERVER.getOnlinePlayers().size()));
                    l.setScore(6);
                    Score b2 = objective.getScore("  ");
                    b2.setScore(5);
                    if (Bukkit.getOnlinePlayers().size() >= startPlayerCount) {
                        Score s = objective.getScore(String.format("§a%d초 §f후 시작", CountdownTimer.getStartCountdown()));
                        s.setScore(4);
                    } else {
                        Score s = objective.getScore("§f플레이어를 기다리는 중...");
                        s.setScore(4);
                    }
                    Score b3 = objective.getScore("   ");
                    b3.setScore(3);
                    Score mo = objective.getScore("§f모드: §a일반");
                    mo.setScore(2);
                }
                Score b4 = objective.getScore("    ");
                b4.setScore(1);
                Score a = objective.getScore("§eChoco24h");
                a.setScore(0);
                p.setScoreboard(board);
            }, 0, 20L);
            boardId.put(p, i);
        } catch (Exception e) {
            printException(e);
        }
    } @EventHandler(priority=EventPriority.HIGHEST)
    public void onAttack(@NotNull EntityDamageByEntityEvent e) {
        try {
            e.setCancelled(true);
            if (!e.getDamager().getType().equals(EntityType.PLAYER) || !e.getEntity().getType().equals(EntityType.PLAYER)) return;
            Player attacker = (Player) e.getDamager();
            Player victim = (Player) e.getEntity();
            if (attacker.getInventory().getItemInMainHand() == null || attacker.getInventory().getItemInMainHand().getItemMeta() == null || attacker.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null) return;
            if (murderer == e.getDamager() && (MurderHandler.roleType.get(victim).contains("시민") || MurderHandler.roleType.get(victim).contains("탐정")) && attacker.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("칼")) playerDeath(victim, DeathCause.MURDER_KNIFE);
            e.setCancelled(true);
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler(priority=EventPriority.HIGH)
    public void onSnipe(@NotNull ProjectileHitEvent e) {
        try {
            if (e.getHitEntity() == null || !e.getHitEntity().getType().equals(EntityType.PLAYER)) return;
            Player attacker = (Player) e.getEntity().getShooter();
            Player victim = (Player) e.getHitEntity();
            if (!MurderHandler.roleType.get(victim).contains("사망") || !MurderHandler.roleType.get(victim).contains("관전")) {
                if (MurderHandler.roleType.get(victim).contains("시민")) {
                    playerDeath(victim, DeathCause.INNOCENT_SNIPE);
                    playerDeath(attacker, DeathCause.INNOCENT_SHOOT);
                } else if (MurderHandler.roleType.get(victim).contains("살인자")) {
                    playerDeath(victim, DeathCause.INNOCENT_SNIPE);
                    MurderHandler.stopGame(CURRENTMAP, true, MurderHandler.WinType.MURDER_DIED, DeathCause.INNOCENT_SNIPE);
                }
            }
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {
        try {
            if (!e.getEntity().getType().equals(EntityType.PLAYER)) return;
            Player p = (Player) e.getEntity();
            if (MurderHandler.roleType.get(p).contains("탐정")) {
                ItemCooldownTimer.setBowCooldown(p, 5.0);
            }
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onPickupArrow(PlayerPickupArrowEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent e) {
        try {
            Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);
            if (ItemParser.isNotCustom(p.getInventory().getItemInMainHand()) || ItemParser.isNotCustom(e.getCurrentItem())) return;
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            if (name.contains("게임 나가기") || name.contains("칼") || name.equals("활")) {
                if (name.contains("게임 나가기")) {
                    if (ExitTimer.getExitTimer().containsKey(p)) {
                        ExitTimer.getExitTimer().remove(p);
                        p.sendMessage(INDEX + "로비로 이동이 취소되었습니다.");
                    } else {
                        ExitTimer.getExitTimer().put(p, 60);
                        p.sendMessage(INDEX + "§e3초 후에 로비로 이동합니다. 취소하려면 다시 우클릭하세요.");
                    }
                }
            }
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent e) {
        try {
            Player p = e.getPlayer();
            e.setCancelled(true);
            if (ItemParser.isNotCustom(p.getInventory().getItemInMainHand())) return;
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType().equals(Material.CAKE_BLOCK)) {
                    p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, SoundCategory.MASTER, 100, 1);
                    p.getWorld().spawnParticle(Particle.CRIT, 98.5, 98.5, 176.5, 10, 0.125, 0.125, 0.125, 3);
                }
            } if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("게임 나가기")) {
                if (ExitTimer.getExitTimer().containsKey(p)) {
                    ExitTimer.getExitTimer().remove(p);
                    p.sendMessage(INDEX + "로비로 이동이 취소되었습니다.");
                } else {
                    ExitTimer.getExitTimer().put(p, 60);
                    p.sendMessage(INDEX + "§e3초 후에 로비로 이동합니다. 취소하려면 다시 우클릭하세요.");
                }
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("스폰 위치 설정 도구")) {
                if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    SpawnLocationData.addSpawnLocation(p.getWorld().getName(), p.getLocation());
                    p.sendMessage(String.format("%s%s §a맵에서 §2(§a%d§2, §a%d§2, §a%d§2)§a를 스폰 위치에 추가했습니다.", INDEX, p.getWorld().getName(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()));
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.75F, 1);
                } if (e.getAction().equals(Action.LEFT_CLICK_AIR)) {
                    boolean notRemoved = true;
                    for (int x = p.getLocation().getBlockX() - 2; x <= p.getLocation().getBlockX() + 2; x++) for (int y = p.getLocation().getBlockY() - 2; y <= p.getLocation().getBlockY() + 2; y++) for (int z = p.getLocation().getBlockZ() - 2; z <= p.getLocation().getBlockZ() + 2; z++) {
                        for (String s : SpawnLocationData.getSpawnLocation(p.getWorld().getName())) {
                            int[] i = SpawnLocationData.toSplitCoord(s);
                            int x2 = i[0]; int y2 = i[1]; int z2 = i[2];
                            if (x == x2 && y == y2 && z == z2) {
                                notRemoved = false;
                                SpawnLocationData.removeSpawnLocation(p.getWorld().getName(), new Location(p.getWorld(), x2, y2, z2));
                                p.sendMessage(String.format("%s%s §e맵에서 §2(§a%d§2, §a%d§2, §a%d§2)§e에 있는 스폰 위치를 제거했습니다.", INDEX, p.getWorld().getName(), x2, y2, z2));
                                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.75F, 0);
                            }
                        }
                    } if (notRemoved) p.sendMessage(INDEX + "§c제거할 스폰 위치가 근처에 없습니다.");
                }
            }
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent e) {
        try {
            e.setCancelled(true);
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        try {
            Player p = e.getPlayer();
            onlineNameList.add(p.getName());
            p.removePotionEffect(PotionEffectType.INVISIBILITY);
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().clear();
            p.teleport(new Location(p.getWorld(), 104.5, 88.0, 176.5, 90F, 0F));
            if (p.getUniqueId().toString().equals("604d2144-5577-4330-a2b4-dbe04e3b9cc3")) {
                rankType.put(p, "§b[MVP§c+§b] ");
                rankColor.put(p, ChatColor.AQUA);
                p.setPlayerListName("§b[MVP§c+§b] " + p.getName() + " ");
                e.setJoinMessage(String.format("%s§b누군가...?§e가 참여했습니다. (§b%d§e/§b32§e)", INDEX, Bukkit.getOnlinePlayers().size()));
                for (Player o : Bukkit.getOnlinePlayers()) o.playSound(o.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 100, 1);
                SCHEDULER.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    SERVER.broadcastMessage(INDEX + "§c키가 너무 작아서 이름이 안보여요... 죄송합니다");
                    for (Player o : Bukkit.getOnlinePlayers()) o.playSound(o.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 100, 1);
                }, 60L);
            } else {
                if (p.isOp()) {
                    rankType.put(p, "§c[ADMIN] ");
                    rankColor.put(p, ChatColor.RED);
                    p.setPlayerListName("§c[ADMIN] " + p.getName() + " ");
                } else {
                    rankType.put(p, "§a[VIP] ");
                    rankColor.put(p, ChatColor.GREEN);
                    p.setPlayerListName("§a[VIP] " + p.getName() + " ");
                }
            } if (MurderHandler.gameStarted) {
                p.setGameMode(GameMode.SPECTATOR);
                e.setJoinMessage(null);
                p.setAllowFlight(true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
                MurderHandler.roleType.put(p, "§8관전자");
            } else {
                ItemStack i = new ItemStack(Material.BED, 1, (short) 14);
                ItemMeta im = i.getItemMeta();
                im.setDisplayName("§c게임 나가기 §7(우클릭)");
                im.setLore(Arrays.asList("§a우클릭 시 3초 후 로비로 돌아갑니다.", "§7다시 우클릭을 누르면 취소됩니다.", "", "§e클릭해서 로비로 돌아가기"));
                i.setItemMeta(im);
                i.setData(new MaterialData(Material.BED));
                p.getInventory().setItem(8, i);
                if (!p.getUniqueId().toString().equals("604d2144-5577-4330-a2b4-dbe04e3b9cc3")) e.setJoinMessage(INDEX + rankColor.get(p) + p.getName() + "§e님이 참여했습니다! (§b" + Bukkit.getOnlinePlayers().size() + "§e/§b32§e)");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HAT, SoundCategory.MASTER, 100, 1);
            } p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(Double.MAX_VALUE);
            registerBoard(p);
            registerAntiOutMap(p);
            mainScoreboardSet(p);
            SpawnLocationData.registerSLWand(p);
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        try {
            Player p = e.getPlayer();
            if (MurderHandler.gameStarted) e.setQuitMessage(null);
            else e.setQuitMessage(String.format("%s%s%s§e님이 나갔습니다! (§b%d§e/§b32§e)", INDEX, rankColor.get(p), p.getName(), Bukkit.getOnlinePlayers().size() - 1));
            SCHEDULER.cancelTask(boardId.get(e.getPlayer()));
            SCHEDULER.cancelTask(antiOutMapId.get(e.getPlayer()));
            SCHEDULER.cancelTask(SpawnLocationData.slWandId.get(e.getPlayer()));
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent e) {
        try {
            e.setFormat(String.format("%s%s§7: §f%s", rankType.get(e.getPlayer()), e.getPlayer().getName(), e.getMessage()));
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onDamage(@NotNull EntityDamageEvent e) {
        try {
            if (e.getEntity().getType().equals(EntityType.PLAYER)) {
                e.setCancelled(true);
                Player p = (Player) e.getEntity();
                p.setHealth(p.getHealthScale());
                if (p.getFireTicks() > 0) p.setFireTicks(0);
                if (e.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)) {
                    if (p == murderer) {
                        playerDeath(p, DeathCause.DROWNED);
                        MurderHandler.stopGame(CURRENTMAP, true, MurderHandler.WinType.MURDER_DIED, DeathCause.DROWNED);
                    } else playerDeath(p, DeathCause.DROWNED);
                }
            }
        } catch (Exception exception) {
            printException(exception);
        }
    } @EventHandler
    public void onFood(@NotNull FoodLevelChangeEvent e) {
        try {
            e.setCancelled(true);
            Player p = (Player) e.getEntity();
            p.setFoodLevel(20);
        } catch (Exception exception) {
            printException(exception);
        }
    }
    public static void mainScoreboardSet(@NotNull Player p) {
        try {
            ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), p.getName());
            team.setCollisionRule(ScoreboardTeamBase.EnumTeamPush.NEVER);
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
            team.setCanSeeFriendlyInvisibles(false);
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
                connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
                connection.sendPacket(new PacketPlayOutScoreboardTeam(team, onlineNameList, 3));
            }
        } catch (Exception e) {
            printException(e);
        }
    }
    public void playerDeath(Player victim, DeathCause deathCause) {
        try {
            MurderHandler.roleType.put(victim, "§7사망");
            victim.setGameMode(GameMode.SPECTATOR);
            String subtitle = null;
            if (victim != murderer) {
                if (deathCause.equals(DeathCause.MURDER_KNIFE)) subtitle = "§e살인자가 당신을 찔렀습니다!";
                else if (deathCause.equals(DeathCause.MURDER_THROW)) subtitle = "§e살인자가 칼을 당신에게 던졌습니다";
                else if (deathCause.equals(DeathCause.MURDER_SNIPE)) subtitle = "§e살인자가 쏜 활에 맞았습니다!";
                else if (deathCause.equals(DeathCause.INNOCENT_SNIPE)) subtitle = "§e시민이 쏜 활에 맞았습니다!";
                else if (deathCause.equals(DeathCause.INNOCENT_SHOOT)) subtitle = "§e당신은 시민을 쐈습니다!";
                else if (deathCause.equals(DeathCause.DROWNED)) subtitle = "§e당신은 익사했습니다!";
                else if (deathCause.equals(DeathCause.PORTAL)) subtitle = "§e당신은 포탈에 빠졌습니다!";
                victim.sendTitle("§c죽었습니다!", subtitle, 0, 100, 20);
                victim.sendMessage(INDEX + "§c죽었습니다! §e이제부터 관전자 상태입니다.");
                MurderHandler.innocentAlive--;
                MurderHandler.murderKills++;
            } if (victim == detective && innocentAlive > 0) {
                bowType = BowType.BowDrop;
                Bukkit.broadcastMessage(INDEX + "§6활이 떨어졌습니다! §e활을 찾아 살인자를 처치할 기회를 포착하세요.");
                for (Player p : Bukkit.getOnlinePlayers()) p.sendTitle("", "§6활이 떨어졌습니다!", 0, 100, 0);
                Location l = victim.getLocation();
                ArmorStand a = CURRENTMAP.spawn(new Location(victim.getWorld(), l.getX(), l.getY()+0.5, l.getZ()), ArmorStand.class);
                a.setGravity(false);
                a.setCustomNameVisible(false);
                a.setArms(true);
                a.setVisible(false);
                a.setMarker(true);
                a.setItemInHand(new ItemStack(Material.BOW));
                a.setRightArmPose(new EulerAngle(45, 0, 0));
                registerArmorstandSpin(a);
            } for (Player p : Bukkit.getOnlinePlayers())
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, SoundCategory.MASTER, 100F, 1F);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0), true);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
            victim.setAllowFlight(true);
            victim.getInventory().clear();
            int i1 = 0;
            int i2 = 0;
            final float yaw = victim.getLocation().getYaw();
            if (yaw >= -45 && yaw < 45) {
                i1 = 104;
                i2 = 175;
            } else if (yaw >= 45 && yaw < 135) {
                i1 = 105;
                i2 = 176;
            } else if ((yaw >= 135 && yaw <= 180) || (yaw >= -180 && yaw < -135)) {
                i1 = 104;
                i2 = 177;
            } else if (yaw >= -135 && yaw < -45) {
                i1 = 103;
                i2 = 176;
            }
            if (i1 == 0) {
                double r = Math.random();
                if (r <= 0.25) {
                    i1 = 104;
                    i2 = 175;
                } else if (r <= 0.5) {
                    i1 = 105;
                    i2 = 176;
                } else if (r <= 0.75) {
                    i1 = 104;
                    i2 = 177;
                } else {
                    i1 = 103;
                    i2 = 176;
                }
            }
            final int x = i1;
            final int z = i2;
            Location l = victim.getLocation();
            for (Player player : Bukkit.getOnlinePlayers()) {
                CraftPlayer c = (CraftPlayer) victim;
                Property property = c.getProfile().getProperties().get("textures").iterator().next();
                MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
                EntityPlayer npc = new EntityPlayer(server, world, new GameProfile(UUID.randomUUID(), ""), new PlayerInteractManager(world));
                ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), victim.getName());
                team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
                team.setCollisionRule(ScoreboardTeamBase.EnumTeamPush.NEVER);
                npc.getProfile().getProperties().removeAll("textures");
                npc.getProfile().getProperties().put("textures", new Property("textures", property.getValue(), property.getSignature()));
                npc.getDataWatcher().set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);
                npc.setLocation(l.getX(), l.getY(), l.getZ(), 0F, 0F);
                summonedNpcsId.add(npc.getBukkitEntity().getEntityId());
                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
                SCHEDULER.scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
                    connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) 64));
                    connection.sendPacket(new PacketPlayOutBlockChange(npc.world, new BlockPosition(npc.locX, npc.locY, npc.locZ)));
                    connection.sendPacket(new PacketPlayOutBed(npc, new BlockPosition(x, 79, z)));
                    connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 1));
                    connection.sendPacket(new PacketPlayOutScoreboardTeam(team, 0));
                    connection.sendPacket(new PacketPlayOutScoreboardTeam(team, Collections.singletonList(npc.getName()), 3));
                    npc.teleportTo(new Location(victim.getWorld(), l.getX(), l.getY(), l.getZ(), 90F, 0F), false);
                    connection.sendPacket(new PacketPlayOutEntityTeleport(npc));
                }, 1L);
            }
            if (MurderHandler.innocentAlive == 0)
                MurderHandler.stopGame(CURRENTMAP, false, MurderHandler.WinType.INNOCENT_ALL_DIED, null);
        } catch (Exception exception) {
            printException(exception);
        }
    }
}

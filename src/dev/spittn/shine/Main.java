package dev.spittn.shine;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.glow.GlowAPI;
import org.inventivetalent.glow.GlowAPI.Color;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Main extends JavaPlugin implements CommandExecutor, Listener {

	private HashMap<String, Entity> selectingMap; 
	
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		
		selectingMap = Maps.newHashMap();
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
		if (!(s instanceof Player)) {
			s.sendMessage("Only players can use this command...");
			return true;
		}
		
		Player sender = (Player) s;
		
		boolean isMenu = getConfig().getString("command-or-menu").equalsIgnoreCase("menu");
		
		if (label.equalsIgnoreCase("glow")) {
			if (!sender.hasPermission("shine.glow")) {
				sender.sendMessage(getConfig().getString("permission-message").replace("&", "§"));
				return true;
			}
			if (isMenu) {
				openMenu(sender, sender);
				return true;
			}
 			if (args.length == 1) {
				Color color = null;
				for (Color c : Color.values()) {
					if (args[0].equalsIgnoreCase(c.name().replace("_", ""))) {
						color = c;
						break;
					}
				}
				
				if (color == null) {
					if (!args[0].equalsIgnoreCase("off")) {
						sender.sendMessage(getConfig().getString("invalidcolor-message").replace("&", "§"));
					} else {
						for (Player player : Bukkit.getOnlinePlayers()) {
							GlowAPI.setGlowing(sender, false, player);
						}
					}
				} else {
					if (!sender.hasPermission("shine.glow." + args[0].toLowerCase())) {
						sender.sendMessage(getConfig().getString("permission-message").replace("&", "§"));
					} else { 
						sender.sendMessage(getConfig().getString("glow-message").replace("&", "§").replace("%color%", WordUtils.capitalizeFully(color.name().replace("_", " ").toLowerCase())));
						
						GlowAPI.setGlowing(sender, color, Bukkit.getOnlinePlayers());
					}
				}
			} else {
				StringBuilder builder = new StringBuilder("§6Colors §8» ");
				for (Color c : Color.values()) {
					if (c != Color.NONE) {
						builder.append((sender.hasPermission("shine.glow." + c.name().replace("_", "").toLowerCase()) ? "§a" : "§c") + WordUtils.capitalizeFully(c.name().replace("_", " ").toLowerCase()) + "§7, ");
					}
				}
				sender.sendMessage(builder.toString().substring(0, builder.length() - ", ".length()));
			}
			return true;
		}
		
		if (label.equalsIgnoreCase("setglow")) {
			if (!sender.hasPermission("shine.setglow")) {
				sender.sendMessage(getConfig().getString("permission-message").replace("&", "§"));
				return true;
			} 
			Entity target = null;
			Location targetLocation = sender.getTargetBlock(null, 10).getLocation();
			
			for (Entity entity : sender.getWorld().getNearbyEntities(targetLocation, 1.5, 1.5, 1.5)) {
				target = entity; break;
			}
			
			if (target == null) {
				sender.sendMessage(getConfig().getString("invalidtarget-message").replace("&", "§"));
				return true;
			}
			
			if (isMenu) {
				openMenu(sender, target);
				return true;
			}
			if (args.length == 1) {
				Color color = null;
				for (Color c : Color.values()) {
					if (args[0].equalsIgnoreCase(c.name().replace("_", ""))) {
						color = c;
						break;
					}
				}
				
				if (color == null) {	
					sender.sendMessage(getConfig().getString("invalidcolor-message").replace("&", "§"));
				} else {
					if (!sender.hasPermission("glow." + args[0].toLowerCase())) {
						sender.sendMessage(getConfig().getString("permission-message").replace("&", "§"));
					} else { 
						sender.sendMessage(getConfig().getString("glow-message").replace("&", "§").replace("%color%", getChatColor(color) + WordUtils.capitalizeFully(color.name().replace("_", " ").toLowerCase())));
						
						GlowAPI.setGlowing(sender, color, Bukkit.getOnlinePlayers());
					}
				}
			} else {
				StringBuilder builder = new StringBuilder("§6Colors §8» ");
				for (Color c : Color.values()) {
					if (c != Color.NONE) {
						builder.append((sender.hasPermission("glow." + c.name().replace("_", "").toLowerCase()) ? "§a" : "§c") + WordUtils.capitalizeFully(c.name().replace("_", " ").toLowerCase()) + "§7, ");
					}
				}
				sender.sendMessage(builder.toString().substring(0, builder.length() - ", ".length()));
			}
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		for (Player oplayer : Bukkit.getOnlinePlayers()) {
			if (GlowAPI.isGlowing(oplayer, oplayer)) {
				GlowAPI.setGlowing(oplayer, GlowAPI.getGlowColor(oplayer, oplayer), player);
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		for (Player oplayer : Bukkit.getOnlinePlayers()) {
			if (GlowAPI.isGlowing(oplayer, player)) {
				GlowAPI.setGlowing(oplayer, false, player);
			}
		}
	}
	
	@EventHandler
	public void onSelect(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		
		if (!selectingMap.containsKey(player.getName())) {
			return;
		}
		
		if (event.getInventory().getName().equalsIgnoreCase(getConfig().getString("menu-title").replace("&", "§"))) {
			event.setCancelled(true);
			
			if (event.getCurrentItem() == null || event.getRawSlot() >= event.getInventory().getSize()) {
				return;
			}
			
			int x = -1;
			
			Entity target = selectingMap.get(player.getName());
			
			if (target == null) return; if (target.isDead()) return; 
			
			for (Color color : Color.values()) {
				if (color != Color.NONE) x++; 
				
				if (event.getSlot() == x) {
					if (player.hasPermission("shine.glow." + color.name().replace("_", "").toLowerCase())) {
						GlowAPI.setGlowing(target, color, Bukkit.getOnlinePlayers());
						player.closeInventory();
						
						if (target.equals(player)) {
							player.sendMessage(getConfig().getString("glow-message").replace("&", "§").replace("%color%", getChatColor(color) + WordUtils.capitalizeFully(color.name().replace("_", " ").toLowerCase())));
						}
					} 
					break;
				}
			}
		}
	}
	
	private void openMenu(Player player, Entity target) {
		Inventory menu = Bukkit.createInventory(null, 18, getConfig().getString("menu-title").replace("&", "§"));
		
		int i = 0;
		
		for (Color color : Color.values()) {
			if (color != Color.NONE) {
				ItemStack is; ItemMeta meta;
				
				if (player.hasPermission("shine.glow." + color.name().replace("_", "").toLowerCase())) {
					is = ItemType.valueOf(getConfig().getString("menu-haspermission-item.type")).apply(color);
					meta = is.getItemMeta();
					meta.setDisplayName(getConfig().getString("menu-haspermission-item.name").replace("&", "§").replace("%color%", getChatColor(color) + WordUtils.capitalizeFully(color.name().replace("_", " ").toLowerCase()))); 
					
					List<String> lore = Lists.newArrayList();
					for (String line : getConfig().getStringList("menu-haspermission-item.lore")) {
						lore.add(line.replace("&", "§").replace("%color%", getChatColor(color) + WordUtils.capitalizeFully(color.name().replace("_", " ").toLowerCase())));
					}
		
					meta.setLore(lore);
					is.setItemMeta(meta);
				} else {
					is = ItemType.valueOf(getConfig().getString("menu-nopermission-item.type")).apply(color);
					meta = is.getItemMeta();
					meta.setDisplayName(getConfig().getString("menu-nopermission-item.name").replace("&", "§").replace("%color%", getChatColor(color) + WordUtils.capitalizeFully(color.name().replace("_", " ").toLowerCase()))); 
					
					List<String> lore = Lists.newArrayList();
					for (String line : getConfig().getStringList("menu-nopermission-item.lore")) {
						lore.add(line.replace("&", "§").replace("%color%", getChatColor(color) + WordUtils.capitalizeFully(color.name().replace("_", " ").toLowerCase())));
					}
		
					meta.setLore(lore);
					is.setItemMeta(meta);
				}
				
				menu.setItem(i, is); i++;	
			}
		}
		
		player.openInventory(menu);
		selectingMap.put(player.getName(), target);
	}
	
	private ChatColor getChatColor(Color color) {
		if (color == Color.PURPLE) return ChatColor.LIGHT_PURPLE;
	
		return ChatColor.valueOf(color.name());
	}
}

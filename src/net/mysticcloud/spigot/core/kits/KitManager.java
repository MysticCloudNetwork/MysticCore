package net.mysticcloud.spigot.core.kits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.mysticcloud.spigot.core.Main;
import net.mysticcloud.spigot.core.runnables.KitCooldownTimer;
import net.mysticcloud.spigot.core.utils.CoreUtils;
import net.mysticcloud.spigot.core.utils.InventoryCreator;

public class KitManager {
	
	static boolean loaded = false;
	
	
	static String name = "Kit GUI";
	
	static Map<String, Kit> kitsManager = new HashMap<>();
	static Map<UUID, KitPacket> cooldown = new HashMap<>();
	static List<Kit> kits = new ArrayList<>();

	public static List<Kit> getKits(){
		return kits;
	}
	
	@SuppressWarnings("deprecation")
	public static void registerKits() {
		try {
			for (File file : getAllFiles()) {
				Kit kit = new Kit(file.getName().replace(".yml", ""));
				FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
				if(fc.isSet("Items")){
					for(String s : fc.getStringList("Items")){
						if(s.contains("CustomItem")){
							kit.addItem(CoreUtils.getItem(s.split(":")[1]));
						} else {
							kit.addItem(CoreUtils.decryptItemStack(s));
						}
					}
				}
				if(fc.isSet("Item")){
					if(fc.getString("Item").contains("CustomItem")){
						kit.setGUIItem(CoreUtils.getItem(fc.getString("Item").split(":")[1]));
					} else {
						kit.setGUIItem(CoreUtils.decryptItemStack(fc.getString("Item")));
					}
				}
				if(fc.isSet("DisplayName")){
					kit.setDisplayName(fc.getString("DisplayName"));
				}
				if(fc.isSet("Description")){
					kit.setDescription(fc.getString("Description"));
				}
				if(fc.isSet("Cooldown")){
					kit.setCooldown(fc.getInt("Cooldown"));
				}
				
				kitsManager.put(kit.getName(), kit);
				kits.add(kit);
			}
		} catch (NullPointerException ex) {
			createDemoFile();
			
		}
		if(!loaded){
			loaded = true;
			reloadKits();
		}
	}

	private static File[] getAllFiles() {
		return new File(Main.getPlugin().getDataFolder() + "/kits").listFiles();
	}

	public static void createFiles() {
		new File(Main.getPlugin().getDataFolder() + "/kits").mkdir();
	}

	public static void createDemoFile() {
		createFiles();
		File demo = new File(Main.getPlugin().getDataFolder() + "/kits/demo.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(demo);
		try {
			if(!demo.exists())
				demo.getParentFile().mkdirs();
			demo.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		fc.set("Cooldown", 100);
		
		List<String> demoList1 = new ArrayList<String>();
		demoList1.add("CustomItem:SuperSword-5");
		
		fc.set("Items", demoList1);
		
		fc.set("DisplayName", "&6Demo");
		fc.set("Description", "&fThis is the default kit.%nGenerated when no other kits exist.");
		fc.set("Item", "CustomItem:Backpack");
		

		try {
			fc.save(demo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		registerKits();

	}
	
	public static void reloadKits(){
		kitsManager.clear();
		kits.clear();
		registerKits();
	}

	public static boolean kitExists(String kit) {
		return kitsManager.containsKey(kit);
	}

	public static boolean isInCooldown(UUID uid, String kit) {
		if(cooldown.containsKey(uid)){
			if(cooldown.get(uid).equals(kit)) return true;
		}
		return false;
	}
	
	public static Kit getKit(String kit){
		return kitsManager.get(kit);
	}

	public static void applyKit(Player player, String kit) {
		if(cooldown.containsKey(player.getUniqueId()) && !player.hasPermission("mysticcloud.admin.kit.override")){
			player.sendMessage(CoreUtils.colorize("&e&lKits &f>&7 You can't use that kit yet. You must wait &f" + getSimpleTimeFormat(cooldown.get(player.getUniqueId()).cooldown) + "&7."));
			return;
		}
		for(ItemStack item : kitsManager.get(kit).getItems()){
			if(player.getInventory().firstEmpty() != -1){
				player.getInventory().addItem(item);
			} else {
				player.getWorld().dropItemNaturally(player.getLocation(), item);
				player.sendMessage(CoreUtils.colorize("&e&lKits &f>&7 No room in inventory. Dropping item on ground.."));
			}
		}
		KitPacket packet = new KitPacket(kit, getKit(kit).getCooldown());
		cooldown.put(player.getUniqueId(), packet);
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(), new KitCooldownTimer(player.getUniqueId(), packet), 20);
	}

	private static String getSimpleTimeFormat(int s) {
		
		
		
		
		
		
		
		int sec = s % 60;
	    int min = (s / 60)%60;
	    int hours = ((s/60)/60)%24;
	    int days = (((s/60)/60)/24)%7;
	    int weeks = (((s/60)/60)/24)/7;
	    
	    if(weeks > 0){
	    	return weeks + " weeks, " + days + " days, " + hours + " hours, " + min + " minutes, and " + sec + " seconds";
	    }
	    if(days > 0){
	    	return days + " days, " + hours + " hours, " + min + " minutes, and " + sec + " seconds";
	    }
	    if(hours > 0){
	    	return hours + " hours, " + min + " minutes, and " + sec + " seconds";
	    }
	    if(min > 0){
	    	return min + " minutes, and " + sec + " seconds";
	    }
	    if(sec > 0){
	    	return sec + " seconds";
	    }
	    
	    return "";
	}

	public static Inventory getGUI(Player player) {

		InventoryCreator inv = new InventoryCreator(name, (player), ((KitManager.getKits().size()/9)+1)*9);
		inv.addItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), "&eComing Soon", 'X',(String[]) null);
		ArrayList<Character> c = new ArrayList<Character>();
		for(int i=0;i!=(((int)(KitManager.getKits().size()/9))+1)*9;i++){
			if(i < KitManager.getKits().size()){
				if(player.hasPermission("mysticcloud.kit." + KitManager.getKits().get(i).getName())){
					inv.addItem(KitManager.getKits().get(i).getItem(), KitManager.getKits().get(i).getDisplayName(), (char) i,KitManager.getKits().get(i).getDescription(), KitManager.getKits().get(i).getItem().getDurability(), false);
				} else {
					inv.addItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), KitManager.getKits().get(i).getDisplayName(), (char) i, new String[] {"&cLocked..."}, (short) 15, false);
				}
				c.add((char)i);
			} else {
				c.add('X');
			}
			
			
		}
		inv.setConfiguration(c);		
		c.clear();
		c = null;
		return inv.getInventory();
	
	}

	public static String getGUIName() {
		return name;
	}

	public static void removeFromCooldown(UUID uid, String kit) {
		cooldown.remove(uid);
	}

}

package net.mysticcloud.spigot.core.utils;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.mysticcloud.spigot.core.Main;
import net.mysticcloud.spigot.core.utils.pets.v1_15_R1.PetManager;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class CoreUtils {

	private static IDatabase db = new IDatabase("localhost", "Minecraft", 3306, "mysql", "v4pob8LW");
	private static IDatabase wbconn = new IDatabase("localhost", "Website", 3306, "mysql", "v4pob8LW");
	private static boolean connected = false;
	private static Holiday holiday = Holiday.NONE;
	private static Date date = new Date();
	private static Inventory settingsMenu = null;
	public static Map<UUID, Boolean> playerparticles = new HashMap<>();

	private static boolean debug = false;

	private static File itemfile = new File(Main.getPlugin().getDataFolder() + "/Items");
	private static List<FileConfiguration> itemFiles = new ArrayList<>();
	private static Map<String, ItemStack> items = new HashMap<>();
	private static Map<String, FoodInfo> food = new HashMap<>();

	public static void start() {
		if (!connected) {
			if (testSQLConnection()) {
				connected = true;
				Bukkit.getConsoleSender().sendMessage(colorize("&e[SQL] &7>&f Successfully connected to SQL."));
			} else {
				connected = false;
				Bukkit.getConsoleSender().sendMessage(colorize("&e[SQL] &7>&f Error connecting to SQL. Disabling..."));
			}
		}
		if (!itemfile.exists()) {
			itemfile.mkdirs();
		}
		try {

			for (File file : itemfile.listFiles()) {
				if (file.getName().startsWith("item") && file.getName().endsWith(".yml")) {
					itemFiles.add(YamlConfiguration.loadConfiguration(file));
				}
			}
			itemFiles.size();

		} catch (NullPointerException ex) {

			File file = new File(itemfile.getPath() + "/itemDefault.yml");
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

			fc.set("SuperSword.Id", "DIAMOND_SWORD");
			fc.set("SuperSword.Data", "570");
			fc.set("SuperSword.Display", "&6Super Sword");

			try {
				fc.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			itemFiles.add(fc);

		}

		if (itemFiles.size() == 0) {

			File file = new File(itemfile.getPath() + "/itemDefault.yml");
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

			fc.set("SuperSword.Id", "DIAMOND_SWORD");
			fc.set("SuperSword.Data", "570");
			fc.set("SuperSword.Options.Display", "&6Super Sword");

			try {
				fc.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			itemFiles.add(fc);

		}
		PetManager.registerPets();
	}
	
	
	public static Map<String, ItemStack> getCachedItems(){
		
		return items;
	}

	public static void sendPluginMessage(Player player, String channel, String... arguments) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		for (String s : arguments) {
			out.writeUTF(s);
		}
		player.sendPluginMessage(Main.getPlugin(), channel, out.toByteArray());
	}

	private static boolean testSQLConnection() {
		return connected ? db.init() : connected;

	}

	public static String colorize(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	@SuppressWarnings("deprecation")
	public static String getTime() {

		String min = date.getMinutes() + "";
		String hr = date.getHours() + "";
		if (min.length() == 1)
			min = "0" + min;
		if (Integer.parseInt(hr) > 12) {
			return (Integer.parseInt(hr) - 12) + ":" + min + " PM";
		} else {
			return hr + ":" + min + " AM";
		}
	}

	public static int registerPlayer(String webName, Player player) throws SQLException {
		if (isPlayerRegistered(webName, player))
			return -100;
		return connected ? wbconn.update("UPDATE Users SET REGISTERED='waiting',MINECRAFT_UUID='" + player.getUniqueId()
				+ "' WHERE USERNAME='" + webName + "'") : -99;
	}

	private static boolean isPlayerRegistered(String webName, Player player) throws SQLException {

		if (connected) {
			wbconn.init();
			ResultSet rs = wbconn.query("SELECT * FROM Users");
			while (rs.next()) {
				if (rs.getString("USERNAME").equalsIgnoreCase(webName)) {
					try {
						if (!rs.getString("REGISTERED").equalsIgnoreCase("true"))
							return false;
						else
							return true;
					} catch (NullPointerException ex) {
						return false;
					}

				}

			}
			return false;
		} else {
			return false;
		}

	}

	public static ResultSet sendQuery(String query) throws NullPointerException {
		if (connected) {
			return db.query(query);
		} else {
			if (testSQLConnection()) {
				return db.query(query);
			}
			return null;
		}
	}

	public static Integer sendUpdate(String query) throws NullPointerException {
		if (connected) {
			return db.update(query);
		} else {
			if (testSQLConnection()) {
				return db.update(query);
			}
			return null;
		}
	}

	public static boolean sendInsert(String query) throws NullPointerException {
		if (connected) {
			return db.input(query);
		} else {
			if (testSQLConnection()) {
				return db.input(query);
			}
			return false;
		}
	}

	public static String toString(String[] args) {
		String s = "";
		for (String a : args) {
			s = s + " " + a;
		}
		return s;
	}

	@SuppressWarnings("deprecation")
	public static int getMonth() {
		return date.getMonth();
	}

	@SuppressWarnings("deprecation")
	public static int getDay() {
		return date.getDay();
	}

	public static void setHoliday(Holiday hol) {
		holiday = hol;
	}

	public static Holiday getHoliday() {
		return holiday;
	}

	public static List<String> colorizeStringList(List<String> stringList) {
		List<String> ret = new ArrayList<>();
		for (String s : stringList) {
			ret.add(colorize(s));
		}
		return ret;
	}

	public static List<String> colorizeStringList(String[] stringList) {
		List<String> ret = new ArrayList<>();
		for (String s : stringList) {
			ret.add(colorize(s));
		}
		return ret;
	}

	public static Random getRandom() {
		return new Random();
	}

	public static Inventory createSettingsMenu() {
		InventoryCreator inv = new InventoryCreator("&6Settings", null, 9);
		inv.addItem(new ItemStack(Material.DIAMOND), "&eParticles", 'A', new String[] {});
		inv.setConfiguration(new char[] { 'A', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' });
		settingsMenu = inv.getInventory();
		return settingsMenu;
	}

	public static Inventory getSettingsMenu() {
		if (settingsMenu == null) {
			createSettingsMenu();

		}
		return settingsMenu;
	}

	public static void toggleParticles(Player player) {
		if (playerparticles.get(player.getUniqueId())) {
			player.sendMessage(colorize("&e[Settings] &f> &7Particles turned off"));
			playerparticles.put(player.getUniqueId(), false);
		} else {
			player.sendMessage(colorize("&e[Settings] &f> &7Particles turned on"));
			playerparticles.put(player.getUniqueId(), true);
		}
	}

	@SuppressWarnings("deprecation")
	public static String getPlayerPrefix(Player player) {
		if (PermissionsEx.getUser(player).getGroups().length > 0) {

			String prefix = "";
			for (PermissionGroup group : PermissionsEx.getUser(player).getGroups()) {

				prefix = prefix + group.getPrefix();
			}
			return colorize(prefix);
		}
		return "";
	}

	@SuppressWarnings("deprecation")
	public static String getPlayerSuffix(Player player) {
		if (PermissionsEx.getUser(player).getGroups().length > 0) {

			String suffix = "";
			for (PermissionGroup group : PermissionsEx.getUser(player).getGroups()) {

				suffix = suffix + group.getSuffix();
			}
			return colorize(suffix);
		}
		return "";
	}

	public static boolean debugOn() {
		return debug;
	}

	public static boolean toggleDebug() {
		debug = !debug;
		return debug;
	}

	public static void setDebug(boolean status) {
		debug = status;
	}

	public static void debug(String message) {
		if (debug)
			Bukkit.broadcastMessage(colorize("&eDebug &7> &f" + message));
		Bukkit.getConsoleSender().sendMessage(colorize("&eDebug &7>&f " + message));
	}

	public static String encryptItemStack(ItemStack i) {
		try {
			return i.getType() + ":" + i.getAmount() + ":" + i.getDurability();
		} catch (NullPointerException ex) {
			return "AIR:1:0";
		}

	}

	@Deprecated
	public static ItemStack decryptItemStack(String s) {
		if (s.contains(":")) {
			String[] d = s.split(":");
			if (d.length == 2) {
				return new ItemStack(Material.valueOf(d[0]), 1, Short.parseShort(d[1]));
			}
			if (d.length == 3) {
				return new ItemStack(Material.valueOf(d[0]), Integer.parseInt(d[1]), Short.parseShort(d[2]));
			}
			return new ItemStack(Material.valueOf(d[0]));

		} else {
			return new ItemStack(Material.valueOf(s));
		}

	}

	public static ItemStack getItem(String name) {
		debug("Loading Item " + name);

		ItemStack i = new ItemStack(Material.STONE);
		int amount = 1;
		if (name.contains("-")) {
			amount = Integer.parseInt(name.split("-")[1]);
			name = name.split("-")[0];
		}
		if (items.containsKey(name)) {

			i = items.get(name).clone();
			i.setAmount(amount);
			return i;
		}
		debug("Couldn't find item " + name + ". Searching in Items folder...");
		boolean food = false;
		boolean found = false;
		FoodInfo info = new FoodInfo(name);
		ItemMeta a = i.getItemMeta();
		for (FileConfiguration item : itemFiles) {
			if (!item.isSet(name))
				continue;
			found = true;
			
			
			
			if (item.isSet(name + ".Id"))
				i.setType(Material.valueOf(item.getString(name + ".Id")));
			if (item.isSet(name + ".Data"))
				i.setDurability(Short.parseShort(item.getString(name + ".Data")));
			a.setDisplayName(item.isSet(name + ".Options.Display") ? colorize(item.getString(name + ".Options.Display"))
					: ChatColor.RESET + (i.getType() + "").substring(0, 1).toUpperCase()
							+ (i.getType() + "").substring(1, (i.getType() + "").length()).toLowerCase());
			if (item.isSet(name + ".Options.Hide"))
				if (item.get(name + ".Options.Hide") != "All")
					for (String s : item.getStringList(name + ".Options.Hide"))
						a.addItemFlags(ItemFlag.valueOf("HIDE_" + s.toUpperCase()));
				else
					for (ItemFlag flag : ItemFlag.values())
						a.addItemFlags(flag);

			if (item.isSet(name + ".Options.Unbreakable"))
				a.setUnbreakable(Boolean.parseBoolean(item.getString(name + ".Options.Unbreakable")));

			if (item.isSet(name + ".Options.Lore"))
				a.setLore(CoreUtils.colorizeStringList(item.getStringList(name + ".Options.Lore")));

			if (item.isSet(name + ".Food.Hunger")) {
				food = true;
				info.setHungerLevel(item.getInt(name + ".Food.Hunger"));
			}
			if (item.isSet(name + ".Food.Potion")) {
				food = true;
				for (String s : item.getStringList(name + ".Food.Potion")) 
					info.addPotionEffect(new PotionEffect(PotionEffectType.getByName(s.split("-")[0].toUpperCase()),
							Integer.parseInt(s.split("-")[1]), Integer.parseInt(s.split("-")[2])));
				
			}
			if (item.isSet(name + ".Food.Health")) {
				food = true;
				info.setHealingFactor(item.getInt(name + ".Food.Health"));
			}
			if (food) {
				List<String> lore = a.hasLore() ? a.getLore() : new ArrayList<>();
				lore.add(ChatColor.DARK_GRAY + "Food:" + name);
				a.setLore(lore);
			}
			i.setAmount(amount);

			if (item.isSet(name + ".Attributes.MainHand.Damage")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Attack Damage",
						item.getDouble(name + ".Attributes.MainHand.Damage"), Operation.ADD_NUMBER, EquipmentSlot.HAND);
				a.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, am);
			}
			if (item.isSet(name + ".Attributes.MainHand.AttackSpeed")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Attack Speed",
						item.getDouble(name + ".Attributes.MainHand.AttackSpeed"), Operation.ADD_NUMBER,
						EquipmentSlot.HAND);
				a.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, am);
			}
			if (item.isSet(name + ".Attributes.Helmet.Protection")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Helmet Protection",
						item.getDouble(name + ".Attributes.Helmet.Protection"), Operation.ADD_NUMBER,
						EquipmentSlot.HEAD);
				a.addAttributeModifier(Attribute.GENERIC_ARMOR, am);
			}
			if (item.isSet(name + ".Attributes.Chestplate.Protection")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Chestplate Protection",
						item.getDouble(name + ".Attributes.Chestplate.Protection"), Operation.ADD_NUMBER,
						EquipmentSlot.CHEST);
				a.addAttributeModifier(Attribute.GENERIC_ARMOR, am);
			}
			if (item.isSet(name + ".Attributes.Pants.Protection")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Pants Protection",
						item.getDouble(name + ".Attributes.Pants.Protection"), Operation.ADD_NUMBER,
						EquipmentSlot.LEGS);
				a.addAttributeModifier(Attribute.GENERIC_ARMOR, am);
			}
			if (item.isSet(name + ".Attributes.Boots.Protection")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Boot Protection",
						item.getDouble(name + ".Attributes.Boots.Protection"), Operation.ADD_NUMBER,
						EquipmentSlot.FEET);
				a.addAttributeModifier(Attribute.GENERIC_ARMOR, am);

			}

			if (item.isSet(name + ".Attributes.Helmet.MovementSpeed")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Helmet Movement Speed",
						item.getDouble(name + ".Attributes.Helmet.MovementSpeed"), Operation.ADD_NUMBER,
						EquipmentSlot.HEAD);
				a.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, am);
			}
			if (item.isSet(name + ".Attributes.Chestplate.MovementSpeed")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Chestplate Movement Speed",
						item.getDouble(name + ".Attributes.Chestplate.MovementSpeed"), Operation.ADD_NUMBER,
						EquipmentSlot.CHEST);
				a.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, am);
			}
			if (item.isSet(name + ".Attributes.Pants.MovementSpeed")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Pants Movement Speed",
						item.getDouble(name + ".Attributes.Pants.MovementSpeed"), Operation.ADD_NUMBER,
						EquipmentSlot.LEGS);
				a.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, am);
			}
			if (item.isSet(name + ".Attributes.Boots.MovementSpeed")) {

				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Boots Movement Speed",
						item.getDouble(name + ".Attributes.Boots.MovementSpeed"), Operation.ADD_NUMBER,
						EquipmentSlot.FEET);
				a.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, am);
			}

		}
		a.setDisplayName(a.hasDisplayName() ? a.getDisplayName() : CoreUtils.colorize("&cERROR"));
		i.setItemMeta(a);
		if (!found)

		{
			debug("Item was not found.");

			return i.clone();
		}

		debug("Item loaded from config, and saved to cache.");
		if (food)
			debug("Item " + name + " was food.");
		items.put(name, i.clone());
		if (food)
			CoreUtils.food.put(name, info);
		return i.clone();
	}

	public static boolean connected() {
		return connected;
	}

}

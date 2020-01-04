package net.mysticcloud.spigot.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.mysticcloud.spigot.core.commands.DebugCommand;
import net.mysticcloud.spigot.core.commands.GRLCommand;
import net.mysticcloud.spigot.core.commands.ItemCommand;
import net.mysticcloud.spigot.core.commands.KitCommand;
import net.mysticcloud.spigot.core.commands.PetCommand;
import net.mysticcloud.spigot.core.commands.RegisterCommand;
import net.mysticcloud.spigot.core.commands.SQLCommand;
import net.mysticcloud.spigot.core.commands.SettingsCommand;
import net.mysticcloud.spigot.core.kits.KitManager;
import net.mysticcloud.spigot.core.listeners.PlayerListener;
import net.mysticcloud.spigot.core.runnables.DateChecker;
import net.mysticcloud.spigot.core.utils.CoreUtils;
import net.mysticcloud.spigot.core.utils.pets.v1_15_R1.PetManager;

public class Main extends JavaPlugin {
	
	static Main plugin;
	public void onEnable() {
		plugin = this;
		
		CoreUtils.start();
		
		KitManager.registerKits();
		
		new PlayerListener(this);
		new KitCommand(this, "kit");
		new SQLCommand("sql", this);
		new SettingsCommand("settings", this);
		new PetCommand(this, "pet");
		new DebugCommand(this, "debug");
		new ItemCommand(this, "item");
		new GRLCommand(this, "grl");
		new RegisterCommand(this, "register");
		
		
		startDateChecker();
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.setPlayerListName(CoreUtils.colorize(CoreUtils.getPlayerPrefix(player) + player.getName()));
			CoreUtils.playerparticles.put(player.getUniqueId(), true);
		}
		
		
		
	}
	
	public void onDisable() {
		PetManager.removeAllPets();
	}
	
	private static void startDateChecker(){
		
		Bukkit.getScheduler().runTaskLater(getPlugin(), new DateChecker(), 1);
	}
	
	
	
	public static Main getPlugin(){
		return plugin;
	}

}

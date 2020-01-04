package net.mysticcloud.spigot.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import net.mysticcloud.spigot.core.Main;
import net.mysticcloud.spigot.core.utils.pets.v1_15_R1.PetManager;

public class PetCommand implements CommandExecutor {

	public PetCommand(Main plugin, String cmd){
		plugin.getCommand(cmd).setExecutor(this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
//			PetManager.spawnPet(((Player)sender), ((Player)sender).getLocation());
//			Minion minion = (Minion) CoreUtils.spawnEntity(new Minion(((CraftWorld)((Player)sender).getWorld()).getHandle()), ((Player)sender).getLocation());
//			minion.setOwner(((Player)sender).getName());
			((Player)sender).openInventory(PetManager.getGUI(((Player)sender)));
			((Player)sender).setMetadata("petinv", new FixedMetadataValue(Main.getPlugin(), "yes"));
//			PetManager.spawnPet(((Player) sender), "demo", ((Player)sender).getLocation());
		}
		return false;
	}
}

package net.mysticcloud.spigot.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.mysticcloud.spigot.core.Main;
import net.mysticcloud.spigot.core.commands.listeners.CommandTabCompleter;
import net.mysticcloud.spigot.core.utils.CoreUtils;

public class ItemCommand implements CommandExecutor {

	public ItemCommand(Main plugin, String cmd) {
		PluginCommand com = plugin.getCommand(cmd);
		com.setExecutor(this);
		com.setTabCompleter(new CommandTabCompleter());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(CoreUtils.colorize("&eItem &7>&f ")
					+ ((sender instanceof Player) 
							? "Usage: /item <item>" 
							: "Usage: /item <item> <player>"));
		}
		if (args.length == 1) {
			if (sender instanceof Player && sender.hasPermission("hyverse.item." + args[0].toLowerCase())) 
				((Player) sender).getInventory().addItem(CoreUtils.getItem(args[0]));
			 else
				sender.sendMessage(
						CoreUtils.colorize("&eItem &7>&f Error proccessing command. Try /item <item> <player>"));
		}
		if (args.length == 2) {
			if (sender instanceof Player && sender.hasPermission("hyverse.item." + args[0].toLowerCase())) {
				Player player = (Player) sender;
				ItemStack i = CoreUtils.getItem(args[0]);
				i.setAmount(Integer.parseInt(args[1]));
				player.getInventory().addItem(i);
			}
		}
		if (args.length == 3) {
			if (sender.hasPermission("mysticcloud.item." + args[0].toLowerCase())
					&& sender.hasPermission("mysticcloud.item.give")) {
				if (Bukkit.getPlayer(args[1]) == null) {
					if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("*")) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							ItemStack i = CoreUtils.getItem(args[0]);
							i.setAmount(Integer.parseInt(args[1]));
							player.getInventory().addItem(i);
						}
						return true;
					}
					sender.sendMessage(CoreUtils.colorize("&eHyverse &7>&f That player is not online."));
					return true;
				}
				ItemStack i = CoreUtils.getItem(args[0]);
				i.setAmount(Integer.parseInt(args[1]));
				Bukkit.getPlayer(args[1]).getInventory().addItem(i);
			}
		}

		return true;
	}
}

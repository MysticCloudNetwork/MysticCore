package net.mysticcloud.spigot.core.commands.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.mysticcloud.spigot.core.kits.Kit;
import net.mysticcloud.spigot.core.kits.KitManager;
import net.mysticcloud.spigot.core.utils.CoreUtils;

public class CommandTabCompleter implements TabCompleter {


	public CommandTabCompleter() {

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		
		if(label.toUpperCase().contains("ITEM")) {
			if(args.length == 1) {
				StringUtil.copyPartialMatches(args[0], CoreUtils.getCachedItems().keySet(), completions);
			}
			if(args.length == 2) {
				List<String> players = new ArrayList<>();
				for(Player player :Bukkit.getOnlinePlayers()) {
					players.add(player.getName());
				}
				StringUtil.copyPartialMatches(args[1], players, completions);
			}
		}
		
		if(label.toUpperCase().contains("KIT")) {
			if(args.length == 1) {
				List<String> kits = new ArrayList<>();
				for(Kit kit : KitManager.getKits()) {
					if(sender.hasPermission("mysticcloud.kit." + kit.getName()))
						kits.add(kit.getName());
				}
				StringUtil.copyPartialMatches(args[0], kits, completions);
			}
			if(args.length == 2) {
				List<String> players = new ArrayList<>();
				for(Player player :Bukkit.getOnlinePlayers()) {
					players.add(player.getName());
				}
				StringUtil.copyPartialMatches(args[1], players, completions);
			}
		}
		
		
		
		return completions;

	}

}

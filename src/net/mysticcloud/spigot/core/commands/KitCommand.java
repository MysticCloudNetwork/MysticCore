package net.mysticcloud.spigot.core.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import net.mysticcloud.spigot.core.Main;
import net.mysticcloud.spigot.core.kits.Kit;
import net.mysticcloud.spigot.core.kits.KitManager;
import net.mysticcloud.spigot.core.utils.CoreUtils;

public class KitCommand implements CommandExecutor {

	public KitCommand(Main plugin, String cmd) {
		plugin.getCommand(cmd).setExecutor(this);
//		com.set/abCompleter(new CommandTabCompleter());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {

				List<String> s = new ArrayList<>();
				for (Kit kit : KitManager.getKits()) {
					if (sender.hasPermission("mysticcloud.kit." + kit.getName())) {
						s.add(kit.getName());
					}
				}
				if (sender.hasPermission("mysticcloud.kit.gui")) {
					((Player) sender).openInventory(KitManager.getGUI(((Player) sender)));
					((Player)sender).setMetadata("kitinv", new FixedMetadataValue(Main.getPlugin(), "yes"));
				}
				sender.sendMessage(CoreUtils.colorize("&e&lKits &f>&7 You have access to these kits: " + s.toString()));
			} else {
				sender.sendMessage(CoreUtils.colorize("&e&lKits &f>&7 Only players can run that command."));
			}
		} else {
			if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("mysticcloud.kit.admin.reload")) {
				KitManager.reloadKits();
			} else {
				if (sender instanceof Player) {

					if (KitManager.kitExists(args[0])) {
						if (sender.hasPermission("mysticcloud.kit." + args[0])) {
							if (!KitManager.isInCooldown(((Player) sender).getUniqueId(), args[0])
									|| sender.hasPermission("mysticcloud.kit.overridecooldown")) {
								KitManager.applyKit(((Player) sender), args[0]);
								return false;
							} else {
								sender.sendMessage(
										CoreUtils.colorize("&e&lKits &f>&7 You need to wait to use that kit."));
							}
						} else {
							sender.sendMessage(
									CoreUtils.colorize("&e&lKits &f>&7 You don't have permission to use that kit"));
						}

					} else {
						sender.sendMessage(CoreUtils.colorize("&e&lKits &f>&7 That kit doesn't exist."));
					}
				} else {
					sender.sendMessage(CoreUtils.colorize("&e&lKits &f>&7 Only players can run that command."));
				}

			}
		}

		return false;
	}

}

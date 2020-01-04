package net.mysticcloud.spigot.core.runnables;

import java.util.UUID;

import org.bukkit.Bukkit;

import net.mysticcloud.spigot.core.Main;
import net.mysticcloud.spigot.core.kits.KitManager;
import net.mysticcloud.spigot.core.kits.KitPacket;


public class KitCooldownTimer implements Runnable {
	
	UUID uid;
	KitPacket packet;
	
	public KitCooldownTimer(UUID uid, KitPacket packet){
		this.uid = uid;
		this.packet = packet;
	}

	@Override
	public void run() {
		packet.cooldown -=1;
		if(packet.cooldown <= 0){
			KitManager.removeFromCooldown(uid, packet.kit);
		} else {
			Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(), new KitCooldownTimer(uid,packet), 20);
		}
	}

}

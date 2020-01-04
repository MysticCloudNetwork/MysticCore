package net.mysticcloud.spigot.core.kits;

public class KitPacket {
	
	public int cooldown;
	public String kit;
	
	public KitPacket(String kit, int cooldown){
		this.cooldown = cooldown;
		this.kit = kit;
	}
	
}

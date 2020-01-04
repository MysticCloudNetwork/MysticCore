package net.mysticcloud.spigot.core.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.mysticcloud.spigot.core.utils.CoreUtils;

public class Kit {
	
	List<ItemStack> items = new ArrayList<>();
	int cooldown = 0;
	String name;
	List<String> desc = new ArrayList<>();
	ItemStack guiitem = new ItemStack(Material.DIAMOND_SWORD);
	String display = "";
	
	public Kit(String name){
		this.name = name;
		display = name;
	}
	
	public void setDisplayName(String displayName){
		this.display = CoreUtils.colorize(displayName);
	}
	
	public void setGUIItem(ItemStack i){
		guiitem = i;
	}
	public void setDescription(String desc){
		if(desc.contains("%n")){
			for(String s : desc.split("%n")){
				this.desc.add(CoreUtils.colorize("&f" + s));
			}
		} else{
			this.desc.add(CoreUtils.colorize(desc));
		}
		
	}
	
	public String getName(){
		return name;
	}

	public void addItem(ItemStack item) {
		items.add(item);
	}
	
	public void setCooldown(int cooldown){
		this.cooldown = cooldown;
	}
	
	public int getCooldown(){
		return cooldown;
	}

	public List<ItemStack> getItems() {
		return items;
	}

	public ItemStack getItem() {
		
		return guiitem;
	}
	public List<String> getDescription(){
		return desc;
	}

	public String getDisplayName() {
		return display;
	}

}

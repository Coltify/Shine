package dev.spittn.shine;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.glow.GlowAPI.Color;

public enum ItemType {

	WOOL(Material.WOOL), CLAY(Material.STAINED_CLAY), CONCRETE(Material.CONCRETE), CONCRETE_POWDER(Material.CONCRETE_POWDER);
	
	private Material base;
	
	private ItemType(Material base) {
		this.base = base;
	}
	
	public Material getBase() {
		return base;
	}
	
	public ItemStack apply(Color color) {
		short data = 0;
		
		switch (color) {
		case AQUA:
			data = 3;
			break;
		case BLACK:
			data = 15;
			break;
		case BLUE:
			data = 11;
			break;
		case DARK_AQUA:
			data = 9;
			break;
		case DARK_BLUE:
			data = 11;
			break;
		case DARK_GRAY:
			data = 7;
			break;
		case DARK_GREEN:
			data = 13;
			break;
		case DARK_PURPLE:
			data = 10;
			break;
		case DARK_RED:
			data = 14;
			break;
		case GOLD:
			data = 1;
			break;
		case GRAY:
			data = 8;
			break;
		case GREEN:
			data = 5;
			break;
		case PURPLE:
			data = 10;
			break;
		case RED:
			data = 14;
			break;
		case YELLOW:
			data = 4;
			break;
		default:
			break;
		}
		
		ItemStack is = new ItemStack(base, 1, data);
		return is;
	}
}

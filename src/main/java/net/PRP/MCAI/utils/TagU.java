package net.PRP.MCAI.utils;

import java.util.HashMap;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;

public class TagU {
	public static CompoundTag createEnchant(short lvl, String name_id) {
		if (name_id.startsWith("minecraft:")) name_id=name_id.replace("minecraft:", "");
		HashMap<String, Tag> tags = new HashMap<String, Tag>();
		
		tags.put("lvl", new ShortTag("lvl",lvl));
		tags.put("id", new StringTag("id","minecraft:"+name_id));
		
		return new CompoundTag("",tags);
	}
	
	public static CompoundTag createItemLore(String lore) {
		return null;
	}
	
}

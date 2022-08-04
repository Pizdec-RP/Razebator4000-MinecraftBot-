package net.PRP.MCAI.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;


public class MapUtils {
	
	public static BufferedImage mapToPng(ServerMapDataPacket data) {
		int w = (int)Math.sqrt(data.getData().getData().length);
		int h = (int)Math.sqrt(data.getData().getData().length);
		BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		int i = 0;
		Map<Byte, Color> colors = new HashMap<>();
		for (byte bt : data.getData().getData()) {
			boolean alreadyin = false;
			for (Entry<Byte, Color> entry : colors.entrySet()) {
				if (entry.getKey() == bt) {
					alreadyin = true;
				}
			}
			if (!alreadyin) {
				colors.put(bt, new Color(MathU.rnd(1,255),MathU.rnd(1,255),MathU.rnd(1,255)));
			}
		}
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				img.setRGB(y, x, colors.get(data.getData().getData()[i]).getRGB());
				i++;
			}
		}
		return img;
	}
}

package net.PRP.MCAI.bot.specific;

import java.util.List;

import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Vector3D;

public class VirtualMouse {
	public Bot client;
	private boolean RMB = false, LMB = false;
	private static final double scale = 0.4D;
	public Block currentMineable = null;
	private int animPassTicks = 0;
	
	public VirtualMouse(Bot client) {
		this.client = client;
	}
	
	public void tick() {
		if (animPassTicks > 0) animPassTicks--;
		if (LMB) {
			swingArm();
			LMB = false;
			List<Vector3D> ray = client.vis.createRay(client.getEyeLocation(), client.getYaw(), client.getPitch(), (int)((int)Main.getset("maxpostoblock") / scale), scale);
			for (Vector3D point : ray) {
				Block b = point.getBlock(client);
				if (!b.isAvoid() && !b.isLiquid()) {
					currentMineable = b;
					break;
				}
			}
		}
		if (RMB) {
			
		}
		if (currentMineable != null) {
			
		}
		
	}
	
	public void swingArm() {
		if (animPassTicks <= 0) {
			client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
			animPassTicks = 7;
		}
	}
	
	public void reset() {
		RMB = false;
		LMB = false;
		currentMineable = null;
	}
	
	
}

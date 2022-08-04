package net.PRP.MCAI.utils;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
//import com.github.steveice10.mc.protocol.packet.ingame.client.player.
import georegression.struct.point.Point3D_F64;
import georegression.struct.point.Vector3D_F64;
import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;
public class BotU {
	public static void log(Object f) {
    	if (Main.debug) System.out.println("[log] "+f.toString());
    }
	public static void wn(Object f) {
    	System.out.println("[warn] "+f.toString());
    }
	public static void ts(Object p) {
		System.out.println(p);
	}
	public static void chat (Bot client, String text) {
		client.getSession().send(new ClientChatPacket(text));
		//log("message sended, content: "+text);
	}
	
	/*public static void calibratePosition(Bot client) {
		client.setPosX((int) Math.floor(client.getPosX())+0.5);
		client.setPosZ((int) Math.floor(client.getPosZ())+0.5);
	}
	
	public static void calibrateY(Bot client) {
		client.setPosY(Math.floor(client.getPosY()));
	}*/

	public static void SetSlot(Bot client, int slot) {
		if (slot >= 0 && slot <=8) {
			if (client.currentHotbarSlot != 36+slot) {
				client.getSession().send(new ClientPlayerChangeHeldItemPacket(slot));
		        client.currentHotbarSlot = 36+slot;
			}
		} else if (slot >= 36 && slot <= 44) {
			if (client.currentHotbarSlot != slot) {
				client.getSession().send(new ClientPlayerChangeHeldItemPacket(slot-36));
		        client.currentHotbarSlot = slot;
			}
		}
    }
	
	public static void LookHead(Bot client, Vector3D p) {
		if (p == null) return;
		LookHead(client, new Point3D_F64(Math.floor(p.x),Math.floor(p.y),Math.floor(p.z)));
	}
	
	public static void LookHead(Bot client, Point3D_F64 position) {
		Point3D_F64 PlayerPosition = new Point3D_F64(client.getPosX()-0.5, client.getPosY()+1.025, client.getPosZ()-0.5);
        Vector3D_F64 vect = new Vector3D_F64(PlayerPosition, position);
        vect.normalize();
        //System.out.println(position.x +" "+position.y+" "+position.z);
        double yaw = Math.toDegrees(Math.atan2(vect.z, vect.x)) - 90;
        double pitch = Math.toDegrees(Math.asin(-vect.y));
        client.setYaw((float) yaw);
        client.setPitch((float) pitch);
    }
	
	
	
}


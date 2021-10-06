package pizdecrp.MCAI.utils;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;

import pizdecrp.MCAI.bot.Bot;
import pizdecrp.MCAI.bot.SessionListener;
public class BotU {
	//private static HashMap<ChunkCoordinates, Column> columns = new HashMap<>();
	public static void chat (Bot client, String text) {
		client.getSession().send(new ClientChatPacket(text));
	}
	public static void move (Bot client, double range, String ax) {
		try {
			switch (ax) {
				case "x":
					client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX() + range, client.getPosY(), client.getPosZ()));
					client.setPosX(client.getPosX() + range);
					break;
	            case "y":
	            	client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX(), client.getPosY() + range, client.getPosZ()));
	            	client.setPosX(client.getPosY() + range);
	            	break;
	            case "z":
	            	client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX(), client.getPosY(), client.getPosZ() + range));
	            	client.setPosX(client.getPosZ() + range);
	            	break;
	            default:
	            	SessionListener.log("корда "+ax+" неверная");
	            	break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void walk (Bot client, int range, String ax) {
		try {
			switch (ax) {
				case "x":
					for (int i = 0; i < range; i++) {
						for (int o = 0; o < 5; o++) {
							client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX() + 0.2, client.getPosY(), client.getPosZ()));
							client.setPosX(client.getPosX() + 0.2);
							ThreadU.sleep(50);
						}
					}
					break;
	            case "z":
	            	for (int i = 0; i < range; i++) {
						for (int o = 0; o < 5; o++) {
							client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX(), client.getPosY(), client.getPosZ() + 0.2));
							client.setPosX(client.getPosZ() + 0.2);
							ThreadU.sleep(50);
						}
					}
					break;
	            default:
	            	SessionListener.log("корда "+ax+" неверная");
	            	break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void jump (Bot client, double range, String ax) {
		try {
			switch (ax) {
				case "x":
					client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX() + range, client.getPosY()+1, client.getPosZ()));
					client.setPosX(client.getPosX() + range);
					break;
	            case "z":
	            	client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX(), client.getPosY()+1, client.getPosZ() + range));
	            	client.setPosX(client.getPosZ() + range);
	            	break;
	            default:
	            	SessionListener.log("корда "+ax+" неверная");
	            	break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void teleport(Bot client, Position pos) {
		double dfx = client.getPosX() + 0.5;
		double dfy = client.getPosY();
		double dfz = client.getPosZ() + 0.5;
		try {
			client.getSession().send(new ClientPlayerPositionPacket(true, pos.getX(), pos.getY(), pos.getZ()));
	    	client.setPosX(pos.getX());
	    	client.setPosY(pos.getY());
	    	client.setPosZ(pos.getZ());
		} catch (Exception e) {
			SessionListener.log("произошла ошибка при попытке телепорта, возвращаю бота. "+e);
			client.setPosX(dfx);
			client.setPosY(dfy);
			client.setPosZ(dfz);
			client.getSession().send(new ClientPlayerPositionPacket(true, dfx, dfy, dfz));
		}
	}
	
}


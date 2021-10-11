package pizdecrp.MCAI.utils;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
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
			if (range < 0) SessionListener.log("tp down");
			switch (ax) {
				case "x":
					client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX() + range, client.getPosY(), client.getPosZ()));
					client.setPosX(client.getPosX() + range);
	            case "y":
	            	client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX(), client.getPosY() + range, client.getPosZ()));
	            	client.setPosY(client.getPosY() + range);
	            case "z":
	            	client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX(), client.getPosY(), client.getPosZ() + range));
	            	client.setPosZ(client.getPosZ() + range);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void walk (Bot client, int range, String ax) {
		try {
			if (range > 0) {
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
								client.setPosZ(client.getPosZ() + 0.2);
								ThreadU.sleep(50);
							}
						}
						break;
		            default:
		            	SessionListener.log("корда "+ax+" неверная");
		            	break;
				}
			} else if (range < 0) {
				switch (ax) {
				case "x":
					for (int i = 0; i < range; i++) {
						for (int o = 0; o < 5; o++) {
							client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX() - 0.2, client.getPosY(), client.getPosZ()));
							client.setPosX(client.getPosX() - 0.2);
							ThreadU.sleep(50);
						}
					}
					break;
	            case "z":
	            	for (int i = 0; i < range; i++) {
						for (int o = 0; o < 5; o++) {
							client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX(), client.getPosY(), client.getPosZ() - 0.2));
							client.setPosZ(client.getPosZ() - 0.2);
							ThreadU.sleep(50);
						}
					}
					break;
	            default:
	            	SessionListener.log("корда "+ax+" неверная");
	            	break;
				}
			} else {
				SessionListener.log("walk for 0??? rily u dumbass");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void jump (Bot client, int range, String ax) {
		try {
			double onetprange;
			if (range > 0) {
				onetprange = 0.5;
			} else if (range < 0) {
				onetprange = -0.5;
			} else {
				onetprange = 0;
			}
			switch (ax) {
				case "x":
					if (range == 2) {
						teleport(client,onetprange,0.7,0);
						ThreadU.sleep(175);
						teleport(client,onetprange,0.7,0);
						ThreadU.sleep(175);
						teleport(client,onetprange,-0.7,0);
						ThreadU.sleep(175);
						teleport(client,onetprange,-0.7,0);
						ThreadU.sleep(175);
					} else if (range == 3) {
						teleport(client,onetprange,0.46,0);
						ThreadU.sleep(116);
						teleport(client,onetprange,0.46,0);
						ThreadU.sleep(116);
						teleport(client,onetprange,0.46,0);
						ThreadU.sleep(116);
						teleport(client,onetprange,-0.46,0);
						ThreadU.sleep(116);
						teleport(client,onetprange,-0.46,0);
						ThreadU.sleep(116);
						teleport(client,onetprange,-0.46,0);
						ThreadU.sleep(116);
					} else if (range == 4) {
						teleport(client,onetprange,0.25,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,0.25,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,0.25,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,0.25,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,-0.25,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,-0.25,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,-0.25,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,-0.25,0);
						ThreadU.sleep(175);
					} else if (range == 5) {
						teleport(client,onetprange,0.2,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,0.2,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,0.2,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,0.2,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,0.2,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,-0.2,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,-0.2,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,-0.2,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,-0.2,0);
						ThreadU.sleep(100);
						teleport(client,onetprange,-0.2,0);
						ThreadU.sleep(175);
					} else {
						SessionListener.log("jummp range is too high "+range);
					}
					break;
	            case "z":
	            	if (range == 2) {
						teleport(client,0,0.7,onetprange);
						ThreadU.sleep(175);
						teleport(client,0,0.7,onetprange);
						ThreadU.sleep(175);
						teleport(client,0,-0.7,onetprange);
						ThreadU.sleep(175);
						teleport(client,0,-0.7,onetprange);
						ThreadU.sleep(175);
					} else if (range == 3) {
						teleport(client,0,0.46,onetprange);
						ThreadU.sleep(116);
						teleport(client,0,0.46,onetprange);
						ThreadU.sleep(116);
						teleport(client,0,0.46,onetprange);
						ThreadU.sleep(116);
						teleport(client,0,-0.46,onetprange);
						ThreadU.sleep(116);
						teleport(client,0,-0.46,onetprange);
						ThreadU.sleep(116);
						teleport(client,0,-0.46,onetprange);
						ThreadU.sleep(116);
					} else if (range == 4) {
						teleport(client,0,0.25,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,0.25,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,0.25,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,0.25,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,-0.25,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,-0.25,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,-0.25,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,-0.25,onetprange);
						ThreadU.sleep(175);
					} else if (range == 5) {
						teleport(client,0,0.2,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,0.2,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,0.2,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,0.2,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,0.2,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,-0.2,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,-0.2,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,-0.2,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,-0.2,onetprange);
						ThreadU.sleep(100);
						teleport(client,0,-0.2,onetprange);
						ThreadU.sleep(175);
					} else {
						SessionListener.log("jummp range is too high "+range);
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
	
	public static void setposto(Bot client, double x, double y, double z) {
		try {
			client.getSession().send(new ClientPlayerPositionPacket(true, x, y, z));
	    	client.setPosX(x);
	    	client.setPosY(y);
	    	client.setPosZ(z);
		} catch (Exception e) {
			SessionListener.log("произошла ошибка при попытке телепорта. ");
		}
	}
	public static void teleport(Bot client, double xa, double ya, double za) {
		double x = client.getPosX() + xa;
		double y = client.getPosY() + ya;
		double z = client.getPosZ() + za;
		SessionListener.log("trying to tp to x:"+x+" y:"+y+" z:"+z);
		client.getSession().send(new ClientPlayerPositionPacket(true, x, y, z));
    	client.setPosX(x);
    	client.setPosY(y);
    	client.setPosZ(z);
	}
	
	public static void mineBlock (Bot client, Position pos) {
		ClientPlayerActionPacket a = new ClientPlayerActionPacket(PlayerAction.START_DIGGING, pos, BlockFace.UP);
		client.getSession().send(a);
    }
	
	public static void placeBlock (Bot client, Position pos) {
		//idk
	}
	
}


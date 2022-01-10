package net.PRP.MCAI.utils;

import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import georegression.struct.point.Point3D_F64;
import georegression.struct.point.Vector3D_F64;
import net.PRP.MCAI.bot.Bot;
public class BotU {
	//private HashMap<ChunkCoordinates, Column> columns = new HashMap<>();
	public static void log(String f) {
    	System.out.println("[log] "+f);
    }
	public static void chat (Bot client, String text) {
		client.getSession().send(new ClientChatPacket(text));
	}
	public static void move (Bot client, double range, String ax) {
		try {
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
	
	public static void calibratePosition(Bot client) {
		double cx = (int) Math.floor(client.getPosX());
		cx += 0.5;
		double cz = (int) Math.floor(client.getPosZ());
		cz += 0.5;
		setposto(client, cx, client.getPosY(), cz);
	}
	
	public static void calibrateY(Bot client) {
		client.setPosY(Math.floor(client.getPosY()));
	}
	
	public static void walk (Bot client, int range, String ax) {
		calibratePosition(client);
		try {
			if (range > 0) {
				switch (ax) {
					case "x":
						Point3D_F64 position = new Point3D_F64(client.getPosX()+2,client.getPosY()-1,client.getPosZ());
						LookHead(client, position);
						for (int i = 0; i < range; i++) {
							for (int o = 0; o < 5; o++) {
								client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX() + 0.2, client.getPosY(), client.getPosZ()));
								client.setPosX(client.getPosX() + 0.2);
								ThreadU.sleep(25);
							}
						}
						break;
		            case "z":
		            	Point3D_F64 position1 = new Point3D_F64(client.getPosX(),client.getPosY()-1,client.getPosZ()+2);
						LookHead(client, position1);
		            	for (int i = 0; i < range; i++) {
							for (int o = 0; o < 5; o++) {
								client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX(), client.getPosY(), client.getPosZ() + 0.2));
								client.setPosZ(client.getPosZ() + 0.2);
								ThreadU.sleep(25);
							}
						}
						break;
		            default:
		            	log("корда "+ax+" неверная");
		            	break;
				}
			} else if (range < 0) {
				switch (ax) {
				case "x":
					Point3D_F64 position = new Point3D_F64(client.getPosX()-2,client.getPosY()-1,client.getPosZ());
					LookHead(client, position);
					for (int i = 0; i < Math.abs(range); i++) {
						for (int o = 0; o < 5; o++) {
							client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX() - 0.2, client.getPosY(), client.getPosZ()));
							client.setPosX(client.getPosX() - 0.2);
							ThreadU.sleep(50);
						}
					}
					break;
	            case "z":
	            	Point3D_F64 position1 = new Point3D_F64(client.getPosX(),client.getPosY()-1,client.getPosZ()-2);
					LookHead(client, position1);
	            	for (int i = 0; i < Math.abs(range); i++) {
						for (int o = 0; o < 5; o++) {
							client.getSession().send(new ClientPlayerPositionPacket(true, client.getPosX(), client.getPosY(), client.getPosZ() - 0.2));
							client.setPosZ(client.getPosZ() - 0.2);
							ThreadU.sleep(50);
						}
					}
					break;
	            default:
	            	log("корда "+ax+" неверная");
	            	break;
				}
			} else {
				log("kavo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void jump (Bot client, int range, String ax) {
		calibratePosition(client);
		try {
			double onetprange;
			if (range > 0) {
				onetprange = 0.5;
			} else if (range < 0) {
				range = Math.abs(range);
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
						//hz
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
					}
	            	break;
	            default:
	            	log("корда "+ax+" неверная");
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
			e.printStackTrace();
		}
	}
	public static void teleport(Bot client, double xa, double ya, double za) {
		double x = client.getPosX() + xa;
		double y = client.getPosY() + ya;
		double z = client.getPosZ() + za;
		client.getSession().send(new ClientPlayerPositionPacket(true, x, y, z));
    	client.setPosX(x);
    	client.setPosY(y);
    	client.setPosZ(z);
	}
	
	@SuppressWarnings("deprecation")
	public static void mineBlock(Bot client, Vector3D pos) {
		Point3D_F64 position = new Point3D_F64(pos.getX(),pos.getY(),pos.getZ());
		LookHead(client, position);
		ClientPlayerActionPacket a = new ClientPlayerActionPacket(PlayerAction.START_DIGGING, pos.translate(), BlockFace.UP);
		client.getSession().send(a);
		ThreadU.sleep(400);
		ClientPlayerActionPacket aa = new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), BlockFace.UP);
		client.getSession().send(aa);
    }
	
	public static void SetSlot(Bot client, int slot) {
        client.getSession().send(new ClientPlayerChangeHeldItemPacket(slot));
    }
	
	public static void LookHead(Bot client, Point3D_F64 position) {
		int y = (int) (position.getY() - 1);
		position.setY(y);
		Point3D_F64 PlayerPosition = new Point3D_F64(client.posX, client.posY, client.posZ);
        if (!position.equals(PlayerPosition)) {
            Vector3D_F64 vect = new Vector3D_F64(PlayerPosition, position);
            vect.normalize();
            double yaw = Math.toDegrees(Math.atan2(vect.z, vect.x)) - 90;
            double pitch = Math.toDegrees(Math.asin(-vect.y));
            client.getSession().send(new ClientPlayerRotationPacket(true, (float) yaw, (float) pitch));
        } else {log("hueta");}
    }
	
}


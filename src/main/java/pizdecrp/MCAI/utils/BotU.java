package pizdecrp.MCAI.utils;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;

import cz.GravelCZLP.AABB;
import cz.GravelCZLP.MathHelp;
import cz.GravelCZLP.Vector3D;
import georegression.struct.point.Point3D_F64;
import georegression.struct.point.Vector3D_F64;
import pizdecrp.MCAI.bot.Bot;
import pizdecrp.MCAI.bot.SessionListener;
public class BotU {
	//private HashMap<ChunkCoordinates, Column> columns = new HashMap<>();
	public static void log(String f) {
    	System.out.println("[log] "+f);
    }
	public static void chat (Bot client, String text) {
		client.getSession().send(new ClientChatPacket(text));
	}
	public void move (Bot client, double range, String ax) {
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
	
	public static void mineBlock (Bot client, Position pos, boolean inthread) {
		/*if (inthread) {
			new Thread(() -> {
				Point3D_F64 position = new Point3D_F64(pos.getX(),pos.getY(),pos.getZ());
				LookHead(client, position);
				//BlockFace bf = blockFaceCollide(client, MathHelp.vectorDirection(new EntityLocation(pos.getX(),pos.getY(),pos.getZ())), null);
				//if (bf == null) bf = BlockFace.UP;
				ClientPlayerActionPacket a = new ClientPlayerActionPacket(PlayerAction.START_DIGGING, pos, BlockFace.UP);
				client.getSession().send(a);
				ThreadU.sleep(400);
				ClientPlayerActionPacket aa = new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos, BlockFace.UP);
				client.getSession().send(aa);
				while (!SessionListener.blockIsEmpty(SessionListener.getblockid(pos))) {
	        		ThreadU.sleep(100);
	        	}
			}).start();
		} else {*/
		Point3D_F64 position = new Point3D_F64(pos.getX(),pos.getY(),pos.getZ());
		LookHead(client, position);
		//BlockFace bf = blockFaceCollide(client, MathHelp.vectorDirection(new EntityLocation(pos.getX(),pos.getY(),pos.getZ())), null);
		//if (bf == null) bf = BlockFace.UP;
		ClientPlayerActionPacket a = new ClientPlayerActionPacket(PlayerAction.START_DIGGING, pos, BlockFace.UP);
		client.getSession().send(a);
		ThreadU.sleep(400);
		ClientPlayerActionPacket aa = new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos, BlockFace.UP);
		client.getSession().send(aa);
		//}
    }
	
	public static void placeBlock (Hand h,Bot client, Position pos, boolean jumpup) {
		if (jumpup) {
			teleport(client, 0, 0.5, 0);
			ThreadU.sleep(100);
			teleport(client, 0, 0.5, 0);
			ThreadU.sleep(100);
			client.getSession().send(new ClientPlayerPlaceBlockPacket(pos, BlockFace.UP, h, 7, 7, 7));
		} else {
			client.getSession().send(new ClientPlayerPlaceBlockPacket(pos, BlockFace.UP, h, 7, 7, 7));
		}
	}
	
	public static void rightClickOnBlock(Bot client, Position pos) {
		LookHead(client, new Point3D_F64(pos.getX(),pos.getY(),pos.getZ()));
		client.getSession().send(new ClientPlayerPlaceBlockPacket(pos, BlockFace.DOWN, Hand.MAIN_HAND, 0, 0, 0));
	}
	
	public static void SetSlot(Bot client, int slot) {
        client.getSession().send(new ClientPlayerChangeHeldItemPacket(slot));
    }
	
	public static void LookHead(Bot client, Point3D_F64 position) {
		int y = (int) (position.getY() - 1);
		position.setY(y);
		Point3D_F64 PlayerPosition = client.PlayerPosition;
        if (!position.equals(PlayerPosition)) {
            Vector3D_F64 vect = new Vector3D_F64(PlayerPosition, position);
            vect.normalize();
            double yaw = Math.toDegrees(Math.atan2(vect.z, vect.x)) - 90;
            double pitch = Math.toDegrees(Math.asin(-vect.y));
            client.getSession().send(new ClientPlayerRotationPacket(true, (float) yaw, (float) pitch));
        } else {log("hueta");}
    }
	
	public BlockFace blockFaceCollide(Bot client, Vector3D vec, AABB aabb) {
		double cons = Double.MAX_VALUE;
		BlockFace bf = null;
		
		if (vec.y > 0) {
			double b = aabb.getMinY() - client.getPosY();
			double tc = b / vec.y;
			if (tc > 0 && tc < cons) {
				double xCollide = tc * vec.x + client.getPosX();
				double zCollide = tc * vec.z + client.getPosZ();
				if (MathHelp.between(xCollide, aabb.getMinX(), aabb.getMaxX(), 0) &&
						MathHelp.between(zCollide, aabb.getMinZ(), aabb.getMaxZ(), 0)) {
					cons = tc;
					bf = BlockFace.DOWN;
				}
			}
		} else {
			double b = aabb.getMaxY() - client.getPosY();
			double tc = b / vec.y;
			if (tc > 0 && tc < cons) {
				double xCollide = tc * vec.x + client.getPosX();
				double zCollide = tc * vec.z + client.getPosZ();
				if (MathHelp.between(xCollide, aabb.getMinX(), aabb.getMaxX(), 0) &&
						MathHelp.between(zCollide, aabb.getMinZ(), aabb.getMaxZ(), 0)) {
					cons = tc;
					bf = BlockFace.UP;
				}
			}
		}
		
		if (vec.x < 0) {
			double b = aabb.getMaxX() - client.getPosX();
			double tc = b / vec.x;
			if (tc > 0 && tc < cons) {
				double yCollide = tc * vec.y + client.getPosY();
				double zCollide = tc * vec.z + client.getPosZ();
				if (MathHelp.between(yCollide, aabb.getMinY(), aabb.getMaxY(), 0) &&
						MathHelp.between(zCollide, aabb.getMinZ(), aabb.getMaxZ(), 0)) {
					cons = tc;
					bf = BlockFace.EAST;
				}
			}
		} else {
			double b = aabb.getMinX() - client.getPosX();
			double tc = b / vec.x;
			if (tc > 0 && tc < cons) {
				double yCollide = tc * vec.y + client.getPosY();
				double zCollide = tc * vec.z + client.getPosZ();
				if (MathHelp.between(yCollide, aabb.getMinY(), aabb.getMaxY(), 0) &&
						MathHelp.between(zCollide, aabb.getMinZ(), aabb.getMaxZ(), 0)) {
					cons = tc;
					bf = BlockFace.WEST;
				}
			}
		}
		
		if (vec.z > 0) {
			double b = aabb.getMinZ() - client.getPosZ();
			double tc = b / vec.z;
			if (tc > 0 && tc < cons) {
				double xCollide = tc * vec.x + client.getPosX();
				double yCollide = tc * vec.y + client.getPosY();
				if (MathHelp.between(xCollide, aabb.getMinX(), aabb.getMaxX(), 0) &&
						MathHelp.between(yCollide, aabb.getMinY(), aabb.getMaxY(), 0)) {
					cons = tc;
					bf = BlockFace.NORTH;
				}
			}
		} else {
			double b = aabb.getMaxZ() - client.getPosZ();
			double tc = b / vec.z;
			if (tc > 0 && tc < cons) {
				double xCollide = tc * vec.x + client.getPosX();
				double yCollide = tc * vec.y + client.getPosY();
				if (MathHelp.between(xCollide, aabb.getMinX(), aabb.getMaxX(), 0) &&
						MathHelp.between(yCollide, aabb.getMinY(), aabb.getMaxY(), 0)) {
					cons = tc;
					bf = BlockFace.NORTH;
				}
			}
		}
		
		return bf;
	}
}


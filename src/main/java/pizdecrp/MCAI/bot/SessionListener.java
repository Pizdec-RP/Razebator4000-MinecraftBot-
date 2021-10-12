package pizdecrp.MCAI.bot;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;

import java.io.FileNotFoundException;
import java.util.HashMap;
import pizdecrp.MCAI.utils.*;
import world.ChunkCoordinates;

public class SessionListener extends SessionAdapter {
    private final Bot client;
    static int exline = -1;
    private static HashMap<ChunkCoordinates, Column> columns = new HashMap<>();
    private boolean movelocked = false;

    public SessionListener(Bot client) {
        this.client = client;
    }
        
    public boolean walkTo(Position pos) {
    	setmovelocked(true);
    	//---------------------x-------------------
    	double raznicax = pos.getX()-client.getPosX();
    	if (raznicax > 0) {
			for(double o = client.getPosX(); o < pos.getX() ;o++) {
				if ((int) Math.floor(client.getPosX()) == (int) Math.floor(pos.getX())) break;
				if (botCanWalkAt(client.getPosX()+1,client.getPosY(),client.getPosZ())) {
					if (blockIsEmpty(getblockid(client.getPosX()+1,client.getPosY()-1,client.getPosZ()))) {
						if (blockIsEmpty(getblockid(client.getPosX()+2,client.getPosY()-1,client.getPosZ()))) {
							if (blockIsEmpty(getblockid(client.getPosX()+3,client.getPosY()-1,client.getPosZ()))) {
								if (blockIsEmpty(getblockid(client.getPosX()+4,client.getPosY()-1,client.getPosZ()))) {
									if (blockIsEmpty(getblockid(client.getPosX()+5,client.getPosY()-1,client.getPosZ()))) {
										if (blockIsEmpty(getblockid(client.getPosX()+1,client.getPosY()-1,client.getPosZ())) && !blockIsEmpty(getblockid(client.getPosX()+1,client.getPosY()-2,client.getPosZ()))) {
											BotU.teleport(client, 1, -1, 0);
											ThreadU.sleep(250);
										} else {
											log("passed with client x:"+client.getPosX()+" y:"+client.getPosY()+" and tested pos x:"+client.getPosX()+5+" y:"+(int) (client.getPosY()-1)+"/");
											ThreadU.sleep(100);
										}
									} else {
										BotU.jump(client, 5, "x");
										o += 4;
										ThreadU.sleep(100);
									}
								} else {
									BotU.jump(client, 4, "x");
									o+=3;
									ThreadU.sleep(100);
								}
							} else {
								BotU.jump(client, 3, "x");
								o+=2;
								ThreadU.sleep(100);
							}
						} else {
							BotU.jump(client, 2, "x");
							o++;
							ThreadU.sleep(100);
						}
					} else {
						BotU.walk(client, 1, "x");
						ThreadU.sleep(100);
					}
				} else {
					if (botCanWalkAt(client.getPosX()+1,client.getPosY()+1,client.getPosZ())) {
						BotU.teleport(client, 1, 1, 0);
						ThreadU.sleep(250);
					} else {
						log("obozhe x");
					}
				}
			}
    	} else if (raznicax == 0) {
    		//pass
		} else {
			for(double o = client.getPosX(); o > pos.getX() ;o++) {
				if ((int) Math.floor(client.getPosX()) == (int) Math.floor(pos.getX())) break;
				if (botCanWalkAt(client.getPosX()-1,client.getPosY(),client.getPosZ())) {
					if (blockIsEmpty(getblockid(client.getPosX()-1,client.getPosY()-1,client.getPosZ()))) {
						if (blockIsEmpty(getblockid(client.getPosX()-2,client.getPosY()-1,client.getPosZ()))) {
							if (blockIsEmpty(getblockid(client.getPosX()-3,client.getPosY()-1,client.getPosZ()))) {
								if (blockIsEmpty(getblockid(client.getPosX()-4,client.getPosY()-1,client.getPosZ()))) {
									if (blockIsEmpty(getblockid(client.getPosX()-5,client.getPosY()-1,client.getPosZ()))) {
										if (blockIsEmpty(getblockid(client.getPosX()-1,client.getPosY()-1,client.getPosZ())) && !blockIsEmpty(getblockid(client.getPosX()-1,client.getPosY()-2,client.getPosZ()))) {
											BotU.teleport(client, -1, -1, 0);
											ThreadU.sleep(250);
										} else {
											log("passed with client x:"+client.getPosX()+" y:"+client.getPosY()+" and tested pos (broken(-10)x:"+client.getPosX()+5+" y:"+(int) (client.getPosY()-1)+"/");
											ThreadU.sleep(100);
										}
									} else {
										BotU.jump(client, -5, "x");
										o += 4;
										ThreadU.sleep(100);
									}
								} else {
									BotU.jump(client, -4, "x");
									o+=3;
									ThreadU.sleep(100);
								}
							} else {
								BotU.jump(client, -3, "x");
								o+=2;
								ThreadU.sleep(100);
							}
						} else {
							BotU.jump(client, -2, "x");
							o++;
							ThreadU.sleep(100);
						}
					} else {
						BotU.walk(client, -1, "x");
						ThreadU.sleep(100);
					}
				} else {
					if (botCanWalkAt(client.getPosX()-1,client.getPosY()+1,client.getPosZ())) {
						BotU.teleport(client, -1, 1, 0);
						ThreadU.sleep(250);
					} else {
						log("obozhe x");
					}
				}
			}
		}
    	
    	//---------------------z-------------------
    	
    	double raznicaz = pos.getZ() - client.getPosZ();
    	
    	if (raznicaz > 0) {
			for(double o = client.getPosZ(); o < pos.getZ() ;o++) {
				if ((int) Math.floor(client.getPosZ()) == (int) Math.floor(pos.getZ())) break;
				if (botCanWalkAt(client.getPosX(),client.getPosY(),client.getPosZ()+1)) {
					if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+1))) {
						if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+2))) {
							if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+3))) {
								if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+4))) {
									if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+5))) {
										if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+1)) && !blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-2,client.getPosZ()+1))) {
											BotU.teleport(client, 0, -1, 1);
											ThreadU.sleep(250);
										} else {
											log("passed with client z:"+client.getPosZ()+" y:"+client.getPosY()+" and tested pos z:"+client.getPosZ()+5+" y:"+(int) (client.getPosY()-1)+"/");
											ThreadU.sleep(100);
										}
									} else {
										BotU.jump(client, 5, "z");
										o += 4;
										ThreadU.sleep(100);
									}
								} else {
									BotU.jump(client, 4, "z");
									o+=3;
									ThreadU.sleep(100);
								}
							} else {
								BotU.jump(client, 3, "z");
								o+=2;
								ThreadU.sleep(100);
							}
						} else {
							BotU.jump(client, 2, "z");
							o++;
							ThreadU.sleep(100);
						}
					} else {
						BotU.walk(client, 1, "z");
						ThreadU.sleep(100);
					}
				} else {
					if (botCanWalkAt(client.getPosX(),client.getPosY()+1,client.getPosZ()+1)) {
						BotU.teleport(client, 0, 1, 1);
						ThreadU.sleep(250);
					} else {
						log("obozhe z");
					}
				}
			}
    	} else if (raznicaz == 0) {
    		//pass
		} else {
			log("else z event");
			for(double o = pos.getZ(); o < client.getPosZ() ;o--) {
				if ((int) Math.floor(client.getPosZ()) == (int) Math.floor(pos.getZ())) {log("pizdec"); break;}
				if (botCanWalkAt(client.getPosX(),client.getPosY(),client.getPosZ()-1)) {
					if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-1))) {
						if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-2))) {
							if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-3))) {
								if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-4))) {
									if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-5))) {
										if (blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-1)) && !blockIsEmpty(getblockid(client.getPosX(),client.getPosY()-2,client.getPosZ()-1))) {
											BotU.teleport(client, 0, -1, 1);
											ThreadU.sleep(250);
										} else {
											log("passed with client z:"+client.getPosZ()+" y:"+client.getPosY()+" and tested pos z:"+client.getPosZ()+5+" y:"+(int) (client.getPosY()-1)+"/");
											ThreadU.sleep(100);
										}
									} else {
										BotU.jump(client, -5, "z");
										o += 4;
										ThreadU.sleep(100);
									}
								} else {
									BotU.jump(client, -4, "z");
									o+=3;
									ThreadU.sleep(100);
								}
							} else {
								BotU.jump(client, -3, "z");
								o+=2;
								ThreadU.sleep(100);
							}
						} else {
							BotU.jump(client, -2, "z");
							o++;
							ThreadU.sleep(100);
						}
					} else {
						BotU.walk(client, -1, "z");
						ThreadU.sleep(100);
					}
				} else {
					if (botCanWalkAt(client.getPosX(),client.getPosY()+1,client.getPosZ()-1)) {
						BotU.teleport(client, 0, 1, -1);
						ThreadU.sleep(250);
					} else {
						log("obozhe z");
					}
				}
			}
		}
    	
    	//---------------------y-------------------
    	setmovelocked(false);
    	
    	if (client.getPosY() != pos.getY()) {
    		if (client.getPosY() > pos.getY()) {
    			for (int i = pos.getY(); i < (int)client.getPosY();i++) {
    				int yp = (int)client.getPosY()-1;
    				BotU.mineBlock(client, new Position((int)client.getPosX(),yp,(int)client.getPosZ()));
    			}
    		} else {
	    		if (blockIsEmpty(blockUpwBot())) {
	    			
	    		}
    		}
    	}
    	
    	if ((int)client.getPosX() != pos.getX()) {
    		/*walkTo(pos);
    		ThreadU.sleep(500);*/
    		return false;
    	} else if ((int)client.getPosY() != pos.getY()) {
    		/*walkTo(pos);
    		ThreadU.sleep(500);*/
    		return false;
    	} else if ((int)client.getPosZ() != pos.getZ()) {
    		//walkTo(pos);
    		//ThreadU.sleep(500);
    		return false;
    	} else {
    		return true;
    	}
	}
    
    
    public boolean botCanWalkAt(Position pos) {
    	return blockIsEmpty(getblockid(pos.getX(),pos.getY(),pos.getZ())) && blockIsEmpty(getblockid(pos.getX(),pos.getY()+1,pos.getZ()));
    }
    
    public boolean botCanWalkAt(double x, double y, double z) {
    	return botCanWalkAt(new Position((int)x,(int)y,(int)z));
    }
    
    public boolean blockIsEmpty(int bid) {
    	switch (bid) {
    		case 0://air
    			return true;
    		case 6://tree sp
    			return true;
    		case 31://grass
    			return true;
    		case 32://dead brush
    			return true;
    		case 37://flower
    			return true;
    		case 38://too
    			return true;
    		case 39://mushroom
    			return true;
    		case 55://redstone dust
    			return true;
    		case 68://sign
    			return true;
    		case 69://рычаг
    			return true;
    		case 70://плита
    			return true;
    		case 72://плита
    			return true;
    		case 75://torch
    			return true;
    		case 76:
    			return true;
    		case 77://button
    			return true;
    		case 78://snow
    			return true;
    		default:
    			return false;
    	}
    }
    
    public int getblockid (Position pos) {
    	try {
	    	BlockState block = getBlock(pos);
	    	if (block == null) log("cant get block id");
	    	int blockid = block.getId();
			return (int) blockid;
    	} catch (Exception e) {
    		e.printStackTrace();
			return 0;
		}
    }
    
    public int getblockid (double x,double y,double z) {
    	try {
	    	BlockState block = getBlock(new Position((int)x,(int)y,(int)z));
	    	if (block == null) log("cant get block id");
	    	int blockid = block.getId();
			return (int) blockid;
    	} catch (Exception e) {
    		e.printStackTrace();
			return 0;
		}
    }
    
    public int blockUnderBot() {
    	Position pos = new Position((int) Math.floor(client.getPosX()),(int)Math.floor(client.getPosY()) - 1,(int)Math.floor(client.getPosZ()));
    	int id = getblockid(pos);
    	return id;
    }
    
    public int blockUpwBot() {
    	Position pos = new Position((int) Math.floor(client.getPosX()),(int)Math.floor(client.getPosY()) + 2,(int)Math.floor(client.getPosZ()));
    	int id = getblockid(pos);
    	return id;
    }
    
    
    public Position findNearestBlockById(int id) {
    	int xs = (int)client.getPosX();
    	int ys = (int)client.getPosY();
    	int zs = (int)client.getPosZ();
    	int radius = 10;
    	Position pos = null;
    	for (int i = 1; i < radius; i++) {
    		log("ищу с r="+i);
    		for (int x = xs; x < i; x++) {
                for (int y = ys; y < i; y++) {
                    for (int z = zs; z < i; z++) {
                    	log("ищу на x:"+x+" y:"+y+" z:"+z);
                        if (getblockid(x,y,z) == id) {
                        	pos = new Position(x,y,z);
                        	log("чето нашел");
                        	break;
                        }
                    }
                }
            }
    	}
    	return pos;
    }
    
    @Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	System.out.println("(" + client.getGameProfile().getName() + ") Подрубился.");
        	ThreadU.sleep(1000);
        	Thread t = new Thread(() -> {
        		ThreadU.sleep(2000);
            	while (true) {
            		if (client.isOnline()) {
            			if (blockUnderBot() == 0 && !getmovelocked()) {
							BotU.teleport(client, 0, -1, 0);
							ThreadU.sleep(86);
            			} else if (!getmovelocked() && client.getPosY() > (int)client.getPosY()) {
            				int xx = (int)client.getPosX();
            				int zz = (int)client.getPosZ();
            				BotU.setposto(client, xx+0.5,(int)client.getPosY(),zz+0.5);
            			} else {
            				ThreadU.sleep(500);
            			}
            		} else {
            			ThreadU.sleep(1000);
            		}
            		
            	}
            });
        	t.start();
        }  else if (receiveEvent.getPacket() instanceof ServerChatPacket) {
        	Position pos = new Position((int)client.getPosX(),(int)client.getPosY()-2,(int)client.getPosZ());
        	walkTo(pos);
        	//Position pos = findNearestBlockById(2);
        	//log("find block at x:"+pos.getX()+" y:"+pos.getY()+" z:"+pos.getZ());
        	//Position posit = new Position(pos.getX()+1,pos.getY(),pos.getZ());
        	//if (walkTo(posit)) BotU.mineBlock(client, pos);
        	//BotU.mineBlock(client, new Position((int)client.getPosX(),((int)client.getPosY())-1,(int)client.getPosZ()));
            
        } else if (receiveEvent.getPacket() instanceof ServerPlayerPositionRotationPacket) {
            ServerPlayerPositionRotationPacket packet = receiveEvent.getPacket();
            client.setPosX(packet.getX());
            client.setPosY(packet.getY());
            client.setPosZ(packet.getZ());
            client.setYaw(packet.getYaw());
			client.setPitch(packet.getPitch());
            log("pos packet received x:"+packet.getX()+" y:"+packet.getY()+" z:"+packet.getZ()+" yaw:"+packet.getYaw()+" pitch:"+packet.getPitch());
            client.getSession().send(new ClientTeleportConfirmPacket(packet.getTeleportId()));
            client.getSession().send(new ClientPlayerPositionRotationPacket(
            		true, client.getPosX(), client.getPosY(),client.getPosZ(),client.getYaw(), client.getPitch()
            		));
            client.getSession().send(new ClientRequestPacket(ClientRequest.STATS));
        } else if (receiveEvent.getPacket() instanceof ServerPlayerHealthPacket) {
            if (((ServerPlayerHealthPacket) receiveEvent.getPacket()).getHealth() < 1) {
                client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                System.out.println("(" + client.getGameProfile().getName() + ") бот убит, возрождаю");
            }
        //server chunks
        } else if (receiveEvent.getPacket() instanceof ServerMultiBlockChangePacket) {
        	log("mbcp");
			ServerMultiBlockChangePacket packet = (ServerMultiBlockChangePacket) receiveEvent.getPacket();
			for (BlockChangeRecord data : packet.getRecords()) {
				setBlock(data.getPosition(), data.getBlock());
			}
        } else if (receiveEvent.getPacket() instanceof ServerUnloadChunkPacket) {
			ServerUnloadChunkPacket packet = (ServerUnloadChunkPacket) receiveEvent.getPacket();
			ChunkCoordinates coords = new ChunkCoordinates(packet.getX(), packet.getZ());
			unloadColumn(coords);
			 log("chunk unloaded");
		} else if (receiveEvent.getPacket() instanceof ServerChunkDataPacket) {
			ServerChunkDataPacket data = (ServerChunkDataPacket) receiveEvent.getPacket();
			int x = data.getColumn().getX();
			int z = data.getColumn().getZ();
			log("chunk loaded");
			ChunkCoordinates coords = new ChunkCoordinates(x, z);
			addChunkColumn(coords, data.getColumn());
		} else if (receiveEvent.getPacket() instanceof ServerBlockChangePacket) {
			log("bcp");
			ServerBlockChangePacket packet = (ServerBlockChangePacket) receiveEvent.getPacket();
			setBlock(packet.getRecord().getPosition(), packet.getRecord().getBlock());
		}
        
    }
    @Override
    public void disconnected(DisconnectedEvent event) {
		
    	try {
			client.connect();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static BlockState getBlock(Position pos) {
    	try {
    		int cx = (int)Math.floor(pos.getX()/16.0);
    		int cy = (int)Math.floor(pos.getY()/16.0);
    		int cz = (int)Math.floor(pos.getZ()/16.0);
    		//log("chunk pos x "+cx+" z "+cz+" y "+cy);
			ChunkCoordinates coords = new ChunkCoordinates(cx, cz);
			Column c = columns.get(coords);
			if (c == null) {
				return null;
			}
			int yPos = cy;
			BlockStorage blocks = c.getChunks()[yPos].getBlocks();
			int xb = pos.getX() % 16;
			int yb = pos.getY() % 16;
			int zb = pos.getZ() % 16;
			if (xb < 0) {
				xb = 16 - xb;
			}
			if (zb < 0) {
				zb = 16 - zb;
			}
			//log("x "+xb+" z "+zb+" y "+yb);
			return blocks.get(xb, yb, zb);
    	} catch (Exception e) {
    		e.printStackTrace();
			return null;
		}
	}
	public void setBlock(Position pos, BlockState state) {
		ChunkCoordinates coords = new ChunkCoordinates((int) (pos.getX() / 16.0), (int) (pos.getZ() / 16.0));
		int yPos = (int) Math.floor(pos.getY() / 16.0);
		if (!columns.containsKey(coords)) {
			return;
		}
		columns.get(coords).getChunks()[yPos].getBlocks().set(Math.abs(pos.getX() % 16), Math.abs(pos.getY() % 16), Math.abs(pos.getZ() % 16), state);
	}
	public void addChunkColumn(ChunkCoordinates coords, Column column) {
		columns.put(coords, column);
	}
	public void unloadColumn(ChunkCoordinates coords) {
		columns.remove(coords);
	}
	
	public static void log(String f) {
    	System.out.println("[log] "+f);
    }
	
	
	public void setmovelocked(boolean t) {
    	this.movelocked = t;
    }
    
    public boolean getmovelocked() {
    	return movelocked;
    }

    public static BlockState getBlock(EntityLocation loc) {
		return getBlock(new Position((int)loc.getX(), (int)loc.getY(), (int)loc.getZ()));
	}
}
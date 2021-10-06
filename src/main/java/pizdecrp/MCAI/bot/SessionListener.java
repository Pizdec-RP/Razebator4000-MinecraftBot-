package pizdecrp.MCAI.bot;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
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
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;

import java.io.FileNotFoundException;
import java.util.HashMap;
import pizdecrp.MCAI.utils.*;
import world.ChunkCoordinates;

public class SessionListener extends SessionAdapter {
    private final Bot client;
    static int exline = -1;
    private HashMap<ChunkCoordinates, Column> columns = new HashMap<>();
    private boolean movelocked = false;

    public SessionListener(Bot client) {
        this.client = client;
    }
        
    public void walkTo(Position pos) {
		BotU.teleport(client, pos);
    	/*new Thread(() -> {
	    	for (int o = (int)client.getPosX(); o < pos.getX(); o++) {
	    		int x = (int)client.getPosX();
	    		int y = (int)client.getPosY();
	    		int z = (int)client.getPosZ();
	    		if (!botCanWalkAt(new Position(x+1,y,z))) {
	    			
	    		} else if (blockIsEmpty(getblockid(x+1,y-1,z))) {
	    			BotU.walk(client, 1, "x");
	    			ThreadU.sleep(100);
	    			BotU.move(client, -1, "y");
	    		} else if (blockIsEmpty(getblockid(x+1,y,z)))  {
	    			BotU.walk(client, 1, "x");
	    		} else {
	    			BotU.jump(client, 1, "x");
	    		}
	    	}
    	}).start();*/
    	/*
		// ----------------x----------------
		double raznicaX = pos.getX() - client.getPosX();
		if (raznicaX > 0) {
			for (int i=(int)client.getPosX(); i <= pos.getX(); i++) {
				if (getblockid(new Position(pos.getX()+1,pos.getY(),pos.getZ())) != 0) {
					setmovelocked(true);
					BotU.jump(client, 1, "x");
					setmovelocked(false);
				} else if (getblockid(new Position(pos.getX()+1,pos.getY()-1,pos.getZ())) != 0) {
					setmovelocked(true);
					BotU.move(client, 1, "x");
					ThreadU.sleep(10);
					BotU.move(client, -1, "y");
				} else {
					BotU.move(client, 1, "x");
				}
				ThreadU.sleep(250);
			}
		} else if (raznicaX == 0) {
			//pass
		} else if (raznicaX < 0) {
			for (int i=Math.abs((int)client.getPosX()); i <= Math.abs(pos.getX()); i++) {
				if (getblockid(new Position(pos.getX()-1,pos.getY(),pos.getZ())) != 0) {
					setmovelocked(true);
					BotU.jump(client, -1, "x");
					setmovelocked(false);
				} else if (getblockid(new Position(pos.getX()-1,pos.getY()-1,pos.getZ())) != 0) {
					setmovelocked(true);
					BotU.move(client, -1, "x");
					ThreadU.sleep(10);
					BotU.move(client, -1, "y");
				} else {
					BotU.move(client, -1, "x");
				}
				ThreadU.sleep(250);
			}
		}
		
		//-----------------z------------------
		double raznicaZ = pos.getZ() - client.getPosZ();
		if (raznicaZ > 0) {
			for (int i=(int)client.getPosZ(); i <= pos.getZ(); i++) {
				if (getblockid(new Position(pos.getX(),pos.getY(),pos.getZ()+1)) != 0) {
					setmovelocked(true);
					BotU.jump(client, 1, "z");
					setmovelocked(false);
				} else if (getblockid(new Position(pos.getX(),pos.getY()-1,pos.getZ()+1)) != 0) {
					setmovelocked(true);
					BotU.move(client, 1, "z");
					ThreadU.sleep(10);
					BotU.move(client, -1, "y");
				} else {
					BotU.move(client, 1, "z");
				}
				ThreadU.sleep(250);
			}
		} else if (raznicaZ == 0) {
			//pass
		} else if (raznicaZ < 0) {
			for (int i=Math.abs((int)client.getPosZ()); i <= Math.abs(pos.getZ()); i++) {
				if (getblockid(new Position(pos.getX(),pos.getY(),pos.getZ()-1)) != 0) {
					setmovelocked(true);
					BotU.jump(client, -1, "z");
					setmovelocked(false);
				} else if (getblockid(new Position(pos.getX(),pos.getY()-1,pos.getZ()-1)) != 0) {
					setmovelocked(true);
					BotU.move(client, -1, "z");
					ThreadU.sleep(10);
					BotU.move(client, -1, "y");
				} else {
					BotU.move(client, -1, "z");
				}
				ThreadU.sleep(250);
			}
		}*/
	}
    
    public boolean botCanWalkAt(Position pos) {
    	return blockIsEmpty(getblockid(pos.getX(),pos.getY(),pos.getZ())) && blockIsEmpty(getblockid(pos.getX(),pos.getY()+1,pos.getZ()));
    }
    
    public void mineBlock (int blockid) {
    	
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
	    	if (block == null) log("СЕРВЕР ГОВНО, НЕ ПЕРЕДАЕТ ДАННЫЕ О ЧАНКАХ");
	    	int blockid = block.getId();
			return (int) blockid;
    	} catch (Exception e) {
    		e.printStackTrace();
			return 0;
		}
    }
    
    public int getblockid (int x,int y,int z) {
    	try {
	    	BlockState block = getBlock(new Position(x,y,z));
	    	if (block == null) log("СЕРВЕР ГОВНО, НЕ ПЕРЕДАЕТ ДАННЫЕ О ЧАНКАХ");
	    	int blockid = block.getId();
			return (int) blockid;
    	} catch (Exception e) {
    		e.printStackTrace();
			return 0;
		}
    }
    
    public int blockUnderBot() {
    	Position pos = new Position((int)client.getPosX(),(int)client.getPosY() - 1,(int)client.getPosZ());
    	int id = getblockid(pos);
    	return id;
    }
    
    @Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	System.out.println("(" + client.getGameProfile().getName() + ") Подрубился.");
        	ThreadU.sleep(1000);
        	new Thread(() -> {
        		ThreadU.sleep(2000);
            	while (true) {
            		if (client.isOnline()) {
            			if (blockUnderBot() == 0 && !getmovelocked()) {
							BotU.move(client, -0.25, "y");
							ThreadU.sleep(5);
							BotU.move(client, -0.25, "y");
							ThreadU.sleep(5);
							BotU.move(client, -0.25, "y");
							ThreadU.sleep(5);
							BotU.move(client, -0.25, "y");
							ThreadU.sleep(5);
            			} else if (!getmovelocked() && client.getPosY() > (int)client.getPosY()) {
            				BotU.teleport(client, new Position((int)(client.getPosX()+0.5),(int)client.getPosY(),(int)(client.getPosZ()+0.5)));
            				
            			} else {
            				ThreadU.sleep(500);
            			}
            		} else {
            			ThreadU.sleep(1000);
            		}
            		
            	}
            }).start();
        }  else if (receiveEvent.getPacket() instanceof ServerChatPacket) {
        	Position pos = new Position((int)client.getPosX()+10,(int)client.getPosY(),(int)client.getPosZ());
            walkTo(pos);
            
        } else if (receiveEvent.getPacket() instanceof ServerPlayerPositionRotationPacket) {
            ServerPlayerPositionRotationPacket packet = receiveEvent.getPacket();
            client.setPosX(packet.getX());
            client.setPosY(packet.getY());
            client.setPosZ(packet.getZ());
            log("pos packet received x:"+packet.getX()+" y:"+packet.getY()+" z:"+packet.getZ());
            client.getSession().send(new ClientTeleportConfirmPacket(packet.getTeleportId()));
            client.getSession().send(new ClientRequestPacket(ClientRequest.STATS));
        } else if (receiveEvent.getPacket() instanceof ServerPlayerHealthPacket) {
            if (((ServerPlayerHealthPacket) receiveEvent.getPacket()).getHealth() < 1) {
                client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
                System.out.println("(" + client.getGameProfile().getName() + ") бот убит, возрождаю");
            }
        //server chunks
        } else if (receiveEvent.getPacket() instanceof ServerMultiBlockChangePacket) {
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
    
    public BlockState getBlock(Position pos) {
    	try {
			ChunkCoordinates coords = new ChunkCoordinates((int) Math.floor(pos.getX() / 16.0), (int) Math.floor(pos.getZ() / 16.0));
			Column c = columns.get(coords);
			if (c == null) {
				return null;
			}
			int yPos = (int) Math.floor(pos.getY() / 16.0);
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
}
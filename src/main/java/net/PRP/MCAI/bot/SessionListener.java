package net.PRP.MCAI.bot;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.pathfinder.PathFindBuilder;
import net.PRP.MCAI.utils.*;
import net.PRP.MCAI.utils.EntityLocation;


import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import world.Block;
import world.BlockType;
import world.ChunkCoordinates;
import world.Entity;

public class SessionListener extends SessionAdapter {
    private final Bot client;
    static int exline = -1;
    Position actionentity;
    public List<Entity> entities = new CopyOnWriteArrayList<Entity>();
    boolean actioning;

    public SessionListener(Bot client) {
        this.client = client;
    }
    
    
    public boolean walkTo(Position pos, boolean retryifbnip) {
    	boolean r = PathFindBuilder.walkTo(client, pos, retryifbnip);
    	return r;
    }
    
    public void test(EntityLocation start, EntityLocation end) {
    	
    }
    
    
    public boolean botCanWalkAt(Position pos) {
    	return PositionUtils.blockIsEmpty(PositionUtils.getblockid(pos.getX(),pos.getY(),pos.getZ())) && PositionUtils.blockIsEmpty(PositionUtils.getblockid(pos.getX(),pos.getY()+1,pos.getZ()));
    }
    
    public boolean botCanWalkAt(double x, double y, double z) {
    	return botCanWalkAt(new Position((int)x,(int)y,(int)z));
    }
    
    
    
    
    
    public int blockUnderBot() {
    	Position pos = new Position((int) Math.floor(client.getPosX()),(int)Math.floor(client.getPosY()) - 1,(int)Math.floor(client.getPosZ()));
    	int id = PositionUtils.getblockid(pos);
    	return id;
    }
    
    public int blockUpwBot() {
    	Position pos = new Position((int) Math.floor(client.getPosX()),(int)Math.floor(client.getPosY()) + 2,(int)Math.floor(client.getPosZ()));
    	int id = PositionUtils.getblockid(pos);
    	return id;
    }
    
    public Hand findTrashBlockInInventory() {
		return null;
    }
    
    @Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	System.out.println("(" + client.getGameProfile().getName() + ") Подрубился.");
        	ThreadU.sleep(1000);
        	Thread physics = new Thread(() -> {
        		ThreadU.sleep(2000);
            	while (true) {
            		if (client.isOnline()) {
            			int xx = (int)client.getPosX();
            			int yy = (int)client.getPosY();
        				int zz = (int)client.getPosZ();
            			if (blockUnderBot() == 0 && !client.getmovelocked()) {
							BotU.setposto(client, xx+0.5, yy-1, zz+0.5);
							ThreadU.sleep(86);
							log("cal1");
            			} else if (!client.getmovelocked() && client.getPosY() > (int)client.getPosY()) {
            				BotU.setposto(client, xx+0.5,(int)client.getPosY(),zz+0.5);
            				log("cal2");
            			} else {
            				ThreadU.sleep(500);
            			}
            		} else {
            			ThreadU.sleep(1000);
            		}
            		
            	}
            });
        	BotU.chat(client, "/register 112233asd 112233asd");
        	BotU.chat(client, "/login 112233asd");
        	physics.start();
        /*}  else if (receiveEvent.getPacket() instanceof ServerChatPacket) {
        	String message;
        	if(((ServerChatPacket) receiveEvent.getPacket()).getMessage() instanceof TranslationMessage){
        		TranslationMessage tm = (TranslationMessage) ((ServerChatPacket) receiveEvent.getPacket()).getMessage();
        		String mess = "";
        		for(Message m : tm.getTranslationParams()){
        			mess = mess + " " + m.getFullText();
        		}
        		message = mess;
        	} else {
        		message = (((ServerChatPacket) receiveEvent.getPacket()).getMessage().getFullText());
        	}
        	if (message.split(" ").length > 2) {
	            switch (message.split(" ")[2]) {
	            	case "stop":
	            		break;
	            	case "check":
	            		Position pos = PositionUtils.findNearestBlockById(client, 4);
	                	if (pos == null) {
	                		BotU.chat(client, "я нихуя не нашел");
	                	} else {
	        	        	BotU.chat(client, "finded block at x:"+pos.getX()+" y:"+pos.getY()+" z:"+pos.getZ());
	                	}
	                	break;
	            	case "minewood":
	            		if (message.split(" ")[3] == null) {
	            			Actions.mineWood(client, 1);
	            		} else {
	            			Actions.mineWood(client, Integer.parseInt(message.split(" ")[3]));
	            		}
	                	break;
	            	case "mine2":
	            		if (message.split(" ")[4] == null) {
	            			Actions.mine2D(client, Integer.parseInt(message.split(" ")[3]), 1);
	            		} else {
	            			Actions.mine2D(client, Integer.parseInt(message.split(" ")[3]), Integer.parseInt(message.split(" ")[4]));;
	            		}
	            		break;
	            	case "mine3":
	            		if (message.split(" ")[4] == null) {
	            			Actions.mine3D(client, Integer.parseInt(message.split(" ")[3]), 1);
	            		} else {
	            			Actions.mine3D(client, Integer.parseInt(message.split(" ")[3]), Integer.parseInt(message.split(" ")[4]));;
	            		}
	            		break;
	            	case "cslot":
	            		BotU.SetSlot(client, Integer.parseInt(message.split(" ")[3]));
	            		break;
	            	case "faceto":
	            		BotU.LookHead(client, new Point3D_F64(Integer.parseInt(message.split(" ")[3]),Integer.parseInt(message.split(" ")[4]),Integer.parseInt(message.split(" ")[5])));
	            		break;
	            	case "goto":
	            		Thread t1 = new Thread(() -> {
	            			walkTo(new Position(Integer.parseInt(message.split(" ")[3]),Integer.parseInt(message.split(" ")[4]),Integer.parseInt(message.split(" ")[5])),false);
	            		});
	            		t1.start();
	            		break;
	            	case "jump":
	            		BotU.placeBlock(Hand.MAIN_HAND, client, new Position((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()), true);
	            		break;
	            	case "gimmeidvec":
	            		Vector3D pos1488 = new Vector3D(Integer.parseInt(message.split(" ")[3]),Integer.parseInt(message.split(" ")[4]),Integer.parseInt(message.split(" ")[5]));
	            		int a = Main.getWorld().getBlock(pos1488).id;
	            		BotU.chat(client, "id is "+a);
	            		break;
	            	case "gimmeidpos":
	            		Position pos228 = new Position(Integer.parseInt(message.split(" ")[3]),Integer.parseInt(message.split(" ")[4]),Integer.parseInt(message.split(" ")[5]));
	            		int a1 = Main.getWorld().getBlock(pos228).getId();
	            		BotU.chat(client, "id is "+a1);
	            		break;
	            	case "ent":
	            		for (Entity en : Main.world.entities) {
	            			BotU.chat(client, en.toString());
	            		}
	            		break;
	            	case "mypos":
	            		break;
	            	case "test":
	            		new Thread(() -> {
	            			client.setmovelocked(true);
	            			Vector3D start = new Vector3D(client.getPosX(), client.getPosY(),client.getPosZ());
	            			Vector3D end = new Vector3D(Integer.parseInt(message.split(" ")[3]),Integer.parseInt(message.split(" ")[4]),Integer.parseInt(message.split(" ")[5]));
	            			AStar pf = new AStar(client, start, end);
	            			pf.startCalc3D(client);
	            			new Thread(() -> {
	            				while (true) {
		            				if (VectorUtils.equalsInt(end, client.getPosition())) {
			            				System.out.println("pzdc");
			            				client.setmovelocked(false);
			            				break;
		            				} else {
		            					System.out.println("nu");
		            					ThreadU.sleep(1000);
		            				}
	            				}
	            			}).start();
	            		}).start();
	            		break;
	            	case "test2":
	            		new Thread(() -> {
	            			client.setmovelocked(true);
	            			Vector3D start = new Vector3D(client.getPosX(), client.getPosY(),client.getPosZ());
	            			Vector3D end = new Vector3D(Integer.parseInt(message.split(" ")[3]),Integer.parseInt(message.split(" ")[4]),Integer.parseInt(message.split(" ")[5]));
	            			AStar pf = new AStar(client, start, end);
	            			pf.startCalc2D(client);
	            			new Thread(() -> {
	            				while (true) {
		            				if (VectorUtils.equalsInt(end, client.getPosition())) {
			            				System.out.println("pzdc");
			            				client.setmovelocked(false);
			            				break;
		            				} else {
		            					System.out.println("nu");
		            					ThreadU.sleep(1000);
		            				}
	            				}
	            			}).start();
	            		}).start();
	            		break;
	            }
        	} else {}
        	//Position pos = new Position((int)client.getPosX(),(int)client.getPosY()-2,(int)client.getPosZ());
        	//walkTo(pos);
        	
        	/*Position pos = PositionUtils.findNearestBlockById(4);
        	if (pos == null) {
        		log("я нихуя не нашел");
        	} else {
	        	log("finded block at x:"+pos.getX()+" y:"+pos.getY()+" z:"+pos.getZ());
	        	Position posit = new Position(pos.getX()+1,pos.getY(),pos.getZ());
	        	if (walkTo(posit)) BotU.mineBlock(client, pos);
        	}*/
        	//BotU.mineBlock(client, new Position((int)client.getPosX(),((int)client.getPosY())-1,(int)client.getPosZ()));
            
        /*} else if (receiveEvent.getPacket() instanceof ServerKeepAlivePacket) {
            ServerKeepAlivePacket p = receiveEvent.getPacket();
            receiveEvent.getSession().send(new ClientKeepAlivePacket(p.getPingId()));
            log("pinged");*/
        } else if (receiveEvent.getPacket() instanceof ServerPlayerPositionRotationPacket) {
            ServerPlayerPositionRotationPacket packet = receiveEvent.getPacket();
            client.setPosX(packet.getX());
            client.setPosY(packet.getY());
            client.setPosZ(packet.getZ());
            client.setYaw(packet.getYaw());
			client.setPitch(packet.getPitch());
            //log("pos packet received x:"+packet.getX()+" y:"+packet.getY()+" z:"+packet.getZ()+" yaw:"+packet.getYaw()+" pitch:"+packet.getPitch());
            client.getSession().send(new ClientTeleportConfirmPacket(packet.getTeleportId()));
            client.getSession().send(new ClientPlayerPositionRotationPacket(
            		true, client.getPosX(), client.getPosY(),client.getPosZ(),client.getYaw(), client.getPitch()
            		));
            client.getSession().send(new ClientRequestPacket(ClientRequest.STATS));
        } else if (receiveEvent.getPacket() instanceof ServerPlayerHealthPacket) {
            final ServerPlayerHealthPacket p = receiveEvent.getPacket();
            if (p.getHealth() <= 0)
            	client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
        
        //server chunks
        
        } else if (receiveEvent.getPacket() instanceof ServerMultiBlockChangePacket) {
        	//log("mbcp");
			ServerMultiBlockChangePacket packet = (ServerMultiBlockChangePacket) receiveEvent.getPacket();
			for (BlockChangeRecord data : packet.getRecords()) {
				Main.getWorld().setBlock(data.getPosition(), data.getBlock());
				Block block = new Block();
				block.id = data.getBlock().getId();
				block.subid = data.getBlock().getData();
				block.pos = VectorUtils.convert(data.getPosition());
				block.type = BlockType.bt(data.getBlock().getId());
				Main.getWorld().addBlock(block);
			}
        } else if (receiveEvent.getPacket() instanceof ServerUnloadChunkPacket) {
			//ServerUnloadChunkPacket packet = (ServerUnloadChunkPacket) receiveEvent.getPacket();
			//ChunkCoordinates coords = new ChunkCoordinates(packet.getX(), packet.getZ());
			//Main.getWorld().unloadColumn(coords);
			//log("chunk unloaded");
		} else if (receiveEvent.getPacket() instanceof ServerChunkDataPacket) {
			ServerChunkDataPacket data = (ServerChunkDataPacket) receiveEvent.getPacket();
			int x = data.getColumn().getX();
			int z = data.getColumn().getZ();
			ChunkCoordinates coords = new ChunkCoordinates(x, z);
			Main.getWorld().addChunkColumn(coords, data.getColumn());
			//Main.getWorld().columnToBlocks(x, z, data.getColumn());
		} else if (receiveEvent.getPacket() instanceof ServerBlockChangePacket) {
			ServerBlockChangePacket packet = (ServerBlockChangePacket) receiveEvent.getPacket();
			Main.getWorld().setBlock(packet.getRecord().getPosition(), packet.getRecord().getBlock());
			//log("bcp "+packet.getRecord().getPosition().toString());
			Block block = new Block();
			block.id = packet.getRecord().getBlock().getId();
			block.subid = packet.getRecord().getBlock().getData();
			block.pos = VectorUtils.convert(packet.getRecord().getPosition());
			block.type = BlockType.bt(packet.getRecord().getBlock().getId());
			Main.getWorld().addBlock(block);
		} else if (receiveEvent.getPacket() instanceof LoginSuccessPacket) {
            final LoginSuccessPacket p = receiveEvent.getPacket();
            UUID MyUUID = p.getProfile().getId();
            client.setUUID(MyUUID);
            System.out.println("UUID: " + MyUUID);
		}
		//inventory packets
		/*} else if (receiveEvent.getPacket() instanceof ServerWindowItemsPacket) {
			log("swip");
			ServerWindowItemsPacket packet = (ServerWindowItemsPacket) receiveEvent.getPacket();
			if (packet.getWindowId() == 0) {
				if (client.getPlayerInventory() == null) {
					client.setPlayerInventory(new PlayerInventory());
				}
				client.getPlayerInventory().deconstuctItemArrayToIvn(packet.getItems(), client);
			} else if (packet.getWindowId() == 1) {
			}
		} else if (receiveEvent.getPacket() instanceof ServerSetSlotPacket) {
			log("sssp");
			ServerSetSlotPacket packet = (ServerSetSlotPacket) receiveEvent.getPacket();
			if (packet.getWindowId() == 0) {
				try {
					client.getPlayerInventory().updateSlot(packet.getSlot(), packet.getItem());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (packet.getWindowId() == -1) {
				client.getOpenedInventory().updateSlot(packet.getSlot(), packet.getItem());
			}
		} else if (receiveEvent.getPacket() instanceof ServerOpenWindowPacket) {
			log("sowp");
			ServerOpenWindowPacket packet = (ServerOpenWindowPacket) receiveEvent.getPacket();
			IInventory inv = null;
			switch (packet.getType()) {
			case CHEST:
				inv = new ChestInventory();
				break;
			case CRAFTING_TABLE:
				inv = new WorkBenchInventory();
				break;
			case FURNACE:
				break;
			case GENERIC_INVENTORY:
				inv = new PlayerInventory();
				break;
			default:
				log("чо за инвентарь??? я бомж и не шарю");
				break;
			}
			client.setOpendedInventory(inv);
			client.setCurrentWindowId(packet.getWindowId());
		} else if (receiveEvent.getPacket() instanceof ServerWindowPropertyPacket) {
			log("swpp");
			//ServerWindowPropertyPacket packet = (ServerWindowPropertyPacket) receiveEvent.getPacket();
			
		} else if (receiveEvent.getPacket() instanceof ServerConfirmTransactionPacket) {
			log("sctp");
            final ServerConfirmTransactionPacket p = receiveEvent.getPacket();
            client.getSession().send(new ClientConfirmTransactionPacket(p.getWindowId(), p.getActionId(), true));
		} else if (receiveEvent.getPacket() instanceof ServerSpawnPlayerPacket) {
            final ServerSpawnPlayerPacket p = receiveEvent.getPacket();
            Entity entity = new Entity();
            entity.x = p.getX();
            entity.y = p.getY();
            entity.z = p.getZ();
            entities.add(entity);
		
		}*/
        
    }
    
    public Entity getEntById(int id) {
    	for (Entity ent : entities) {
    		if (ent.id == id) {
    			return ent;
    		}
    	}
    	return null;
    }
    
    @Override
    public void disconnected(DisconnectedEvent event) {
		
    	try {
    		log("bot disconected "+event.getReason());
    		event.getCause().printStackTrace();
			client.connect();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public static void log(String f) {
    	System.out.println("[log] "+f);
    }
}
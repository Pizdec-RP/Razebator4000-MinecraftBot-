package net.PRP.MCAI.TestServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.entity.EquipmentSlot;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Equipment;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Pose;
import com.github.steveice10.mc.protocol.data.game.entity.object.FallingBlockData;
import com.github.steveice10.mc.protocol.data.game.entity.object.GenericObjectData;
import com.github.steveice10.mc.protocol.data.game.entity.player.Animation;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerState;
import com.github.steveice10.mc.protocol.data.game.entity.player.PositionElement;
import com.github.steveice10.mc.protocol.data.game.recipe.Recipe;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.particle.BlockParticleData;
import com.github.steveice10.mc.protocol.data.game.world.particle.Particle;
import com.github.steveice10.mc.protocol.data.game.world.particle.ParticleType;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDeclareCommandsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDeclareRecipesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDifficultyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerUnlockRecipesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAnimationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityCollectItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnParticlePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateViewPositionPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.entity.BlockEntity;
import net.PRP.MCAI.TestServer.entity.DefaultEntity;
import net.PRP.MCAI.TestServer.entity.EntityItem;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.ChunkCoordinates;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.VectorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ClientSession extends SessionAdapter {
	public GameProfile profile;
	public Session session;
	public int entityId;
	public GameMode gm;
	public int tpid = 0;
	public Vector3D pos;
	public Vector3D beforePos;
	public Vector3D vel = new Vector3D(0,0,0);
	public float pitch, yaw;
	public int health;
	public int food;
	private Map<Integer, ItemStack> inventory;
	private st state = st.not;
	private int sleepticks = 0;
	public int slot = 0;
	public boolean onGround = true;
	public boolean op = false;
	public boolean banned = false;
	public Server Server;
	
	//internal plugin's shit---------
	private int blocksBySecond = 0;
	private Map<Vector3D, Integer> backup = new ConcurrentHashMap<>();
	public DefaultEntity capturedBlock = null;
	private int csleep = 0;
	//-------------------------------
	
	private enum st {
		not, lsp, succ
	}
	
	public ClientSession(Server s) {
		this.Server = s;
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		try {
			if (banned) return;
			//BotU.log(event.getPacket().getClass().getName());
			if (event.getPacket() instanceof LoginStartPacket) {
				if (profile == null) {
					LoginStartPacket p = event.getPacket();
					profile = new GameProfile(UUID.randomUUID(), p.getUsername());
					BotU.log(profile.getName());
				}
				double[] ppos = Server.getpos(profile.getName());
				pos = new Vector3D(ppos[0],ppos[1],ppos[2]);
				beforePos = new Vector3D(ppos[0],ppos[1],ppos[2]);
				pitch=(float)ppos[3];
				yaw=(float)ppos[4];
				health = Server.getPlayerObject(profile.getName()).get("health").getAsInt();
				food = Server.getPlayerObject(profile.getName()).get("food").getAsInt();
				op = Server.getPlayerObject(profile.getName()).get("op").getAsBoolean();
				Server.newEntityPlayer(this);
				Server.players.add(this);
				state = st.lsp;
				
				sleepticks = 40;
			} else if (event.getPacket() instanceof ClientPluginMessagePacket) {
				ClientPluginMessagePacket p = (ClientPluginMessagePacket)event.getPacket();
				event.getSession().send(new ServerPluginMessagePacket(p.getChannel(), p.getData()));
			} else if (event.getPacket() instanceof ClientPlayerPositionRotationPacket) {
	        	ClientPlayerPositionRotationPacket p = (ClientPlayerPositionRotationPacket) event.getPacket();
	        	Vector3D vec3 = new Vector3D(p.getX(),p.getY(),p.getZ());
	        	vel = vec3.subtract(beforePos);
	        	Server.handleEntityMove(vec3,this);
	        	this.yaw = p.getYaw();
	        	this.pitch = p.getPitch();
	        	this.onGround = p.isOnGround();
	        	
	        } else if (event.getPacket() instanceof ClientPlayerRotationPacket) {
	        	ClientPlayerRotationPacket p = (ClientPlayerRotationPacket) event.getPacket();
	        	this.yaw = p.getYaw();
	        	this.pitch = p.getPitch();
	        	this.onGround = p.isOnGround();
	        	
	        } else if (event.getPacket() instanceof ClientPlayerPositionPacket) {
	        	ClientPlayerPositionPacket p = (ClientPlayerPositionPacket) event.getPacket();
	        	Vector3D vec3 = new Vector3D(p.getX(),p.getY(),p.getZ());
	        	vel = vec3.subtract(beforePos);
	        	Server.handleEntityMove(vec3,this);
	        	this.onGround = p.isOnGround();
	        	
	        } else if (event.getPacket() instanceof ClientChatPacket) {
	        	ClientChatPacket packet = event.getPacket();
	        	BotU.log(profile.getName()+" say: "+packet.getMessage());
	        	if (packet.getMessage().startsWith("/")) {
	        		Server.commandUsed(this, packet.getMessage());
	        	} else {
		        	ServerChatPacket p = new ServerChatPacket(Component.text("<"+profile.getName()+"> "+packet.getMessage()).color(NamedTextColor.GREEN));
		        	
		        	for (ClientSession player : Server.players) {
		        		player.session.send(p);
		        	}
	        	}
	        } else if (event.getPacket() instanceof ClientRequestPacket) {
	        	ClientRequestPacket p = event.getPacket();
	        	if (p.getRequest()==ClientRequest.RESPAWN) {
	        		this.health = 20;
	        		this.food = 20;
	        		this.pos = Server.spawnpoint.clone();
	        		session.send(new ServerPlayerHealthPacket(health,food,0));
	        		session.send(new ServerPlayerPositionRotationPacket(pos.x,pos.y,pos.z,yaw,pitch,tpid++,new ArrayList<PositionElement>()));
	        	}
	        } else if (event.getPacket() instanceof ClientPlayerActionPacket) {
	        	ClientPlayerActionPacket p = event.getPacket();
	        	Vector3D temppos = new Vector3D(p.getPosition());
	        	if (p.getAction() == PlayerAction.START_DIGGING) {
	        		this.blocksBySecond++;
	        		if (!backup.containsKey(temppos) && Multiworld.getBlock(temppos).id == 0) backup.put(temppos,Multiworld.getState(p.getPosition().getX(),p.getPosition().getY(),p.getPosition().getZ()));
	        		if (gm == GameMode.CREATIVE) {
	        			Server.setBlock(new Vector3D(p.getPosition()), 0);
	        		}
	        	} else if (p.getAction() == PlayerAction.FINISH_DIGGING) {
	        		Server.setBlock(new Vector3D(p.getPosition()), 0);
	        	} else if (p.getAction() == PlayerAction.DROP_ITEM_STACK) {
	        		if (getiteminhand().getId() == 0) return;
	        		tossItem(getiteminhand());
	        		this.addToInv(slot, new ItemStack(0,0));
	        	} else if (p.getAction() == PlayerAction.DROP_ITEM) {
	        		if (getiteminhand().getId() == 0) return;
	        		if (getiteminhand().getAmount() > 1) {
		        		tossItem(new ItemStack(getiteminhand().getId(), 1, getiteminhand().getNbt()));
		        		this.addToInv(slot, new ItemStack(getiteminhand().getId(),getiteminhand().getAmount()-1,getiteminhand().getNbt()));
	        		} else {
	        			tossItem(getiteminhand());
	            		this.addToInv(slot, new ItemStack(0,0));
	        		}
	        	} else if (p.getAction() == PlayerAction.RELEASE_USE_ITEM) {
	        		if (getiteminhand() != null && getiteminhand().getId() == 574) {
	        			//Server.spawnTickable(new Arrow(pos.add(0, 1.75, 0), VectorUtils.getDirection(yaw,pitch).multiply(0.3)));
	        		}
	        	}
	        } else if (event.getPacket() instanceof ClientPlayerPlaceBlockPacket) {
	        	ClientPlayerPlaceBlockPacket p = event.getPacket();
	        	BotU.log("pbe");
	        	if (p.getHand() == Hand.MAIN_HAND) {
	        		Server.placeBlockEvent(new Vector3D(p.getPosition()), p.getFace(), getiteminhand().getId(),this);
	        	}/* else {
	        		Server.setBlock(posss, beforeBlock.state);
	        	}*/
	        } else if (event.getPacket() instanceof ClientPlayerChangeHeldItemPacket) {
	        	ClientPlayerChangeHeldItemPacket p = event.getPacket();
	        	this.slot = p.getSlot();
	        	Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.MAIN_HAND, getiteminhand())}));
	        } else if (event.getPacket() instanceof ClientCreativeInventoryActionPacket) {
	        	ClientCreativeInventoryActionPacket p = event.getPacket();
	        	BotU.log("cslot: "+p.getSlot());
	        	if (p.getSlot() < 0) {
	        		tossItem(p.getClickedItem());
	        	} else if (p.getClickedItem() == null) {
	        		addToInv(p.getSlot(), new ItemStack(0,0));
	        	} else {
	        		addToInv(p.getSlot(), p.getClickedItem());
	        	}
	        } else if (event.getPacket() instanceof ClientPlayerSwingArmPacket) {
	        	Server.sendForEver(new ServerEntityAnimationPacket(entityId,Animation.SWING_ARM));
	        	Hit(this.getiteminhand().getId());
	        } else if (event.getPacket() instanceof ClientPlayerStatePacket) {
	        	ClientPlayerStatePacket p = event.getPacket();
	        	//if (p.getEntityId() != this.entityId) return;
	        	if (p.getState() == PlayerState.START_SNEAKING) {
	        		Server.sendForEver(new ServerEntityMetadataPacket(entityId,
	    				new EntityMetadata[] {
	    					new EntityMetadata(0, MetadataType.BYTE, (byte)2),
							new EntityMetadata(6, MetadataType.POSE, Pose.SNEAKING)
	    				}
	        		));
	        	} else if (p.getState() == PlayerState.STOP_SNEAKING) {
	        		Server.sendForEver(new ServerEntityMetadataPacket(entityId,
	    				new EntityMetadata[] {
	    					new EntityMetadata(0, MetadataType.BYTE, (byte)0),
							new EntityMetadata(6, MetadataType.POSE, Pose.STANDING)
	    				}
	        		));
	        	} else if (p.getState() == PlayerState.START_ELYTRA_FLYING) {
	        		Server.sendForEver(new ServerEntityMetadataPacket(entityId,
	    				new EntityMetadata[] {
	    					new EntityMetadata(0, MetadataType.BYTE, (byte)-128),
							new EntityMetadata(6, MetadataType.POSE, Pose.FALL_FLYING)
	    				}
	        		));
	        	} 
	        } else if (event.getPacket() instanceof ClientPlayerInteractEntityPacket) {
	        	ClientPlayerInteractEntityPacket p = event.getPacket();
	        	
	        	if (p.getAction() == InteractAction.ATTACK) {
	        		//Multiworld.Entities.get(p.getEntityId())
	        	}
	        } else if (event.getPacket() instanceof ClientPlayerUseItemPacket) {
	        	ClientPlayerUseItemPacket p = event.getPacket();
	        	if (p.getHand() == Hand.MAIN_HAND) {
	        		if (getiteminhand() != null) {
	        			int iid = getiteminhand().getId();
	        			Click(iid);
	        	
	        		}
	        	}
	        } else if (event.getPacket() instanceof ClientPlayerUseItemPacket) {
	        	
	        }
		} catch (Exception e) {
			e.printStackTrace();
			this.disconnect(Component.text("серверная ошибка. Такие дела, соси хуй быдло и перезаходи давай"));
		}
	}
	
	public void Hit(int iid) {
		if (csleep > 0) return;
		csleep = 10;
		if (iid == 613) {
			BlockEntity tb = new BlockEntity(Server.nextEID(), pos.clone(), MathU.rnd(1, 17111),Server);
			tb.setPos(pos.add(0,1.5F,0));
			Vector3D dir = VectorUtils.getDirection(yaw,pitch);
		    
		    tb.setVel(dir);
			Server.spawnTickableFallingBlock(tb);
			
		}
	}
	
	public void Click(int iid) {
		if (csleep > 0) return;
		csleep = 10;
		if (iid == 613) {//stick
			
		    if (this.capturedBlock == null) {
				List<Vector3D> points = createRay(pos.add(0,1.75,0), yaw, pitch, 9, 0.5);
				for (Vector3D pn : points) {
					Server.sendForEver(new ServerSpawnParticlePacket(new Particle(ParticleType.END_ROD,new BlockParticleData(1)),true,pn.x,pn.y,pn.z,0f,0f,0f,0f,1));
					Block b = Multiworld.getBlock(pn);
					if (!b.isAvoid()) {
						Multiworld.setBlock(pn.clone().floor().translate(), 0);
						BlockEntity tb = new BlockEntity(Server.nextEID(), pn.clone(), b.state,Server);
						
						this.capturedBlock = tb;
						((BlockEntity)this.capturedBlock).captured = true;
						Server.spawnTickableFallingBlock(this.capturedBlock);
						return;
					}
				}
		    } else {
		    	Vector3D dir = VectorUtils.getDirection(yaw, pitch);
		    	this.capturedBlock.setVel(dir);
		    	
		    	((BlockEntity)this.capturedBlock).captured = false;
		    	this.capturedBlock = null;
		    }
		} else if (iid == 578) {//diamond
			List<Vector3D> points = new ArrayList<>();
			for (int i = -179; i < 179;i++) {
				if (i%20==0) {
					points.addAll(createRay(pos, i, pitch, 8, 0.8));
				}
			}
			for (Vector3D pn : points) {
				Server.sendForEver(new ServerSpawnParticlePacket(new Particle(ParticleType.END_ROD,new BlockParticleData(1)),true,pn.x,pn.y+2.1,pn.z,0f,0f,0f,0f,1));
			}
		} else if (iid == 745) {//blaze rod
			List<Vector3D> points = createRay(pos.add(0,1.75,0), yaw, pitch, 15, 1);
			for (Vector3D pn : points) {
				Server.sendForEver(new ServerSpawnParticlePacket(new Particle(ParticleType.FLAME,new BlockParticleData(1)),true,pn.x,pn.y,pn.z,0f,0f,0f,0f,1));
				Block b = Multiworld.getBlock(pn);
				if (!b.isAvoid()) {
					Multiworld.setBlock(pn.clone().floor().translate(), 0);
					BlockEntity tb1 = new BlockEntity(Server.nextEID(), pn.clone(), b.state, new Vector3D(0.5,0.5,0.5),Server);
					BlockEntity tb2 = new BlockEntity(Server.nextEID(), pn.clone(), b.state, new Vector3D(-0.5,0.5,-0.5),Server);
					BlockEntity tb3 = new BlockEntity(Server.nextEID(), pn.clone(), b.state, new Vector3D(-0.5,0.5,0.5),Server);
					BlockEntity tb4 = new BlockEntity(Server.nextEID(), pn.clone(), b.state, new Vector3D(0.5,0.5,-0.5),Server);
					
					Server.spawnTickableFallingBlock(tb1);
					Server.spawnTickableFallingBlock(tb2);
					Server.spawnTickableFallingBlock(tb3);
					Server.spawnTickableFallingBlock(tb4);
					return;
				}
			}
		}
	}
	
	public List<Vector3D> createRay(Vector3D from, float yaw, float pitch, int dist, double amp) {
		List<Vector3D> blocks = new CopyOnWriteArrayList<>();
        final Vector3D v = VectorUtils.getDirection(yaw, pitch).normalize();
        for (int i = 1; i <= dist; i++) {
            from = from.add(v.multiply(amp));
            blocks.add(from);
        }
		return blocks;
	}
	
	public void tossItem(ItemStack item) {
		Vector3D dir = VectorUtils.getDirection(pitch,yaw).multiply(0.2);
		UUID uuid = UUID.randomUUID();
		BotU.log("tossed item: "+item.toString());
		Server.spawnTickableItem(new EntityItem(item,uuid,Server.nextEID(), pos.add(0, 1.75, 0), dir,Server));
	}
	
	public ItemStack getiteminhand() {
		return getItem(36+slot);
	}
	
	public void addToInv(int slott, ItemStack item) {
		if (item == null) item = new ItemStack(0,0);
		session.send(new ServerSetSlotPacket(0,slott,item));
		if (inventory.containsKey(slott)) {
			inventory.replace(slott, item);
		} else {
			inventory.put(slott, item);
		}
		if (slott >= 36 && slott <= 44) {
			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.MAIN_HAND, getiteminhand())}));
		} else if (slott == 45) {
			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.OFF_HAND, getItem(45))}));
		} else if (slott == 5) {
			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.HELMET, getItem(5))}));
		} else if (slott == 6) {
			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.CHESTPLATE, getItem(6))}));
		} else if (slott == 7) {
			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.LEGGINGS, getItem(7))}));
		} else if (slott == 8) {
			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.BOOTS, getItem(8))}));
		}
	}
	
	public boolean invIsNull() {
		return this.inventory == null;
	}
	
	public ItemStack getItem(int slott) {
		ItemStack item = inventory.get(slott);
		if (item == null) {
			return new ItemStack(0,0);
		} else {
			return new ItemStack(item.getId(),item.getAmount(),item.getNbt());
			
		}
	}
	
	public void tick() {
		if (sleepticks > 0) {
			sleepticks--;
			return;
		}
		if (state == st.lsp) {
			sendDefaultPackets();
			state = st.succ;
		} else if (state == st.succ) {
			if (Server.tickCounter%10==0) {
				session.send(new ServerUpdateViewPositionPacket((int)pos.x>>4,(int)pos.z>>4));
			}
			
			if (csleep >0) csleep--;
			if (Server.tickCounter%20==0) {
				if (blocksBySecond > 8) {
					for (Entry<Vector3D, Integer> g : backup.entrySet()) {
						Server.setBlock(g.getKey(), g.getValue());
					}
					if (blocksBySecond > 20) {
						String reason = "нюкерщик ебучий";
						banned = true;
						Server.sendForEver(new ServerChatPacket(Component.text(profile.getName()+" был забанен нахуй. Причина: "+reason+" хуесосим его.").color(NamedTextColor.RED)));
						session.send(new ServerChatPacket(Component.text("ты был опущен(забанен) на этом серве. причина - "+reason+". мне лень тебя кикать поэтому выйди сам (всё что ты сделаешь в этом состоянии игнорируется. можешь хоть нюкер врубить мне похуй)").color(NamedTextColor.RED)));
						session.send(new ServerChatPacket(Component.text("server closed the connection").color(NamedTextColor.AQUA)));
						Server.sendForEver(new ServerPlayerListEntryPacket(
							PlayerListEntryAction.UPDATE_DISPLAY_NAME,
							new PlayerListEntry[] {
								new PlayerListEntry(profile,Component.text("тень забаненого игрока "+profile.getName()))
							}
						));
						
					} else {
						this.disconnect(Component.text("ты чето быстро блоки ломаешь сука. Нюкером пользуешься поди гандон"));
						Server.broadcastMSG(Component.text(profile.getName()+" был кикнут античитом, лох ебаный"));
						
					}
				}
				backup.clear();
				if (blocksBySecond>0) BotU.log("bbs: "+this.blocksBySecond);
				this.blocksBySecond = 0;
			}
		}
	}
	
	public void disconnect(Component reason) {
		session.send(new ServerDisconnectPacket(reason));
	}
	
	@Override
    public void disconnected(DisconnectedEvent event) {
		//BotU.log(event.getReason());
		/*Server.players.remove(this);
		Server.sendForEver(new ServerPlayerListEntryPacket(
			PlayerListEntryAction.REMOVE_PLAYER,
			new PlayerListEntry[] {
				new PlayerListEntry(profile)
			}
		));
		Server.sendForEver(new ServerEntityDestroyPacket(new int[] {entityId}));
		Server.sendForEver(new ServerChatPacket(Component.text(profile.getName()+" ливнул пидарас").color(NamedTextColor.GOLD)));
		
		Server.worldData.get("players").getAsJsonObject().get(profile.getName()).getAsJsonObject().add("op", new JsonPrimitive(op));
		Server.worldData.get("players").getAsJsonObject().get(profile.getName()).getAsJsonObject().add("banned", new JsonPrimitive(banned));
		
		Server.worldData.get("players").getAsJsonObject().get(profile.getName()).getAsJsonObject().add(
    			"pos",
    			new JsonPrimitive(pos.x+" "+pos.y+" "+pos.z));
    	Server.worldData.get("players").getAsJsonObject().get(profile.getName()).getAsJsonObject().add("pitch", new JsonPrimitive(pitch));
    	Server.worldData.get("players").getAsJsonObject().get(profile.getName()).getAsJsonObject().add("yaw", new JsonPrimitive(yaw));
    	JsonArray inv = new JsonArray();
		for (int i = 0; i < 45;i++) {
			JsonObject itemobj = new JsonObject();
			ItemStack item = inventory==null?new ItemStack(0,0):getItem(i);
			itemobj.add("id", new JsonPrimitive(item==null?0:item.getId()));
			itemobj.add("count", new JsonPrimitive(item==null?0:item.getAmount()));
			inv.add(itemobj);
		}
		Server.worldData.get("players").getAsJsonObject().get(profile.getName()).getAsJsonObject().add("inventory", inv);
		Server.worldData.get("players").getAsJsonObject().get(profile.getName()).getAsJsonObject().add("health", new JsonPrimitive(health));
		Server.worldData.get("players").getAsJsonObject().get(profile.getName()).getAsJsonObject().add("food", new JsonPrimitive(food));
		*/
	}
	
	public void sendWorld() {
    	int i = 0;
    	NibbleArray3d[] skyLight = new NibbleArray3d[18];
    	for (i = 0; i<18;i++) {
    		skyLight[i] = new NibbleArray3d(4096);
    		skyLight[i].fill(4);
    	}
    	NibbleArray3d[] blockLight = new NibbleArray3d[18];
    	for (i = 0; i<18;i++) {
    		blockLight[i] = new NibbleArray3d(4096);
    		blockLight[i].fill(4);
    	}
		for (int cx = Server.Mincolumn; cx <= Server.Maxcolumn; cx++) {
			for (int cz = Server.Mincolumn; cz <= Server.Maxcolumn; cz++) {
				//BotU.log("otoslal x:"+cx+" z:"+cz);
				Column column = Multiworld.columns.get(new ChunkCoordinates(cx,cz));
				session.send(new ServerChunkDataPacket(column));
		    	//session.send(new ServerUpdateLightPacket(cx,cz,true, skyLight, blockLight));
			}
		}
	}
	
	public void sendPlayers() {
		for (ClientSession player : Server.players) {
			if (player.entityId != this.entityId) {
				session.send(new ServerPlayerListEntryPacket(
					PlayerListEntryAction.ADD_PLAYER,
					new PlayerListEntry[] {
						new PlayerListEntry(player.profile,player.gm)
					}
				));
				session.send(new ServerPlayerListEntryPacket(
					PlayerListEntryAction.UPDATE_DISPLAY_NAME,
					new PlayerListEntry[] {
						new PlayerListEntry(player.profile,Component.text(player.profile.getName()))
					}
				));
				session.send(new ServerSpawnPlayerPacket(player.entityId,
	    			player.profile.getId(),
	    			player.pos.x,
	    			player.pos.y,
	    			player.pos.z,
	    			player.pitch,
	    			player.yaw
		    	));
				session.send(new ServerEntityEquipmentPacket(entityId,new Equipment[] {
						new Equipment(EquipmentSlot.MAIN_HAND, player.getiteminhand()),
						new Equipment(EquipmentSlot.OFF_HAND, player.getItem(45)),
						new Equipment(EquipmentSlot.HELMET, player.getItem(5)),
						new Equipment(EquipmentSlot.CHESTPLATE, player.getItem(6)),
						new Equipment(EquipmentSlot.LEGGINGS, player.getItem(7)),
						new Equipment(EquipmentSlot.BOOTS, player.getItem(8))
				}));
				
			}
		}
	}
	
	public void sendEntities() {
		for (DefaultEntity t : Multiworld.Entities.values()) {
			session.send(new ServerSpawnEntityPacket(
					t.id, 
					t.uuid,
					t.type,
					(t instanceof BlockEntity? new FallingBlockData(((BlockEntity)t).blockState,0):new GenericObjectData(0)),
					t.getX(),
					t.getY(),
					t.getZ(),
					t.yaw,
					t.pitch,
					t.getMotionX(),
					t.getMotionY(),
					t.getMotionZ()
		    ));
			if (t instanceof EntityItem) {
				session.send(new ServerEntityMetadataPacket(t.id,new EntityMetadata[] {
					new EntityMetadata(7, MetadataType.ITEM, ((EntityItem)t).getItem())
		    	}));
			}
		}
	}
	
	public void sendDefaultPackets() {
		banned = Server.getPlayerObject(profile.getName()).get("banned").getAsBoolean();
		if (banned) {
			session.send(new ServerDisconnectPacket(
					Component.text("ТЫ ЕБЛАН? ТЫ ЗАБАНЕН БЛЯТЬ\n").color(NamedTextColor.RED)
						.append(Component.text("И Я НЕ БУДУ ТЕБЯ РАЗБАНИВАТЬ ПОТМОУЧТО ТЫ ТУПОЙ СУКА\n").color(NamedTextColor.RED)
						.append(Component.text("кароче ты забанен,лох и еще соси хуй вот да\n")).color(NamedTextColor.WHITE)
						.append(Component.text("кста если ты феменистка или не согласен с баном то:\n")).color(NamedTextColor.WHITE)
						.append(Component.text("а) иди нахуй\n")).color(NamedTextColor.WHITE)
						.append(Component.text("б) ты можешь похныкать мне в дс Pizdec RP#0706\n")).color(NamedTextColor.WHITE)
						.append(Component.text("б) не забудь рассказать об этом своей мамочке шаболде пусть она\n")).color(NamedTextColor.WHITE)
						.append(Component.text("узнает какой ты лох что тебя забанили на нн сервере какомто ВОТ ЖЕ БОТИК\n")).color(NamedTextColor.WHITE)
						.append(Component.text("г) .|. - писька\n")).color(NamedTextColor.WHITE)
						.append(Component.text("а на этом все, пописайте на анал ставьте классы, всем пока").color(NamedTextColor.AQUA))
					)));
			return;
		}
		session.send(new ServerJoinGamePacket(
				0,
    			false,
    			gm,
    			gm,
    			1,
    			new String[] {"minecraft:world"},
    			Server.getDimensionTag(),
    			Server.getOverworldTag(),
    			"minecraft:world",
    			1488,
    			(int) Server.gets("maxPlayers"),
    			10,
    			false,
    			true,
    			false,
    			true
    	));
		session.send(new ServerPlayerListEntryPacket(
			PlayerListEntryAction.ADD_PLAYER,
			new PlayerListEntry[] {
				new PlayerListEntry(profile,gm)
			}
		));
		session.send(new ServerPlayerListEntryPacket(
			PlayerListEntryAction.UPDATE_DISPLAY_NAME,
			new PlayerListEntry[] {
				new PlayerListEntry(profile,Component.text(profile.getName()))
			}
		));
		for (ClientSession player : Server.players) {
			if (player.entityId != this.entityId) {
				player.session.send(new ServerPlayerListEntryPacket(
					PlayerListEntryAction.ADD_PLAYER,
					new PlayerListEntry[] {
						new PlayerListEntry(profile,gm)
					}
				));
				player.session.send(new ServerPlayerListEntryPacket(
					PlayerListEntryAction.UPDATE_DISPLAY_NAME,
					new PlayerListEntry[] {
						new PlayerListEntry(profile,Component.text(profile.getName()))
					}
				));
				player.session.send(new ServerSpawnPlayerPacket(entityId,
	    			profile.getId(),
	    			pos.x,
	    			pos.y,
	    			pos.z,
	    			pitch,
	    			yaw
		    	));
				session.send(new ServerPlayerListEntryPacket(
					PlayerListEntryAction.ADD_PLAYER,
					new PlayerListEntry[] {
						new PlayerListEntry(player.profile,player.gm)
					}
				));
				session.send(new ServerPlayerListEntryPacket(
					PlayerListEntryAction.UPDATE_DISPLAY_NAME,
					new PlayerListEntry[] {
						new PlayerListEntry(player.profile,Component.text(player.profile.getName()))
					}
				));
				session.send(new ServerSpawnPlayerPacket(player.entityId,
	    			player.profile.getId(),
	    			player.pos.x,
	    			player.pos.y,
	    			player.pos.z,
	    			player.pitch,
	    			player.yaw
		    	));
			}
			
		}
		sendEntities();
		session.send(new ServerDifficultyPacket(Difficulty.NORMAL, true));
		//session.send(new ServerPlayerAbilitiesPacket(gm==GameMode.CREATIVE,gm==GameMode.CREATIVE,gm==GameMode.CREATIVE,gm==GameMode.CREATIVE,0.1F,0.05F));
		session.send(new ServerPlayerChangeHeldItemPacket(0));
		session.send(new ServerDeclareRecipesPacket(new Recipe[0]));
		session.send(new ServerSpawnPositionPacket(Server.spawnpoint.clone().translate()));
		//session.send(new ServerDeclareTagsPacket());
		session.send(new ServerDeclareCommandsPacket(Server.commands, 0));
		session.send(new ServerUnlockRecipesPacket(new String[0], false, false,false,false,false,false,false,false, new String[0]));
		
		session.send(new ServerPlayerPositionRotationPacket(pos.x,pos.y,pos.z,yaw,pitch,tpid++,new ArrayList<PositionElement>()));
		session.send(new ServerUpdateViewPositionPacket((int)pos.x>>4,(int)pos.z>>4));
		sendWorld();
		session.send(new ServerPlayerHealthPacket(health,food,0));
		inventory = new HashMap<>();
		JsonArray tempinv = Server.getPlayerObject(profile.getName()).get("inventory").getAsJsonArray();
		ItemStack[] ti = new ItemStack[tempinv.size()];
		for (int i = 0; i < 45;i++) {
			JsonObject itemobj = tempinv.get(i).getAsJsonObject();
			ItemStack item = new ItemStack(itemobj.get("id").getAsInt(), itemobj.get("count").getAsInt());
			addToInv(i, item);
			ti[i] = item;
		}
		session.send(new ServerWindowItemsPacket(0, ti));
		Server.sendForEver(new ServerChatPacket(Component.text(profile.getName()+" залетел").color(NamedTextColor.GOLD)));
	}

	public boolean isAlive() {
		return this.health > 0;
	}
	
	public boolean canAddItem(ItemStack item) {
		item = new ItemStack(item.getId(),item.getAmount(),item.getNbt());
        for (int i = 9; i <= 44; i++) {
            ItemStack slot = getItem(i);
            if (item.getId() == slot.getId()) {
                int diff;
                if ((diff = Main.getMCData().items.get(slot.getId()).stackSize - slot.getAmount()) > 0) {
                    item = new ItemStack(item.getId(), item.getAmount() - diff, item.getNbt());
                }
            } else if (slot.getId() == 0) {
                item = new ItemStack(item.getId(), item.getAmount() - 64, item.getNbt());
            }

            if (item.getAmount() <= 0) {
                return true;
            }
        }

        return false;
	}
	
	public int addItem(ItemStack item) {
		int canadd = 0;
		int slotmaxcount = Main.getMCData().items.get(item.getId()).stackSize;
		for (int i = 44; i >= 9; i--) {
			ItemStack slotitem = this.getItem(i);
			if (slotitem.getId() == item.getId()) {
				canadd = slotmaxcount-slotitem.getAmount();
				if (canadd == 0) {
				} else if (canadd == item.getAmount()) {
					this.addToInv(i, item);
					return item.getAmount();
				} else if (canadd > item.getAmount()) {
					this.addToInv(i, new ItemStack(item.getId(), slotitem.getAmount()+item.getAmount(),item.getNbt()));
					return item.getAmount();
				} else if (canadd < item.getAmount()) {
					int toadd = slotmaxcount - slotitem.getAmount();
					this.addToInv(i, new ItemStack(item.getId(), slotmaxcount,item.getNbt()));
					return toadd;
				} else {
					BotU.wn("HOLY SHIT THAT UNBELIVEABLE FUCK THIS MF NIGGA WTFFFF AOOOOOAAAOOA");
					return -228;
				}
			} else if (slotitem.getId() == 0) {
				this.addToInv(i, item);
				return item.getAmount();
			}
		}
		return 0;
	}
	
	public void chat(String msg) {
		session.send(new ServerChatPacket(Component.text(msg)));
	}

	public boolean pickupEntity(DefaultEntity entity) {
		if (entity instanceof EntityItem) {
			if (((EntityItem) entity).getPickupDelay() <= 0) {
                ItemStack item = ((EntityItem) entity).getItem();

                if (item != null && item.getId() != 0) {
                    if (!this.canAddItem(item)) {
                        return false;
                    }
                    
                    int count = this.addItem(item);

                    if (count>0) Server.sendForEver(new ServerEntityCollectItemPacket(entity.id,this.entityId, count));
                    
                    if (count >= item.getAmount()) {
                    	entity.close();
                    }
                    
                    if (count>0) return true;
                    else return false;
                }
            }
		}
		return false;
	}
}

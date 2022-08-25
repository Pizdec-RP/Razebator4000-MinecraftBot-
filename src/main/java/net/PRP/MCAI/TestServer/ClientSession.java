package net.PRP.MCAI.TestServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import com.github.steveice10.mc.protocol.data.game.world.particle.ParticleData;
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
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerUnlockRecipesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAnimationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
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

import net.PRP.MCAI.Main;
import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.entity.Arrow;
import net.PRP.MCAI.TestServer.entity.Item;
import net.PRP.MCAI.TestServer.entity.Tickable;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.ChunkCoordinates;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
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
	public Map<Integer, ItemStack> inventory;
	private st state = st.not;
	private int sleepticks = 0;
	public int slot = 0;
	public boolean onGround = true;
	public boolean op = false;
	public boolean banned = false;
	
	private enum st {
		not, lsp, succ
	}
	
	public ClientSession() {
		
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
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
        	if (packet.getMessage().startsWith("/")) {
        		Server.commandUsed(this, packet.getMessage());
        	} else {
	        	ServerChatPacket p = new ServerChatPacket(Component.text("<"+profile.getName()+"> "+packet.getMessage()));
	        	
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
        	if (p.getAction() == PlayerAction.START_DIGGING) {
        		if (gm == GameMode.CREATIVE) {
        			Server.setBlock(new Vector3D(p.getPosition()), 0);
        		}
        	} else if (p.getAction() == PlayerAction.FINISH_DIGGING) {
        		Server.setBlock(new Vector3D(p.getPosition()), 0);
        	} else if (p.getAction() == PlayerAction.DROP_ITEM | p.getAction() == PlayerAction.DROP_ITEM_STACK) {
        		Server.sendForEver(new ServerEntityAnimationPacket(entityId,Animation.SWING_ARM));
        	} else if (p.getAction() == PlayerAction.RELEASE_USE_ITEM) {
        		if (getiteminhand() != null && getiteminhand().getId() == 574) {
        			Server.spawnTickable(new Arrow(pos.add(0, 1.75, 0), getDirection(yaw,pitch).multiply(0.3)));
        		}
        	}
        } else if (event.getPacket() instanceof ClientPlayerPlaceBlockPacket) {
        	ClientPlayerPlaceBlockPacket p = event.getPacket();
        	ItemStack item = getiteminhand();
        	if (item != null && item.getId() == 572) {//fire the tnt
        		if (Multiworld.getBlock(new Vector3D(p.getPosition())).id == 137) {
        			Server.setBlock(new Vector3D(p.getPosition()), 0);
        			//fire the tnt
        			return;
        		}
        	}
        	
        	Vector3D posss = VectorUtils.convert(p.getPosition()).add(VectorUtils.BFtoVec(p.getFace()));
        	Block beforeBlock = Multiworld.getBlock(posss);
        	if (beforeBlock.id != 0) return;
        	if (p.getHand() == Hand.MAIN_HAND) {
        		if (item == null || item.getId() == 0) {
        			//empty click
        		} else {
        			int state = -1;
        			int oid = Main.getMCData().itemToOldId(item.getId());
        			if (oid != -1) {
        				state = Main.getMCData().oldIdToNew(oid);
        			}
        			if (state != -1) Server.setBlock(posss, state);
        		}
        	} else {
        		Server.setBlock(posss, beforeBlock.state);
        	}
        } else if (event.getPacket() instanceof ClientPlayerChangeHeldItemPacket) {
        	ClientPlayerChangeHeldItemPacket p = event.getPacket();
        	this.slot = p.getSlot();
        	Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.MAIN_HAND, getiteminhand())}));
        } else if (event.getPacket() instanceof ClientCreativeInventoryActionPacket) {
        	ClientCreativeInventoryActionPacket p = event.getPacket();
        	BotU.log("cslot: "+p.getSlot());
        	
        	if (p.getSlot() < 0) {
        		tossItem(p.getClickedItem());
        	} else {
        		inventory.replace(p.getSlot(), p.getClickedItem());
        		if (p.getSlot() >= 36 && p.getSlot() <= 44) {
        			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.MAIN_HAND, getiteminhand())}));
        		} else if (p.getSlot() == 45) {
        			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.OFF_HAND, inventory.get(45))}));
        		} else if (p.getSlot() == 5) {
        			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.HELMET, inventory.get(5))}));
        		} else if (p.getSlot() == 6) {
        			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.CHESTPLATE, inventory.get(6))}));
        		} else if (p.getSlot() == 7) {
        			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.LEGGINGS, inventory.get(7))}));
        		} else if (p.getSlot() == 8) {
        			Server.sendForEver(new ServerEntityEquipmentPacket(entityId,new Equipment[] {new Equipment(EquipmentSlot.BOOTS, inventory.get(8))}));
        		}
        	}
        } else if (event.getPacket() instanceof ClientPlayerSwingArmPacket) {
        	Server.sendForEver(new ServerEntityAnimationPacket(entityId,Animation.SWING_ARM));
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
        			if (iid == 613) {
        				List<Vector3D> points = createRay(pos, yaw, pitch, 30, 0.3);
            			for (Vector3D pn : points) {
            				Server.sendForEver(new ServerSpawnParticlePacket(new Particle(ParticleType.END_ROD,new BlockParticleData(1)),true,pn.x,pn.y+1.75,pn.z,0f,0f,0f,0f,1));
            			}
        			} else if (iid == 578) {
        				List<Vector3D> points = new ArrayList<>();
        				for (int i = -179; i < 179;i++) {
        					if (i%20==0) {
        						points.addAll(createRay(pos, i, pitch, 8, 0.8));
        					}
        				}
        				for (Vector3D pn : points) {
            				Server.sendForEver(new ServerSpawnParticlePacket(new Particle(ParticleType.END_ROD,new BlockParticleData(1)),true,pn.x,pn.y+2.1,pn.z,0f,0f,0f,0f,1));
            			}
        			}
        		}
        	}
        } else if (event.getPacket() instanceof ClientPlayerUseItemPacket) {
        	
        }
	}
	
	public List<Vector3D> createRay(Vector3D from, float yaw, float pitch, int dist, double amp) {
		List<Vector3D> blocks = new CopyOnWriteArrayList<>();
        final Vector3D v = getDirection(yaw, pitch).normalize();
        for (int i = 1; i <= dist; i++) {
            from = from.add(v.multiply(amp));
            blocks.add(from);
        }
		return blocks;
	}
	
	public Vector3D getDirection(double yaw, double pitch) {
        Vector3D vector = new Vector3D(0,0,0);
        double rotX = yaw;
        double rotY = pitch;
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }
	
	public void tossItem(ItemStack item) {
		BotU.log("itemtossed");
		Server.spawnTickable(new Item(item, pos.add(0, 1.75, 0), getDirection(pitch,yaw)));
	}
	
	public ItemStack getiteminhand() {
		return inventory.get(36+slot);
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
			Multiworld.Entities.get(entityId).pos = pos;
			Multiworld.Entities.get(entityId).vel = vel;
			Multiworld.Entities.get(entityId).pitch = pitch;
			Multiworld.Entities.get(entityId).yaw = yaw;
			if (Server.tickCounter%10==0) {
				session.send(new ServerUpdateViewPositionPacket((int)pos.x>>4,(int)pos.z>>4));
			}
		}
	}
	
	@Override
    public void disconnected(DisconnectedEvent event) {
		//BotU.log(event.getReason());
		Server.sendForEver(new ServerPlayerListEntryPacket(
			PlayerListEntryAction.REMOVE_PLAYER,
			new PlayerListEntry[] {
				new PlayerListEntry(profile)
			}
		));
		Server.players.remove(this);
		Server.sendForEver(new ServerEntityDestroyPacket(new int[] {entityId}));
		Server.sendForEver(new ServerChatPacket(Component.text(profile.getName()+" ливнул пидарас. Уходишь? ну и пиздуй! не побывает в тебе мой хуй")));
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
			}
		}
	}
	
	public void sendEntities() {
		for (Tickable t : Server.tickable) {
			session.send(new ServerSpawnEntityPacket(
					t.getEntity().eid, 
					t.getEntity().uuid,
					t.getEntity().type,
					t.getEntity().pos.x,
					t.getEntity().pos.y,
					t.getEntity().pos.z,
					t.getEntity().yaw,
					t.getEntity().pitch,
					t.getEntity().vel.x,
					t.getEntity().vel.y,
					t.getEntity().vel.z
		    	));
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
    			6,
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
			inventory.put(i, item);
			ti[i] = item;
		}
		session.send(new ServerWindowItemsPacket(0, ti));
		Server.sendForEver(new ServerChatPacket(Component.text(profile.getName()+" жеска залетел на гейпати")));
	}
}

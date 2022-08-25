package net.PRP.MCAI.bot;

import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntryAction;
import com.github.steveice10.mc.protocol.data.game.ResourcePackStatus;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import com.github.steveice10.mc.protocol.data.game.entity.player.HandPreference;
import com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility;
import com.github.steveice10.mc.protocol.data.game.setting.SkinPart;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDeclareCommandsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPropertiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnLivingEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateLightPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerResourcePackSendPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.data.ChunkCoordinates;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.*;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;

import java.util.ArrayList;
import java.util.UUID;

public class SessionListener extends SessionAdapter {
    private Bot client;
    

    public SessionListener(Bot client) {
        this.client = client;
    }
    
    @Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
    	System.out.println(receiveEvent.getPacket().getClass().getName());
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	//System.out.println("(" + client.getGameProfile().getName() + ") Подлючился");
        	ServerJoinGamePacket p = (ServerJoinGamePacket) receiveEvent.getPacket();
        	client.getWorld().renderDistance = p.getViewDistance();
        	client.setId(p.getEntityId());
        	client.pm.sleepticks = 600;
        	client.connected = true;
        	client.gamemode = p.getGameMode();
        	client.getSession().send(new ClientSettingsPacket(
        			"ru",
        			16,
        			ChatVisibility.FULL,
        			true,
        			new ArrayList<SkinPart>() {
					private static final long serialVersionUID = 1562002041284663871L;
					{
						add(SkinPart.CAPE);
						add(SkinPart.HAT);
						add(SkinPart.JACKET);
						add(SkinPart.LEFT_PANTS_LEG);
						add(SkinPart.LEFT_SLEEVE);
						add(SkinPart.RIGHT_PANTS_LEG);
						add(SkinPart.RIGHT_SLEEVE);
					}},
        			HandPreference.RIGHT_HAND
        	));
        	client.getSession().send(new ClientPluginMessagePacket("minecraft:brand", new byte[] {7,118,97,110,105,108,108,97}));
        	client.register();
        } else if (receiveEvent.getPacket() instanceof ServerPlayerPositionRotationPacket) {
            ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket) receiveEvent.getPacket();
            client.getSession().send(new ClientTeleportConfirmPacket(packet.getTeleportId()));
            client.pm.sleepticks = 1;
            client.setPosX(packet.getX());
            client.setPosY(packet.getY());
            client.setPosZ(packet.getZ());
            client.pm.before = new Vector3D(packet.getX(),packet.getY(),packet.getZ());
            client.setYaw(packet.getYaw());
			client.setPitch(packet.getPitch());
			client.pm.beforePitch = packet.getPitch();
			client.pm.beforeYaw = packet.getYaw();
            BotU.log("pos packet received x:"+packet.getX()+" y:"+packet.getY()+" z:"+packet.getZ()+" yaw:"+packet.getYaw()+" pitch:"+packet.getPitch());
            client.getSession().send(new ClientPlayerPositionRotationPacket(client.onGround,packet.getX(),packet.getY(),packet.getZ(), client.getYaw(), client.getPitch()));
            //client.getSession().send(new ClientRequestPacket(ClientRequest.STATS));
            client.pm.resetVel();
            if (client.crafter.windowType != null) {
            	if (VectorUtils.sqrt(client.crafter.lastWindowPos, new Vector3D(packet.getX(),packet.getY(),packet.getZ())) > 8) {
            		client.getSession().send(new ClientCloseWindowPacket(client.playerInventory.currentWindowId));
            		client.crafter.windowType = null;
            		client.playerInventory.currentWindowId = 0;
            	}
            }
        } else if (receiveEvent.getPacket() instanceof ServerPlayerHealthPacket) {
            final ServerPlayerHealthPacket p = (ServerPlayerHealthPacket) receiveEvent.getPacket();
            client.foodlvl = p.getFood();
            client.health = p.getHealth();
            if (p.getHealth() <= 0)
            	client.pvp.endPVP();
            	client.bbm.reset();
            	client.pathfinder.reset();
            	BotU.log("h:"+p.getHealth()+" f:"+p.getFood()+" s:"+p.getSaturation());
        
        //server chunks
        
        } else if (receiveEvent.getPacket() instanceof ServerMultiBlockChangePacket) {
			ServerMultiBlockChangePacket packet = (ServerMultiBlockChangePacket) receiveEvent.getPacket();
			for (BlockChangeRecord data : packet.getRecords()) {
				client.getWorld().setBlock(data.getPosition(), data.getBlock());
				//if (!client.pathfinder.ignored.contains(VectorUtils.convert(data.getPosition()))) client.pathfinder.func_2(VectorUtils.convert(data.getPosition()));
			}
        } else if (receiveEvent.getPacket() instanceof ServerUnloadChunkPacket) {
			if ((boolean) Main.gamerule("multiworld")) {
				ServerUnloadChunkPacket a = ((ServerUnloadChunkPacket)receiveEvent.getPacket());
				client.getWorld().unloadColumn(new ChunkCoordinates(a.getX(), a.getZ()));
			}
		} else if (receiveEvent.getPacket() instanceof ServerChunkDataPacket) {
			
			ServerChunkDataPacket data = (ServerChunkDataPacket) receiveEvent.getPacket();
			//BotU.log(data.getColumn().getBiomeData().length);
			
			//System.exit(0);

			client.getWorld().addChunkColumn(new ChunkCoordinates(data.getColumn().getX(), data.getColumn().getZ()), data.getColumn());
		} else if (receiveEvent.getPacket() instanceof ServerPluginMessagePacket) {
			
			ServerPluginMessagePacket data = (ServerPluginMessagePacket) receiveEvent.getPacket();
			BotU.log(data.getChannel());
			
			for (byte b:data.getData()) {
				BotU.log(b);
			}
			
		} else if (receiveEvent.getPacket() instanceof ServerBlockChangePacket) {
			ServerBlockChangePacket packet = (ServerBlockChangePacket) receiveEvent.getPacket();
			client.getWorld().setBlock(packet.getRecord().getPosition(), packet.getRecord().getBlock());
			//if (!client.pathfinder.ignored.contains(VectorUtils.convert(packet.getRecord().getPosition()))) client.pathfinder.func_2(VectorUtils.convert(packet.getRecord().getPosition()));
			
		} else if (receiveEvent.getPacket() instanceof LoginSuccessPacket) {
            final LoginSuccessPacket p = (LoginSuccessPacket) receiveEvent.getPacket();
            UUID MyUUID = p.getProfile().getId();
            client.setUUID(MyUUID);
            //System.out.println("UUID: " + MyUUID);
		} else if (receiveEvent.getPacket() instanceof ServerPlayerListEntryPacket) {
			final ServerPlayerListEntryPacket p = (ServerPlayerListEntryPacket) receiveEvent.getPacket();
			if (p.getAction() == PlayerListEntryAction.ADD_PLAYER) {
				for (PlayerListEntry entry : p.getEntries()) {
					//BotU.log(entry.getProfile().getName());
					client.getWorld().ServerTabPanel.add(entry);
				}
			} else if (p.getAction() == PlayerListEntryAction.REMOVE_PLAYER) {
				for (PlayerListEntry entry : p.getEntries()) {
					client.getWorld().ServerTabPanel.remove(entry);
				}
			} else if (p.getAction() == PlayerListEntryAction.UPDATE_DISPLAY_NAME) {
				for (PlayerListEntry entry : p.getEntries()) {
					for (PlayerListEntry pl : client.getWorld().ServerTabPanel) {
						if (pl.getProfile().getId().equals(entry.getProfile().getId())) {
							
							client.getWorld().ServerTabPanel.add(entry);
							client.getWorld().ServerTabPanel.remove(pl);
						}
					}
				}
			} else if (p.getAction() == PlayerListEntryAction.UPDATE_GAMEMODE) {
				for (PlayerListEntry entry : p.getEntries()) {
					for (PlayerListEntry pl : client.getWorld().ServerTabPanel) {
						if (pl.getProfile().getId().equals(entry.getProfile().getId())) {
							
							client.getWorld().ServerTabPanel.add(entry);
							client.getWorld().ServerTabPanel.remove(pl);
						}
					}
				}
			}
			
		} else if (receiveEvent.getPacket() instanceof ServerResourcePackSendPacket) {
			//final ServerResourcePackSendPacket p = (ServerResourcePackSendPacket) receiveEvent.getPacket();
			ThreadU.sleep(50);
			client.getSession().send(new ClientResourcePackStatusPacket(ResourcePackStatus.ACCEPTED));
			client.getSession().send(new ClientResourcePackStatusPacket(ResourcePackStatus.SUCCESSFULLY_LOADED));
		} else if (receiveEvent.getPacket() instanceof ServerPlayerAbilitiesPacket) {
			ServerPlayerAbilitiesPacket p = (ServerPlayerAbilitiesPacket)receiveEvent.getPacket();
			client.walkSpeed = p.getWalkSpeed();
			client.flySpeed = p.getFlySpeed();
			BotU.log("walkspeed = "+p.getWalkSpeed());
		} else if (receiveEvent.getPacket() instanceof ServerPlayerChangeHeldItemPacket) {
			ServerPlayerChangeHeldItemPacket p = (ServerPlayerChangeHeldItemPacket)receiveEvent.getPacket();
			BotU.SetSlot(client, p.getSlot());
		} else if (receiveEvent.getPacket() instanceof ServerSpawnLivingEntityPacket) {
			BotU.log(receiveEvent.getPacket().toString());
		} else if (receiveEvent.getPacket() instanceof ServerEntityMetadataPacket) {
			BotU.log(receiveEvent.getPacket().toString());
		}
    }
    
    @Override
    public void disconnected(DisconnectedEvent event) {
    	if (!client.reconectAvable) return;
    	client.connected = false;
    	BotU.log("disconnected");
    	System.out.println(event.getReason());
    	Main.write("[dc] ",event.getReason());
    	if (event.getCause() != null) {
    		event.getCause().printStackTrace();
    	}
		if ((boolean) Main.gamerule("reconect")) {
			ThreadU.sleep(6000);
			client.build();
			client.getSession().connect();
		}
		//event.getCause().printStackTrace();
    }
}
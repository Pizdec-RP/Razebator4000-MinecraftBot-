package net.PRP.MCAI.bot;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.ResourcePackStatus;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerResourcePackSendPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.data.ChunkCoordinates;
import net.PRP.MCAI.data.ServerPlayerObject;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.*;


import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import java.util.UUID;

public class SessionListener extends SessionAdapter {
    private final Bot client;
    static int exline = -1;
    Position actionentity;
    

    public SessionListener(Bot client) {
        this.client = client;
    }
    
    public Hand findTrashBlockInInventory() {
		return null;
    }
    
    @Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	System.out.println("(" + client.getGameProfile().getName() + ") Подлючился");
        	ServerJoinGamePacket p = (ServerJoinGamePacket) receiveEvent.getPacket();
        	client.getWorld().renderDistance = p.getViewDistance();
        	client.setId(p.getEntityId());
        	client.pm.sleepticks = 600;
        	client.connected = true;
        	ThreadU.sleep(500);
        	client.register();
        } else if (receiveEvent.getPacket() instanceof ServerPlayerPositionRotationPacket) {
            ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket) receiveEvent.getPacket();
            client.getSession().send(new ClientTeleportConfirmPacket(packet.getTeleportId()));
            client.pm.sleepticks = 15;
            ThreadU.sleep(100);
            client.setPosX(packet.getX());
            client.setPosY(packet.getY());
            client.setPosZ(packet.getZ());
            client.pm.before = new Vector3D(packet.getX(),packet.getY(),packet.getZ());
            client.setYaw(packet.getYaw());
			client.setPitch(packet.getPitch());
			//BotU.calibratePosition(client);
            log("pos packet received x:"+packet.getX()+" y:"+packet.getY()+" z:"+packet.getZ()+" yaw:"+packet.getYaw()+" pitch:"+packet.getPitch());
            client.getSession().send(new ClientPlayerPositionRotationPacket(client.onGround,packet.getX(),packet.getY(),packet.getZ(), client.getYaw(), client.getPitch()));
            client.getSession().send(new ClientRequestPacket(ClientRequest.STATS));
        } else if (receiveEvent.getPacket() instanceof ServerPlayerHealthPacket) {
            final ServerPlayerHealthPacket p = (ServerPlayerHealthPacket) receiveEvent.getPacket();
            
            if (p.getHealth() <= 0)
            	client.pvp.reset();
            	client.bbm.reset();
            	client.pathfinder.reset();
            	client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
        
        //server chunks
        
        } else if (receiveEvent.getPacket() instanceof ServerMultiBlockChangePacket) {
			ServerMultiBlockChangePacket packet = (ServerMultiBlockChangePacket) receiveEvent.getPacket();
			for (BlockChangeRecord data : packet.getRecords()) {
				client.getWorld().setBlock(data.getPosition(), data.getBlock());
				if (!client.pathfinder.ignored.contains(VectorUtils.convert(data.getPosition()))) client.pathfinder.func_2(VectorUtils.convert(data.getPosition()));
			}
        //} else if (receiveEvent.getPacket() instanceof ServerUnloadChunkPacket) {
			
		} else if (receiveEvent.getPacket() instanceof ServerChunkDataPacket) {
			
			ServerChunkDataPacket data = (ServerChunkDataPacket) receiveEvent.getPacket();
			client.getWorld().addChunkColumn(new ChunkCoordinates(data.getColumn().getX(), data.getColumn().getZ()), data.getColumn());
		
		} else if (receiveEvent.getPacket() instanceof ServerBlockChangePacket) {
			ServerBlockChangePacket packet = (ServerBlockChangePacket) receiveEvent.getPacket();
			client.getWorld().setBlock(packet.getRecord().getPosition(), packet.getRecord().getBlock());
			if (!client.pathfinder.ignored.contains(VectorUtils.convert(packet.getRecord().getPosition()))) client.pathfinder.func_2(VectorUtils.convert(packet.getRecord().getPosition()));
			
		} else if (receiveEvent.getPacket() instanceof LoginSuccessPacket) {
            final LoginSuccessPacket p = (LoginSuccessPacket) receiveEvent.getPacket();
            UUID MyUUID = p.getProfile().getId();
            client.setUUID(MyUUID);
            System.out.println("UUID: " + MyUUID);
		} else if (receiveEvent.getPacket() instanceof ServerPlayerListEntryPacket) {
			final ServerPlayerListEntryPacket p = (ServerPlayerListEntryPacket) receiveEvent.getPacket();
			client.getWorld().ServerTabPanel.clear();
			for (PlayerListEntry entry : p.getEntries()) {
				client.getWorld().ServerTabPanel.add(new ServerPlayerObject(entry));
			}
		} else if (receiveEvent.getPacket() instanceof ServerPlayerListDataPacket) {
			//final ServerPlayerListDataPacket p = (ServerPlayerListDataPacket) receiveEvent.getPacket();
		} else if (receiveEvent.getPacket() instanceof ServerResourcePackSendPacket) {
			//final ServerResourcePackSendPacket p = (ServerResourcePackSendPacket) receiveEvent.getPacket();
			ThreadU.sleep(50);
			client.getSession().send(new ClientResourcePackStatusPacket(ResourcePackStatus.ACCEPTED));
			ThreadU.sleep(100);
			client.getSession().send(new ClientResourcePackStatusPacket(ResourcePackStatus.SUCCESSFULLY_LOADED));
		}
    }
    
    @Override
    public void disconnected(DisconnectedEvent event) {
    	if (!client.reconectAvable) return;
    	client.connected = false;
    	System.out.println(event.getReason().toString());
		ThreadU.sleep(6100);
		if ((boolean) Main.getsett("reconect")) {
			client.reset();
			client.connect();
		}
		//event.getCause().printStackTrace();
    }
	
	public static void log(String f) {
    	System.out.println("[log] "+f);
    }
}
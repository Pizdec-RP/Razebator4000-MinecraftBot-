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
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockValuePacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import net.PRP.MCAI.Main;
import net.PRP.MCAI.utils.*;


import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

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
    
    public Hand findTrashBlockInInventory() {
		return null;
    }
    
    @Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	System.out.println("(" + client.getGameProfile().getName() + ") Подрубился.");
        	ThreadU.sleep(1000);
        	Thread physics = new Thread(() -> {
            	while (true) {
            		if (client.isOnline()) {
            			if (VectorUtils.BTavoid(client.getPosition().add(0, -1, 0).getBlock(client).type) && !client.isInAction()) {
							client.remY(1);
							ThreadU.sleep(86);
            			} else if (!client.isInAction() && client.getPosY() > (int)client.getPosY()) {
            				BotU.calibratePosition(client);
            				BotU.calibrateY(client);
            			} else {
            				ThreadU.sleep(500);
            			}
            		} else {
            			ThreadU.sleep(500);
            		}
            		
            	}
            });
        	physics.start();
        } else if (receiveEvent.getPacket() instanceof ServerPlayerPositionRotationPacket) {
            ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket) receiveEvent.getPacket();
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
            final ServerPlayerHealthPacket p = (ServerPlayerHealthPacket) receiveEvent.getPacket();
            if (p.getHealth() <= 0)
            	client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
        
        //server chunks
        
        } else if (receiveEvent.getPacket() instanceof ServerMultiBlockChangePacket) {
        	//log("mbcp");
			ServerMultiBlockChangePacket packet = (ServerMultiBlockChangePacket) receiveEvent.getPacket();
			for (BlockChangeRecord data : packet.getRecords()) {
				client.getWorld().setBlock(data.getPosition(), data.getBlock());
				
			}
        } else if (receiveEvent.getPacket() instanceof ServerUnloadChunkPacket) {
			
		} else if (receiveEvent.getPacket() instanceof ServerChunkDataPacket) {
			
			ServerChunkDataPacket data = (ServerChunkDataPacket) receiveEvent.getPacket();
			client.getWorld().addChunkColumn(new ChunkCoordinates(data.getColumn().getX(), data.getColumn().getZ()), data.getColumn());
		
		} else if (receiveEvent.getPacket() instanceof ServerBlockChangePacket) {
			
			ServerBlockChangePacket packet = (ServerBlockChangePacket) receiveEvent.getPacket();
			client.getWorld().setBlock(packet.getRecord().getPosition(), packet.getRecord().getBlock());
			//System.out.println(packet.toString());
			
		} else if (receiveEvent.getPacket() instanceof LoginSuccessPacket) {
            final LoginSuccessPacket p = (LoginSuccessPacket) receiveEvent.getPacket();
            UUID MyUUID = p.getProfile().getId();
            client.setUUID(MyUUID);
            System.out.println("UUID: " + MyUUID);
		} /*else if (receiveEvent.getPacket() instanceof ServerBlockValuePacket) {
			final ServerBlockValuePacket p = (ServerBlockValuePacket) receiveEvent.getPacket();
			
		}*/
    }
    
    @Override
    public void disconnected(DisconnectedEvent event) {
    	log("bot disconected "+event.getReason());
    	if (!Main.debug) {
    		if ((boolean) Main.getsett("reconect")) client.connect();
    	}
		event.getCause().printStackTrace();
    }
	
	public static void log(String f) {
    	System.out.println("[log] "+f);
    }
}
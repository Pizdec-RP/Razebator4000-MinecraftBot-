package net.PRP.MCAI.ListenersForServers;

import java.util.List;

import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.physics;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class NukerFucker implements ServerListener{

	Bot client;
	boolean run = false;
	Vector3D startpos = null;
	double i = 10;
	double playerSpeed = 4;

	public NukerFucker(Bot client) {
		this.client = client;
	}

	@Override
	public void tick() {
		
		if (run) {
			BotU.log("nuking");
			BotU.LookHead(client, startpos);
			client.yaw += i;
			Vector3D nextvel = VectorUtils.vector(client.getYaw(), client.getPitch(), playerSpeed,client);
			client.posX+=nextvel.x;
			client.posZ+=nextvel.z;
			client.getSession().send(new ClientPlayerPositionRotationPacket(client.onGround, client.posX, client.posY, client.posZ, client.getYaw(), client.getPitch()));
			nukeBlocks(4);
			client.pm.velocity.origin();
			playerSpeed+=0.04;
		} else {
			run = true;
			client.pm.fly = true;
			BotU.log("runing");
			this.startpos = client.getPosition();
			return;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void nukeBlocks(int radius) {
		List<Vector3D> list = VectorUtils.getAllInBox(client.getPosition(), radius);
		for (Vector3D pos : list) {
			client.getSession().send(new ClientPlayerActionPacket(PlayerAction.START_DIGGING, pos.translate(), BlockFace.UP));
			client.getSession().send(new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), BlockFace.UP));
		}
	}

}

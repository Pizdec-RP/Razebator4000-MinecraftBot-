package net.PRP.MCAI.bot.specific;

import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;

import java.util.ArrayList;
import java.util.List;

import com.github.steveice10.mc.protocol.data.game.entity.player.CombatState;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class PVP extends SessionAdapter {
	
	public int enemy = -1;
	public Bot client;
	public int cooldownticks = 0;
	public int pvpticks = 0;
	public CombatState state = CombatState.END_COMBAT;
	public List<Integer> swords = new ArrayList<Integer>() {
		private static final long serialVersionUID = 5529429801037940521L;

	{
		add(268);
		add(272);
		add(283);
		add(267);
		add(276);
	}};
	
	public PVP(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		
	}
	
	@Override
    public void disconnected(DisconnectedEvent event) {
		
	}
	
	public void endPVP() {
		pvpticks = 0;
		enemy = -1;
		state = CombatState.END_COMBAT;
		client.rl.enemy = null;
	}
	
	public void tick() {
		try {
		//System.out.println("atacking: "+enemy+" state:"+state+" toen:"+VectorUtils.sqrt(client.getWorld().Entites.get(enemy).Position, client.getPosition()));
		if (state == CombatState.ENTER_COMBAT) {
			if (pvpticks > 1200) {
				endPVP();
				return;
			}
			
			Entity tempenemy = client.getWorld().Entites.get(enemy);
			if (tempenemy == null || !tempenemy.alive || enemy == -1) {
				endPVP();
				return;
			}
			
			if (VectorUtils.sqrt(tempenemy.Position, client.getPosition()) > 4) {
				if (VectorUtils.sqrt(tempenemy.Position, client.getPosition()) <= 10)  {
					if (client.pathfinder.testForPath(tempenemy.Position)) {
						client.pathfinder.setup(tempenemy.Position);
						//client.rl.state = raidState.GOING;
						//client.rl.asd = null;
						return;
					} else {
						Vector3D pos = VectorUtils.func_31(client, client.getPositionInt(), 3);
						if (pos == null) {
							endPVP();
							return;
						} else {
							client.pathfinder.setup(pos);
							//client.rl.asd = null;
							//client.rl.state = raidState.GOING;
							return;
						}
					}
				} else {
					endPVP();
					return;
				}
			}
			
			//continuepvp
			pvpticks++;
			prepareitem();
			cooldownticks = 20;
			BotU.LookHead(client, tempenemy.Position);
			client.getSession().send(new ClientPlayerInteractEntityPacket(enemy, InteractAction.ATTACK, false));
			client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
		}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			reset();
			System.out.println("2");
		}
	}
	
	public void pvp(int entityId) {
		enemy = entityId;
		state = CombatState.ENTER_COMBAT;
		pvpticks = 0;
	}
	
	public void reset() {
		this.enemy = -1;
		this.cooldownticks = 0;
		this.state = CombatState.END_COMBAT;
		this.pvpticks = 0;
	}
	
	public void prepareitem() {
		for (int sword : swords) {
			client.setToSlotInHotbarWithItemId(sword);
		}
	}
}

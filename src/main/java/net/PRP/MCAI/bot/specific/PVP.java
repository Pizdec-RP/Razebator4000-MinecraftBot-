package net.PRP.MCAI.bot.specific;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.CombatState;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class PVP {
	
	public int enemy = -1;
	public Bot client;
	public int cooldownticks = 0;
	public int pvpticks = 0;
	public CombatState state = CombatState.ENTER_COMBAT;
	boolean folowEnemy = false;
	public int maxPos = 25;
	boolean shieldmode = false;
	boolean shieldCovered = false;
	
	public List<Integer> swords = new ArrayList<Integer>() {
		private static final long serialVersionUID = 5529429801037940521L;
	{
		add(583);
		add(588);
		add(593);
		add(598);
		add(603);
		add(608);
	}};
	
	public PVP(Bot client) {
		this.client = client;
	}
	
	public void endPVP() {
		lowerShield();
		pvpticks = 0;
		enemy = -1;
		state = CombatState.END_COMBAT;
		folowEnemy = false;
		shieldmode = false;
	}
	
	public void tick() {
		//System.out.println("atacking: "+enemy+" state:"+state+" toen:"+VectorUtils.sqrt(client.getWorld().Entites.get(enemy).Position, client.getPosition()));
		if (state == CombatState.ENTER_COMBAT) {
			
			if (!shieldmode) {
				if (client.playerInventory.contain("shield")) {
					if (client.crafter.windowType != null) {
						client.getSession().send(new ClientCloseWindowPacket(client.playerInventory.currentWindowId));
					}
					int shieldslot = client.playerInventory.getSlotWithItem("shield");
					if (shieldslot != 45) {
						client.crafter.fromSlotToSlotStack(shieldslot, 45);
						shieldmode = true;
					} else {
						shieldmode = true;
					}
				} else if (client.playerInventory.getSlot(45) != null && client.playerInventory.getSlot(45).getId() == 897) {
					shieldmode = true;
				}
			} else {
				if (client.playerInventory.getSlot(45) != null && client.playerInventory.getSlot(45).getId() == 897) {
					shieldmode = true;
				} else {
					shieldmode = false;
				}
			}
			
			++pvpticks;
			if (pvpticks > 3000) {
				endPVP();
				return;
			}
			
			Entity tempenemy = client.getWorld().Entities.get(enemy);
			if (tempenemy == null || !tempenemy.alive || enemy == -1) {
				endPVP();
				return;
			}
			double toEnemy = VectorUtils.sqrt(tempenemy.pos, client.getPosition());
			
			if (folowEnemy) {
				if (client.pathfinder.state == net.PRP.MCAI.bot.pathfinder.PathExecutor.State.FINISHED) {
					folowEnemy = false;
					return;
				} else if (client.pathfinder.state == net.PRP.MCAI.bot.pathfinder.PathExecutor.State.SEARCHING) {
					BotU.LookHead(client, tempenemy.pos);
					client.pm.Walk();
				}
				
				if (VectorUtils.sqrt(client.pathfinder.end, tempenemy.pos) > 5) {
					client.pathfinder.finish("pvp-not relevant pos");
				}
			} else {
				BotU.LookHead(client, tempenemy.pos);
			}
			if (!folowEnemy) if (toEnemy > 4) {
				
				if (toEnemy <= maxPos)  {
					
					if (toEnemy >= 6) {
						lowerShield();
					} else {
						holdShield();
					}
					
					if ((int)tempenemy.pos.y == (int)client.posY) {
						BotU.LookHead(client, tempenemy.pos.add(0,1,0));
						client.pm.Walk();
					} else {
						if (client.pathfinder.testForPath(tempenemy.pos)) {
							client.pathfinder.setup(tempenemy.pos);
							folowEnemy = true;
							return;
						} else {
							Vector3D pos = VectorUtils.randomPointInRaduis(client, 1, 2);
							if (pos == null || VectorUtils.sqrt(pos, tempenemy.pos) > 4) {
								endPVP();
								return;
							} else {
								client.pathfinder.setup(pos);
								folowEnemy = true;
								return;
							}
						}
					}
				} else {
					endPVP();
					return;
				}
			}
			
			/*for (Entry<Integer, Entity> entity : client.getWorld().Entites.entrySet()) {
				if (entity.getValue().type == EntityType.ARROW) {
					if (client.distance(entity.getValue().pos) <= 3) {
						holdShield();
						return;
					}
				}
			}*/
			
			if (cooldownticks >= 0) {
				if (toEnemy < 0.5) {
					hit(tempenemy);
					return;
				}
				--cooldownticks;
				if (cooldownticks == 5 && !folowEnemy) {
					client.pm.jump();
				}
				return;
			}
			if (toEnemy <= 4) hit(tempenemy);
		}
	}
	
	public void holdShield() {
		if (!shieldmode || client.isHoldSlowdownItem) return;
		client.getSession().send(new ClientPlayerUseItemPacket(Hand.OFF_HAND));
		client.isHoldSlowdownItem = true;
	}
	
	public void lowerShield() {
		if (!shieldmode || !client.isHoldSlowdownItem) return;
		client.getSession().send(new ClientPlayerActionPacket(PlayerAction.RELEASE_USE_ITEM, new Position(0,0,0),BlockFace.UP));
		client.isHoldSlowdownItem = false;
	}
	
	public void hit(Entity tempenemy) {
		lowerShield();
		prepareitem();
		cooldownticks = 10;
		BotU.LookHead(client, tempenemy.pos);
		client.getSession().send(new ClientPlayerInteractEntityPacket(enemy, InteractAction.ATTACK, false));
		client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
		holdShield();
	}
	
	public void pvp(int entityId) {
		enemy = entityId;
		state = CombatState.ENTER_COMBAT;
		pvpticks = 0;
	}
	
	public void reset() {
		lowerShield();
		enemy = -1;
		cooldownticks = 0;
		state = CombatState.END_COMBAT;
		pvpticks = 0;
		folowEnemy = false;
		shieldmode = false;
		shieldCovered = false;
	}
	
	public void prepareitem() {
		for (int sword : swords) {
			client.setToSlotInHotbarWithItemId(sword);
		}
	}
}

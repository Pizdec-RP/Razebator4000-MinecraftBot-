package net.PRP.MCAI.bot.specific;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.github.steveice10.mc.protocol.data.game.entity.player.CombatState;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.bot.pathfinder.AStar.State;
import net.PRP.MCAI.data.BlockData;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.materialsBreakTime;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class Miner {
	
	private Bot client;
	private Vector3D pos;
	public bbmct state = bbmct.ENDED;
	private int d1 = 0;
	public int ticksToBreak = 0;
	private Entity droppedItem = null;
	private Vector3D beforePos = null;
	private Map<Integer, Entity> te = new HashMap<>();
	
	public Miner(Bot client) {
		setBlockPos(new Vector3D(0,0,0));
		this.client = client;
	}
	
	public enum bbmct {
		STARTED, IN_PROGRESS, WAITFORDROPITEM, PATHINGTODROP, GOINGTODROPEDITEM, ENDED;
	}
	
	public void reset() {
		pos = new Vector3D(0,0,0);
		d1 = 0;
		ticksToBreak = 0;
		state = bbmct.ENDED;
		droppedItem = null;
		beforePos = null;
	}
	
	@SuppressWarnings("deprecation")
	public void tick() {
		if (state == bbmct.ENDED) return;
		if (!client.isOnline() || client.pvp.state != CombatState.END_COMBAT || pos == null) {
			reset();
			return;
		}
		if (state == bbmct.STARTED) {
			client.pathfinder.ignored.add(pos);
			BotU.LookHead(client, pos);
			prepareitem();
			client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
			client.getSession().send(new ClientPlayerActionPacket(PlayerAction.START_DIGGING, pos.translate(), BlockFace.UP));
			ticksToBreak = (int) Math.floor(calculateBreakTime());
			if (ticksToBreak == 1.0) {
				FinishDiggingAGTI();
				return;
			} else {
				state = bbmct.IN_PROGRESS;
				return;
			}
		} else if (state == bbmct.IN_PROGRESS) {
			if (pos == null) {endDigging(); return;}
			if (!(VectorUtils.sqrt(pos, client.getEyeLocation()) <= (int)Main.gamerule("maxpostoblock"))) {
				endDigging();
				return;
			}
			d1++;
			if (d1 >= 3) {
				client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
				d1 = 0;
			}
			
			if (pos.getBlock(client).type == Type.AIR) {
				client.getSession().send(new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), BlockFace.UP));
				state = bbmct.ENDED;
				return;
			}
			
			ticksToBreak--;
			if (ticksToBreak <= 0) {
				FinishDiggingAGTI();
				return;
			}
		} else if (state == bbmct.WAITFORDROPITEM) {
			te.clear();
			te.putAll(client.getWorld().Entites);
			if (droppedItem == null) {
				for (Entry<Integer, Entity> entry : te.entrySet()) {
					if (entry.getValue().type == EntityType.ITEM && Math.floor(entry.getValue().Position.x) == Math.floor(pos.x) && Math.floor(entry.getValue().Position.z) == Math.floor(pos.z)) {
						droppedItem = entry.getValue();
						return;
					}
				}
			} else {
				if (beforePos == null) {
					beforePos = droppedItem.Position;
					return;
				} else {
					if (beforePos.y == droppedItem.Position.y) {
						state = bbmct.PATHINGTODROP;
					}
				}
			}
			
		} else if (state == bbmct.PATHINGTODROP) {
			if (client.pathfinder.testForPath(beforePos)) {
				state = bbmct.GOINGTODROPEDITEM;
				client.pathfinder.setup(beforePos);
				return;
			} else {
				Vector3D poss = VectorUtils.func_31(client, beforePos, (int)Main.gamerule("maxpostoblock"));
				if (pos == null) {
					state = bbmct.ENDED;
					return;
				}
				state = bbmct.GOINGTODROPEDITEM;
				client.pathfinder.setup(poss);
				return;
			}
		} else if (state == bbmct.GOINGTODROPEDITEM) {
			System.out.println(2);
			if (client.pathfinder.state == State.FINISHED) {state = bbmct.ENDED; droppedItem = null; beforePos = null; return;}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void endDigging() {
		if (Main.debug) System.out.println("mining ended");
		client.getSession().send(new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), BlockFace.UP));
		state = bbmct.ENDED;
		droppedItem = null;
		beforePos = null;
	}
	
	@SuppressWarnings("deprecation")
	public void FinishDiggingAGTI() {
		if (Main.debug) System.out.println("mining ended");
		client.getSession().send(new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), BlockFace.UP));
		state = bbmct.ENDED;
		droppedItem = null;
		beforePos = null;
		/*System.out.println(3);
		if (Main.debug) System.out.println("mining ended");
		client.getSession().send(new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), BlockFace.UP));
		state = bbmct.WAITFORDROPITEM;
		droppedItem = null;
		beforePos = null;*/
	}
	
	public void setup(Vector3D block) {
		client.bbm.setBlockPos(block);
		client.bbm.state = bbmct.STARTED;
	}
	
	public void prepareitem() {
		BlockData blockdata = Main.getMCData().blockData.get(pos.getBlock(client).id);
		List<materialsBreakTime> mtm = Main.getMCData().materialToolMultipliers.get(blockdata.material);
		if (mtm != null) {for (materialsBreakTime item : mtm) {
			//System.out.println(client.getItemInHand().getId()+" != "+item.toolId);
			if (client.getItemInHand().getId() == item.toolId) return;
		}
		for (materialsBreakTime item : mtm) {
			client.setToSlotInHotbarWithItemId(item.toolId);
		}}
	}

	public Vector3D getBlockPos() {
		return pos;
	}

	public void setBlockPos(Vector3D blockPos) {
		pos = blockPos;
	}
	
	public Double calculateBreakTime() {
		BlockData blockdata = Main.getMCData().blockData.get(pos.getBlock(client).id);
		List<materialsBreakTime> mtm = Main.getMCData().materialToolMultipliers.get(blockdata.material);
		double materialToolMultipliers = func_12(mtm);
		//boolean isBestTool = client.getItemInHand() == null && materialToolMultipliers != 0 && materialToolMultipliers[heldItemType]
		
		double blockBreakingSpeed = 1.0;//default
		
		if (materialToolMultipliers > 0) {
			blockBreakingSpeed = materialToolMultipliers;
		}
		/*if (isBestTool) {
		      blockBreakingSpeed = 
		}*/
		
		int efficiencyLevel = 0;//getEnchantmentLevel();
		if (efficiencyLevel > 0 && blockBreakingSpeed > 1.0) {
		      blockBreakingSpeed += efficiencyLevel * efficiencyLevel + 1;
		}
		
		int hasteLevel = Math.max(
        client.effects.haste,
        client.effects.conduitPower);
		
		if (hasteLevel > 0) {
		    blockBreakingSpeed *= 1 + (0.2 * hasteLevel);
		}

	    if (client.effects.miningFatigue > 0) {
	      blockBreakingSpeed *= client.getMiningFatigueMultiplier();
	    }
	    
	    int aquaAffinityLevel = 0;//getEnchantmentLevel('aqua_affinity', enchantments)

	    if (client.isInWater() && aquaAffinityLevel == 0) {
	      blockBreakingSpeed /= 5.0;
	    }
	    
	    if (!client.onGround) {
	        blockBreakingSpeed /= 5.0;
	    }
	    
	    double blockHardness = blockdata.hardness;
	    double matchingToolMultiplier = canHarvest(blockdata, mtm) ? 30.0 : 100.0;
	    
	    double blockBreakingDelta = blockBreakingSpeed / blockHardness / matchingToolMultiplier;
	    
	    if (blockHardness == -1.0) {
	        blockBreakingDelta = 0.0;
	    }
	    
	    if (blockBreakingDelta == 0.0) {
	        return null;
	    }
	    
	    if (blockBreakingDelta >= 1.0) {
	        return 0.0;
	    }
	    
	    double ticksToBreakBlock = Math.ceil(1.0 / blockBreakingDelta);
	    return ticksToBreakBlock / 3;
	}
	
	public boolean func_1(List<materialsBreakTime> asd) {
		if (client.getItemInHand() == null) return false;
		for (materialsBreakTime a : asd) {
			if (client.getItemInHand().getId() == a.toolId) {
				return true;
			}
		}
		return false;
	}
	
	public double func_12(List<materialsBreakTime> asd) {
		if (asd == null) return 0;
		if (client.getItemInHand() == null) return 0;
		for (materialsBreakTime a : asd) {
			if (client.getItemInHand().getId() == a.toolId) {
				return a.multipiler;
			}
		}
		return 0;
	}
	
	public boolean canHarvest(BlockData data, List<materialsBreakTime> mtm) {
		if (data.material.equalsIgnoreCase("default")) return true;
		return client.getItemInHand() == null && !data.material.equalsIgnoreCase("default") && func_1(mtm);
	}
	
}

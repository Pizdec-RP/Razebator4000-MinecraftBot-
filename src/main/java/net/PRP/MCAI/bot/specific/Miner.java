package net.PRP.MCAI.bot.specific;

import java.util.List;
import com.github.steveice10.mc.protocol.data.game.entity.player.CombatState;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.BlockData;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.materialsBreakTime;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class Miner {
	
	private Bot client;
	private Vector3D pos;
	public bbmct state = bbmct.ENDED;
	public int ticksToBreak = 0;
	
	public Miner(Bot client) {
		setBlockPos(new Vector3D(0,0,0));
		this.client = client;
	}
	
	public enum bbmct {
		STARTED, IN_PROGRESS, ENDED;
	}
	
	public void reset() {
		client.getSession().send(new ClientPlayerActionPacket(PlayerAction.CANCEL_DIGGING, pos.translate(), VectorUtils.rbf(client, pos.add(0.5, 0.5, 0.5))));
		pos = new Vector3D(0,0,0);
		ticksToBreak = 0;
		state = bbmct.ENDED;
	}
	
	@SuppressWarnings("deprecation")
	public void tick() {
		if (state == bbmct.ENDED) return;
		if (!client.isOnline() || client.pvp.state != CombatState.END_COMBAT || pos == null) {
			reset();
			return;
		}
		if (state == bbmct.STARTED) {
			if (pos.getBlock(client).type == Type.AIR || pos.getBlock(client).type == Type.LIQUID || pos.getBlock(client).type == Type.UNBREAKABLE) {
				BotU.log("a");
				state = bbmct.ENDED;
				return;
			}
			//client.pathfinder.ignored.add(pos);
			BotU.LookHead(client, pos);
			prepareitem();
			client.cursor.swingArm();
			client.getSession().send(new ClientPlayerActionPacket(PlayerAction.START_DIGGING, pos.translate(), VectorUtils.rbf(client, pos.add(0.5, 0.5, 0.5))));
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
			BotU.LookHead(client, pos);
			client.cursor.swingArm();
			
			if (pos.getBlock(client).type == Type.AIR || pos.getBlock(client).type == Type.LIQUID || pos.getBlock(client).type == Type.UNBREAKABLE) {
				client.getSession().send(new ClientPlayerActionPacket(PlayerAction.CANCEL_DIGGING, pos.translate(), VectorUtils.rbf(client, pos.add(0.5, 0.5, 0.5))));
				state = bbmct.ENDED;
				return;
			}
			
			ticksToBreak--;
			if (ticksToBreak <= 0) {
				FinishDiggingAGTI();
				return;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void endDigging() {
		if (Main.debug) System.out.println("mining ended");
		client.getSession().send(new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), VectorUtils.rbf(client, pos.add(0.5, 0.5, 0.5))));
		state = bbmct.ENDED;
	}
	
	@SuppressWarnings("deprecation")
	public void FinishDiggingAGTI() {
		if (Main.debug) System.out.println("mining ended");
		client.getSession().send(new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), VectorUtils.rbf(client, pos.add(0.5, 0.5, 0.5))));
		state = bbmct.ENDED;
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
	
	@SuppressWarnings("unchecked")
	public Double calculateBreakTime() {
		BlockData blockdata = Main.getMCData().blockData.get(pos.getBlock(client).id);
		List<materialsBreakTime> mtm = Main.getMCData().materialToolMultipliers.get(blockdata.material);
		
		double materialToolMultipliers = getToolMultipiler(mtm);
		//BotU.log("mtm: "+materialToolMultipliers);
		
		double blockBreakingSpeed = 1.0;//default
		
		//boolean isBestTool = client.getItemInHand() != null && materialToolMultipliers > 0 && materialToolMultipliers[heldItemType];
		
		if (materialToolMultipliers > 0) {
			blockBreakingSpeed = materialToolMultipliers;
		}
		
		int efficiencyLevel = 0;
		if (client.getItemInHand().getNbt() != null) {
			for (Tag nbt : client.getItemInHand().getNbt()) {
				if (nbt.getName().equals("Enchantments")) {
					List<CompoundTag> val = (List<CompoundTag>)nbt.getValue();
					for (CompoundTag ct : val) {
						StringTag st = (StringTag) ct.getValue().get("id");
						if (st.getValue().contains("efficiency")) {
							if (ct.getValue().get("lvl") instanceof ShortTag) {
								efficiencyLevel = ((ShortTag) ct.getValue().get("lvl")).getValue();
							} else if (ct.getValue().get("lvl") instanceof IntTag) {
								efficiencyLevel = ((IntTag) ct.getValue().get("lvl")).getValue();
							}
						}
					}
				}
			}
		}
		if (efficiencyLevel > 0 && blockBreakingSpeed > 1.0) {
		      blockBreakingSpeed += efficiencyLevel * efficiencyLevel + 1;
		}
		//BotU.log("bbs after eff: "+blockBreakingSpeed);
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
	    	//BotU.log("client in water");
	      blockBreakingSpeed /= 5.0;
	    }
	    
	    if (!client.onGround) {
	    	//BotU.log("client not on ground");
	        blockBreakingSpeed /= 5.0;
	    }
	    
	    double blockHardness = blockdata.hardness;
	    //BotU.log("hardness: "+blockHardness);
	    double matchingToolMultiplier = canHarvest(blockdata, mtm) ? 30.0 : 100.0;
	    //BotU.log("matchingToolMultiplier: "+matchingToolMultiplier);
	    double blockBreakingDelta = blockBreakingSpeed / blockHardness / matchingToolMultiplier;
	    //BotU.log("blockBreakingDelta: "+blockBreakingDelta);
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
	    
	    return ticksToBreakBlock;
	}
	
	public boolean isItRightTool(BlockData data) {
		if (data.harvestTools.get(client.getItemInHand().getId())) {
			return true;
		} else {
			return false; 
		}
	}
	
	public double getToolMultipiler(List<materialsBreakTime> asd) {
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
		//BotU.log("material: "+data.material);
		if (data.harvestTools == null) {
			//BotU.log("can harvest: true");
			return true;
		}
		return client.getItemInHand() == null && data.harvestTools != null && isItRightTool(data);
		//BotU.log("can harvest: "+a);
		//return a;
	}
	
}

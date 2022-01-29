package net.PRP.MCAI.bot;

import java.util.List;

import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.data.BlockData;
import net.PRP.MCAI.data.materialsBreakTime;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.Vector3D;
import net.PRP.MCAI.utils.VectorUtils;

public class BlockBreakManager {
	
	public Bot client;
	private Vector3D pos;
	public boolean readyToBreak = false;
	//public BlockBreakStage breakPhase;
	public bbmct state = bbmct.ENDED;
	public int d1 = 0;
	public int ticksToBreak = 0;
	
	public BlockBreakManager(Bot client) {
		setBlockPos(new Vector3D(0,-1,0));
		this.client = client;
	}
	
	public enum bbmct {
		STARTED, IN_PROGRESS, ENDED, ABORT;
	}
	
	public void reset() {
		this.pos = Vector3D.ORIGIN;
		this.d1 = 0;
		this.ticksToBreak = 0;
		this.state = bbmct.ENDED;
		this.readyToBreak = false;
	}
	
	@SuppressWarnings("deprecation")
	public void tick() {
		try {
			if (!readyToBreak) return;
			//System.out.println("blockid:"+pos.getBlock(client).id+" bbm pos:"+pos.toStringInt()+" readyToBreak:"+readyToBreak+" ticksToBreak:"+ticksToBreak+" state:"+state);
			BotU.LookHead(client, pos);
			if (pos.getBlock(client).id == 0) {
				//System.out.println("4");
				client.getSession().send(new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), BlockFace.UP));
				state = bbmct.ENDED;
				readyToBreak = false;
			}
			if (state == bbmct.STARTED) {
				prepareitem();
				client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
				client.getSession().send(new ClientPlayerActionPacket(PlayerAction.START_DIGGING, pos.translate(), BlockFace.UP));
				this.ticksToBreak = (int) Math.floor(calculateBreakTime());
				if (ticksToBreak == 1.0) {
					//System.out.println("3");
					endDigging();
				} else {
					this.state = bbmct.IN_PROGRESS;
				}
			} else if (this.state == bbmct.IN_PROGRESS) {
				if (VectorUtils.sqrt(pos, client.getPosition()) > 3.8) {
					endDigging();
				}
				d1++;
				if (d1 >= 3) {
					client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
					d1 = 0;
				}
				
				if (pos.getBlock(client).id == 0) {
					//System.out.println("1");
					endDigging();
				}
				
				this.ticksToBreak--;
				if (ticksToBreak <= 0) {
					//System.out.println("2");
					endDigging();
				}
			} else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void endDigging() {
		client.getSession().send(new ClientPlayerActionPacket(PlayerAction.FINISH_DIGGING, pos.translate(), BlockFace.UP));
		this.readyToBreak = false;
		this.state = bbmct.ENDED;
	}
	
	public void setup(Vector3D block) {
		client.bbm.setBlockPos(block);
		client.bbm.state = bbmct.STARTED;
		client.bbm.setReadyToBreak(true);
	}
	
	public void prepareitem() {
		BlockData blockdata = Main.getMCData().blockData.get(pos.getBlock(client).id);
		List<materialsBreakTime> mtm = Main.getMCData().materialToolMultipliers.get(blockdata.material);
		for (materialsBreakTime item : mtm) {
			//System.out.println(client.getItemInHand().getId()+" != "+item.toolId);
			if (client.getItemInHand().getId() == item.toolId) return;
		}
		for (materialsBreakTime item : mtm) {
			client.setToSlotInHotbarWithItemId(item.toolId);
		}
		//System.out.println("hand");
	}

	public Vector3D getBlockPos() {
		return pos;
	}

	public void setBlockPos(Vector3D blockPos) {
		this.pos = blockPos;
	}

	public boolean isReadyToBreak() {
		return readyToBreak;
	}
	public void setReadyToBreak(boolean readyToBreak) {
		this.readyToBreak = readyToBreak;
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
	    double matchingToolMultiplier = this.canHarvest(blockdata, mtm) ? 30.0 : 100.0;
	    
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

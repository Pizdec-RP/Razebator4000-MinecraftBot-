package net.PRP.MCAI.bot.specific;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.entity.player.CombatState;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.bot.pathfinder.AStar.State;
import net.PRP.MCAI.bot.specific.BlockBreakManager.bbmct;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.*;

public class LivingListener extends SessionAdapter {
	
	private Bot client;
	private boolean firstJoin = false;
	public raidState state = raidState.IDLE;
	public Vector3D asd = Vector3D.ORIGIN;
	public List<Vector3D> blacklist = new CopyOnWriteArrayList<>();
	public boolean trusted = false;
	private int sleepticks = (int)Main.getsett("walkeverymilseconds") / 50;
	public Integer enemy = null;
	
	public int tickstocheck = 0;
	
	public List<EntityType> badentities = new ArrayList<>() {/**
		 * 
		 */
		private static final long serialVersionUID = -6373621458088442703L;

	{
		add(EntityType.ZOMBIE);
		add(EntityType.CAVE_SPIDER);
		add(EntityType.SPIDER);
		add(EntityType.WITCH);
		add(EntityType.MAGMA_CUBE);
		add(EntityType.ENDERMITE);
		add(EntityType.CREEPER);
		add(EntityType.PHANTOM);
		add(EntityType.SHULKER);
		add(EntityType.SLIME);
		add(EntityType.DROWNED);
		add(EntityType.SILVERFISH);
		add(EntityType.VEX);
		add(EntityType.VINDICATOR);
	}};

	public LivingListener(Bot client) {
        this.client = client;
        this.trusted = (boolean) Main.getsett("living");
    }
	
	public enum raidState {
		IDLE, GOING, MINING, WAIT;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	if (firstJoin) return;
        	ThreadU.sleep((int) Main.getsett("timebeforeraidon"));
        	firstJoin = true;
        	if ((boolean) Main.getsett("raidspam")) {
				new Thread(()-> {
					while (true) {
						if (client.isOnline() && Main.pasti.size() > 0 && (boolean)Main.getsett("raidspam")) {
	                        int rand = MathU.rnd(0, Main.pasti.size()-1);
	                        String pasta = (String)Main.pasti.get(rand);
	                        BotU.chat(this.client, pasta);
	                        int sr = 1;
	                        sr = (int) Main.getsett("spamrange");
	                        ThreadU.sleep(sr);
	                    } else {
	                    	ThreadU.sleep(5000);
	                    }
					}
				}).start();
				
			}
        } else if (receiveEvent.getPacket() instanceof ServerMapDataPacket) {
        	//final ServerMapDataPacket p = (ServerMapDataPacket) receiveEvent.getPacket();
        }
	}
	
	public void tick() {
		//try {
			if (!firstJoin || !client.isOnline()) return;
			if (state == raidState.IDLE) {
				if (!this.trusted) return;
				if (client.pathfinder.state == State.WALKING) return;
				
				if (client.getPositionInt().getBlock(client).ishard()) {
					state = raidState.MINING;
					client.bbm.setup(client.getPositionInt());
					return;
				} else if (client.getPositionInt().add(0,1,0).getBlock(client).ishard()) {
					state = raidState.MINING;
					client.bbm.setup(client.getPositionInt());
					return;
				}
				
				if (sleepticks > 0) {
					sleepticks--;
					return;
				}
				if ((int)Main.getsett("walkeverymilseconds") >= 50) sleepticks = (int)Main.getsett("walkeverymilseconds") / 50;
				if (client.pvp.state != CombatState.END_COMBAT) {
					return;
				}
				if ((int)Main.getsett("walkeverymilseconds") != 0) sleepticks = (int)Main.getsett("walkeverymilseconds") / 50;
				
				tickstocheck++; if (tickstocheck > 5) { tickstocheck = 0;
					
				Map<Integer, Entity> tempentities = client.getWorld().Entites;
				for(Entry<Integer, Entity> entry : tempentities.entrySet()) {
					badentities.forEach((entity)->{
						if (entity == entry.getValue().type) {
							if (enemy == null) {
								enemy = entry.getValue().EntityID;
							} else if (tempentities.get(enemy).alive == true && VectorUtils.sqrt(tempentities.get(enemy).Position,client.getPosition()) > VectorUtils.sqrt(tempentities.get(entry.getValue().EntityID).Position, client.getPosition())) {
								enemy = entry.getValue().EntityID;
							}
						}
					});
				}
				
				if (enemy != null) {
					//System.out.println("5 "+tempentities.get(enemy).Position.toStringInt());
					if (VectorUtils.sqrt(tempentities.get(enemy).Position, client.getPosition()) <= 4 && tempentities.get(enemy).alive == true) {
					
					client.pvp.pvp(enemy);
					enemy = null;
					return;
				}}
				
				
				
				for(Entry<Integer, Entity> entry : tempentities.entrySet()) {
					if (entry.getValue().type == EntityType.PLAYER && entry.getValue().uuid != client.getUUID() && VectorUtils.equalsInt(entry.getValue().Position, client.getPositionInt())) {
						Vector3D pos = VectorUtils.func_31(client, client.getPositionInt(), 5);
						client.pathfinder.setup(pos);
						return;
					}
				}}
				
				if (client.ztp && VectorUtils.sqrt(client.getPositionInt(), client.targetpos) < client.targetradius) {
					Vector3D pos = VectorUtils.func_31(client, client.targetpos, client.targetradius);
					if (pos == null) state = raidState.IDLE;
					client.pathfinder.setup(pos);
			    	this.state = raidState.GOING;
				} else {
					if ((boolean) Main.getsett("mining")) {
						state = raidState.WAIT;
						Vector3D block;
						if ((boolean) Main.getsett("iol")) {
							@SuppressWarnings("unchecked")
							List<Integer> d1 = (ArrayList<Integer>)Main.getsett("minertargetid");
							block = VectorUtils.findNearestBlockByArrayId(client, d1, this.blacklist);
							if (block == null) block = VectorUtils.func_1488(client, d1, this.blacklist);
						} else {
							@SuppressWarnings("unchecked")
							List<String> d2 = (ArrayList<String>)Main.getsett("minetargetnames");
							block = VectorUtils.func_32(client, d2, this.blacklist);
						}
						if (block == null) {//no one block founded
							for (Bot cli : Main.bots) {
								if (cli.rl.state == raidState.MINING) {
									if (client.pathfinder.testForPath(cli.getPositionInt())) {
										client.pathfinder.setup(cli.getPositionInt());
										this.state = raidState.GOING;
										this.asd = null;
										break;
									} 
								}
							}
							//i dont know what to do
							Vector3D to = VectorUtils.func_31(client, client.getPositionInt(), 8);
							this.sleepticks = 100;
							client.pathfinder.setup(to);
							asd = null;
							this.state = raidState.GOING;
							return;
						}
						if (block.getBlock(client).touchLiquid(client)) {
							this.blacklist.add(block);
							state = raidState.IDLE;
							return;
						}
						if (VectorUtils.sqrt(client.getPosition(), block) <= 5) {
							client.bbm.setup(block);
							this.state = raidState.MINING;
					    } else {
					    	Vector3D pos = VectorUtils.func_31(client, block, 5);
					    	if (pos == null) {
					    		this.blacklist.add(block);
					    		state = raidState.IDLE;
					    		return;
					    	}
					    	this.asd = block;
					    	client.pathfinder.setup(pos);
					    	this.state = raidState.GOING;
					    }
					}
				}
			} else if (state == raidState.GOING) {
				if (client.pathfinder.state == State.FINISHED) {
					if (asd == null) {
						this.state = raidState.MINING;
						client.bbm.setup(asd);
						asd = null;
					} else {
						state = raidState.IDLE;
					}
				}
			} else if (state == raidState.MINING) {
				if (client.bbm.state == bbmct.ENDED) {
					this.asd = null;
					this.state = raidState.IDLE;
				} else {
					if (client.bbm.ticksToBreak < -200) {
						this.asd = null;
						client.bbm.endDigging();
						this.blacklist.add(client.bbm.getBlockPos());
						this.state = raidState.IDLE;
					}
				}
			}
		//} catch (Exception e) {
			//e.printStackTrace();
			//System.out.println("1");
		//}
	}
}

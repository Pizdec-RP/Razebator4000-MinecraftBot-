package net.PRP.MCAI.bot.specific;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.player.CombatState;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.ListenersForServers.ServerListener;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.bot.pathfinder.PathExecutor.State;
import net.PRP.MCAI.bot.specific.Miner.bbmct;
import net.PRP.MCAI.bot.specific.Crafting.crState;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.StringU;

public class Living extends SessionAdapter {
	
	private Bot client;
	private boolean firstJoin = false;
	public raidState state = raidState.IDLE;
	public Vector3D mineAfterWalk = null;
	public List<Vector3D> blacklist = new CopyOnWriteArrayList<>();
	public boolean trusted;
	private int sleepticks = (int)Main.getset("walkeverymilseconds") / 50;
	public Entity target = null; 
	public int tickstocheck = 0;
	public short stage = 0;
	public List<String> tasklist = new ArrayList<>();
	public List<ServerListener> listeners = new ArrayList<>();
	Integer pEnemy = null;
	Integer enemy = null;
	public boolean a = false;
	
	public List<EntityType> badentities = new ArrayList<EntityType>() {
		private static final long serialVersionUID = -6373621458088442703L;

	{
		add(EntityType.ZOMBIE);
		add(EntityType.ZOGLIN);
		add(EntityType.ZOMBIFIED_PIGLIN);
		add(EntityType.PIGLIN);
		add(EntityType.PIGLIN_BRUTE);
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
		add(EntityType.ZOMBIE_VILLAGER);
		add(EntityType.SKELETON);
		add(EntityType.EVOKER);
		add(EntityType.WITHER);
		add(EntityType.ENDER_DRAGON);
		add(EntityType.ENDERMAN);
		add(EntityType.HUSK);
	}};
	int spamticks = 0;
	public int goforwardticks = 0;
	
	int degradationLvl = 0;
	String doubledtask = "";

	public Living(Bot client) {
        this.client = client;
        this.trusted = (boolean) Main.getset("living");
        if (client.automaticMode) {
        	switch (MathU.rnd(1, 3)) {
        		case 1:
        			tasklist.add("goforwardwithangle 30");
    	        	tasklist.add("goforwardwithangle 30");
    	        	tasklist.add("goforwardwithangle 30");
        			break;
        		case 2:
        			tasklist.add("goforwardwithangle 30");
        			break;
        	}
        }
    }
	
	public enum raidState {
		IDLE, GOING, MINING, CRAFTING, GOFORWARD, PVP, FOLLOW;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	if (firstJoin) return;
        	sleepticks += (int) Main.getset("timebeforeraidon")/50;
        	firstJoin = true;
        	spamticks = (int) Main.getset("spamrange") / 50;
        }
	}
	
	@SuppressWarnings("unchecked")
	public void tick() {
		//try {
			if (!firstJoin || !client.isOnline()) return;
			if (!this.trusted) return;
			if (sleepticks > 0) {sleepticks--;return;}
			
			if (client.health <= 0) {
				client.getSession().send(new ClientRequestPacket(ClientRequest.RESPAWN));
				return;
			}
			
			if (client.isInWater()) {
				client.pm.jump();
			}
			
			
			if ((boolean) Main.getset("raidspam") && Main.pasti.size() > 0) {
				spamticks--;
				if (spamticks <= 0) {
					spamticks =  (int) Main.getset("spamrange") / 50;
	                String pasta = (String)Main.pasti.get(MathU.rnd(0, Main.pasti.size()-1));
	                
            		while (pasta.contains("=rel=")) {
            			pasta = pasta.replaceFirst("=rel=", StringU.RndLetter());
            		}
            		while (pasta.contains("=rrl=")) {
            			pasta = pasta.replaceFirst("=rrl=", StringU.RndRuLetter());
            		}
            		BotU.chat(this.client, pasta);
            		//BotU.log("chat: ("+client.getHost()+") "+pasta);
				}
			}
			
			if (!listeners.isEmpty()) {
				for (ServerListener listener : listeners) {
					listener.tick();
					if (listener.allGameCapt) return;
				}
	
			}
			
			if (state == raidState.IDLE) {
				if (a) {
					Entity t = client.getWorld().getNearestIfContain("mineflayer");
					if (t != null) {
						client.pvp.pvp(t.eid);
						state = raidState.PVP;
						return;
					}
				}
				
				if ((boolean) Main.getset("isitfollow")) {
					Vector3D target = new Vector3D(0,-999999,0);
					boolean itsstring = false;
					if (((String) Main.getset("followtarget")).split(" ").length > 1) {
						if (((String) Main.getset("followtarget")).split(" ").length == 2) {
							if (StringUtils.isNumeric(((String) Main.getset("followtarget")).split(" ")[0].replace("-", ""))) {
								target.x = Integer.parseInt(((String) Main.getset("followtarget")).split(" ")[0]);
							} else {
								//System.out.println(1+" "+StringUtils.isNumeric(((String) Main.gamerule("followtarget")).split(" ")[0])+" '"+((String) Main.gamerule("followtarget")).split(" ")[0]+"'");
								itsstring = true;
							}
							
							if (StringUtils.isNumeric(((String) Main.getset("followtarget")).split(" ")[1].replace("-", ""))) {
								target.z = Integer.parseInt(((String) Main.getset("followtarget")).split(" ")[1]);
							} else {
								itsstring = true;
							}
							
						} else if (((String) Main.getset("followtarget")).split(" ").length == 3) {
							if (StringUtils.isNumeric(((String) Main.getset("followtarget")).split(" ")[0].replace("-", ""))) {
								target.x = Integer.parseInt(((String) Main.getset("followtarget")).split(" ")[0]);
							} else {
								itsstring = true;
							}
							if (StringUtils.isNumeric(((String) Main.getset("followtarget")).split(" ")[2].replace("-", ""))) {
								target.z = Integer.parseInt(((String) Main.getset("followtarget")).split(" ")[2]);
							} else {
								itsstring = true;
							}
						} else if (((String) Main.getset("followtarget")).split(" ")[0].startsWith("/execute")) {
							//    /execute in minecraft:overworld run tp @s 172.27 64.00 -993.02 398.15 20.57
							target.x = Double.parseDouble(((String) Main.getset("followtarget")).split(" ")[6]);
							target.z = Double.parseDouble(((String) Main.getset("followtarget")).split(" ")[8]);
						}
					} else {
						itsstring = true;
					}
					if (itsstring) {
						UUID TargetUUID = null;
						for (PlayerListEntry player : client.getWorld().ServerTabPanel) {
							if (player != null) if ((player.getDisplayName() != null && player.getDisplayName().toString().contains((String) Main.getset("followtarget"))) || 
									(player.getProfile().getName() != null && player.getProfile().getName().contains((String) Main.getset("followtarget")))) {
								TargetUUID = player.getProfile().getId();
							}
						}
						
						if (TargetUUID != null) for (Entry<Integer, Entity> entry : client.getWorld().Entities.entrySet()) {
							if (entry.getValue().uuid.equals(TargetUUID)) {
								target = entry.getValue().pos;
								break;
							}
						}
					}
					if (!target.equals(new Vector3D(0,-999999,0))) {
						if (VectorUtils.sqrt2D(client.getPosition(), target) > (int) Main.getset("radius")) {
							Vector3D to = VectorUtils.randomPointInRaduis(client, 0, (int)Main.getset("radius"), (int)target.x, (int)target.z);
							if (to != null) {
								client.pathfinder.setup(to);
								state = raidState.GOING;
								return;
							}
						}
						
					}
				}
				
				/*if (client.onGround) {
					for(Entry<Integer, Entity> entry : client.getWorld().Entities.entrySet()) {
						if (entry.getValue().type == EntityType.PLAYER && !entry.getValue().uuid.equals(client.getUUID()) && VectorUtils.equalsInt(entry.getValue().Position, client.getPositionInt())) {
							Vector3D pos = VectorUtils.func_31(client, client.getPositionInt(), 4);
							client.pathfinder.setup(pos);
							return;
						}
					}
				}*/
				enemy = null;
				for(Entry<Integer, Entity> entry : client.getWorld().Entities.entrySet()) {
					if (entry.getValue() != null && badentities.contains(entry.getValue().type)) {
						//System.out.println(1);
						if (enemy == null) {
							//System.out.println(2);
							enemy = entry.getValue().eid;
						} else if (client.getWorld().Entities.get(enemy) != null && client.getWorld().Entities.get(enemy).alive && VectorUtils.sqrt(client.getWorld().Entities.get(enemy).pos,client.getPosition()) > VectorUtils.sqrt(client.getWorld().Entities.get(entry.getValue().eid).pos, client.getPosition())) {
							//System.out.println(3);
							enemy = entry.getValue().eid;
						}
					}
				}
				
				
				if ((boolean) Main.getset("pvpwithplayers")) {
					pEnemy = playerForPVP();
				}
				
				if (enemy != null && pEnemy != null) {
					if (client.distance(client.getWorld().Entities.get(enemy).pos) > client.distance(client.getWorld().Entities.get(pEnemy).pos)) {
						enemy = pEnemy;
					}
				} else if (enemy == null && pEnemy != null) {
					enemy = pEnemy;
					pEnemy = null;
				}
				
				if (enemy != null) {
					if (client.getWorld().Entities.get(enemy) != null && VectorUtils.sqrt(client.getWorld().Entities.get(enemy).pos, client.getPosition()) <= (client.pvp.maxPos*0.7) && client.getWorld().Entities.get(enemy).alive) {
						client.pvp.pvp(enemy);
						state = raidState.PVP;
						enemy = null;
						return;
					}
				}
				
				if (client.foodlvl >= 8) {
					
				}
				
				if (!tasklist.isEmpty()) {
					if (doubledtask == "") {
						doubledtask = tasklist.get(0);
					} else {
						if (doubledtask.equalsIgnoreCase(tasklist.get(0))) {
							++degradationLvl;
							if (degradationLvl >6) {
								tasklist.remove(0);
								client.pathfinder.setup(VectorUtils.randomPointInRaduis(client, 25));
							}
						} else {
							degradationLvl = 0;
						}
					}
					try {
						if (tasklist.get(0).toLowerCase().split(" ")[0].equalsIgnoreCase("mine")) {
							Vector3D block;
							block = VectorUtils.findBlockByName(client, tasklist.get(0).split(" ")[1], blacklist);
							if (block == null) {//подходящий блок небыл найден
								tasklist.remove(0);
							} else {
								if (block.getBlock(client).touchLiquid(client) || ((boolean)Main.getset("mineonlyiftouchair") && !block.getBlock(client).touchAir(client))) {
									this.blacklist.add(block); 
									return;
								}
								if (VectorUtils.sqrt(client.getEyeLocation(), block) <= (int)Main.getset("maxpostoblock")) {//блок довольно близко
									
									if (VectorUtils.sqrt(client.getEyeLocation(), block) <= 2.2) {
										client.bbm.setup(block);
										this.state = raidState.MINING;
										tasklist.remove(0);
										return;
									}
									Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getset("maxpostoblock"));
									if (pos != null) {//к нему можно приблизиться
										client.pathfinder.setup(pos);
								    	this.state = raidState.GOING;
								    	this.mineAfterWalk = block;
								    	tasklist.remove(0);
									} else {
										client.bbm.setup(block);
										this.state = raidState.MINING;
										tasklist.remove(0);
									}
									return;
							    } else {
							    	Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getset("maxpostoblock"));
							    	if (pos == null) {
							    		this.blacklist.add(block);
							    		return;
							    	}
							    	if (!client.pathfinder.testForPath(pos)) {
							    		blacklist.add(block);
							    		return;
							    	}
							    	client.pathfinder.setup(pos);
							    	this.state = raidState.GOING;
							    	this.mineAfterWalk = block;
							    	tasklist.remove(0);
							    	return;
							    }
							}
						} else if (tasklist.get(0).toLowerCase().startsWith("goto")) {
						
							int x = Integer.parseInt(tasklist.get(0).split(" ")[1]);
							int z = Integer.parseInt(tasklist.get(0).split(" ")[2]);
							int radius = Integer.parseInt(tasklist.get(0).split(" ")[3]);
							Vector3D to = VectorUtils.randomPointInRaduis(client, 0, radius, x, z);
							if (to != null) {		
								client.pathfinder.setup(to);
								state = raidState.GOING;
							}
							tasklist.remove(0);
							return;
						} else if (tasklist.get(0).toLowerCase().startsWith("come")) {
							int x = Integer.parseInt(tasklist.get(0).split(" ")[1]);
							int y = Integer.parseInt(tasklist.get(0).split(" ")[2]);
							int z = Integer.parseInt(tasklist.get(0).split(" ")[3]);
							Vector3D to = new Vector3D(x,y,z);		
							client.pathfinder.setup(to);
							state = raidState.GOING;
							tasklist.remove(0);
						} else if (tasklist.get(0).toLowerCase().startsWith("craft")) {
							String item = tasklist.get(0).split(" ")[1];
							if (client.crafter.Recepies.get(item).isInventoried()) {
								client.crafter.setup(item, null);
								state = raidState.CRAFTING;
								tasklist.remove(0);
								return;
							} else if (client.crafter.Recepies.get(item).isWorkbenched()) {
								Vector3D block;
								block = VectorUtils.findBlockByName(client, "crafting", blacklist);
								if (block == null) {
									if (client.playerInventory.contain("crafting")) {
										Block ct = VectorUtils.placeBlockNear(client, "crafting");
										if (ct == null) {
											tasklist.remove(0);
											return;
											//нужно чтобы он пробовал кудато пойти и при 5 неудачных попытках сдавался но чето хз как это сделать
										} else {
											block = ct.getPos();
										}
									} else if (client.crafter.canCraft(client.crafter.Recepies.get("crafting_table"))) {
										client.crafter.setup("crafting_table", null);
										state = raidState.CRAFTING;
										return;
									} else if (client.playerInventory.contain("log", 1)) {
										client.crafter.setup("planks",null);
										state = raidState.CRAFTING;
										return;
									} else {
										tasklist.remove(0);
										return;
									}
								} else if (client.distance(block) > (int)Main.getset("maxpostoblock")) {
									Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getset("maxpostoblock"));
									if (pos != null) {
										client.pathfinder.setup(pos);
										state = raidState.GOING;
										return;
									} else {
										//если дойти неполучается то бот пытается сделать свой верстак
										if (client.playerInventory.contain("crafting")) {
											Block ct = VectorUtils.placeBlockNear(client, "crafting");
											if (ct == null) {
												tasklist.remove(0);
												return;
												//нужно чтобы он пробовал кудато пойти и при 5 неудачных попытках сдавался но чето хз как это сделать
											} else {
												block = ct.getPos();
											}
										} else if (client.crafter.canCraft(client.crafter.Recepies.get("crafting_table"))) {
											client.crafter.setup("crafting_table", null);
											state = raidState.CRAFTING;
											return;
										} else if (client.playerInventory.contain("log", 1)) {
											client.crafter.setup("planks",null);
											state = raidState.CRAFTING;
											return;
										} else {
											tasklist.remove(0);
											return;
										}
										//конец
									}
								} else if (client.distance(block) <= (int)Main.getset("maxpostoblock")) {
									client.crafter.setup(item, block);
									state = raidState.CRAFTING;
									tasklist.remove(0);
									return;
								} else {
									tasklist.remove(0);
									throw new Exception("невозможно");
								}
							}
						} else if (tasklist.get(0).toLowerCase().startsWith("minepos")) {
							Vector3D block = new Vector3D(Integer.parseInt(tasklist.get(0).split(" ")[1]),Integer.parseInt(tasklist.get(0).split(" ")[2]),Integer.parseInt(tasklist.get(0).split(" ")[3]));
							if (block.getBlock(client).type == Type.AIR || block.getBlock(client).type == Type.LIQUID || block.getBlock(client).type == Type.UNBREAKABLE) {
								tasklist.remove(0);
								return;
							}
							if (block.getBlock(client).touchLiquid(client)) {//блок касается жидкости
								this.blacklist.add(block); 
								return;
							}
							
							if (VectorUtils.sqrt(client.getEyeLocation(), block) <= (int)Main.getset("maxpostoblock")) {//блок довольно близко
								
								if (VectorUtils.sqrt(client.getEyeLocation(), block) <= 2.2) {
									client.bbm.setup(block);
									this.state = raidState.MINING;
									tasklist.remove(0);
									return;
								}
								Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getset("maxpostoblock"));
								if (pos != null) {//к нему можно приблизиться
									
									client.pathfinder.setup(pos);
							    	this.state = raidState.GOING;
							    	this.mineAfterWalk = block;
							    	tasklist.remove(0);
							    	return;
								} else {
									client.bbm.setup(block);
									this.state = raidState.MINING;
									tasklist.remove(0);
									return;
								}
						    } else {
						    	Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getset("maxpostoblock"));
						    	if (pos == null) {
						    		this.blacklist.add(block);
						    		return;
						    	}
						    	if (!client.pathfinder.testForPath(pos)) {
						    		blacklist.add(block);
						    		return;
						    	}
						    	client.pathfinder.setup(pos);
						    	this.state = raidState.GOING;
						    	this.mineAfterWalk = block;
						    	tasklist.remove(0);
						    	return;
						    }
						} else if (tasklist.get(0).toLowerCase().startsWith("goforwardwithangle")) {
							goforwardticks = Integer.parseInt(tasklist.get(0).split(" ")[1]);
							client.yaw += MathU.rnd(-100, 100);
							state = raidState.GOFORWARD;
							tasklist.remove(0);
							return;
						} else if (tasklist.get(0).toLowerCase().startsWith("faceto")) {
							Vector3D block = new Vector3D(Integer.parseInt(tasklist.get(0).split(" ")[1]),Integer.parseInt(tasklist.get(0).split(" ")[2]),Integer.parseInt(tasklist.get(0).split(" ")[3]));
							BotU.LookHead(client, block);
							tasklist.remove(0);
							return;
						} else if (tasklist.get(0).toLowerCase().startsWith("dropitemstack")) {
							for (int i = 9; i <= 44; i++) {
								if (client.playerInventory.slots.get(i) != null && Main.getMCData().items.get(client.playerInventory.slots.get(i).getId()).name.contains(tasklist.get(0).split(" ")[1])) {
									client.playerInventory.dropItem(true, i);
								}
							}
							tasklist.remove(0);
							return;
						} else {
							tasklist.remove(0);
							return;
						}
					
					} catch (Exception e) {
						e.printStackTrace();
						tasklist.remove(0);
						return;
					}
				} else {
					if (!Main.tomine.isEmpty()) {
						if (Main.tomine.get(0).isEmpty()) {
							Main.tomine.remove(0);
							return;
						} else {
							Vector3D rnd = VectorUtils.getNear(client.getPosition(), Main.tomine.get(0));
							if (rnd == null) {
								BotU.log("!!!!!!!!!!!!!!!!!!!!!!!null");
								return;
							}
							tasklist.add("minepos "+(int)rnd.x+" "+(int)rnd.y+" "+(int)rnd.z);
							Main.tomine.get(0).remove(rnd);
							return;
						}
						
					}
					if (client.automaticMode) {
						int i = MathU.rnd(1, 20);
						if (i == 1) {
							client.pathfinder.setup(VectorUtils.randomPointInRaduis(client, 5, 10, (int)Math.floor(client.posX), (int)Math.floor(client.posZ)));
							state = raidState.GOING;
							return;
						} else if (i >= 2 && i <= 7) {
							Vector3D block;
							if ((boolean) Main.getset("iol")) {
								block = VectorUtils.findNearestBlockByArrayId(client, (ArrayList<Integer>)Main.getset("minertargetid"), this.blacklist);
								if (block == null) block = VectorUtils.func_1488(client, (ArrayList<Integer>)Main.getset("minertargetid"), this.blacklist);
							} else {
								block = VectorUtils.func_32(client, (ArrayList<String>)Main.getset("minetargetnames"), this.blacklist);
							}
							if (block != null) {
								if (VectorUtils.sqrt(client.getEyeLocation(), block) <= (int)Main.getset("maxpostoblock")) {
									client.bbm.setup(block);
									this.state = raidState.MINING;
									return;
							    } else {
							    	Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getset("maxpostoblock"));
							    	if (pos == null) {
							    		blacklist.add(block);
							    		return;
							    	}
							    	if (!client.pathfinder.testForPath(pos)) {
							    		blacklist.add(block);
							    		return;
							    	}
							    	mineAfterWalk = block;
							    	client.pathfinder.setup(pos);
							    	this.state = raidState.GOING;
							    	return;
							    }
							}
							return;
						} else if (i == 8) {
							tasklist.add("goforwardwithangle 20");
						} else if (i >= 9 && i <= 18) {
							VectorUtils.placeBlockNear(client, (String)MathU.random((ArrayList<String>)Main.getset("minetargetnames")));
						} else if (i > 18 && i <= 20) {
							enemy = playerForPVP();
							if (enemy != null) {
								if (client.getWorld().Entities.get(enemy) != null && VectorUtils.sqrt(client.getWorld().Entities.get(enemy).pos, client.getPosition()) <= client.pvp.maxPos && client.getWorld().Entities.get(enemy).alive) {
									client.pvp.pvp(enemy);
									state = raidState.PVP;
									enemy = null;
									return;
								}
							}
						}
					}
					doubledtask = "";
					degradationLvl = 0;
				}
				
			} else if (state == raidState.GOING) {
				if (client.pathfinder.state == State.FINISHED) {
					if (mineAfterWalk != null) {
						state = raidState.MINING;
						client.bbm.setup(mineAfterWalk);
						mineAfterWalk = null;
						return;
					} else {
						state = raidState.IDLE;
					}
					
				}
			} else if (state == raidState.MINING) {
				if (client.bbm.state == bbmct.ENDED) {
					state = raidState.IDLE;
					sleepticks = 10;
				} else {
					if (client.bbm.ticksToBreak < -300) {
						client.bbm.endDigging();
						blacklist.add(client.bbm.getBlockPos());
						state = raidState.IDLE;
						sleepticks = 10;
					}
				}
				return;
			} else if (state == raidState.CRAFTING) {
				if (client.crafter.state == crState.ENDED) this.state = raidState.IDLE;
			} else if (state == raidState.GOFORWARD) {
				if (goforwardticks > 0) {
					client.pm.Walk();
					--goforwardticks;
					/*if (MathU.rnd(1, 3) == 1) {
						client.pm.jump();
					}*/
				} else {
					this.state = raidState.IDLE;
				}
			} else if (state == raidState.PVP) {
				if (client.pvp.state == CombatState.END_COMBAT) {
					state = raidState.IDLE;
				} else {
					
				}
			} else if (state == raidState.FOLLOW) {
				if (target != null) {
					
				} else {
					state = raidState.IDLE;
				}
			}
	}
	
	@SuppressWarnings("unchecked")
	public Integer playerForPVP() {
		List<Entry<Integer, Entity>> targetss = new ArrayList<>();
		for (PlayerListEntry ia : client.getWorld().ServerTabPanel) {
			if (!ia.getProfile().getId().equals(client.getUUID()) && ia.getGameMode() == GameMode.SURVIVAL) {
				Entry<Integer, Entity> entity = client.getWorld().getEntity(ia.getProfile().getId());
				if (entity != null) {
					if ((boolean) Main.getset("pvpamongthemselves")) {
						if (ia.getProfile().getName() != null) {
							if (!StringU.contains((List<String>)Main.getset("pvpblacklist"), ia.getProfile().getName())) {
								targetss.add(entity);
							}
						} else if (ia.getDisplayName() != null) {
							if (!StringU.backwardContains((List<String>)Main.getset("pvpblacklist"), ia.getDisplayName().toString())) {
								targetss.add(entity);
							}
						}
					} else {
						if (ia.getProfile().getName() != null) {
							if (!StringU.contains((List<String>)Main.getset("pvpblacklist"), ia.getProfile().getName())
							&&
							!StringU.contains(Main.nicks, ia.getProfile().getName())
							) {
								targetss.add(entity);
							}
						} else if (ia.getDisplayName() != null) {
							if (!StringU.backwardContains((List<String>)Main.getset("pvpblacklist"), ia.getDisplayName().toString())
							&&
							!StringU.backwardContains(Main.nicks, ia.getProfile().getName())
							) {
								targetss.add(entity);
							}
						}
					}
				}
			}
		}
		if (!targetss.isEmpty()) {
			
			if (targetss.size() > 1) {
				return VectorUtils.getNearE(client.getPosition(), targetss).getKey();
			} else {
				return targetss.get(0).getKey();
			}
		} else {
			return null;
		}
	}
	
	/*if (!client.playerInventory.contain("sticks", 4)) {
		if (!client.playerInventory.contain("planks", 2)) {
			if (!client.playerInventory.contain("log", 1)) {
				Vector3D block = VectorUtils.findBlockByName(client, "log", this.blacklist);
				if (client.distance(block) > (int)Main.getsett("maxpostoblock")) {
					
				}
			}
		}
	}
	
	/*
	 * if ((int)Main.getsett("walkeverymilseconds") >= 50) sleepticks = (int)Main.getsett("walkeverymilseconds") / 50;
				
				if ((int)Main.getsett("walkeverymilseconds") != 0) sleepticks = (int)Main.getsett("walkeverymilseconds") / 50;
				
				if (MathU.rnd(1, 10) == 1) {
					Vector3D to = VectorUtils.func_31(client, client.getPositionInt(), 8);
					this.sleepticks = 20;
					if (to != null) client.pathfinder.setupNoBreak(to);
					asd = null;
					this.state = raidState.GOING;
					return;
				}
				
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
					if (VectorUtils.sqrt(tempentities.get(enemy).Position, client.getPosition()) <= 4 && tempentities.get(enemy).alive == true) {
						client.pvp.pvp(enemy);
						enemy = null;
						return;
					}
				}
				for(Entry<Integer, Entity> entry : tempentities.entrySet()) {
					if (entry.getValue().type == EntityType.PLAYER && entry.getValue().uuid != client.getUUID() && VectorUtils.equalsInt(entry.getValue().Position, client.getPositionInt())) {
						Vector3D pos = VectorUtils.func_31(client, client.getPositionInt(), (int)Main.getsett("maxpostoblock"));
						client.pathfinder.setup(pos);
						return;
					}
				}
				
				if (client.ztp && VectorUtils.sqrt(client.getPositionInt(), client.targetpos) < client.targetradius) {
					Vector3D pos = VectorUtils.func_31(client, client.targetpos, client.targetradius);
					if (pos == null) state = raidState.IDLE;
					client.pathfinder.setup(pos);
			    	this.state = raidState.GOING;
				} else {
					if ((boolean) Main.getsett("mining")) {
						Vector3D block = null;
						if ((boolean) Main.getsett("dbc")) {
						if (!client.playerInventory.contain("stone_axe")) {
							System.out.println(1);
							//BotU.chat(client, "no axe");
							if (!client.playerInventory.contain("cobblestone", 9)) {
								System.out.println(2);
								
								if (!client.playerInventory.contain("pickaxe")) {
									System.out.println(3);
									
									if (!client.playerInventory.contain("planks", 12)) {
										System.out.println(4);
										if (!client.playerInventory.contain("log", 4)) {
											System.out.println(5);
											block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
										} else {
											System.out.println(6);
											client.crafter.setup("planks", null);
											state = raidState.CRAFTING;
											return;
										}
									} else {
										System.out.println(7);
										if (!client.playerInventory.contain("stick", 2)) {
											System.out.println(8);
											if (!client.playerInventory.contain("planks", 4)) {
												System.out.println(9);
												if (!client.playerInventory.contain("log", 3)) {
													System.out.println(10);
													block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
												} else {
													System.out.println(11);
													client.crafter.setup("planks", null);
													state = raidState.CRAFTING;
													return;
												}
											} else {
												System.out.println(12);
												client.crafter.setup("sticks", null);
												state = raidState.CRAFTING;
												return;
											}
										} else {
											System.out.println(13);
											//--------------
											block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("crafting");}}, this.blacklist, 5);
											if (block == null) {
												System.out.println(14);
												if (client.playerInventory.contain("crafting_table")) {
													block = VectorUtils.placeBlockNear(client, "crafting_table").pos;
													client.crafter.setup("wooden_pickaxe", block);
													state = raidState.CRAFTING;
													return;
												} else {
													System.out.println(15);
													if (!client.playerInventory.contain("planks", 8)) {
														if (!client.playerInventory.contain("log", 4)) {
															System.out.println(16);
															block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
														} else {
															System.out.println(17);
															client.crafter.setup("planks", null);
															state = raidState.CRAFTING;
															return;
														}
													} else {
														System.out.println(18);
														client.crafter.setup("bench", null);
														state = raidState.CRAFTING;
														return;
													}
												}
											} else {
												System.out.println(19);
												if (VectorUtils.sqrt(block, client.getEyeLocation()) <= (int)Main.getsett("maxpostoblock")) {
													client.crafter.setup("wooden_pickaxe", block);
													state = raidState.CRAFTING;
													return;
												} else {
													System.out.println(20);
													Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getsett("maxpostoblock"));
											    	if (pos == null) {
											    		this.blacklist.add(block);
											    		state = raidState.IDLE;
											    		return;
											    	}
											    	this.asd = block;
											    	if (!client.pathfinder.testForPath(pos)) blacklist.add(block);
											    	client.pathfinder.setup(pos);
											    	this.state = raidState.GOING;
											    	return;
												}
											}
											//--------------
										}
										
									}
									
								} else {
									System.out.println(22);
									block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("stone");}}, this.blacklist);
									System.out.println(block.toString());
								}
								
								
							} else if (!client.playerInventory.contain("stick", 8)) {
								System.out.println(23);
								//---------------------------------------------------
								if (!client.playerInventory.contain("planks", 6)) {
									System.out.println(24);
									if (!client.playerInventory.contain("log", 5)) {
										System.out.println(25);
										block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
									} else {
										System.out.println(26);
										client.crafter.setup("planks", null);
										state = raidState.CRAFTING;
										return;
									}
								} else {
									System.out.println(27);
									client.crafter.setup("sticks", null);
									state = raidState.CRAFTING;
									return;
								}
								
							} else {
								System.out.println(28);
								block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("crafting");}}, this.blacklist);
								if (block == null) {
									System.out.println(29);
									if (!client.playerInventory.contain("crafting")) {
										block = VectorUtils.placeBlockNear(client, "crafting_table").pos;
										client.crafter.setup("stone_axe", block);
										state = raidState.CRAFTING;
										return;
									} else {
										System.out.println(30);
										if (!client.playerInventory.contain("planks", 8)) {
											if (!client.playerInventory.contain("log", 3)) {
												block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
											} else {
												System.out.println(31);
												client.crafter.setup("planks", null);
												state = raidState.CRAFTING;
												return;
											}
										} else {
											System.out.println(32);
											client.crafter.setup("bench", null);
											state = raidState.CRAFTING;
											return;
										}
									}
								} else {
									System.out.println(33);
									if (VectorUtils.sqrt(block, client.getEyeLocation()) <= (int)Main.getsett("maxpostoblock")) {
										client.crafter.setup("stone_axe", block);
										state = raidState.CRAFTING;
										return;
									} else {
										System.out.println(34);
										Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getsett("maxpostoblock"));
								    	if (pos == null) {
								    		System.out.println(35);
								    		this.blacklist.add(block);
								    		state = raidState.IDLE;
								    		return;
								    	}
								    	System.out.println(36);
								    	this.asd = block;
								    	if (!client.pathfinder.testForPath(pos)) blacklist.add(block);
								    	client.pathfinder.setup(pos);
								    	this.state = raidState.GOING;
								    	return;
									}
								}
							}
						} else if (!client.playerInventory.contain("stone_pickaxe")) {
							//BotU.chat(client, "no axe");
							if (!client.playerInventory.contain("cobblestone", 9)) {
								
								if (!client.playerInventory.contain("pickaxe")) {
									
									if (!client.playerInventory.contain("planks", 12)) {
										if (!client.playerInventory.contain("log", 4)) {
											block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
										} else {
											client.crafter.setup("planks", null);
											state = raidState.CRAFTING;
											return;
										}
									} else {
										if (!client.playerInventory.contain("stick", 2)) {
											if (!client.playerInventory.contain("planks", 4)) {
												if (!client.playerInventory.contain("log", 3)) {
													block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
												} else {
													client.crafter.setup("planks", null);
													state = raidState.CRAFTING;
													return;
												}
											} else {
												client.crafter.setup("sticks", null);
												state = raidState.CRAFTING;
												return;
											}
										} else {
											//--------------
											block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("crafting");}}, this.blacklist, 5);
											if (block == null) {
												if (client.playerInventory.contain("crafting_table")) {
													block = VectorUtils.placeBlockNear(client, "crafting_table").pos;
													client.crafter.setup("wooden_pickaxe", block);
													state = raidState.CRAFTING;
													return;
												} else {
													if (!client.playerInventory.contain("planks", 8)) {
														if (!client.playerInventory.contain("log", 4)) {
															block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
														} else {
															client.crafter.setup("planks", null);
															state = raidState.CRAFTING;
															return;
														}
													} else {
														client.crafter.setup("bench", null);
														state = raidState.CRAFTING;
														return;
													}
												}
											} else {
												if (VectorUtils.sqrt(block, client.getEyeLocation()) <= (int)Main.getsett("maxpostoblock")) {
													client.crafter.setup("wooden_pickaxe", block);
													state = raidState.CRAFTING;
													return;
												} else {
													Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getsett("maxpostoblock"));
											    	if (pos == null) {
											    		this.blacklist.add(block);
											    		state = raidState.IDLE;
											    		return;
											    	}
											    	this.asd = block;
											    	if (!client.pathfinder.testForPath(pos)) blacklist.add(block);
											    	client.pathfinder.setup(pos);
											    	this.state = raidState.GOING;
											    	return;
												}
											}
											//--------------
										}
										
									}
									
								} else {
									block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("stone");}}, this.blacklist);
									System.out.println(block.toString());
								}
								
								
							} else if (!client.playerInventory.contain("stick", 8)) {
								//---------------------------------------------------
								if (!client.playerInventory.contain("planks", 6)) {
									if (!client.playerInventory.contain("log", 5)) {
										block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
									} else {
										client.crafter.setup("planks", null);
										state = raidState.CRAFTING;
										return;
									}
								} else {
									client.crafter.setup("sticks", null);
									state = raidState.CRAFTING;
									return;
								}
								
							} else {
								block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("crafting");}}, this.blacklist);
								if (block == null) {
									if (!client.playerInventory.contain("crafting")) {
										block = VectorUtils.placeBlockNear(client, "crafting_table").pos;
										client.crafter.setup("stone_pickaxe", block);
										state = raidState.CRAFTING;
										return;
									} else {
										if (!client.playerInventory.contain("planks", 8)) {
											if (!client.playerInventory.contain("log", 3)) {
												block = VectorUtils.func_32(client, new CopyOnWriteArrayList<>() {{add("log");}}, this.blacklist);
											} else {
												client.crafter.setup("planks", null);
												state = raidState.CRAFTING;
												return;
											}
										} else {
											client.crafter.setup("bench", null);
											state = raidState.CRAFTING;
											return;
										}
									}
								} else {
									if (VectorUtils.sqrt(block, client.getEyeLocation()) <= (int)Main.getsett("maxpostoblock")) {
										client.crafter.setup("stone_pickaxe", block);
										state = raidState.CRAFTING;
										return;
									} else {
										Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getsett("maxpostoblock"));
								    	if (pos == null) {
								    		this.blacklist.add(block);
								    		state = raidState.IDLE;
								    		return;
								    	}
								    	this.asd = block;
								    	if (!client.pathfinder.testForPath(pos)) blacklist.add(block);
								    	client.pathfinder.setup(pos);
								    	this.state = raidState.GOING;
								    	return;
									}
								}
							}
						}
						}//-------------------dbc-------------------------
						
						if (block == null) {
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
						}
						if (block == null) {
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
							Vector3D to = VectorUtils.randomPointInRaduis(client, 25);
							if (to == null) to = VectorUtils.func_31(client, client.getPositionInt(), 8);
							this.sleepticks = 100;
							if (to != null) client.pathfinder.setup(to);
							asd = null;
							this.state = raidState.GOING;
							return;
						}
						System.out.println(40);
						if (block.getBlock(client).touchLiquid(client)) {
							System.out.println(41);
							this.blacklist.add(block);
							state = raidState.IDLE;
							return;
						}
						if (VectorUtils.sqrt(client.getEyeLocation(), block) <= (int)Main.getsett("maxpostoblock")) {
							System.out.println(42);
							client.bbm.setup(block);
							this.state = raidState.MINING;
							return;
					    } else {
					    	System.out.println(VectorUtils.sqrt(client.getEyeLocation(), block));
					    	System.out.println(43);
					    	Vector3D pos = VectorUtils.func_31(client, block, (int)Main.getsett("maxpostoblock"));
					    	if (pos == null) {
					    		System.out.println(44);
					    		this.blacklist.add(block);
					    		state = raidState.IDLE;
					    		return;
					    	}
					    	System.out.println(45);
					    	this.asd = block;
					    	if (!client.pathfinder.testForPath(pos)) {
					    		blacklist.add(block);
					    		System.out.println(46);
					    	}
					    	client.pathfinder.setup(pos);
					    	this.state = raidState.GOING;
					    	return;
					    }
				 	}
				}
	 */
}

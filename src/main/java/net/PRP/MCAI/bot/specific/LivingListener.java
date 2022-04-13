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
import net.PRP.MCAI.bot.specific.Crafting.crState;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.*;

public class LivingListener extends SessionAdapter {
	
	private Bot client;
	private boolean firstJoin = false;
	public raidState state = raidState.IDLE;
	public Vector3D asd = Vector3D.ORIGIN;
	public List<Vector3D> blacklist = new CopyOnWriteArrayList<>();
	public boolean trusted;
	private int sleepticks = (int)Main.getsett("walkeverymilseconds") / 50;
	public Integer enemy = null;
	
	public int tickstocheck = 0;
	public short stage = 0;
	
	public List<EntityType> badentities = new ArrayList<>() {
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
	int spamticks = 0;

	public LivingListener(Bot client) {
        this.client = client;
        this.trusted = (boolean) Main.getsett("living");
    }
	
	public enum raidState {
		IDLE, GOING, MINING, CRAFTING, WAIT;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	if (firstJoin) return;
        	ThreadU.sleep((int) Main.getsett("timebeforeraidon"));
        	firstJoin = true;
        	spamticks = (int) Main.getsett("spamrange") / 50;
        } else if (receiveEvent.getPacket() instanceof ServerMapDataPacket) {
        	//final ServerMapDataPacket p = (ServerMapDataPacket) receiveEvent.getPacket();
        }
	}
	
	@SuppressWarnings("serial")
	public void tick() {
		//try {
			if (!firstJoin || !client.isOnline()) return;
			//System.out.println(state+" "+trusted);
			if ((boolean) Main.getsett("raidspam") && Main.pasti.size() > 0) {
				spamticks--;
				if (spamticks <= 0) {
					spamticks =  (int) Main.getsett("spamrange") / 50;
					int rand = MathU.rnd(0, Main.pasti.size()-1);
	                String pasta = (String)Main.pasti.get(rand);
	                BotU.chat(this.client, pasta);
				}
			}
			
			if (state == raidState.IDLE) {
				if (!this.trusted) return;
				if (client.pathfinder.state == State.WALKING) return;
				if (client.pvp.state != CombatState.END_COMBAT) return;
				if (client.getPositionInt().getBlock(client).ishard()) {
					state = raidState.MINING;
					client.bbm.setup(client.getPositionInt());
					return;
				} else if (client.getPositionInt().add(0,1,0).getBlock(client).ishard()) {
					state = raidState.MINING;
					client.bbm.setup(client.getPositionInt());
					return;
				}
				if (sleepticks > 0) {sleepticks--;return;}
				if ((int)Main.getsett("walkeverymilseconds") >= 50) sleepticks = (int)Main.getsett("walkeverymilseconds") / 50;
				
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
				//System.out.println(client.bbm.getBlockPos());
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
			} else if (state == raidState.CRAFTING) {
				if (client.crafter.state == crState.ENDED) {
					this.asd = null;
					this.state = raidState.IDLE;
				}
			}
		//} catch (Exception e) {
			//e.printStackTrace();
			//System.out.println("1");
		//}
	}
}

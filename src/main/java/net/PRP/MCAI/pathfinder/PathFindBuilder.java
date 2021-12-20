package net.PRP.MCAI.pathfinder;

import java.awt.List;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;

public class PathFindBuilder {
	
    Object[] paths = {
    };
    
    public static boolean walkTo(Bot client, Position pos, boolean retryifbnip) {
    	//---------------------x-------------------
    	double raznicax = pos.getX()-client.getPosX();
    	if (raznicax > 0) {
			for(double o = client.getPosX(); o < pos.getX() ;o++) {
				if ((int) Math.floor(client.getPosX()) == (int) Math.floor(pos.getX())) break;
				if (botCanWalkAt(client.getPosX()+1,client.getPosY(),client.getPosZ())) {
					if (botCanWalkThrough(getblockid(client.getPosX()+1,client.getPosY()-1,client.getPosZ()))) {
						if (botCanWalkThrough(getblockid(client.getPosX()+2,client.getPosY()-1,client.getPosZ()))) {
							if (botCanWalkThrough(getblockid(client.getPosX()+3,client.getPosY()-1,client.getPosZ()))) {
								if (botCanWalkThrough(getblockid(client.getPosX()+4,client.getPosY()-1,client.getPosZ()))) {
									if (botCanWalkThrough(getblockid(client.getPosX()+5,client.getPosY()-1,client.getPosZ()))) {
										if (botCanWalkThrough(getblockid(client.getPosX()+1,client.getPosY()-1,client.getPosZ())) && !botCanWalkThrough(getblockid(client.getPosX()+1,client.getPosY()-2,client.getPosZ()))) {
											BotU.teleport(client, 1, -1, 0);
											ThreadU.sleep(250);
										} else {
											//log("passed with client x:"+client.getPosX()+" y:"+client.getPosY()+" and tested pos x:"+client.getPosX()+5+" y:"+(int) (client.getPosY()-1)+"/");
											walkTo(client, new Position((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()+1),false);
										}
									} else {
										BotU.jump(client, 5, "x");
										o += 4;
										ThreadU.sleep(100);
									}
								} else {
									BotU.jump(client, 4, "x");
									o+=3;
									ThreadU.sleep(100);
								}
							} else {
								BotU.jump(client, 3, "x");
								o+=2;
								ThreadU.sleep(100);
							}
						} else {
							BotU.jump(client, 2, "x");
							o++;
							ThreadU.sleep(100);
						}
					} else {
						BotU.walk(client, 1, "x");
						ThreadU.sleep(50);
					}
				} else {
					if (botCanWalkAt(client.getPosX()+1,client.getPosY()+1,client.getPosZ())) {
						BotU.teleport(client, 1, 1, 0);
						ThreadU.sleep(250);
					} else {
						Position pos2 = new Position((int)client.getPosX()+1,(int)client.getPosY()+1,(int)client.getPosZ());
						if (getblockid(pos2) != 0) {
							BotU.mineBlock(client, pos2 ,false);
							while (!botCanWalkThrough(getblockid(pos2))) {
				        		ThreadU.sleep(200);
				        	}
						}
						Position pos3 = new Position((int)client.getPosX()+1,(int)client.getPosY(),(int)client.getPosZ());
						if (getblockid(pos3) != 0) {
							BotU.mineBlock(client, pos3,false);
							while (!botCanWalkThrough(getblockid(pos2))) {
				        		ThreadU.sleep(200);
				        	}
						}
					}
				}
			}
    	} else if (raznicax == 0) {
    		//pass
		} else {
			for(double o = client.getPosX(); o > pos.getX() ;o++) {
				if ((int) Math.floor(client.getPosX()) == (int) Math.floor(pos.getX())) break;
				if (botCanWalkAt(client.getPosX()-1,client.getPosY(),client.getPosZ())) {
					if (botCanWalkThrough(getblockid(client.getPosX()-1,client.getPosY()-1,client.getPosZ()))) {
						if (botCanWalkThrough(getblockid(client.getPosX()-2,client.getPosY()-1,client.getPosZ()))) {
							if (botCanWalkThrough(getblockid(client.getPosX()-3,client.getPosY()-1,client.getPosZ()))) {
								if (botCanWalkThrough(getblockid(client.getPosX()-4,client.getPosY()-1,client.getPosZ()))) {
									if (botCanWalkThrough(getblockid(client.getPosX()-5,client.getPosY()-1,client.getPosZ()))) {
										if (botCanWalkThrough(getblockid(client.getPosX()-1,client.getPosY()-1,client.getPosZ())) && !botCanWalkThrough(getblockid(client.getPosX()-1,client.getPosY()-2,client.getPosZ()))) {
											BotU.teleport(client, -1, -1, 0);
											ThreadU.sleep(250);
										} else {
											//log("passed with client x:"+client.getPosX()+" y:"+client.getPosY()+" and tested pos (broken(-10)x:"+client.getPosX()+5+" y:"+(int) (client.getPosY()-1)+"/");
											walkTo(client, new Position((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()+1),false);
										}
									} else {
										BotU.jump(client, -5, "x");
										o += 4;
										ThreadU.sleep(100);
									}
								} else {
									BotU.jump(client, -4, "x");
									o+=3;
									ThreadU.sleep(100);
								}
							} else {
								BotU.jump(client, -3, "x");
								o+=2;
								ThreadU.sleep(100);
							}
						} else {
							BotU.jump(client, -2, "x");
							o++;
							ThreadU.sleep(100);
						}
					} else {
						BotU.walk(client, -1, "x");
						ThreadU.sleep(50);
					}
				} else {
					if (botCanWalkAt(client.getPosX()-1,client.getPosY()+1,client.getPosZ())) {
						BotU.teleport(client, -1, 1, 0);
						ThreadU.sleep(250);
					} else {
						Position pos2 = new Position((int)client.getPosX()-1,(int)client.getPosY()+1,(int)client.getPosZ());
						if (getblockid(pos2) != 0) {
							BotU.mineBlock(client, pos2 ,false);
							while (!botCanWalkThrough(getblockid(pos2))) {
				        		ThreadU.sleep(200);
				        	}
						}
						Position pos3 = new Position((int)client.getPosX()-1,(int)client.getPosY(),(int)client.getPosZ());
						if (getblockid(pos3) != 0) {
							BotU.mineBlock(client, pos3,false);
							while (!botCanWalkThrough(getblockid(pos2))) {
				        		ThreadU.sleep(200);
				        	}
						}
					}
				}
			}
		}
    	
    	//---------------------z-------------------
    	
    	double raznicaz = pos.getZ() - client.getPosZ();
    	
    	if (raznicaz > 0) {
			for(double o = client.getPosZ(); o < pos.getZ() ;o++) {
				if ((int) Math.floor(client.getPosZ()) == (int) Math.floor(pos.getZ())) break;
				if (botCanWalkAt(client.getPosX(),client.getPosY(),client.getPosZ()+1)) {
					if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+1))) {
						if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+2))) {
							if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+3))) {
								if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+4))) {
									if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+5))) {
										if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()+1)) && !botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-2,client.getPosZ()+1))) {
											BotU.teleport(client, 0, -1, 1);
											ThreadU.sleep(250);
										} else {
											//log("passed with client z:"+client.getPosZ()+" y:"+client.getPosY()+" and tested pos z:"+client.getPosZ()+5+" y:"+(int) (client.getPosY()-1)+"/");
											walkTo(client, new Position((int)client.getPosX()+1,(int)client.getPosY(),(int)client.getPosZ()),false);
										}
									} else {
										BotU.jump(client, 5, "z");
										o += 4;
										ThreadU.sleep(100);
									}
								} else {
									BotU.jump(client, 4, "z");
									o+=3;
									ThreadU.sleep(100);
								}
							} else {
								BotU.jump(client, 3, "z");
								o+=2;
								ThreadU.sleep(100);
							}
						} else {
							BotU.jump(client, 2, "z");
							o++;
							ThreadU.sleep(100);
						}
					} else {
						BotU.walk(client, 1, "z");
						ThreadU.sleep(50);
					}
				} else {
					if (botCanWalkAt(client.getPosX(),client.getPosY()+1,client.getPosZ()+1)) {
						BotU.teleport(client, 0, 1, 1);
						ThreadU.sleep(250);
					} else {
						Position pos2 = new Position((int)client.getPosX(),(int)client.getPosY()+1,(int)client.getPosZ()+1);
						if (getblockid(pos2) != 0) {
							BotU.mineBlock(client, pos2 ,false);
							while (!botCanWalkThrough(getblockid(pos2))) {
				        		ThreadU.sleep(200);
				        	}
						}
						Position pos3 = new Position((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()+1);
						if (getblockid(pos3) != 0) {
							BotU.mineBlock(client, pos3,false);
							while (!botCanWalkThrough(getblockid(pos2))) {
				        		ThreadU.sleep(200);
				        	}
						}
					}
				}
			}
    	} else if (raznicaz == 0) {
    		//pass
		} else {
			for(double o = pos.getZ(); o < client.getPosZ() ;o--) {
				if ((int) Math.floor(client.getPosZ()) == (int) Math.floor(pos.getZ())) {System.out.println("z ravno"); break;}
				if (botCanWalkAt(client.getPosX(),client.getPosY(),client.getPosZ()-1)) {
					if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-1))) {
						if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-2))) {
							if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-3))) {
								if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-4))) {
									if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-5))) {
										if (botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-1,client.getPosZ()-1)) && !botCanWalkThrough(getblockid(client.getPosX(),client.getPosY()-2,client.getPosZ()-1))) {
											BotU.teleport(client, 0, -1, 1);
											ThreadU.sleep(250);
										} else {
											//log("passed with client z:"+client.getPosZ()+" y:"+client.getPosY()+" and tested pos z:"+client.getPosZ()+5+" y:"+(int) (client.getPosY()-1)+"/");
											walkTo(client, new Position((int)client.getPosX()+1,(int)client.getPosY(),(int)client.getPosZ()),false);
										}
									} else {
										BotU.jump(client, -5, "z");
										o += 4;
										ThreadU.sleep(100);
									}
								} else {
									BotU.jump(client, -4, "z");
									o+=3;
									ThreadU.sleep(100);
								}
							} else {
								BotU.jump(client, -3, "z");
								o+=2;
								ThreadU.sleep(100);
							}
						} else {
							BotU.jump(client, -2, "z");
							o++;
							ThreadU.sleep(100);
						}
					} else {
						BotU.walk(client, -1, "z");
						ThreadU.sleep(50);
					}
				} else {
					if (botCanWalkAt(client.getPosX(),client.getPosY()+1,client.getPosZ()-1)) {
						BotU.teleport(client, 0, 1, -1);
						ThreadU.sleep(250);
					} else {
						Position pos2 = new Position((int)client.getPosX(),(int)client.getPosY()+1,(int)client.getPosZ()-1);
						if (getblockid(pos2) != 0) {
							BotU.mineBlock(client, pos2 ,false);
							while (!botCanWalkThrough(getblockid(pos2))) {
				        		ThreadU.sleep(200);
				        	}
						}
						Position pos3 = new Position((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()-1);
						if (getblockid(pos3) != 0) {
							BotU.mineBlock(client, pos3,false);
							while (!botCanWalkThrough(getblockid(pos2))) {
				        		ThreadU.sleep(200);
				        	}
						}
					}
				}
			}
		}
    	
    	//---------------------y-------------------
    	
    	if (client.getPosY() != pos.getY()) {
    		if (client.getPosY() > pos.getY()) {
    			for (int i = pos.getY(); i < (int)client.getPosY();i++) {
    				int yp = (int)client.getPosY()-1;
    				BotU.mineBlock(client, new Position((int)client.getPosX(),yp,(int)client.getPosZ()), false);
    			}
    		} else {
	    		if (botCanWalkThrough(blockUpwBot(client))) {
	    			
	    		}
    		}
    	}
    	if (retryifbnip) {
	    	if ((int)client.getPosX() != pos.getX()) {
	    		walkTo(client, pos,true);
	    		ThreadU.sleep(500);
	    	} else if ((int)client.getPosY() != pos.getY()) {
	    		walkTo(client, pos,true);
	    		ThreadU.sleep(500);
	    	} else if ((int)client.getPosZ() != pos.getZ()) {
	    		walkTo(client, pos,true);
	    		ThreadU.sleep(500);
	    	} else {
	    		return true;
	    	}
    	} else {
    		return true;
    	}
    	return false;
	}
	
	/*public boolean executeMove(String move) {
		String[] splitted = move.split(" ");
		switch (splitted[0]) {
			case "walk":
				int howlong = Integer.parseInt(splitted[1]);
				String vector = splitted[2];
				BotU.walk(bot, howlong, vector);
				return true;
			default:
				return false;
		}
			
	}*/
	
	public List INeedAPlan(Position target, Position start) {
		return null;
	}
	
	public static boolean botCanWalkAt(Position pos) {
    	return botCanWalkThrough(getblockid(pos.getX(),pos.getY(),pos.getZ())) && botCanWalkThrough(getblockid(pos.getX(),pos.getY()+1,pos.getZ())) && !botCanWalkThrough(getblockid(pos.getX(),pos.getY()-1,pos.getZ()));
    }
    
    public static boolean botCanWalkAt(double x, double y, double z) {
    	return botCanWalkAt(new Position((int)x,(int)y,(int)z));
    }
    
    public static boolean botCanWalkThrough(int bid) {
    	switch (bid) {
    		case 0://air
    			return true;
    		case 6://tree sp
    			return true;
    		case 31://grass
    			return true;
    		case 32://dead brush
    			return true;
    		case 37://flower
    			return true;
    		case 38://too
    			return true;
    		case 39://mushroom
    			return true;
    		case 55://redstone dust
    			return true;
    		case 68://sign
    			return true;
    		case 69://рычаг
    			return true;
    		case 70://плита
    			return true;
    		case 72://плита
    			return true;
    		case 75://torch
    			return true;
    		case 76:
    			return true;
    		case 77://button
    			return true;
    		case 78://snow
    			return true;
    		default:
    			return false;
    	}
    }
    
    public static int getblockid (Position pos) {
    	try {
	    	BlockState block = Main.getWorld().getBlock(pos);
	    	if (block == null) return -1;
	    	int blockid = block.getId();
			return (int) blockid;
    	} catch (Exception e) {
    		e.printStackTrace();
			return -1;
		}
    }
    
    public static int getblockid (double x,double y,double z) {
    	BlockState block = Main.getWorld().getBlock(new Position((int)x,(int)y,(int)z));
    	if (block == null) return 0;
    	int blockid = block.getId();
		return (int) blockid;
    }
    
    public int blockUnderBot(Bot client) {
    	Position pos = new Position((int) Math.floor(client.getPosX()),(int)Math.floor(client.getPosY()) - 1,(int)Math.floor(client.getPosZ()));
    	int id = getblockid(pos);
    	return id;
    }
    
    public static int blockUpwBot(Bot client) {
    	Position pos = new Position((int) Math.floor(client.getPosX()),(int)Math.floor(client.getPosY()) + 2,(int)Math.floor(client.getPosZ()));
    	int id = getblockid(pos);
    	return id;
    }
    
    public static void walk(Bot client, Position pos) {
    	
    }
	
}

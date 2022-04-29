package net.PRP.MCAI.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.google.common.collect.AbstractIterator;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.MinecraftData.Type;

public class VectorUtils {
	/**
	 * 
	 * @param client
	 * @param целевая точка
	 * @param radius
	 * @return позицию близжайшую к указаной в заданом радиусе.
	 */
	public static Vector3D func_31(Bot client, Vector3D pos, int radius) {
		List<Vector3D> positions = filterByRadius(getAllInBox(pos, radius),pos,radius);
		List<Vector3D> normal = new CopyOnWriteArrayList<>();
		for (Vector3D position : positions) {
			if (!positionIsSafe(position, client)) {
				positions.remove(position);
			}
		}
		for (Vector3D position : positions) {
			if (client.pathfinder.testForPath(position)) {
				normal.add(position);
			}
		}
		if (normal.isEmpty()) {
			normal.addAll(filterByRadius(getAllInBox(pos, radius),pos,radius));
		}
		return getNear(pos, normal);
	}
	
	public static Vector3D vector(float Yaw, float Pitch, double speed) {
        Vector3D vector = Vector3D.ORIGIN;
        double rotX = Yaw;
        double rotY = 0;//pitch

        //vector.setY(-Math.sin(Math.toRadians(rotY)));

        double xz = Math.cos(Math.toRadians(rotY));

        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        vector = vector.multiply(speed);
        return vector;
    }
	
	public static List<Vector3D> filterByRadius(List<Vector3D> positions, Vector3D target, int radius) {
		for (Vector3D position : positions) {
			if (sqrt(target, position) > radius) positions.remove(position);
		}
		return positions;
	}
	
	public static boolean equals(Vector3D one, Vector3D two) {
		if (one == null || two == null) return false;
		//System.out.println(one.toString() + " <<>> " + two.toString());
		if (one.getX() == two.getX() && one.getY() == two.getY() && one.getZ() == two.getZ()) return true;
		return false;
	}
	
	public static boolean equalsInt(Vector3D one, Vector3D two) {
		if (one == null || two == null) return false;
		//System.out.println(one.toStringInt() + " <<>> " + two.toStringInt());
		if ((int)Math.floor(one.getX()) == (int)Math.floor(two.getX()) && (int)Math.floor(one.getY()) == (int)Math.floor(two.getY()) && (int)Math.floor(one.getZ()) == (int)Math.floor(two.getZ())) return true;
		return false;
	}
	
	public static boolean equalsIntNoY(Vector3D one, Vector3D two) {
		if (one == null || two == null) return false;
		if ((int)one.getX() == (int)two.getX() && (int)one.getZ() == (int)two.getZ()) return true;
		return false;
	}
	
	public static boolean equalsForPF(Vector3D one, Vector3D two, boolean nonY) {
		if (one == null || two == null) return false;
		if (nonY) {
			if ((int)one.getX() == (int)two.getX() && (int)one.getZ() == (int)two.getZ()) return true;
			return false;
		} else {
			if ((int)one.getX() == (int)two.getX() && (int)one.getY() == (int)two.getY() && (int)one.getZ() == (int)two.getZ()) return true;
			return false;
		}
	}
	
	public static Vector3D convert(Position pos) {
		return new Vector3D(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static boolean positionIsSafe(Vector3D pos, Bot client) {
		boolean a = BTavoid(client.getWorld().getBlock(pos).type) && BTavoid(client.getWorld().getBlock(pos.add(0,1,0)).type) && icanstayhere(client.getWorld().getBlock(pos.add(0,-1,0)).type);
		//System.out.println(pos+" is safe:"+a);
		return a;
	}
	
	public static boolean BTavoid(Type bt) {
		return bt == Type.AIR || bt == Type.AVOID || bt == Type.VOID;
	}
	
	public static boolean icanstayhere(Type bt) {
		return bt == Type.HARD || bt == Type.UNBREAKABLE;
	}
	
	public static boolean BThard(Type bt) {
		return bt == Type.HARD;
	}
	
	public static double sqrt(Vector3D one, Vector3D two) {
		double distance = Math.sqrt(Math.pow(one.getX() - two.getX(), 2) + Math.pow(one.getY() - two.getY(), 2) + Math.pow(one.getZ() - two.getZ(), 2));
		return distance;
	}
	
	public static double sqrt2D(Vector3D one, Vector3D IIdPoint) {
		double distance = Math.sqrt(Math.pow(one.getX() - IIdPoint.getX(), 2) + Math.pow(one.getY(), 2) + Math.pow(one.getZ() - IIdPoint.getZ(), 2));
		return distance;
	}
	
	public static Iterable<Vector3D> getAllInBox(Vector3D from, Vector3D to) {
        return getAllInBox(
        		(int)Math.min(from.getBlockX(), to.getBlockX()), (int)Math.min(from.getBlockY(), to.getBlockY()), (int)Math.min(from.getBlockZ(), to.getBlockZ()),
                (int)Math.max(from.getBlockX(), to.getBlockX()), (int)Math.max(from.getBlockY(), to.getBlockY()), (int)Math.max(from.getBlockZ(), to.getBlockZ())
        );
    }
	
	public static Vector3D getNear(Vector3D target, List<Vector3D> allPos) {
        Vector3D minpos = null;
        List<Vector3D> temp = new ArrayList<>();
        for (Vector3D position : allPos) {
        	if (!equalsInt(position, target)) {
	        	double distance = sqrt(position, target);
	        	if (minpos == null) {
	        		minpos = position;
	        	} else {
	        		double distanceminpos = sqrt(minpos, target);
	        		if (distance < distanceminpos) {
	        			minpos = position;
	        		}
	        	}
        	}
        }
        temp.add(minpos);
        for (Vector3D position : allPos) {
        	if (sqrt(minpos, target) == sqrt(position, target)) {
        		temp.add(position);
        	}
        }
        return temp.get(MathU.rnd(0, temp.size()-1));
    }
	
	public static Vector3D getNearBlock(Vector3D target, List<Block> allPos) {
        Vector3D minpos = null;
        List<Vector3D> temp = new ArrayList<>();
        for (Block b : allPos) {
        	Vector3D position = b.pos;
        	double distance = Math.sqrt(Math.pow(position.getX() - target.getX(), 2) + Math.pow(position.getY() - target.getY(), 2) + Math.pow(position.getZ() - target.getZ(), 2));
        	if (minpos == null) {
        		minpos = position;
        	} else {
        		double distanceminpos = Math.sqrt(Math.pow(minpos.getX() - target.getX(), 2) + Math.pow(minpos.getY() - target.getY(), 2) + Math.pow(minpos.getZ() - target.getZ(), 2));
        		if (distance < distanceminpos) {
        			minpos = position;
        		}
        	}
        }
        temp.add(minpos);
        for (Block b : allPos) {
        	Vector3D position = b.pos;
        	if (sqrt(minpos, target) == sqrt(position, target)) {
        		temp.add(position);
        	}
        }
        return temp.get(MathU.rnd(0, temp.size()-1));
    }
	
	public static Vector3D findNearestBlockById(Bot client, int id) {
    	List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	int x = (int)client.getEyeLocation().getPosX();
    	int y = (int)client.getEyeLocation().getPosY();
    	int z = (int)client.getEyeLocation().getPosZ();
    	int radius = 30;
    	Vector3D pos = null;
    	for (int i = 1; i < radius; i++) {
    		int localf = 0;
    		int xs = x-i;
    		int ys = y-i;
    		if (ys < 1) ys = 0;
    		int yi = y+i;
    		if (yi > 255) yi = 255;
    		int zs = z-i;
    		for (int y1 = ys; y1 < yi; y1++) {
    			for (int x1 = xs; x1 < x+i; x1++) {
                    for (int z1 = zs; z1 < z+i; z1++) {
                    	int b;
                    	//log("ищу на x:"+x1+" y:"+y1+" z:"+z1);
                    	b = new Vector3D(x1,y1,z1).getBlock(client).id;
                        if (b == id && !pos.getBlock(client).touchLiquid(client)) {
                        	localf++;
                        	pos = new Vector3D(x1,y1,z1);
                        	positions.add(pos);
                        }
                    }
                }
            }
    		if (localf > 0 && !positions.isEmpty()) {
    			pos = getNear(new Vector3D((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	    	return pos;
    		}
    	}
    	pos = getNear(new Vector3D((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	return pos;
    }
	
	public static Vector3D func_1488(Bot client, List<Integer> ids, List<Vector3D> blacklist) {
		List<Block> blocks = new CopyOnWriteArrayList<>();
		blocks.addAll(client.vis.getVisibleBlocks());
		for (Block a : blocks) {
			if (blacklist.contains(a.pos) || a.touchLiquid(client) || !ids.contains(a.id)) {
				blocks.remove(a);
			}
		}
		if (blocks.isEmpty()) return null;
		return getNearBlock(client.getEyeLocation(), blocks);
	}
	
	/*
	 * ищет блок по названию
	 */
	public static Vector3D findBlockByName(Bot client, String name, List<Vector3D> blacklist) {
		Vector3D tb = findblockByString(client, name, blacklist, 30);
		if (tb != null) return tb;
		
		List<Block> blocks = new CopyOnWriteArrayList<>();
		blocks.addAll(client.vis.getVisibleBlocks());
		for (Block a : blocks) {
			if (a.touchLiquid(client) || !name.contains(a.getName())) {
				blocks.remove(a);
				client.rl.blacklist.add(a.pos);
			} else if (blacklist.contains(a.pos)) {
				blocks.remove(a);
			}
		}
		if (blocks.isEmpty()) return null;
		return getNearBlock(client.getEyeLocation(), blocks);
	}
	
	/*
	 * ищет блок по списку из названий
	 */
	public static Vector3D func_32(Bot client, List<String> namelist, List<Vector3D> blacklist) {
		Vector3D tb = findblockByLOS(client, namelist, blacklist, 30);
		if (tb != null) return tb;
		
		List<Block> blocks = new CopyOnWriteArrayList<>();
		blocks.addAll(client.vis.getVisibleBlocks());
		for (Block a : blocks) {
			if (a.touchLiquid(client) || !namelist.contains(a.getName())) {
				blocks.remove(a);
				client.rl.blacklist.add(a.pos);
			} else if (blacklist.contains(a.pos)) {
				blocks.remove(a);
			}
		}
		if (blocks.isEmpty()) return null;
		return getNearBlock(client.getEyeLocation(), blocks);
	}
	
	public static Vector3D func_32(Bot client, List<String> d2, List<Vector3D> blacklist, int radius) {
		Vector3D tb = findblockByLOS(client, d2, blacklist, radius);
		if (tb != null) return tb;
		
		List<Block> blocks = new CopyOnWriteArrayList<>();
		blocks.addAll(client.vis.getVisibleBlocks());
		for (Block a : blocks) {
			if (a.touchLiquid(client) || !d2.contains(a.getName())) {
				blocks.remove(a);
				client.rl.blacklist.add(a.pos);
			} else if (blacklist.contains(a.pos)) {
				blocks.remove(a);
			}
		}
		if (blocks.isEmpty()) return null;
		return getNearBlock(client.getEyeLocation(), blocks);
	}
	
	public static Vector3D randomPointInRaduis(Bot client, int min) {
		int tryy = 0;
		while (true) {
			tryy++;
			if (tryy > 20) return null;
			int max = client.getWorld().renderDistance*16;
			int x;
			if (MathU.rnd(0, 1) == 1) 
				x = MathU.rnd(min, max);
			else 
				x = MathU.rnd(-min, -max);
			int z;
			if (MathU.rnd(0, 1) == 1) 
				z = MathU.rnd(min, max);
			else 
				z = MathU.rnd(-min, -max);
			Vector3D pos = new Vector3D(x+client.getPosX(), 256, z+client.getPosZ());
			while (true) {
				if (!pos.add(0,-1,0).getBlock(client).isAvoid() && positionIsSafe(pos, client) && client.pathfinder.testForPath(pos)) {
					return pos;
				}
				pos = pos.add(0,-1,0);
				if (pos.y < 0) break;
			}
		}
	}
	
	@SuppressWarnings({ "deprecation", "serial" })
	public static Block placeBlockNear(Bot client, String block) {
		Vector3D pos = findPosForPlace(client);
		if (client.playerInventory.hotbarContain(block, 1)) {
			Integer slot = client.playerInventory.getHotbarContain(block, 1);
			if (slot == null) return null;
			BotU.SetSlot(client, slot-36);
			client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
			client.getSession().send(new ClientPlayerPlaceBlockPacket(pos.translate(), BlockFace.DOWN, Hand.MAIN_HAND, 0,0,0, false));
			return pos.getBlock(client);
		} else if (client.playerInventory.invContain(block, 1)) {
			client.playerInventory.fromInventoryToHotbar(new CopyOnWriteArrayList<>() {{add(block);}}, 1);
			ThreadU.sleep(10);
			Integer slot = client.playerInventory.getHotbarContain(block, 1);
			if (slot == null) return null;
			BotU.SetSlot(client, slot-36);
			client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
			client.getSession().send(new ClientPlayerPlaceBlockPacket(pos.translate(), BlockFace.DOWN, Hand.MAIN_HAND, 0,0,0, false));
			return pos.getBlock(client);
		}
		return null;
	}
	
	public static Vector3D findPosForPlace(Bot client) {
    	List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	Vector3D ps = client.getEyeLocation();
    	int x = (int)ps.getPosX();
    	int y = (int)ps.getPosY();
    	int z = (int)ps.getPosZ();
    	int radius = (int)Main.getsett("maxpostoblock");
    	Vector3D pos = null;
    	for (int i = 1; i <= radius; i++) {
    		int xs = x-i;
    		int ys = y-i;
    		if (ys < 0) ys = 0;
    		int yi = y+i;
    		if (yi > 256) yi = 256;
    		int zs = z-i;
    		for (int y1 = ys; y1 < yi; y1++) {
    			for (int x1 = xs; x1 < x+i; x1++) {
                    for (int z1 = zs; z1 < z+i; z1++) {
                    	pos = new Vector3D(x1,y1,z1);
                		if (pos.getBlock(client) != null && pos.getBlock(client).id == 0 && pos.add(0, -1, 0).getBlock(client).ishard()) {
                			positions.add(pos);
                		}
                    }
                }
            }
    	}
    	pos = VectorUtils.getNear(client.getPositionInt(),positions);
    	return pos;
    }
	
	public static Vector3D findNearestBlockByArrayId(Bot client, List<Integer> ids, List<Vector3D> blacklist) {
    	List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	int x = (int)client.getEyeLocation().getPosX();
    	int y = (int)client.getEyeLocation().getPosY();
    	int z = (int)client.getEyeLocation().getPosZ();
    	int radius = 30;
    	Vector3D pos = null;
    	for (int i = 1; i < radius; i++) {
    		int localf = 0;
    		int xs = x-i;
    		int ys = y-i;
    		if (ys < 1) ys = 0;
    		int yi = y+i;
    		if (yi > 255) yi = 255;
    		int zs = z-i;
    		for (int y1 = ys; y1 < yi; y1++) {
    			for (int x1 = xs; x1 < x+i; x1++) {
                    for (int z1 = zs; z1 < z+i; z1++) {
                    	Vector3D a = new Vector3D(x1,y1,z1);
                        if (ids.contains(a.getBlock(client).id)) {
                        	if (!blacklist.contains(a) && !a.getBlock(client).touchLiquid(client)) {
                        		positions.add(a);
                        		localf++;
                        	}
                        }
                    }
                }
            }
    		if (localf > 0 && !positions.isEmpty()) {
    			pos = getNear(new Vector3D((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	    	return pos;
    		}
    	}
    	pos = getNear(new Vector3D((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	return pos;
    }
	
	public static Vector3D findblockByString(Bot client, String nm, List<Vector3D> blacklist, int radius) {
    	List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	int x = (int)client.getEyeLocation().getPosX();
    	int y = (int)client.getEyeLocation().getPosY();
    	int z = (int)client.getEyeLocation().getPosZ();
    	Vector3D pos = null;
    	for (int i = 1; i < radius; i++) {
    		int xs = x-i;
    		int ys = y-i;
    		if (ys < 1) ys = 0;
    		int yi = y+i;
    		if (yi > 255) yi = 255;
    		int zs = z-i;
    		for (int y1 = ys; y1 < yi; y1++) {
    			for (int x1 = xs; x1 < x+i; x1++) {
                    for (int z1 = zs; z1 < z+i; z1++) {
                    	Vector3D a = new Vector3D(x1,y1,z1);
                		if (a.getBlock(client).name.toLowerCase().contains(nm)) {
                			if (!blacklist.contains(a) && !a.getBlock(client).touchLiquid(client) && !positions.contains(a)) {
                				positions.add(a);
                			}
                		}
                    }
                }
            }
    		if (!positions.isEmpty()) {
    			pos = getNear(new Vector3D((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	    	return pos;
    		}
    	}
    	pos = getNear(new Vector3D((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	return pos;
    }
	
	public static Vector3D findblockByLOS(Bot client, List<String> nms, List<Vector3D> blacklist, int radius) {
    	List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	int x = (int)client.getEyeLocation().getPosX();
    	int y = (int)client.getEyeLocation().getPosY();
    	int z = (int)client.getEyeLocation().getPosZ();
    	Vector3D pos = null;
    	for (int i = 1; i < radius; i++) {
    		int xs = x-i;
    		int ys = y-i;
    		if (ys < 1) ys = 0;
    		int yi = y+i;
    		if (yi > 255) yi = 255;
    		int zs = z-i;
    		for (int y1 = ys; y1 < yi; y1++) {
    			for (int x1 = xs; x1 < x+i; x1++) {
                    for (int z1 = zs; z1 < z+i; z1++) {
                    	Vector3D a = new Vector3D(x1,y1,z1);
                    	nms.forEach((name) -> { 
                    		if (a.getBlock(client).name.toLowerCase().contains(name)) {
                    			if (!blacklist.contains(a) && !a.getBlock(client).touchLiquid(client) && !positions.contains(a)) {
                    				positions.add(a);
                    			}
                    		}
                    	});
                    }
                }
            }
    		if (!positions.isEmpty()) {
    			pos = getNear(new Vector3D((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	    	return pos;
    		}
    	}
    	pos = getNear(new Vector3D((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	return pos;
    }
	
	public static List<Vector3D> getAllInBox(Vector3D pos, int radius) {
		List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	int x = (int)pos.getPosX();
    	int y = (int)pos.getPosY();
    	int z = (int)pos.getPosZ();
    	for (int i = 1; i < radius; i++) {
    		int xs = x-i;
    		int ys = y-i;
    		if (ys < 1) ys = 0;
    		int yi = y+i;
    		if (yi > 255) yi = 255;
    		int zs = z-i;
    		for (int y1 = ys; y1 < yi; y1++) {
    			for (int x1 = xs; x1 < x+i; x1++) {
                    for (int z1 = zs; z1 < z+i; z1++) {
                        positions.add(new Vector3D(x1,y1,z1));
                    }
                }
            }
    	}
    	return positions;
	}
	
	public static List<Vector3D> getAllInBoxWithBlackList(Vector3D pos, int radius, List<Vector3D> blacklist) {
		List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	int x = (int)pos.getPosX();
    	int y = (int)pos.getPosY();
    	int z = (int)pos.getPosZ();
    	for (int i = 1; i < radius; i++) {
    		int xs = x-i;
    		int ys = y-i;
    		if (ys < 1) ys = 0;
    		int yi = y+i;
    		if (yi > 255) yi = 255;
    		int zs = z-i;
    		for (int y1 = ys; y1 < yi; y1++) {
    			for (int x1 = xs; x1 < x+i; x1++) {
                    for (int z1 = zs; z1 < z+i; z1++) {
                    	Vector3D a = new Vector3D(x1,y1,z1);
                        if (!blacklist.contains(a)) positions.add(a);
                    }
                }
            }
    	}
    	return positions;
	}

	public static Iterable<Vector3D> getAllInBox(final int fromX, final int fromY, final int fromZ, final int toX, final int toY, final int toZ) {
        final Vector3D posMin = new Vector3D(Math.min(fromX, toX), Math.min(fromY, toY), Math.min(fromZ, toZ));
        final Vector3D posMax = new Vector3D(Math.max(fromX, toX), Math.max(fromY, toY), Math.max(fromZ, toZ));
        return new Iterable<Vector3D>() {
            @Override
            public Iterator<Vector3D> iterator() {
                return new AbstractIterator<Vector3D>() {
                    private Vector3D pos = null;
                    private int x;
                    private int y;
                    private int z;

                    @Override
                    protected Vector3D computeNext() {
                        if (this.pos == null) {
                            this.x = (int) posMin.getX();
                            this.y = (int) posMin.getY();
                            this.z = (int) posMin.getZ();
                            this.pos = new Vector3D(this.x, this.y, this.z);
                            return this.pos;
                        }

                        if (VectorUtils.equals(this.pos, posMax)) {
                            return this.endOfData();
                        }

                        if (this.x < posMax.getX()) {
                            this.x++;
                        } else if (this.y < posMax.getY()) {
                            this.x = (int) posMin.getX();
                            this.y++;
                        } else if (this.z < posMax.getZ()) {
                            this.x = (int) posMin.getX();
                            this.y = (int) posMin.getY();
                            this.z++;
                        }

                        this.pos.x = this.x;
                        this.pos.y = this.y;
                        this.pos.z = this.z;
                        return this.pos;
                    }
                };
            }
        };
    }
	
	/**
	 * абсолютно ненужные и допотопные методы но пусть останутся
	 */
	public static Vector3D botCanTouchBlockAt(Bot client, Vector3D blockPos) {
    	List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	if (BTavoid(new Vector3D(blockPos.getX(),blockPos.getY()+1,blockPos.getZ()).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY()+1,blockPos.getZ()));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()-1).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()-1));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX()-1,blockPos.getY(),blockPos.getZ()).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX()-1,blockPos.getY(),blockPos.getZ()));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()+1).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()+1));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX()+1,blockPos.getY(),blockPos.getZ()).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX()+1,blockPos.getY(),blockPos.getZ()));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX(),blockPos.getY()-2,blockPos.getZ()).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY()-2,blockPos.getZ()));
    	}
    	if (positions.size() <= 0) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY()+1,blockPos.getZ()));
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()-1));
    		positions.add(new Vector3D(blockPos.getX()-1,blockPos.getY(),blockPos.getZ()));
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()+1));
    		positions.add(new Vector3D(blockPos.getX()+1,blockPos.getY(),blockPos.getZ()));
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY()-2,blockPos.getZ()));
    	} else {}
    	return getNear(new Vector3D((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    }
	
	public static Vector3D randomOfBotCanTouchBlockAt(Bot client, Vector3D blockPos) {
    	List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	if (BTavoid(new Vector3D(blockPos.getX(),blockPos.getY()+1,blockPos.getZ()).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY()+1,blockPos.getZ()));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()-1).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()-1));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX()-1,blockPos.getY(),blockPos.getZ()).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX()-1,blockPos.getY(),blockPos.getZ()));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()+1).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()+1));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX()+1,blockPos.getY(),blockPos.getZ()).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX()+1,blockPos.getY(),blockPos.getZ()));
    	}
    	if (BTavoid(new Vector3D(blockPos.getX(),blockPos.getY()-2,blockPos.getZ()).getBlock(client).type)) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY()-2,blockPos.getZ()));
    	}
    	if (positions.size() <= 0) {
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY()+1,blockPos.getZ()));
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()-1));
    		positions.add(new Vector3D(blockPos.getX()-1,blockPos.getY(),blockPos.getZ()));
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY(),blockPos.getZ()+1));
    		positions.add(new Vector3D(blockPos.getX()+1,blockPos.getY(),blockPos.getZ()));
    		positions.add(new Vector3D(blockPos.getX(),blockPos.getY()-2,blockPos.getZ()));
    	} else {}
    	return positions.get(MathU.rnd(0, positions.size()-1));
    }
}

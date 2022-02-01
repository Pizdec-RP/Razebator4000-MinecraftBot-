package net.PRP.MCAI.utils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.google.common.collect.AbstractIterator;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.minecraft.server.v1_12_R1.Blocks;

public class VectorUtils {
	
	public static Vector3D func_31(Bot client, Vector3D pos, int radius) {
		List<Vector3D> positions = getAllInBox(pos, radius);
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
		int i = normal.size()-1;
		if (normal.isEmpty()) return null;
		return normal.get(MathU.rnd(0, i));
	}
	
	public static boolean equals(Vector3D one, Vector3D two) {
		//System.out.println(one.toString() + " <<>> " + two.toString());
		if (one.getX() == two.getX() && one.getY() == two.getY() && one.getZ() == two.getZ()) return true;
		return false;
	}
	
	public static boolean equalsInt(Vector3D one, Vector3D two) {
		//System.out.println(one.toStringInt() + " <<>> " + two.toStringInt());
		if ((int)one.getX() == (int)two.getX() && (int)one.getY() == (int)two.getY() && (int)one.getZ() == (int)two.getZ()) return true;
		return false;
	}
	
	public static boolean equalsIntNoY(Vector3D one, Vector3D two) {
		if ((int)one.getX() == (int)two.getX() && (int)one.getZ() == (int)two.getZ()) return true;
		return false;
	}
	
	public static boolean equalsForPF(Vector3D one, Vector3D two, boolean nonY) {
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
		//System.out.println(pos.toStringInt());
		//System.out.println(BTavoid(client.getWorld().getBlock(pos).type)+" "+client.getWorld().getBlock(pos).state+" "+BTavoid(client.getWorld().getBlock(pos.add(0,1,0)).type)+" "+client.getWorld().getBlock(pos.add(0,1,0)).type+" "+icanstayhere(client.getWorld().getBlock(pos.add(0,-1,0)).type)+" "+client.getWorld().getBlock(pos.add(0,-1,0)).type);
		return BTavoid(client.getWorld().getBlock(pos).type) && BTavoid(client.getWorld().getBlock(pos.add(0,1,0)).type) && icanstayhere(client.getWorld().getBlock(pos.add(0,-1,0)).type);
	}
	
	public static boolean BTavoid(Type bt) {
		if (bt == Type.AIR || bt == Type.AVOID || bt == Type.VOID) {
			return true;
		}
		return false;
	}
	
	public static boolean icanstayhere(Type bt) {
		return bt == Type.HARD || bt == Type.UNBREAKABLE;
	}
	
	public static boolean BThard(Type bt) {
		if (bt == Type.HARD) {
			return true;
		}
		return false;
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
        for (Vector3D position : allPos) {
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
        return minpos;
    }
	
	public static Vector3D getNearBlock(Vector3D target, List<Block> allPos) {
        Vector3D minpos = null;
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
        return minpos;
    }
	
	public static Vector3D findSafePointInRadius(Vector3D position, int rad) {
		Vector3D aye = position.add(MathU.rnd(-rad, rad), 0, MathU.rnd(-rad, rad));
		//System.out.println(aye);
		return aye;
	}
	
	public static Vector3D findNearestBlockById(Bot client, int id) {
    	List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	int x = (int)client.getPosX();
    	int y = (int)client.getPosY();
    	int z = (int)client.getPosZ();
    	int radius = 50;
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
    		if (positions.size() > 1) {
    			for (Vector3D poss : positions) {
    				if (VectorUtils.equalsInt(new Vector3D(poss.x,poss.y+1,poss.z), client.getPosition())) positions.remove(poss);
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
		return getNearBlock(client.getPosition(), blocks);
	}
	
	public static Vector3D findNearestBlockByArrayId(Bot client, List<Integer> ids, List<Vector3D> blacklist) {
    	List<Vector3D> positions = new CopyOnWriteArrayList<>();
    	int x = (int)client.getPosX();
    	int y = (int)client.getPosY();
    	int z = (int)client.getPosZ();
    	int radius = 50;
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
    		if (positions.size() > 1) {
    			for (Vector3D poss : positions) {
    				if (VectorUtils.equalsInt(new Vector3D(poss.x,poss.y+1,poss.z), client.getPosition())) positions.remove(poss);
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
    	return positions.get(MathU.rnd(0, positions.size()));
    }
}

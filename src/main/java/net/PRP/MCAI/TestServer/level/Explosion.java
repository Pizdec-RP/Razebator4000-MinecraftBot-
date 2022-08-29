package net.PRP.MCAI.TestServer.level;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Vector3D;

public class Explosion {
	private final int rays = 16;
	private Vector3D source;
	private final double size;
	private List<Vector3D> affectedBlocks = new ArrayList<>();
	private final double stepLen = 0.3d;
	
	public Explosion(Vector3D source, double size) {
		this.source = source;
		this.size = size;
	}
	
	public List<Vector3D> getAffectedBlocks() {
		return this.affectedBlocks;
	}
	
	public void explode() {
		
		Vector3D vector = new Vector3D(0, 0, 0);
        Vector3D vBlock = new Vector3D(0, 0, 0);
        
		int mRays = this.rays - 1;
        for (int i = 0; i < this.rays; ++i) {
            for (int j = 0; j < this.rays; ++j) {
                for (int k = 0; k < this.rays; ++k) {
                    if (i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents((double) i / (double) mRays * 2d - 1, (double) j / (double) mRays * 2d - 1, (double) k / (double) mRays * 2d - 1);
                        double len = vector.length();
                        vector.setComponents((vector.x / len) * this.stepLen, (vector.y / len) * this.stepLen, (vector.z / len) * this.stepLen);
                        double pointerX = this.source.x;
                        double pointerY = this.source.y;
                        double pointerZ = this.source.z;

                        for (double blastForce = this.size * (ThreadLocalRandom.current().nextInt(700, 1301)) / 1000d; blastForce > 0; blastForce -= this.stepLen * 0.75d) {
                            int x = (int) pointerX;
                            int y = (int) pointerY;
                            int z = (int) pointerZ;
                            vBlock.x = pointerX >= x ? x : x - 1;
                            vBlock.y = pointerY >= y ? y : y - 1;
                            vBlock.z = pointerZ >= z ? z : z - 1;
                            if (vBlock.y < 0 || vBlock.y > 255) {
                                break;
                            }
                            Block block = Multiworld.getBlock(vBlock);

                            if (block.getId() != 0) {
                                blastForce -= (block.getResistance() / 5 + 0.3d) * this.stepLen;
                                if (blastForce > 0) {
                                    if (!this.affectedBlocks.contains(block.getPos())) {
                                        this.affectedBlocks.add(block.getPos());
                                    }
                                }
                            }
                            pointerX += vector.x;
                            pointerY += vector.y;
                            pointerZ += vector.z;
                        }
                    }
                }
            }
        }
	}
}

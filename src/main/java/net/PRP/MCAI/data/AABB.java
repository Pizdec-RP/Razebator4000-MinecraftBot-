package net.PRP.MCAI.data;

import java.util.ArrayList;
import java.util.List;

public class AABB {
	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;

	public AABB(double x0, double y0, double z0, double x1, double y1, double z1) {
	    this.minX = x0;
	    this.minY = y0;
	    this.minZ = z0;
	    this.maxX = x1;
	    this.maxY = y1;
	    this.maxZ = z1;
	}
	
	public AABB clone() {
	    return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}
	
	public AABB floor() {
	    this.minX = Math.floor(this.minX);
	    this.minY = Math.floor(this.minY);
	    this.minZ = Math.floor(this.minZ);
	    this.maxX = Math.floor(this.maxX);
	    this.maxY = Math.floor(this.maxY);
	    this.maxZ = Math.floor(this.maxZ);
	    return this;
	}
	
	public AABB grow(double x, double y, double z) {
        return new AABB(this.getMinX() - x, this.getMinY() - y, this.getMinZ() - z, this.getMaxX() + x, this.getMaxY() + y, this.getMaxZ() + z);
    }
	
	public List<Vector3D> getCorners() {
		List<Vector3D> c = new ArrayList<>();
		
		c.add(new Vector3D(minX,minY,minZ));
		c.add(new Vector3D(minX,maxY,minZ));
		c.add(new Vector3D(minX,minY,maxZ));
		c.add(new Vector3D(minX,maxY,maxZ));
		
		c.add(new Vector3D(maxX,minY,minZ));
		c.add(new Vector3D(maxX,maxY,minZ));
		c.add(new Vector3D(maxX,minY,maxZ));
		c.add(new Vector3D(maxX,maxY,maxZ));
		return c;
	}
	
	public AABB extend(double dx, double dy, double dz) {
	    if (dx < 0) this.minX += dx;
	    else this.maxX += dx;

	    if (dy < 0) this.minY += dy;
	    else this.maxY += dy;

	    if (dz < 0) this.minZ += dz;
	    else this.maxZ += dz;

	    return this;
	}
	
	public AABB offset(Vector3D a) {
		this.minX += a.x;
	    this.minY += a.y;
	    this.minZ += a.z;
	    this.maxX += a.x;
	    this.maxY += a.y;
	    this.maxZ += a.z;
	    return this;
	}
	
	public AABB contract(double x, double y, double z) {
	    this.minX += x;
	    this.minY += y;
	    this.minZ += z;
	    this.maxX -= x;
	    this.maxY -= y;
	    this.maxZ -= z;
	    return this;
	}
	
	public AABB expand(double x, double y, double z) {
	    this.minX -= x;
	    this.minY -= y;
	    this.minZ -= z;
	    this.maxX += x;
	    this.maxY += y;
	    this.maxZ += z;
	    return this;
	}
	
	public AABB offset(double x, double y, double z) {
	    this.minX += x;
	    this.minY += y;
	    this.minZ += z;
	    this.maxX += x;
	    this.maxY += y;
	    this.maxZ += z;
	    return this;
	}
	
	public double computeOffsetX (AABB other, double offsetX) {
	    if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
	      if (offsetX > 0.0 && other.maxX <= this.minX) {
	        offsetX = Math.min(this.minX - other.maxX, offsetX);
	      } else if (offsetX < 0.0 && other.minX >= this.maxX) {
	        offsetX = Math.max(this.maxX - other.minX, offsetX);
	      }
	    }
	    return offsetX;
	}
	
	public double computeOffsetY (AABB other, double offsetY) {
	    if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
	      if (offsetY > 0.0 && other.maxX <= this.minX) {
	        offsetY = Math.min(this.minX - other.maxX, offsetY);
	      } else if (offsetY < 0.0 && other.minX >= this.maxX) {
	        offsetY = Math.max(this.maxX - other.minX, offsetY);
	      }
	    }
	    return offsetY;
	}
	
	public double computeOffsetZ (AABB other, double offsetZ) {
	    if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
	      if (offsetZ > 0.0 && other.maxX <= this.minX) {
	        offsetZ = Math.min(this.minX - other.maxX, offsetZ);
	      } else if (offsetZ < 0.0 && other.minX >= this.maxX) {
	        offsetZ = Math.max(this.maxX - other.minX, offsetZ);
	      }
	    }
	    return offsetZ;
	}
	
	public boolean collide(AABB other) {
	    return this.minX < other.maxX && this.maxX > other.minX &&
	           this.minY < other.maxY && this.maxY > other.minY &&
	           this.minZ < other.maxZ && this.maxZ > other.minZ;
	}

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public double getMinZ() {
		return minZ;
	}

	public void setMinZ(double minZ) {
		this.minZ = minZ;
	}

	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public void setMaxZ(double maxZ) {
		this.maxZ = maxZ;
	}
	
	public AABB setBB(AABB bb) {
        this.setMinX(bb.getMinX());
        this.setMinY(bb.getMinY());
        this.setMinZ(bb.getMinZ());
        this.setMaxX(bb.getMaxX());
        this.setMaxY(bb.getMaxY());
        this.setMaxZ(bb.getMaxZ());
        return this;
    }
	
	public double calculateXOffset(AABB bb, double x) {
        if (bb.getMaxY() <= this.getMinY() || bb.getMinY() >= this.getMaxY()) {
            return x;
        }
        if (bb.getMaxZ() <= this.getMinZ() || bb.getMinZ() >= this.getMaxZ()) {
            return x;
        }
        if (x > 0 && bb.getMaxX() <= this.getMinX()) {
            double x1 = this.getMinX() - bb.getMaxX();
            if (x1 < x) {
                x = x1;
            }
        }
        if (x < 0 && bb.getMinX() >= this.getMaxX()) {
            double x2 = this.getMaxX() - bb.getMinX();
            if (x2 > x) {
                x = x2;
            }
        }

        return x;
    }

    public double calculateYOffset(AABB bb, double y) {
        if (bb.getMaxX() <= this.getMinX() || bb.getMinX() >= this.getMaxX()) {
            return y;
        }
        if (bb.getMaxZ() <= this.getMinZ() || bb.getMinZ() >= this.getMaxZ()) {
            return y;
        }
        if (y > 0 && bb.getMaxY() <= this.getMinY()) {
            double y1 = this.getMinY() - bb.getMaxY();
            if (y1 < y) {
                y = y1;
            }
        }
        if (y < 0 && bb.getMinY() >= this.getMaxY()) {
            double y2 = this.getMaxY() - bb.getMinY();
            if (y2 > y) {
                y = y2;
            }
        }

        return y;
    }

    public double calculateZOffset(AABB bb, double z) {
        if (bb.getMaxX() <= this.getMinX() || bb.getMinX() >= this.getMaxX()) {
            return z;
        }
        if (bb.getMaxY() <= this.getMinY() || bb.getMinY() >= this.getMaxY()) {
            return z;
        }
        if (z > 0 && bb.getMaxZ() <= this.getMinZ()) {
            double z1 = this.getMinZ() - bb.getMaxZ();
            if (z1 < z) {
                z = z1;
            }
        }
        if (z < 0 && bb.getMinZ() >= this.getMaxZ()) {
            double z2 = this.getMaxZ() - bb.getMinZ();
            if (z2 > z) {
                z = z2;
            }
        }

        return z;
    }

	@Override
	public String toString() {
		return "AABB [minX=" + minX + ", minY=" + minY + ", minZ=" + minZ + ", maxX=" + maxX + ", maxY=" + maxY + ", maxZ=" + maxZ + "]";
	}

	public AABB addCoord(double x, double y, double z) {
        double minX = this.getMinX();
        double minY = this.getMinY();
        double minZ = this.getMinZ();
        double maxX = this.getMaxX();
        double maxY = this.getMaxY();
        double maxZ = this.getMaxZ();

        if (x < 0) minX += x;
        if (x > 0) maxX += x;

        if (y < 0) minY += y;
        if (y > 0) maxY += y;

        if (z < 0) minZ += z;
        if (z > 0) maxZ += z;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

	public AABB setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.setMinX(minX);
        this.setMinY(minY);
        this.setMinZ(minZ);
        this.setMaxX(maxX);
        this.setMaxY(maxY);
        this.setMaxZ(maxZ);
        return this;
    }
	
	
}

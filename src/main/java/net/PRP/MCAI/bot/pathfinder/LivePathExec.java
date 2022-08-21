package net.PRP.MCAI.bot.pathfinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.NeuralNetworkTests.Perceptron;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class LivePathExec {
	private Bot client;
	private Vector3D start;
	private Vector3D end;
	private states state = states.idle;
	public Map<String, Perceptron> pcts = new HashMap<>();
	private List<Vector3D> ableMovements = new ArrayList<>();
	private Vector3D target = null;
	private int execticks = 0;
	
	enum states {
		idle, formoutput, executing, learning;
	}
	
	public void tick() {
		if (state == states.idle) return;
		if (state == states.formoutput) {
			//input: rdistance, blocksAround
			//output
			Map<Vector3D, Double> temp = new HashMap<>();
			for (Vector3D movement : ableMovements) {
				double[] data = new double[37];
				String movementPattern = (int)movement.x+" "+(int)movement.y+" "+(int)movement.z;
				double rdistance = this.rdistance(client.getPositionInt().add(movement));
				double[] blocksAround = formAround();
				int i = 0;
				for (double d : blocksAround) {
					data[i] = d;
					i++;
				}
				data[35] = rdistance;
				double sum = pcts.get(movementPattern).output(data);
				temp.put(movement, sum);
				//BotU.log(movementPattern+" "+String.format("%.5f",sum));
			}
			Entry<Vector3D, Double> max = null;
			for (Entry<Vector3D, Double> entry : temp.entrySet()) {
				if (max == null) {
					max = entry;
				} else if (max.getValue() < entry.getValue()) {
					max = entry;
				}
			}
			target = max.getKey();
			state = states.executing;
			execticks = 0;
		} else if (state == states.executing) {
			execticks++;
			if (target == null || execticks > 100) {
				stop(false);
				return;
			}
			BotU.LookHead(client, target);
			client.pm.Walk();
			if (VectorUtils.equalsInt(client.getPositionInt(), target)) {
				state = states.formoutput;
			} else if (VectorUtils.equalsInt(client.getPositionInt(), end)) {
				stop(true);
			}
		}
	}
	
	public LivePathExec(Bot client) {
		this.client = client;
		pcts.put("1 0 0", new Perceptron(36,0));
		pcts.put("0 0 1", new Perceptron(36,1));
		pcts.put("-1 0 0", new Perceptron(36,2));
		pcts.put("0 0 -1", new Perceptron(36,3));
		pcts.put("1 1 0", new Perceptron(36,4));
		pcts.put("0 1 1", new Perceptron(36,5));
		pcts.put("-1 1 0", new Perceptron(36,6));
		pcts.put("0 1 -1", new Perceptron(36,7));
		pcts.put("1 -1 0", new Perceptron(36,8));
		pcts.put("0 -1 1", new Perceptron(36,9));
		pcts.put("-1 -1 0", new Perceptron(36,10));
		pcts.put("0 -1 -1", new Perceptron(36,11));
		ableMovements.add(new Vector3D(1, 0, 0));//100
		ableMovements.add(new Vector3D(0, 0, 1));//1
		ableMovements.add(new Vector3D(-1, 0, 0));//-1
		ableMovements.add(new Vector3D(0, 0, -1));//-100
		ableMovements.add(new Vector3D(1,-1,0));//-9
		ableMovements.add(new Vector3D(0,-1,1));//-11
		ableMovements.add(new Vector3D(-1,-1,0));//-11
		ableMovements.add(new Vector3D(0,-1,-1));//-2
		ableMovements.add(new Vector3D(1,1,0));//110W
		ableMovements.add(new Vector3D(0,1,1));//11
		ableMovements.add(new Vector3D(-1,1,0));//-110
		ableMovements.add(new Vector3D(0,1,-1));//0
		
	}
	
	public void start(Vector3D end) {
		BotU.chat(client, "started");
		this.start = client.getPosition();
		this.end = end;
		state = states.formoutput;
	}
	
	public void stop(boolean succesfull) {
		BotU.chat(client, "stoped");
		state = states.idle;
		start = null;
		end = null;
		execticks = 0;
		target = null;
	}
	
	public double rdistance(Vector3D nextpos) {
		return Math.max(
				VectorUtils.sqrt(end, client.getPosition()),
				VectorUtils.sqrt(end, nextpos)
				)
				- 
				Math.min(
				VectorUtils.sqrt(end, client.getPosition()),
				VectorUtils.sqrt(end, nextpos)
				);
	}
	
	public void restart() {
		BotU.chat(client, "/tp @p "+start.forCommandD());
	}
	
	public double[] formAround() {
		return formAround(client.getPositionInt());
	}
	
	public double[] formAround(Vector3D pos) {
		double[] data = new double[36];
		int i = 0;
		for (int x = (int) (pos.x - 1); x <= pos.x + 1; x++) {
			for (int z = (int) (pos.z - 1); z <= pos.z + 1; z++) {
				for (int y = (int) (pos.y - 1); y <= pos.y + 2; y++) {
					data[i] = ttn(client.getWorld().getBlock(x, y, z));
					i++;
				}
			}
		}
		return data;
	}
	
	public double ttn(Block b) {
		if (b.isAvoid()) {
			return 10.0D;
		} else if (b.isWater()) {
			return 4.0D;
		} else if (b.isLava()) {
			return 7.0D;
		} else {
			return 0.0D;
		}
	}
	
	public Vector3D calcMovement() {
		
		return null;
	}
	
	public static double[] convertDoubles(List<Double> doubles) {
	    double[] ret = new double[doubles.size()];
	    Iterator<Double> iterator = doubles.iterator();
	    int i = 0;
	    while(iterator.hasNext()) {
	        ret[i] = iterator.next();
	        i++;
	    }
	    return ret;
	}
	
	public double getHeadRotate() {
		return client.getYaw();
	}
	
	public double getDist() {
		return VectorUtils.sqrt(client.getPosition(), end);
	}
	
	public double getSpeed() {
		Vector3D vec1 = client.pm.getVel().abs();
		return (vec1.x + vec1.y + vec1.z) / 3;
	}
}

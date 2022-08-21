package net.PRP.MCAI.NeuralNetworkTests;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Perceptron {
	double[] weight;
	//double[] lastInput = null;
	double alpha = 0.5;
	double loop = 100;
	int ct = 10;
	public int id;
	
	public Perceptron(int weightLongness, int id) {
		this.id = id;
		this.ct = weightLongness;
		this.weight = new double[ct+1];
		double[] temp;
		Random rand=new Random();
		
		try {
			temp = read();
			if (temp.length > 0) {
				this.weight = temp;
			} else {
				for (int i = 0; i < weight.length; i++) {
					weight[i] = rand.nextDouble()-0.5;
				}
				write();
			}
		} catch (Exception e) {
			e.printStackTrace();
			for (int i = 0; i < weight.length; i++) {
				weight[i] = rand.nextDouble()-0.5;
			}
			write();
		}
	}
	
	public void write() {
		try {
			JsonReader reader = new JsonReader(new FileReader("data.json"));
			JsonElement data = JsonParser.parseReader(reader);
			reader.close();
			JsonArray jsonArray = new JsonArray();
			for (double d : weight) {
				jsonArray.add(d);
			}
			data.getAsJsonObject().remove("pct"+id);
			data.getAsJsonObject().add("pct"+id, jsonArray);
			
			FileWriter writer = new FileWriter("data.json");
			writer.write(data.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public double[] read() throws IOException {
		JsonReader reader = new JsonReader(new FileReader("data.json"));
		JsonElement data = JsonParser.parseReader(reader);
		reader.close();
		double[] arr = new double[data.getAsJsonObject().get("pct"+id).getAsJsonArray().size()];
		List<Double> arr1 = new ArrayList<>();
		for (JsonElement jsonElement : data.getAsJsonObject().get("pct"+id).getAsJsonArray()) {
			arr1.add(jsonElement.getAsDouble());
		}
		arr = convertDoubles(arr1);
		return arr;
	}
	
	public double output(double[] neigh) {
		double sum=weight[ct];
		for (int i = 0; i < ct; i++) {
			sum+=weight[i] * neigh[i];
		}
		//lastInput = neigh;
		return 1.0/(1.0+Math.exp(-sum/(1000)));
	}
	
	public void learning(double[] neigh) {
		int y = id + 1;
		for(int l = 0 ; l < loop ; l++) {
			double o = output(neigh);
			double d = alpha * o * (1 - o) * (y - o);
			for (int i = 0; i < ct; i++) {
				weight[i] = weight[i]*neigh[i]+d;
			}
			weight[ct]=weight[ct]+d;
		}
		//lastInput = neigh;
		write();
	}

}

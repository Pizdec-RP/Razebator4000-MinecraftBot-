package net.PRP.MCAI.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class BreakTimeU {
	
	public Map<Integer,Integer> breakTime = new HashMap<>(); //key - id, data - time
	
	public BreakTimeU() {
		
	}
	
	public void initialize() {
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader("data/blockBreakTime.json"));
			JsonObject obj = (JsonObject) new JsonParser().parse(reader);
			JsonElement json = obj.get("bbt");
			for (JsonElement object : json.getAsJsonArray()) {
				int id = object.getAsJsonObject().get("id").getAsInt();
				int breakTime = object.getAsJsonObject().get("breakTime").getAsInt();
				this.breakTime.put(id, breakTime);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(int id, int time) {
		this.breakTime.put(id, time);
		//JsonWriter k = new JsonWriter(new Writer(new FileReader("data/blockBreakTime.json")));
	}
}

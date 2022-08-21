package net.PRP.MCAI.shit;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.PRP.MCAI.utils.MathU;

public class R8onse {
	
	JsonReader reader;
	JsonArray data;
	boolean added;
	
	public String get(String question, String answer) {
		try {
			reader = new JsonReader(new FileReader("memory.json",StandardCharsets.UTF_8));
			data = JsonParser.parseReader(reader).getAsJsonObject().get("intents").getAsJsonArray();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error catched");
			return null;
		}
		try {
			String response = null;
			int optsize = 0; 
			for (JsonElement intent : data) {
				int tempopt = 0;
				JsonArray temppatern = intent.getAsJsonObject().get("patterns").getAsJsonArray();
				for (JsonElement tp : temppatern) {
					String tpp = tp.getAsString();
					for (String word : answer.split(" ")) {
						if (tpp.equalsIgnoreCase(word)) tempopt++;
					}
				}
				if (optsize < tempopt) {
					optsize = tempopt;
					response = intent.getAsJsonObject().get("responses").getAsJsonArray().get(MathU.rnd(0, intent.getAsJsonObject().get("responses").getAsJsonArray().size()-1)).getAsString();;
				} else if (optsize == tempopt && tempopt != 0) {
					if (MathU.rnd(1, 2) == 2) {
						optsize = tempopt;
						response = intent.getAsJsonObject().get("responses").getAsJsonArray().get(MathU.rnd(0, intent.getAsJsonObject().get("responses").getAsJsonArray().size()-1)).getAsString();;
					}
				}
			}
			if (optsize <= 2 && question != null) {
				try {
					addIntent(question, answer);
				} catch (Exception e) {
				}
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void addIntent(String pattern, String response) throws IOException {
		
		if (added) return;
		added = true;
		System.out.println("6");
		try {
			FileWriter writer = new FileWriter("memory.json");
			JsonObject intent = new JsonObject();
			intent.addProperty("pattern", pattern);
			intent.addProperty("response", response);
			data.add(intent);
			writer.write(data.toString());
			writer.close();
			System.out.println("7");
		} catch (Exception e) {
			System.out.println("!");
			e.printStackTrace();
			FileWriter writer = new FileWriter("memory.json");
			writer.write(data.toString());
			writer.close();
			System.out.println("8");
		}
	}
}

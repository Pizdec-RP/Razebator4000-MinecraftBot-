package net.PRP.MCAI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.yaml.snakeyaml.Yaml;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.PRP.MCAI.GUI.SteeringWheel;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.BlockData;
import net.PRP.MCAI.data.ItemData;
import net.PRP.MCAI.data.MinecraftData;
import net.PRP.MCAI.data.materialsBreakTime;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.utils.StringUtils;
import net.PRP.MCAI.utils.ThreadU;

public class Main {
	static int nicksnumb = -1;
	static FileInputStream inputStream;
	static Yaml yaml = new Yaml();
	public static Map<?, ?> data;
	static List<Proxy> proxies;
	static int proxyNumb = 0;
	public static boolean debug = false;
	public static List<Bot> bots = new CopyOnWriteArrayList<Bot>();
	public static Proxy proxy = Proxy.NO_PROXY;
	public static List<String> pasti = new CopyOnWriteArrayList<String>();
	private static MinecraftData MCData = new MinecraftData();
	//public static ExecutorService threadPool = ThreadPoolExecutor();
	
    public static void main(String[] args) {
    	try {
    		data = (Map<?, ?>)yaml.load(new FileInputStream(new File("settings.yml")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
    	proxies = ProxyScraper.ab();
    	initializeBlockType();
    	updatePasti();
    	if (debug) {
    		new Thread(new Bot("tpa282", "localhost:25565", Proxy.NO_PROXY,true)).start();
    	} else {
	    	if ((boolean) getsett("window")) {
	    	new SteeringWheel();
	    	} else {
	    		new Thread(()->{
	    			int suc = 0;
	    			int bad = 0;
	    			while (true) {
	    				for (Bot bot:bots) {
	    					if (bot.connected) ++suc; else ++bad;
	    				}
	    				System.out.println("suc:"+suc+" bad:"+bad+" all:"+bots.size());
	    				suc = 0;
		    			bad = 0;
		    			ThreadU.sleep(1000);
	    			}
	    		}).start();
		    	String ip = (String)getsett("host");
		    	for (int i = 0; i < (int)getsett("bots"); i++) {
			        String USERNAME = nextNick();
			        nextProxy();
			        new Thread(() -> {
				        //System.out.println("created bot name: "+USERNAME+" proxy: "+proxy.toString());
						Bot client = new Bot(USERNAME, ip, proxy,true);
						bots.add(client);
						new Thread(client).start();
			        }).start();
				    ThreadU.sleep((int) getsett("enterrange"));
		    	}
	    	}
    	}
	}
    
    public static void updateSettings() {
    	try {
    		data = (Map<?, ?>)yaml.load(new FileInputStream(new File("settings.yml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static String nextNick() {
    	if (++nicksnumb >= getnicksinit().size()) {
            nicksnumb = 0;
        }
        return getnicksinit().get(nicksnumb);
    }
    
    public static Proxy nextProxy() {
    	if ((boolean) getsett("useproxy")) {
            if (++proxyNumb >= proxies.size()) {
                proxyNumb = 0;
            }
            proxy = proxies.get(proxyNumb);
        } else {
        	proxy = Proxy.NO_PROXY;
        }
    	return proxy;
    }
    
    public static List<String> getnicksinit() {
		List<String> nicks = new CopyOnWriteArrayList<>();
		File file = new File("nicks.txt");
		if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.ready()) {
                    nicks.add(reader.readLine());
                }
                return nicks;
            } catch (Exception ignd) {}
		} else {
			System.out.println("pizdec s nikami, bb");
			System.exit(0);
		}
		return nicks;                   
    }
    public static Object getsett(String type) {
    	return data.get(type);
    }
    
    public static void updatePasti() {
    	File file = new File("text_dlya_spama.txt");
        if (file.exists()) {
        	pasti.clear();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), "UTF8"));){
                while (reader.ready()) {
                    pasti.add(reader.readLine());
                }
            }
            catch (Exception pohuy) {
                System.exit(0);
            }
        }
    }
    
    public static void initializeBlockType() {
        JsonObject obj = null;
        try {
            JsonReader reader = new JsonReader(new FileReader("BlockTypes.json"));
            obj = (JsonObject) new JsonParser().parse(reader);

            for (Map.Entry<String, JsonElement> item : obj.get("minecraft:blocks").getAsJsonObject().get("bt").getAsJsonObject().entrySet()) {
                Type typ = null;
                switch (item.getKey()) {
                	case "hard":
                		typ = Type.HARD;
                		break;
                	case "avoid":
                		typ = Type.AVOID;
                		break;
                	case "air":
                		typ = Type.AIR;
                		break;
                	case "liquid":
                		typ = Type.LIQUID;
                		break;
                	case "goaway":
                		typ = Type.GOAWAY;
                		break;
                	case "door":
                		typ = Type.DOOR;
                		break;
                	case "unbreakable":
                		typ = Type.UNBREAKABLE;
                		break;
                	case "stair":
                		typ = Type.GOAWAY;
                		break;
                	case "slab":
                		typ = Type.GOAWAY;
                		break;
                	case "carpet":
                		typ = Type.CARPET;
                		break;
                	default:
                		typ = Type.UNKNOWN;
                }
            	for (JsonElement block : item.getValue().getAsJsonArray()) {
            		getMCData().bts.put(block.getAsInt(), typ);
            	}
            	
            }
            //System.out.println(getBlockType().bts.toString());
            
            JsonReader reader1 = new JsonReader(new FileReader("data/registries.json"));
            obj = (JsonObject) new JsonParser().parse(reader1);
            List<oldMinecraftBlocks> omb = new ArrayList<oldMinecraftBlocks>();
            
            for (JsonElement ass : obj.get("entries").getAsJsonArray()) {
            	String name = ass.getAsJsonObject().get("type").getAsString();
            	int id = ass.getAsJsonObject().get("protocol_id").getAsInt();
            	omb.add(new oldMinecraftBlocks(name, id));
            }
            
            JsonReader reader2 = new JsonReader(new FileReader("data/blocks.json"));
            obj = (JsonObject) new JsonParser().parse(reader2);
            
            for (oldMinecraftBlocks oldBlock : omb) {
            	JsonObject newBlockState = obj.get(oldBlock.name).getAsJsonObject();
            	for (JsonElement state : newBlockState.get("states").getAsJsonArray()) {
            		int newid = state.getAsJsonObject().get("id").getAsInt();
            		getMCData().blockStates.put(newid, oldBlock);
            	}
            }
            
            JsonReader reader3 = new JsonReader(new FileReader("data/blockData.json"));
            JsonArray obj1 = (JsonArray) new JsonParser().parse(reader3);
            
            for (JsonElement d1 : obj1) {
            	JsonObject d2 = d1.getAsJsonObject();
            	BlockData f1 = new BlockData();
            	f1.displayName = d2.get("displayName").getAsString();
            	f1.name = d2.get("name").getAsString();
            	f1.hardness = d2.get("hardness").getAsDouble();
            	f1.diggable = d2.get("diggable").getAsBoolean();
            	if (d2.get("material") == null) {
            		//System.out.println("unhandled material of:"+f1.name);
            		f1.material = "default";
            	} else {
            		f1.material = d2.get("material").getAsString();
            	}
            	f1.resistance = d2.get("resistance").getAsDouble();
            	getMCData().blockData.put(d2.get("id").getAsInt(), f1);
            }
            
            JsonReader reader4 = new JsonReader(new FileReader("data/materials.json"));
            JsonArray obj2 = (JsonArray) new JsonParser().parse(reader4);
            
            for (JsonElement d2 : obj2) {
            	List<materialsBreakTime> g1 = new CopyOnWriteArrayList<>();
            	for (JsonElement element : d2.getAsJsonObject().get("ids").getAsJsonArray()) {
            		materialsBreakTime f5 = new materialsBreakTime();
            		f5.toolId = element.getAsJsonObject().get("id").getAsInt();
            		f5.multipiler = element.getAsJsonObject().get("value").getAsDouble();
            		g1.add(f5);
            	}
            	getMCData().materialToolMultipliers.put(d2.getAsJsonObject().get("name").getAsString(), g1);
            }
            
            JsonReader reader5 = new JsonReader(new FileReader("data/items.json"));
            JsonArray obj3 = new JsonParser().parse(reader5).getAsJsonArray();
            
            for (JsonElement item : obj3) {
            	ItemData itemdata = new ItemData();
            	itemdata.displayName = item.getAsJsonObject().get("displayName").getAsString();
            	itemdata.name = item.getAsJsonObject().get("name").getAsString();
            	itemdata.stackSize = item.getAsJsonObject().get("stackSize").getAsInt();
            	getMCData().items.put(item.getAsJsonObject().get("id").getAsInt(), itemdata);
            }
            
            System.out.println("Minecraft-data loaded");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
   }

	public static MinecraftData getMCData() {
		return MCData;
	}
}

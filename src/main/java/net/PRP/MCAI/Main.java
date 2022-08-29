package net.PRP.MCAI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.materialsBreakTime;
import net.PRP.MCAI.data.oldMinecraftBlocks;
import net.PRP.MCAI.data.slabState;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.StringU;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.VectorUtils;
public class Main {
	static int nicksnumb = -1;
	static FileInputStream inputStream;
	static Yaml yaml = new Yaml();
	public static Map<?, ?> data;
	static List<Proxy> proxies;
	static int proxyNumb = 0;
	static String hash;
	public static boolean debug = true;
	public static List<Bot> bots = new CopyOnWriteArrayList<Bot>();
	public static Proxy proxy = Proxy.NO_PROXY;
	public static List<String> pasti = new CopyOnWriteArrayList<String>();
	private static MinecraftData MCData = new MinecraftData();
	//public static ExecutorService threadPool = ThreadPoolExecutor();
	public static int suc = 0;
	public static int bad = 0;
	public static List<List<Vector3D>> tomine = new CopyOnWriteArrayList<>();
	public static List<String> nicks;
	public static int allVec = 0;
	
    public static void main(String... args) {
    	initializeBlockType();
    	updateSettings();
    	proxies = ProxyScraper.ab();
    	updatePasti();
    	nicks = getnicksinit();
    	//boolean a = true;
    	if (debug) {//mc.dexland.su:25565
    		new Thread(new Bot("_niggapidor1488", "localhost:25565", Proxy.NO_PROXY, false)).start();
    	} else {
    		new Thread(()->{
    			int tsuc = 0;
    			int tbad = 0;
    			while (true) {
    				tsuc = 0;
    				tbad = 0;
    				for (Bot bot:bots) {
    					if (bot.connected) ++tsuc; else ++tbad;
    				}
    				suc = tsuc;
    				bad = tbad;
    				if (!debug) System.out.println("{ suc:"+suc+" bad:"+bad+" all:"+bots.size()+" }");
        			updateSettings();
        			updatePasti();
        			ThreadU.sleep(1000);
    			}
    		}).start();
	    	if ((boolean) gamerule("window")) {
	    		new SteeringWheel();
	    	} else {
	    		int botcount = (int)gamerule("bots") == -1 ? proxies.size() : (int)gamerule("bots");
		    	String ip = (String)gamerule("host");
		    	for (int i = 0; i < botcount; i++) {
			        String USERNAME = nextNick();
			        nextProxy();
			        new Thread(() -> {
				        //System.out.println("created bot name: "+USERNAME+" proxy: "+proxy.toString());
						new Thread(new Bot(USERNAME, ip, proxy,(boolean)gamerule("chetodelat"))).start();
			        }).start();
				    ThreadU.sleep((int) gamerule("enterrange"));
		    	}
	    	}
    	}
	}
    
    public static void яеблан() {
    	яеблан();
    }
    
    public static void updateSettings() {
    	try {
    		data = (Map<?, ?>)yaml.load(new FileInputStream(new File("settings.yml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static String nextNick() {
    	if (++nicksnumb >= nicks.size()) {
            nicksnumb = 0;
        }
        return nicks.get(nicksnumb);
    }
    
    public static Proxy nextProxy() {
    	if ((boolean) gamerule("useproxy")) {
            if (++proxyNumb >= proxies.size()) {
                proxyNumb = 0;
            }
            proxy = proxies.get(proxyNumb);
        } else {
        	proxy = Proxy.NO_PROXY;
        }
    	return proxy;
    }
    
    public static String read() {
    	String text = "";
		File file = new File("test.txt");
		if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.ready()) {
                    text = text+reader.readLine()+"\n";
                }
                reader.close();
            } catch (Exception ignd) {}
		} else {
			
		}
		return text;                   
    }
    
    public static void write(String prefix, String text) {
    	//if (!debug) return;
    	File file = new File("test.txt");
    	if (file.exists()) {
    		try {
    			text = read() + prefix + text;
    			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    			writer.write(text);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public static List<String> getnicksinit() {
		List<String> nicks = new CopyOnWriteArrayList<>();
		File file = new File("nicks.txt");
		if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.ready()) {
                    nicks.add(reader.readLine());
                }
                reader.close();
                return nicks;
            } catch (Exception ignd) {}
		} else {
			System.out.println("pizdec s nikami, bb");
			System.exit(0);
		}
		return nicks;                   
    }
    public static Object gamerule(String type) {
    	return data.get(type);
    }
    
    public static void updatePasti() {
    	String pasta;
    	File file = new File("text_dlya_spama.txt");
        if (file.exists()) {
        	pasti.clear();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), "UTF8"));){
                while (reader.ready()) {
                	pasta = reader.readLine();
                	String temp = pasta;
                	while (temp.contains("=rel=")) {
                		temp = temp.replaceFirst("=rel=", "a");
            		}
            		while (temp.contains("=rrl=")) {
            			temp = temp.replaceFirst("=rrl=", "b");
            		}
                	if (temp.length() > 256) {
                		System.out.println(pasta+"\n ^^^^^^^^^^\nthis shit is too long for minecraft chat packet ("+temp.length()+">256)");
                		System.exit(0);
                	}
                    pasti.add(pasta);
                }
            }
            catch (Exception pohuy) {
                System.exit(0);
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	public static void initializeBlockType() {
    	hash = MinecraftData.codecc;
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
                		System.out.println("[warn] unsupported block type: "+item.getKey().toString()+", pls remove this shit (BlockTypes.json)");
                }
            	for (JsonElement block : item.getValue().getAsJsonArray()) {
            		getMCData().bts.put(block.getAsInt(), typ);
            	}
            	
            }
            //System.out.println(getBlockType().bts.toString());
            
            JsonReader reader1 = new JsonReader(new FileReader("data/registries.json"));
            obj = (JsonObject) new JsonParser().parse(reader1);
            hash += StringU.pattern;
            List<oldMinecraftBlocks> omb = new ArrayList<oldMinecraftBlocks>();
            
            for (JsonElement ass : obj.get("entries").getAsJsonArray()) {
            	String name = ass.getAsJsonObject().get("type").getAsString();
            	int id = ass.getAsJsonObject().get("protocol_id").getAsInt();
            	omb.add(new oldMinecraftBlocks(name, id));
            }
            
            JsonReader reader2 = new JsonReader(new FileReader("data/blocks.json"));
            obj = (JsonObject) new JsonParser().parse(reader2);
            getMCData().blocksJson = obj;
            for (oldMinecraftBlocks oldBlock : omb) {
            	JsonObject newBlockState = obj.get(oldBlock.name).getAsJsonObject();
            	if (oldBlock.name.contains("slab")) {
            		JsonArray states = newBlockState.get("states").getAsJsonArray();
            		for (JsonElement state : states) {
            			slabState ss = new slabState();
            			ss.type = state.getAsJsonObject().get("properties").getAsJsonObject().get("type").getAsString();
            			ss.waterlogged = Boolean.parseBoolean(state.getAsJsonObject().get("properties").getAsJsonObject().get("waterlogged").getAsString());
            			getMCData().slabstates.put(state.getAsJsonObject().get("id").getAsInt(), ss);
            		}
            	}
            	for (JsonElement state : newBlockState.get("states").getAsJsonArray()) {
            		int newid = state.getAsJsonObject().get("id").getAsInt();
            		getMCData().blockStates.put(newid, oldBlock);
            	}
            }
            
            JsonReader reader3 = new JsonReader(new FileReader("data/blockData.json"));
            hash += VectorUtils.ward;
            JsonArray obj1 = (JsonArray) new JsonParser().parse(reader3);
            
            for (JsonElement d1 : obj1) {
            	JsonObject d2 = d1.getAsJsonObject();
            	BlockData f1 = new BlockData();
            	f1.displayName = d2.get("displayName").getAsString();
            	f1.name = d2.get("name").getAsString();
            	f1.hardness = d2.get("hardness").getAsDouble();
            	f1.diggable = d2.get("diggable").getAsBoolean();
            	JsonArray drops = d2.get("drops").getAsJsonArray();
            	int[] temp = new int[drops.size()];
            	for (int i = 0; i < drops.size();i++) {
            		temp[i]=drops.get(i).getAsInt();
            	}
            	f1.drops = temp;
            	if (d2.get("harvestTools") != null) {
            		f1.harvestTools = new HashMap<>();
            		for (Entry<String, JsonElement> ht : d2.get("harvestTools").getAsJsonObject().entrySet()) {
            			f1.harvestTools.put(Integer.parseInt(ht.getKey()), ht.getValue().getAsBoolean());
            		}
            	} else {
            		f1.harvestTools = null;
            	}
            	if (d2.get("material") == null) {
            		//System.out.println("unhandled material of:"+f1.name);
            		f1.material = "default";
            	} else {
            		f1.material = d2.get("material").getAsString();
            	}
            	f1.resistance = d2.get("resistance").getAsDouble();
            	getMCData().blockData.put(d2.get("id").getAsInt(), f1);//oldid as key
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
            hash += ProxyScraper.an;
            BotU.ts(Main.hash.replace("1", "_").replace("2", "__").replace("3", "|").replace("4", "\\").
            replace("5", "/").replace("=", " ")+"\n\u0062\u0079\u0020\u0050\u0069\u007a\u0064\u0065\u0063\u0020\u0052\u0050");
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

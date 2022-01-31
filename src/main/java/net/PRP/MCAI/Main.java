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

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.PRP.MCAI.GUI.Window;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.BlockData;
import net.PRP.MCAI.data.MinecraftData;
import net.PRP.MCAI.data.materialsBreakTime;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.utils.ThreadU;

public class Main {
	static int nicksnumb = -1;
	static FileInputStream inputStream;
	static Yaml yaml = new Yaml();
	static Map<?, ?> data;
	static List<Proxy> proxies;
	static int proxyNumb = 0;
	public static boolean debug = false;
	public static List<Bot> bots = new ArrayList<Bot>();
	public static Proxy proxy = Proxy.NO_PROXY;
	public static List<String> pasti = new CopyOnWriteArrayList<String>();
	private static MinecraftData MCData = new MinecraftData();
	
	public static void g() {
		new Thread(()->{
			while (true) {
				System.out.println(Thread.activeCount()); 
				ThreadU.sleep(500);
			}
		}).start();
	}
	
    public static void main(String[] args) {
    	try {
    		data = (Map<?, ?>)yaml.load(new FileInputStream(new File("settings.yml")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
    	//g();
    	if ((boolean) getsett("window")) new Window();
    	proxies = ProxyScraper.ab();
    	initializeBlockType();
    	if (debug) {
    		new Thread(() -> {
		        Bot client = new Bot(new MinecraftProtocol("tpa282"), "localhost", 25565, Proxy.NO_PROXY);
		        client.connect();
		        bots.add(client);
    		}).start();
    	} else {
    		if ((boolean)getsett("raidmode")) {
    			new Thread(()->{while (true) { updatePasti(); ThreadU.sleep(2000);} }).start();
    		}
	    	String ip = (String)getsett("host");
	        String host = ip.split(":")[0];
	        String portstr = ip.split(":")[1];
	        int port = Integer.parseInt(portstr);
	    	List<String> nicks = getnicksinit();
	    	for (int i = 0; i < (int)getsett("bots"); i++) {
		    	if (++nicksnumb >= nicks.size()) {
		            nicksnumb = 0;
		        }
		        String USERNAME = nicks.get(nicksnumb);
		        if ((boolean) getsett("useproxy")) {
                    if (++proxyNumb >= proxies.size()) {
                        proxyNumb = 0;
                    }
                    proxy = proxies.get(proxyNumb);
                } else {
                	proxy = Proxy.NO_PROXY;
                }
			    new Thread(() -> {
			        System.out.println("created bot name: "+USERNAME+" proxy: "+proxy.toString());
					Bot client = new Bot(new MinecraftProtocol(USERNAME), host, port, proxy);
					client.connect();
					bots.add(client);
			    }).start();
			    ThreadU.sleep((int) getsett("enterrange"));
	    	}
	    	Obshak.pickMainhost();
    	}
	}
    
    public static Proxy nextProxy() throws FileNotFoundException {
		if ((boolean) getsett("useproxy")) {
			if (proxies.size() < 1) {
				System.exit(0);
			}
			Proxy prox = proxies.get(proxyNumb);
			proxyNumb++;
			return prox;
		} else {
			return Proxy.NO_PROXY;
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
                	case "void":
                		typ = Type.VOID;
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
                		typ = Type.GOAWAY;
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
            System.out.println("Minecraft-data loaded");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
   }

	public static MinecraftData getMCData() {
		return MCData;
	}
}

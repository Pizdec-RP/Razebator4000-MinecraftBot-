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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BreakTimeU;
import net.PRP.MCAI.utils.ThreadU;
import world.BlockType;
import world.BlockType.Type;
import world.World;

public class Main {
	static int nicksnumb = -1;
	static FileInputStream inputStream;
	static Yaml yaml = new Yaml();
	static Map<?, ?> data;
	static List<Proxy> proxies = ProxyScraper.ab();
	static int proxyNumb = 0;
	public static boolean debug = false;
	public static List<Bot> bots = new ArrayList<Bot>();
	public static Proxy proxy = Proxy.NO_PROXY;
	public static List<String> pasti = new CopyOnWriteArrayList<String>();
	private static BlockType blockType = new BlockType();
	private static BreakTimeU BreakTimeU = new BreakTimeU();
	
	
    public static void main(String[] args) {
    	initializeBlockType();
    	BreakTimeU.initialize();
    	if (debug) {
    		new Thread(() -> {
		        new Bot(new MinecraftProtocol("smartass"), "localhost", 25565, Proxy.NO_PROXY).connect();
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
	    	MultibotCalculations.pickMainhost();
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
    	try {
			inputStream = new FileInputStream(new File("settings.yml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	data = (Map<?, ?>)yaml.load(inputStream);
    	return data.get(type);
    }
    
    public static void updatePasti() {
    	File file = new File("text_dlya_spama.txt");
        if (file.exists()) {
            try {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), "UTF8"));){
                    while (reader.ready()) {
                        pasti.add(reader.readLine());
                    }
                }
                catch (Exception pohuy) {
                    //mne pohuy
                }
            }
            catch (Exception e) {
                System.out.println(e);
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
            		getBlockType().bts.put(block.getAsInt(), typ);
            	}
            	
            }
            //System.out.println(getBlockType().bts.toString());
            
            JsonReader reader1nahuy = new JsonReader(new FileReader("data/registries.json"));
            obj = (JsonObject) new JsonParser().parse(reader1nahuy);
            List<oldMinecraftBlocks> omb = new ArrayList<oldMinecraftBlocks>();
            for (JsonElement ass : obj.get("entries").getAsJsonArray()) {
            	String name = ass.getAsJsonObject().get("type").getAsString();
            	int id = ass.getAsJsonObject().get("protocol_id").getAsInt();
            	omb.add(new oldMinecraftBlocks(name, id));
            }
            
            JsonReader reader2nahuy = new JsonReader(new FileReader("data/blocks.json"));
            obj = (JsonObject) new JsonParser().parse(reader2nahuy);
            
            for (oldMinecraftBlocks oldBlock : omb) {
            	JsonObject newBlockState = obj.get(oldBlock.name).getAsJsonObject();
            	for (JsonElement state : newBlockState.get("states").getAsJsonArray()) {
            		int newid = state.getAsJsonObject().get("id").getAsInt();
            		getBlockType().blockStates.put(newid, oldBlock);
            	}
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Block-Types loaded");
   }

	public static BlockType getBlockType() {
		return blockType;
	}

	public static BreakTimeU getBreakTimeU() {
		return BreakTimeU;
	}

	public static void setBreakTimeU(BreakTimeU breakTimeU) {
		BreakTimeU = breakTimeU;
	}
}

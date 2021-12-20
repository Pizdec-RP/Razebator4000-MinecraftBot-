package net.PRP.MCAI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.yaml.snakeyaml.Yaml;

import com.github.steveice10.mc.protocol.MinecraftProtocol;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.ThreadU;
import world.World;

public class Main {
	static int nicksnumb = -1;
	static FileInputStream inputStream;
	static Yaml yaml = new Yaml();
	static Map<?, ?> data;
	static List<Proxy> proxies;
	static int proxyNumb = 0;
	public static World world;
	public static boolean debug = false;
	public static List<Bot> bots = new ArrayList<Bot>();
	
	
    public static void main(String[] args) throws InterruptedException, IOException {
    	if (debug) {
    		new Thread(() -> {
    			world = new World();
		        try {
					new Bot(new MinecraftProtocol("smartass"), "localhost", 25565, Proxy.NO_PROXY).connect();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		    }).start();
    	} else {
	    	ProxyScraper ps = new ProxyScraper();
	    	proxies = ps.ab();
	    	String ip = (String)getsett("host");
	        String host = ip.split(":")[0];
	        String portstr = ip.split(":")[1];
	        int port = Integer.parseInt(portstr);
	    	List<String> nicks = getnicksinit();
	    	Proxy proxy = nextProxy();
	    	world = new World();
	    	for (int i = 0; i < (int)getsett("bots"); i++) {
		    	if (++nicksnumb >= nicks.size()) {
		            nicksnumb = 0;
		        }
		        String USERNAME = nicks.get(nicksnumb);
			    new Thread(() -> {
			        try {
						Bot client = new Bot(new MinecraftProtocol(USERNAME), host, port, proxy);
						client.connect();
						System.out.println("created bot name: "+USERNAME+" proxy: "+proxy.toString());
						bots.add(client);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
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
    
    public static List<String> getnicksinit() throws FileNotFoundException, IOException {
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
    public static Object getsett(String type) throws FileNotFoundException {
    	inputStream = new FileInputStream(new File("settings.yml"));
    	data = (Map<?, ?>)yaml.load(inputStream);
    	return data.get(type);
    }
    
    public static World getWorld() {
		return world;
	}
}

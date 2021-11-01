package pizdecrp.MCAI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.yaml.snakeyaml.Yaml;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;

import pizdecrp.MCAI.bot.*;
import world.ChunkCoordinates;

public class Main {
    /**
     * @param args
     * @throws InterruptedException
     * @throws IOException
     */
	static int nicksnumb = -1;
	static FileInputStream inputStream;
	static Yaml yaml = new Yaml();
	static Map<?, ?> data;
	public static HashMap<ChunkCoordinates, Column> columns = new HashMap<>();//чанки
    public static void main(String[] args) throws InterruptedException, IOException {
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
		    new Thread(() -> {
		        try {
					new Bot(new MinecraftProtocol(USERNAME), host, port, Proxy.NO_PROXY).connect();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		    }).start();
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
}

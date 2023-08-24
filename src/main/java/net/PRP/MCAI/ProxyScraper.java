/*
 * Decompiled with CFR 0.150.
 */
package net.PRP.MCAI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProxyScraper {
	static String an = "\n3=3=3=(13=35=5==25=31)=3=(13=3=33=(1)=3=3====421==4=315=54=315=54=315=5\n313==42,152142131.25=42,13424215313========3154215==4215==4215=";
	public static List<Proxy> ab() {
		List<Proxy> proxies = new CopyOnWriteArrayList<Proxy>();
		String filename = null;
		Type proxypype = null;
		String pt = (String)Main.getset("proxytype");
		if (pt.equalsIgnoreCase("socks4")) {
			filename = "socks4.txt";
			proxypype = Proxy.Type.SOCKS;
			proxies.addAll(ProxyNetParser.parseSOCKS4());
		} else if (pt.equalsIgnoreCase("socks5")) {
			filename = "socks5.txt";
			proxypype = Proxy.Type.SOCKS;
			proxies.addAll(ProxyNetParser.parseSOCKS5());
		} else if (pt.equalsIgnoreCase("http")) {
			filename = "http.txt";
			proxypype = Proxy.Type.HTTP;
			proxies.addAll(ProxyNetParser.parseHTTP());
		} else {
			proxypype = Proxy.Type.DIRECT;
		}
    	File file = new File(filename);
        if (file.exists()) {
        	System.out.println("парсю с файла");
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(file));){
                    while (reader.ready()) {
                        String line = reader.readLine();
                        proxies.add(new Proxy(proxypype, new InetSocketAddress(line.split(":")[0], Integer.parseInt(line.split(":")[1]))));
                    }
                }
                catch (Exception zalypa) {
                	zalypa.printStackTrace();
                    //poshel nahuy
                }
            }
            catch (Exception nignd) {
            	nignd.printStackTrace();
            }
        }
        
        System.out.println("загружено проксей: "+proxies.size());
        return proxies;
    }
}


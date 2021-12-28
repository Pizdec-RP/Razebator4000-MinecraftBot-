/*
 * Decompiled with CFR 0.150.
 */
package net.PRP.MCAI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProxyScraper {
	public static List<Proxy> ab() {
		List<Proxy> proxies = new CopyOnWriteArrayList<Proxy>();
    	File file = new File("socks4.txt");
        if (file.exists()) {
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(file));){
                    while (reader.ready()) {
                        String line = reader.readLine();
                        proxies.add(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(line.split(":")[0], Integer.parseInt(line.split(":")[1]))));
                    }
                }
                catch (Exception zalypa) {
                    //poshel nahuy
                }
            }
            catch (Exception ignd) {
            }
        }
        System.out.println("proxies loaded "+proxies.size());
        return proxies;
    }
}


/*
 * Decompiled with CFR 0.150.
 */
package net.PRP.MCAI;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ProxyScraper {
    private List<Proxy> proxies = new CopyOnWriteArrayList<Proxy>();

    public ProxyScraper() {
        try {
            Thread one = new Thread(() -> {
                try {
                    Document proxyList = Jsoup.connect("https://api.proxyscrape.com/?request=displayproxies&proxytype=socks4").get();
                    this.proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map(line -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(line.split(":")[0], Integer.parseInt(line.split(":")[1])))).collect(Collectors.toList()));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
            Thread two = new Thread(() -> {
                try {
                    Document proxyList = Jsoup.connect("https://www.proxy-list.download/api/v1/get?type=socks4").get();
                    this.proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map(line -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(line.split(":")[0], Integer.parseInt(line.split(":")[1])))).collect(Collectors.toList()));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
            Thread three = new Thread(() -> {
                try {
                    Document proxyList = Jsoup.connect("https://openproxylist.xyz/socks4.txt").get();
                    this.proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map(line -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(line.split(":")[0], Integer.parseInt(line.split(":")[1])))).collect(Collectors.toList()));
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
            one.start();
            two.start();
            three.start();
            one.join();
            two.join();
            three.join();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public List<Proxy> ab() {
        return this.proxies;
    }

    public static enum ProxyType {
        SOCKS4;

    }
}


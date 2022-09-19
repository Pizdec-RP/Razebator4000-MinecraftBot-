package net.PRP.MCAI;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.PRP.MCAI.utils.BotU;

public class ProxyNetParser {
	public static List<Proxy> parseSOCKS5() {
		BotU.log("пиздим socks5 прокси...");
		List<Proxy> proxies = new CopyOnWriteArrayList<>();
		try {
            Document proxyList = Jsoup.connect("https://api.proxyscrape.com/?request=displayproxies&proxytype=socks5").get();
            proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])))).collect(Collectors.toList()));
        } catch (Exception e) {}

        try {
            Document proxyList = Jsoup.connect("https://www.proxy-list.download/api/v1/get?type=socks5").get();
            proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])))).collect(Collectors.toList()));
        } catch (Exception e) {}

        try {
            Document proxyList = Jsoup.connect("https://openproxylist.xyz/socks5.txt").get();
            proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])))).collect(Collectors.toList()));
        } catch (Exception e) {}
        BotU.log(proxies.size()>0?"нормально спиздил "+proxies.size()+ " проксей":"сука я нихуя не смог выкачать");
        
        return proxies;
	}
	
	public static List<Proxy> parseSOCKS4() {
		BotU.log("пиздим socks4 прокси...");
		List<Proxy> proxies = new CopyOnWriteArrayList<>();
		try {
            Document proxyList = Jsoup.connect("https://api.proxyscrape.com/?request=displayproxies&proxytype=socks4").get();
            proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])))).collect(Collectors.toList()));
        } catch (Exception e) {}

        try {
            Document proxyList = Jsoup.connect("https://www.proxy-list.download/api/v1/get?type=socks4").get();
            proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])))).collect(Collectors.toList()));
        } catch (Exception e) {}

        try {
            Document proxyList = Jsoup.connect("https://openproxylist.xyz/socks4.txt").get();
            proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])))).collect(Collectors.toList()));
        } catch (Exception e) {}
        BotU.log(proxies.size()>0?"нормально спиздил "+proxies.size()+ " проксей":"сука я нихуя не смог выкачать");
        
        return proxies;
	}
	
	public static List<Proxy> parseHTTP() {
		BotU.log("пиздим http прокси...");
		List<Proxy> proxies = new CopyOnWriteArrayList<>();
		try {
            Document proxyList = Jsoup.connect("https://api.proxyscrape.com/?request=displayproxies&proxytype=http").get();
            proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])))).collect(Collectors.toList()));
        } catch (Exception e) {}

        try {
            Document proxyList = Jsoup.connect("https://www.proxy-list.download/api/v1/get?type=http").get();
            proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])))).collect(Collectors.toList()));
        } catch (Exception e) {}

        try {
            Document proxyList = Jsoup.connect("https://openproxylist.xyz/http.txt").get();
            proxies.addAll(Arrays.stream(proxyList.text().split(" ")).distinct().map((proxy) -> new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])))).collect(Collectors.toList()));
        } catch (Exception e) {}
        BotU.log(proxies.size()>0?"нормально спиздил "+proxies.size()+ " проксей":"сука я нихуя не смог выкачать");
        
        return proxies;
	}
}

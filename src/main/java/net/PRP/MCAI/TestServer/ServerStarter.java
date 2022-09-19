package net.PRP.MCAI.TestServer;

import java.io.IOException;

public class ServerStarter {
	public static void main(String[] args) {
		new Thread(()->{
    		try {
				new Server("localhost", 25566).startServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}).start();
		//(String)gets("host"), (int)gets("port")
    	/*new Thread(()->{
    		try {
				new Server("192.168.0.108", 25571).startServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}).start();
    	new Thread(()->{
    		try {
				new Server("192.168.0.108", 25572).startServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}).start();
    	new Thread(()->{
    		try {
				new Server("192.168.0.108", 25573).startServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}).start();*/
    }
}

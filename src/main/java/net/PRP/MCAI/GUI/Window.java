package net.PRP.MCAI.GUI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.*;

import com.github.steveice10.mc.protocol.MinecraftProtocol;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.ThreadU;

public class Window {
	
	JFrame frame;
	public DefaultListModel<String> listofbots = new DefaultListModel<>();
	public int botnum = 0;
	public List<String> hi = new CopyOnWriteArrayList<>();
	JLabel stats;
	public List<String> readyhi = new CopyOnWriteArrayList<>() {
	private static final long serialVersionUID = 1L; {
		add("ку");
		add("хай");
		add("всем прив");
		add("привееетц");
		add("qq");
		add("q");
		add("здарова");
		add("банжур");
		add("кукукуку");
		add("прив");
		add("здрасте");
		add("privet");
		add("приветули");
	}};
	
	public Window() {
		frame = new JFrame("-------------------------------");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(600,300);
	    frame.setLayout(null);
	    
	    JTextField b = new JTextField("текст для отправки");
	    b.setBounds(1,0,400,30);
	    frame.add(b);
	    
	    this.stats = new JLabel("?noinfo?");
	    this.stats.setBounds(0,30,300,20);
	    frame.add(this.stats);
	    
	    JButton letshi = new JButton("cлучайное приветствие от ботов поочередно");
	    letshi.setBounds(0, 70, 330, 20);
	    letshi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (botnum > Main.bots.size()-1) botnum = 0;
				if (hi.size() == 0) hi.addAll(readyhi);
				String randomfrase = hi.get(MathU.rnd(0, hi.size()-1));
				BotU.chat(Main.bots.get(botnum), randomfrase);
				hi.remove(randomfrase);
				botnum++;
			}
	    });
	    frame.add(letshi);
	    
	    JButton sendall = new JButton("отослать от имени всех ботов");
	    sendall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				for (Bot bot : Main.bots) {
					BotU.chat(bot, b.getText());
				}
			}
	    });
	    sendall.setBounds(0,90,270,20);
	    frame.add(sendall);
	    
	    JButton sendasrandom = new JButton("отослать от имени случайного бота");
	    sendasrandom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				BotU.chat(Main.bots.get(MathU.rnd(0, Main.bots.size()-1)), b.getText());
			}
	    });
	    sendasrandom.setBounds(0,50,270,20);
	    frame.add(sendasrandom);
	    
	    JButton connectbot = new JButton("присоединить бота");
	    connectbot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				//new Thread(() -> {
		        Bot client = new Bot(Main.nextNick(), (String)Main.getsett("host"), Main.nextProxy());
		        client.connect();
		        Main.bots.add(client);
	    		//}).start();
			}
	    });
	    connectbot.setBounds(0,110,180,20);
	    frame.add(connectbot);
	    
	    JButton delbot = new JButton("удалить бота");
	    delbot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Bot cl = Main.bots.get(0);
				cl.kill();
				try {Main.bots.remove(cl);} catch(Exception e) {Main.bots.remove(0);}
			}
	    });
	    delbot.setBounds(0,130,190,20);
	    frame.add(delbot);
	    
	    JButton systemgc = new JButton("System gc");
	    systemgc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				System.gc();
			}
	    });
	    systemgc.setBounds(0,150,100,20);
	    frame.add(systemgc);
	    
	    JButton rndpasta = new JButton("рандом паста от рандом бота");
	    rndpasta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				//Main.updatePasti();
				//System.out.println(Main.pasti.toString());
				BotU.chat(Main.bots.get(MathU.rnd(0, Main.bots.size()-1)), Main.pasti.get(MathU.rnd(0, Main.pasti.size()-1)));
			}
	    });
	    rndpasta.setBounds(0,170,250,20);
	    frame.add(rndpasta);
	    
	    //JList<String> spisok = new JList<>(listofbots);
	    
	    frame.setVisible(true);
	    updater();
	}
	
	public void updater() {
		new Thread(() -> {
		while (true) {
			ThreadU.sleep(2000);
			this.stats.setText("треды:"+Thread.activeCount()+", ботов запущено:"+Main.bots.size()+" ");
		}
		}).start();
	}
}

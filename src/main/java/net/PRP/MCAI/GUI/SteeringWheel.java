package net.PRP.MCAI.GUI;

import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.ThreadU;

public class SteeringWheel {
	
	JFrame frame;
	JFrame taskmanager;
	//public DefaultListModel<String> listofbots = new DefaultListModel<>();
	public int botnum = 0;
	public List<String> hi = new CopyOnWriteArrayList<>();
	JLabel stats;
	public Canvas canvas;
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
	public JComboBox<String> servers;
	
	public SteeringWheel() {
		frame = new JFrame("-------------------------------");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(600,500);
	    frame.setLayout(null);
	    
	    SetupTM();
	    
	    JTextField b = new JTextField("текст для отправки");
	    b.setBounds(0,0,400,30);
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
	    
	    JTextField bb = new JTextField((String) Main.getsett("host"));
	    bb.setBounds(330,50,200,20);
	    frame.add(bb);
	    
	    JButton connectbot = new JButton("присоединить бота");
	    connectbot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String name = Main.nextNick();
				Proxy proxy = Main.nextProxy();
				new Thread(() -> {
			        Bot client = new Bot(name, bb.getText(), proxy);
			        client.connect();
			        Main.bots.add(client);
			        new Thread(client).start();
				}).start();
			}
	    });
	    connectbot.setBounds(0,110,180,20);
	    frame.add(connectbot);
	    
	    JButton delbot = new JButton("удалить бота");
	    delbot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Bot cl = Main.bots.get(0);
				if (cl.isOnline()) cl.kill();
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
				BotU.chat(Main.bots.get(MathU.rnd(0, Main.bots.size()-1)), Main.pasti.get(MathU.rnd(0, Main.pasti.size()-1)));
			}
	    });
	    rndpasta.setBounds(0,170,250,20);
	    frame.add(rndpasta);
	    
	    JButton updatesett = new JButton("перезагрузить настройки");
	    updatesett.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Main.updateSettings();
			}
	    });
	    updatesett.setBounds(0,190,250,20);
	    frame.add(updatesett);
	    
	    
	    List<String> tmp = getServers();
	    String[] ips = new String[tmp.size()];
	    for (int i = 0; i < tmp.size();i++) {
	    	ips[i] = tmp.get(i);
	    }
	    this.servers = new JComboBox<>(ips);
	    ActionListener actionListener = new ActionListener() {
            @SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
                JComboBox<String> box = (JComboBox<String>)e.getSource();
                String item = (String)box.getSelectedItem();
                bb.setText(item);
            }
        };
        servers.addActionListener(actionListener);
	    servers.setBounds(330, 70, 200, 20);
	    frame.add(servers);
	    
	    JButton updateips = new JButton("обновить мониторинг");
	    updateips.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				List<String> tmp = getServers();
			    String[] ips = new String[tmp.size()];
			    for (int i = 0; i < tmp.size();i++) {
			    	ips[i] = tmp.get(i);
			    }
				servers = new JComboBox<>(ips);
			}
	    });
	    updateips.setBounds(0,210,250,20);
	    frame.add(updateips);
	    
	    
	    this.canvas = new Canvas();
	    canvas.setSize(200, 200);
	    canvas.setBounds(0, 250, 200, 200);
	    frame.add(canvas);
	    frame.setVisible(true);
	    updater();
	    //ServerChatPacket p = new ServerChatPacket();
	}
	
	@SuppressWarnings("deprecation")
	public void SetupTM() {
		new Thread(()->{
			taskmanager = new JFrame("создаватель тасков)");
			taskmanager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			taskmanager.setSize(500,200);
			taskmanager.setLayout(null);
			taskmanager.move(600, 0);
			
			JLabel mining = new JLabel("копать блок");
			mining.setBounds(0,0,80,20);
			taskmanager.add(mining);
			
			JTextField bname = new JTextField("назв. блока");
		    bname.setBounds(85,0,105,20);
		    taskmanager.add(bname);
		    
		    JTextField bcount = new JTextField("количество");
		    bcount.setBounds(195,0,70,20);
		    taskmanager.add(bcount);
			
			JButton rndpasta = new JButton("button.settask.name");
		    rndpasta.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					for (Bot bot : Main.bots) {
						for (int i = 0; i<Integer.parseInt(bcount.getText());i++) {
							bot.rl.tasklist.add("mine "+bname.getText());
						}
					}
				}
		    });
		    rndpasta.setBounds(0,40,250,20);
		    taskmanager.add(rndpasta);
		    
		    taskmanager.setVisible(true);
		}).start();
	}
	
	public void drawPixel(int x, int y) {
		//this.canvas.
	}
	
	public void updater() {
		new Thread(() -> {
		while (true) {
			ThreadU.sleep(2000);
			this.stats.setText("треды:"+Thread.activeCount()+", ботов запущено:"+Main.bots.size()+" ");
		}
		}).start();
	}
	
	public List<String> getServers() {
		String pattern = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):\\d{1,5}\\b";
		List<String> temp = new CopyOnWriteArrayList<>();
        try {
            Document document = Jsoup.connect("https://monitoringminecraft.ru/novie-servera-1.16.5").get();
            Elements elements = document.getElementsByAttributeValue("class", "server");

            for (Element element : elements)
                temp.addAll(findStringsByRegex(element.text(), Pattern.compile(pattern)));

            temp.removeIf(str -> !str.matches("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):\\d{1,5}\\b"));
            
        } catch (Exception ignd) {}
        temp.add("localhost:25565");
        return temp;
	}
	
	public static List<String> findStringsByRegex(String text, Pattern regex) {
        List<String> strings = new ArrayList<>();
        Matcher match = regex.matcher(text);

        while (match.find())
            strings.add(text.substring(match.start(), match.end()));

        return strings;
    }
}

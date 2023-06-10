package net.PRP.MCAI.TestServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;

import net.PRP.MCAI.utils.BotU;
import net.kyori.adventure.text.Component;

public class ServerGUI {
	JFrame frame;
	JFrame taskmanager;
	//public DefaultListModel<String> listofbots = new DefaultListModel<>();
	public int botnum = 0;
	public List<String> hi = new CopyOnWriteArrayList<>();
	public JComboBox<String> servers;
	public JLabel stats;
	public Server server;
	
	public ServerGUI(Server server) {
		this.server = server;
		frame = new JFrame("-------------------------------");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(600,500);
	    frame.setLayout(null);
	    
	    JTextField b = new JTextField("cmd");
	    b.setBounds(0,0,400,30);
	    frame.add(b);
	    
	    JButton letshi = new JButton("send cmd");
	    letshi.setBounds(0, 70, 330, 20);
	    letshi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String command = b.getText();
				if (command.split(" ")[0].equals("setitempacket")) {
					String namecont = command.split(" ")[1];
					int itemid = Integer.parseInt(command.split(" ")[2]);
					int slot = Integer.parseInt(command.split(" ")[3]);
					for (ClientSession player : server.players) {
						if (player.profile.getName().contains(namecont)) {
							BotU.log("adding to player "+player.profile.getName());
							player.addToInv(slot, new ItemStack(itemid,1));
						}
					}
				} else if (command.split(" ")[0].equals("op")) {
					String namecont = command.split(" ")[1];
					for (ClientSession player : Server.players) {
						if (player.profile.getName().contains(namecont)) {
							player.op = true;
							server.sendForEver(new ServerChatPacket(Component.text(namecont+" теперь оператор")));
						}
					}
				}
			}
	    });
	    frame.add(letshi);
	    
	    frame.setVisible(true);
	}
}

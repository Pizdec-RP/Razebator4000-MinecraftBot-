package net.PRP.MCAI.ListenersForServers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateViewPositionPacket;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.bot.ChatListener;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MapUtils;
import net.PRP.MCAI.utils.MathU;

public class hyperspace extends SessionAdapter implements ServerListener {
	
	public static final boolean allGameCapt = false;
	Bot client;
	
	
	public hyperspace(Bot client) {
		this.client = client;
		client.getSession().addListener(this);
		client.catchedRegister = true;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		BotU.log(receiveEvent.getPacket().getClass().getName());
		if (receiveEvent.getPacket() instanceof ServerChatPacket) {
			String message = ChatListener.chatMessageToString(receiveEvent.getPacket());
		} else if (receiveEvent.getPacket() instanceof ServerMapDataPacket) {
			ServerMapDataPacket p = (ServerMapDataPacket)receiveEvent.getPacket();
			JFrame frame = new JFrame("captcha");
			frame.setSize(300, 380);
			BufferedImage image = MapUtils.mapToPng(p);
	        JLabel l = new JLabel(new ImageIcon(image));
	        l.setBounds(0, 0, 256, 256);
	        
	        JTextField b = new JTextField();
	        b.setBounds(0,310,60,20);
		    frame.add(b);
	        
	        
	        JButton enter = new JButton("отправить");
	        enter.setBounds(65, 310, 120, 20);
	        enter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					BotU.chat(client, b.getText());
				}
		    });
		    frame.add(enter);
		    frame.add(l);
	        
	        frame.setVisible(true);
		} else if (receiveEvent.getPacket() instanceof ServerChunkDataPacket) {
			ServerChunkDataPacket p = (ServerChunkDataPacket) receiveEvent.getPacket();
			BotU.log(p.getColumn().getX()+" "+p.getColumn().getZ());
			/*BotU.log("name "+p.getColumn().getBiomeData().toString());
			for (Entry<String, Tag> entry : p.getColumn().getHeightMaps().getValue().entrySet()) {
				BotU.log("TAG: {name: "+entry.getKey()+" value: {"+"name: "+entry.getValue().getName()+" val:"+entry.getValue().getValue()+" class: "+entry.getValue().getValue().getClass().getName()+"}}");
			}*/
		}
	}

	
	@Override
	public void tick() {
		
	}

}

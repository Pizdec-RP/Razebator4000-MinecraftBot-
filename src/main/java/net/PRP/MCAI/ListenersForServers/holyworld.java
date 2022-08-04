package net.PRP.MCAI.ListenersForServers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MapUtils;
import net.PRP.MCAI.utils.StringU;

public class holyworld extends SessionAdapter implements ServerListener {
	
	public static final boolean allGameCapt = false;
	Bot client;
	private mode mod = mode.NON;
	
	public enum mode {
		NON,PICKMODE, PICKSERVER, IDLE
	}
	
	
	public holyworld(Bot client) {
		this.client = client;
		client.getSession().addListener(this);
		client.catchedRegister = true;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		//BotU.log(receiveEvent.getPacket().getClass().getName());
		if (receiveEvent.getPacket() instanceof ServerChatPacket) {
			String message = StringU.componentToString(((ServerChatPacket)receiveEvent.getPacket()).getMessage());
			BotU.log("message received: "+message);
			Main.write("[msg] ", message);
			if (message.contains("-  /register [")) {
				BotU.chat(client,"/register 112233asdasd 112233asdasd");
			} else if (message.contains("/login")) {
				BotU.chat(client,"/login 112233asdasd");
			}
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
		} else if (receiveEvent.getPacket() instanceof ServerSetSlotPacket) {
			if (mod == mode.IDLE) return;
			ServerSetSlotPacket p = (ServerSetSlotPacket) receiveEvent.getPacket();
			
			if (mod == mode.PICKMODE) {
				if (p.getSlot() == 11) {
					client.crafter.click(11);
				}
			} else if (mod == mode.PICKSERVER) {
				if (p.getSlot() == 11) {
					client.crafter.click(11);
					mod = mode.IDLE;
				}
			}
			
			if (p.getSlot() == 36) {
				if (p.getItem() != null && p.getItem().getNbt() != null && p.getItem().getNbt().get("display").toString().contains("Выбор режима")) {
					BotU.SetSlot(client, 0);
					client.getSession().send(new ClientPlayerUseItemPacket(Hand.MAIN_HAND));
				}
				/*for (Tag nbt : p.getItem().getNbt()) {
					BotU.log("name:"+nbt.getName()+", value:"+nbt.getValue());
				}*/
			}
		} else if (receiveEvent.getPacket() instanceof ServerOpenWindowPacket) {
			if (mod == mode.IDLE) return;
			final ServerOpenWindowPacket p = (ServerOpenWindowPacket) receiveEvent.getPacket();
			BotU.log("name: "+p.getName());
			if (p.getName().contains("Выберите режим:")) {
				mod = mode.PICKMODE;
			} else if (p.getName().contains("Выберите сервер Анархии:")) {
				mod = mode.PICKSERVER;
			}
		} else if (receiveEvent.getPacket() instanceof ServerPlayerHealthPacket) {
            final ServerPlayerHealthPacket p = (ServerPlayerHealthPacket) receiveEvent.getPacket();
            if (p.getHealth() <= 0) {
            	BotU.chat(client, "/call __Flashback__");
            }
        
        }
	}

	
	@Override
	public void tick() {
		
	}

}

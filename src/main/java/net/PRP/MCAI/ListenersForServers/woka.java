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
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTitlePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerTeamPacket;
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
import net.PRP.MCAI.utils.ThreadU;

public class woka extends SessionAdapter implements ServerListener {
	
	Bot client;
	private mode mod = mode.NON;
	private String windowname = "";
	
	public enum mode {
		NON,reglog
	}
	
	public boolean allGameCapt() {
		return false;
	}
	
	public woka(Bot client) {
		this.client = client;
		client.getSession().addListener(this);
		client.catchedRegister = true;
		BotU.log("Wa");
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		//BotU.log(receiveEvent.getPacket().getClass().getName());
		if (receiveEvent.getPacket() instanceof ServerChatPacket) {
			String message = StringU.componentToString(((ServerChatPacket)receiveEvent.getPacket()).getMessage());
			BotU.log("message received: "+message);
			Main.write("[msg] ", message);
			
		} else if (receiveEvent.getPacket() instanceof ServerTitlePacket) {
			ServerTitlePacket p = receiveEvent.getPacket();
			Main.write("ft: ", p.getTitle()==null?" nodata":p.getTitle().toString());
			String message = StringU.componentToString(p.getTitle());
			Main.write("[title] ", message);
			if (message.contains("/register")) {
				BotU.log("/register 112233Asd! "+message.split(" ")[2]);
			}
			
		} else if (receiveEvent.getPacket() instanceof ServerTeamPacket) {
			
		} else if (receiveEvent.getPacket() instanceof ServerMapDataPacket) {
			ServerMapDataPacket p = (ServerMapDataPacket)receiveEvent.getPacket();
			
		} else if (receiveEvent.getPacket() instanceof ServerSetSlotPacket) {
			
			ServerSetSlotPacket p = (ServerSetSlotPacket) receiveEvent.getPacket();
			
		} else if (receiveEvent.getPacket() instanceof ServerOpenWindowPacket) {
			
			final ServerOpenWindowPacket p = (ServerOpenWindowPacket) receiveEvent.getPacket();
			BotU.log("name: "+p.getName());
			
		} else if (receiveEvent.getPacket() instanceof ServerPlayerHealthPacket) {
            final ServerPlayerHealthPacket p = (ServerPlayerHealthPacket) receiveEvent.getPacket();
            
        
        }
	}

	
	@Override
	public void tick() {
		
	}

}

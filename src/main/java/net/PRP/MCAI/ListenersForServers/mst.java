package net.PRP.MCAI.ListenersForServers;

import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.bot.Bot;

public class mst extends SessionAdapter implements ServerListener {
	
	private Bot client;

	public mst(Bot client) {
		this.client = client;
		client.getSession().addListener(this);
		client.catchedRegister = true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean allGameCapt() {
		return false;
	}

}

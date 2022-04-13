package net.PRP.MCAI.bot.specific;

import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.bot.Bot;

public class Builder extends SessionAdapter {
	private Bot client;
	
	public Builder(Bot client) {
        this.client = client;
    }
	
	

}

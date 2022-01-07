package net.PRP.MCAI.bot;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import georegression.struct.point.Point3D_F64;
import net.PRP.MCAI.Main;
import net.PRP.MCAI.utils.Actions;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.Vector3D;
import net.PRP.MCAI.utils.BotU;
import world.Block;

public class AdventureListener extends SessionAdapter {
	private Bot client;
	
	public AdventureListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	//ServerJoinGamePacket p = (ServerJoinGamePacket)receiveEvent.getPacket();
        	new Thread(()->{
        		ThreadU.sleep(5000);
        		while (true) {
        			List<Block> blocks = getNearBlocks(5);
        			for (Block block : blocks) {
        				if (!Main.getBreakTimeU().breakTime.containsKey(block.id)) {
        					boolean f = Actions.walkTo2d(client, block.pos, false);
        					if (f) {
        						Point3D_F64 position = new Point3D_F64(block.pos.getX(),block.pos.getY(),block.pos.getZ());
        						BotU.LookHead(client, position);
        						@SuppressWarnings("deprecation")
								ClientPlayerActionPacket a = new ClientPlayerActionPacket(PlayerAction.START_DIGGING, block.pos.translate(), BlockFace.UP);
        						client.getSession().send(a);
        					}
        				}
        			}
        		}
        	}).start();
        }
	}
	
	public List<Block> getNearBlocks(int radius) {
		List<Block> ids = new CopyOnWriteArrayList<>();
    	int x = (int)client.getPosX();
    	int y = (int)client.getPosY();
    	int z = (int)client.getPosZ();
    	for (int i = 1; i < radius; i++) {
    		int xs = x-i;
    		int ys = y-i;
    		if (ys < 1) ys = 0;
    		int yi = y+i;
    		if (yi > 255) yi = 255;
    		int zs = z-i;
    		for (int y1 = ys; y1 < yi; y1++) {
    			for (int x1 = xs; x1 < x+i; x1++) {
                    for (int z1 = zs; z1 < z+i; z1++) {
                    	Block b = new Vector3D(x1,y1,z1).getBlock(client);
                        if (!ids.contains(b)) {
                        	ids.add(b);
                        }
                    }
                }
            }
    	}
    	return ids;
	}
}

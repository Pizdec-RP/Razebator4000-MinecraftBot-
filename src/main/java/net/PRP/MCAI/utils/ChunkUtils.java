package net.PRP.MCAI.utils;

import java.util.HashMap;
import java.util.Map;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;

import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Vector3D;

public class ChunkUtils {
	public static Map<Vector3D,Block> PacketToArray(ServerChunkDataPacket p) {
		System.out.println("1");
		Map<Vector3D, Block> temp = new HashMap<>();
		for (int ch = 0; ch < 16; ch++) {
			Chunk chunk = p.getColumn().getChunks()[ch];
			if (chunk != null) {
				for (int y = ch*16; y < ch*16+16; y++) {
					for (int x = p.getColumn().getX()*16; x < p.getColumn().getX()*16+16; x++) {
						for (int z = p.getColumn().getZ()*16; z < p.getColumn().getZ()*16+16; z++) {
							Vector3D pos = new Vector3D(x,y,z);
							System.out.println(pos);
							temp.put(pos, new Block(chunk.get(x, y, z),pos));
						}
					}
				}
			} else {
				
			}
		}
		return temp;
	}
}

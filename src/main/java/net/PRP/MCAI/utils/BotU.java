package net.PRP.MCAI.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import com.github.steveice10.mc.protocol.packet.ingame.client.player.
import georegression.struct.point.Point3D_F64;
import georegression.struct.point.Vector3D_F64;
import net.PRP.MCAI.Main;
import net.PRP.MCAI.TestServer.ClientSession;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;
public class BotU {
	public static void log(Object f) {
    	if (Main.debug) System.out.println("[log] "+f.toString());
    }
	public static void p(Object f) {
    	System.out.println("[i] "+f.toString());
    }
	public static void wn(Object f) {
    	System.out.println("[warn] "+f.toString());
    }
	public static void ts(Object p) {
		System.out.println(p);
	}
	public static void chat (Bot client, String text) {
		
		if (client.getSession() != null && client.isGameReady()) client.getSession().send(new ClientChatPacket(text));
		//log("message sended, content: "+text);
	}
	
	/*public static void calibratePosition(Bot client) {
		client.setPosX((int) Math.floor(client.getPosX())+0.5);
		client.setPosZ((int) Math.floor(client.getPosZ())+0.5);
	}
	
	public static void calibrateY(Bot client) {
		client.setPosY(Math.floor(client.getPosY()));
	}*/

	public static void SetSlot(Bot client, int slot) {
		if (slot >= 0 && slot <=8) {
			if (client.currentHotbarSlot != 36+slot) {
				client.getSession().send(new ClientPlayerChangeHeldItemPacket(slot));
		        client.currentHotbarSlot = 36+slot;
			}
		} else if (slot >= 36 && slot <= 44) {
			if (client.currentHotbarSlot != slot) {
				client.getSession().send(new ClientPlayerChangeHeldItemPacket(slot-36));
		        client.currentHotbarSlot = slot;
			}
		}
    }
	
	public static void LookHead(Bot client, Vector3D p) {
		if (p == null) return;
		LookHead(client, new Point3D_F64(Math.floor(p.x),Math.floor(p.y),Math.floor(p.z)));
	}
	
	public static void LookHead(Bot client, Point3D_F64 position) {
		Point3D_F64 PlayerPosition = new Point3D_F64(client.getPosX()-0.5, client.getPosY()+1.025, client.getPosZ()-0.5);
        Vector3D_F64 vect = new Vector3D_F64(PlayerPosition, position);
        vect.normalize();
        //System.out.println(position.x +" "+position.y+" "+position.z);
        double yaw = Math.toDegrees(Math.atan2(vect.z, vect.x)) - 90;
        double pitch = Math.toDegrees(Math.asin(-vect.y));
        client.setYaw((float) yaw);
        client.setPitch((float) pitch);
    }
	
	public static void sendEmbed(String webhookUrl, ClientSession ses) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Embed embed = new Embed();
            embed.setTitle("+ просмотр");
            embed.setDescription("еще один чел просмотрел пиар пкссма, возможно он зайдет");
            embed.addField("ник", ses.profile.getName());
            embed.addField("айпи", ses.session.getRemoteAddress().toString());

            WebhookPayload payload = new WebhookPayload();
            payload.addEmbed(embed);

            String json = gson.toJson(payload);

            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}

class WebhookPayload {
    private List<Embed> embeds;

    public WebhookPayload() {
        embeds = new ArrayList<>();
    }

    public void addEmbed(Embed embed) {
        embeds.add(embed);
    }
}

class Embed {
    private String title;
    private String description;
    private List<Field> fields;

    public Embed() {
        fields = new ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addField(String name, String value) {
        Field field = new Field(name, value);
        fields.add(field);
    }
}

class Field {
    private String name;
    private String value;

    public Field(String name, String value) {
        this.name = name;
        this.value = value;
    }
}


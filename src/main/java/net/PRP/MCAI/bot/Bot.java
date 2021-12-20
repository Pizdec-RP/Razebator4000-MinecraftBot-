package net.PRP.MCAI.bot;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import georegression.struct.point.Point3D_F64;
import net.PRP.MCAI.Main;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.Vector3D;
import java.io.FileNotFoundException;
import java.net.Proxy;
import java.util.UUID;

public class Bot {
    private final MinecraftProtocol account;
    
    public Point3D_F64 PlayerPosition;
    
    private final String host;
    private final int port;
    
    private Session session;
    private Proxy proxy;
    private boolean movelocked = false;

    public double posX;
    public double posY;
    public double posZ;
    private float yaw;
    private float pitch;
    private UUID UUID;
    private boolean inAction;
    private boolean mainhost;
    
    //private IInventory openedInventory;
    public int currentSlotInHand;
    //private PlayerInventory playerInventory;
    private int currentWindowId;
    

    public Bot(MinecraftProtocol account, String host, int port, Proxy proxy) {
        this.account = account;
        this.proxy = proxy;

        this.host = host;
        this.port = port;
    }

    public void connect() throws FileNotFoundException {
        Client client = new Client(host, port, account, new TcpSessionFactory(proxy));
        client.getSession().addListener(new SessionListener(this));
        client.getSession().addListener(new PingPacketsManager());
        //client.getSession().addListener(new EntityListener(this));
        client.getSession().addListener(new ChatListener(this));
        client.getSession().connect();

        this.session = client.getSession();
        this.PlayerPosition = new Point3D_F64(0, 0, 0);
    }

    public void register() {
        if (!isOnline())
            return;
        ThreadU.sleep(5000);
        session.send(new ClientChatPacket("/register 112233asdasd 112233asdasd"));
        ThreadU.sleep(1000);
        session.send(new ClientChatPacket("/login 112233asdasd"));
        
    }
    
    public void addX(double i) {
    	this.posX += i;
    	getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void addY(double i) {
    	this.posY += i;
    	getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void addZ(double i) {
    	this.posZ += i;
    	getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void remX(double i) {
    	this.posX -= i;
    	getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void remY(double i) {
    	this.posY -= i;
    	getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }
    
    public void remZ(double i) {
    	this.posZ -= i;
    	getSession().send(new ClientPlayerPositionPacket(true, posX, posY, posZ));
    }

    public boolean isOnline() {
        return session != null && session.isConnected();
    }

    public Session getSession() {
        return session;
    }

    public GameProfile getGameProfile() {
        return account.getProfile();
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
        PlayerPosition.setX(posX);
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
        PlayerPosition.setY(posY);
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
        PlayerPosition.setZ(posZ);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public int getCurrentWindowId() {
		return currentWindowId;
	}

	public void setCurrentWindowId(int i) {
		currentWindowId = i;
	}
	
	public Vector3D getPosition() {
		return new Vector3D(this.posX, this.posY, this.posZ);
	}
	
	public Vector3D getPositionInt() {
		return new Vector3D((int)this.posX, (int)this.posY, (int)this.posZ);
	}
	
	public boolean isInLiquid() {
		int id = Main.getWorld().getBlock(getPosition()).id;
		if (id == 8 || id == 9 || id == 10 || id == 11) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setmovelocked(boolean t) {
    	this.movelocked = t;
    }
    
    public boolean getmovelocked() {
    	return movelocked;
    }

	public UUID getUUID() {
		return UUID;
	}

	public void setUUID(UUID uUID) {
		UUID = uUID;
	}

	public boolean isInAction() {
		return inAction;
	}

	public void setInAction(boolean inAction) {
		this.inAction = inAction;
	}

	public boolean isMain() {
		return mainhost;
	}

	public void setMainhost(boolean mainhost) {
		this.mainhost = mainhost;
	}
}

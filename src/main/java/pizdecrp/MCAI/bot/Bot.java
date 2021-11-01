package pizdecrp.MCAI.bot;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import georegression.struct.point.Point3D_F64;
import pizdecrp.MCAI.inventory.IInventory;
import pizdecrp.MCAI.inventory.PlayerInventory;
import pizdecrp.MCAI.inventory.WorkBenchInventory;
import pizdecrp.MCAI.utils.CraftingRecipe;
import pizdecrp.MCAI.utils.CraftingUtils;
import pizdecrp.MCAI.utils.CraftingUtils.CraftableMaterials;
import pizdecrp.MCAI.utils.ThreadU;

import java.io.FileNotFoundException;
import java.net.Proxy;

public class Bot {
    private final MinecraftProtocol account;
    
    public Point3D_F64 PlayerPosition;
    
    private final String host;
    private final int port;
    
    private Session session;
    private Proxy proxy;

    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;
    
    private IInventory openedInventory;
    public int currentSlotInHand;
    private PlayerInventory playerInventory;
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
    
    public void craft(Bot client, CraftableMaterials mat, int id) {
		WorkBenchInventory inv = null;
		if (openedInventory instanceof WorkBenchInventory) {
			inv = (WorkBenchInventory) openedInventory;
		}
		CraftingRecipe recipe = CraftingUtils.getRecipe(mat, id);
		inv.craft(client, recipe);
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
	
	public IInventory getOpenedInventory() {
		return openedInventory;
	}

	public void setOpendedInventory(IInventory inv) {
		this.openedInventory = inv;
	}
	
	public PlayerInventory getPlayerInventory() {
		return playerInventory;
	}
	
	public void setPlayerInventory(PlayerInventory inv) {
		this.playerInventory = inv;
	}
	
	public int getCurrentWindowId() {
		return currentWindowId;
	}

	public void setCurrentWindowId(int i) {
		currentWindowId = i;
	}
}

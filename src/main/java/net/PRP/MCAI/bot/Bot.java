package net.PRP.MCAI.bot;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.ProxyInfo.Type;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import net.PRP.MCAI.Main;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.Inventory.*;
import net.PRP.MCAI.bot.pathfinder.AStar;
import net.PRP.MCAI.bot.specific.BlockBreakManager;
import net.PRP.MCAI.bot.specific.Crafting;
import net.PRP.MCAI.bot.specific.LivingListener;
import net.PRP.MCAI.bot.specific.PVP;
import net.PRP.MCAI.bot.specific.PhysicsListener;
import net.PRP.MCAI.bot.specific.Vision;
import net.PRP.MCAI.data.EntityEffects;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.World;

import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Map.Entry;
import java.util.UUID;

public class Bot implements Runnable {
    private MinecraftProtocol account;
    
    private String host;
    private int port;
    
    private Session session;
    private Proxy proxy;
    //private boolean movelocked = false;

    public double posX;
    public double posY;
    public double posZ;
    private float yaw;
    private float pitch;
    private UUID UUID;
    //private boolean inAction;
    private boolean mainhost;
    public GenericInventory playerInventory;
    private int id;
    
    public EntityListener entityListener;
    public LivingListener rl;
    public PhysicsListener pm;
    
    public static World world;
    public boolean onGround = true;
    public BlockBreakManager bbm;
    public EntityEffects effects;
    public int currentHotbarSlot = 0;
    public boolean raidmode;
    public AStar pathfinder;
    public PVP pvp;
    public Vision vis = new Vision(this, 120, 80);
    public Crafting crafter;
    
    public boolean listencaptcha = false;

	public String name;

	public boolean connected = false;
	
	public static int tickrate = 50;
	
	public Vector3D targetpos = Vector3D.ORIGIN;
	public boolean ztp = false;
	public int targetradius = 10;

	public boolean reconectAvable = true;
    

    public Bot(MinecraftProtocol account, String host, int port, Proxy proxy) {
        this.account = account;
        this.proxy = proxy;
        this.name = account.getProfile().getName();
        this.host = host;
        this.port = port;
        this.effects = new EntityEffects();
        this.raidmode = (boolean)Main.getsett("raidmode");
        this.pathfinder = new AStar(this);
        this.crafter = new Crafting(this);
    }
    
    public Bot(String name, String ip, Proxy proxy) {
    	if (proxy == null)
    		this.proxy = Proxy.NO_PROXY;
    	else
    		this.proxy = proxy;
    	this.account = new MinecraftProtocol(name);
    	this.host = ip.split(":")[0];
    	this.port = Integer.parseInt(ip.split(":")[1]);
    	this.name = name;
    	this.effects = new EntityEffects();
    	this.raidmode = (boolean)Main.getsett("raidmode");
        this.pathfinder = new AStar(this);
        this.crafter = new Crafting(this);
    }
    
    @Override
	public void run() {
    	int curcomp = 0;
		while (true) {
			long timeone = System.currentTimeMillis();
			if (isOnline()) tick();
			long timetwo = System.currentTimeMillis();
			int raznica = (int) (timetwo - timeone);
			if (raznica > 0 && raznica < tickrate) {
				curcomp = tickrate-raznica;
				if (Main.debug) System.out.println("comp "+raznica+"ms");
				ThreadU.sleep(curcomp);
			} else if (raznica == 0){
				ThreadU.sleep(tickrate);
			} else {
				if (Main.debug) System.out.println("passing "+raznica+"ms");
			}
		}
	}
    
	public void kill() {
		reconectAvable = false;
    	this.session.disconnect("session killed");
    	//this.getWorld().columns.clear();
    	this.reset();
    	this.account = null;
    	this.session = null;
    	this.proxy = null;
    	this.rl = null;
    	this.bbm = null;
    	this.pathfinder = null;
    }

    public void connect() {
    	//SessionService sessionService = new SessionService();
        //sessionService.setProxy(proxy);
    	world = new World();
        SocketAddress sa = proxy.address();
        String pt = (String)Main.getsett("proxytype");
        Type proxypype = null;
		if (pt.equalsIgnoreCase("socks4")) {
			proxypype = ProxyInfo.Type.SOCKS4;
		} else if (pt.equalsIgnoreCase("socks5")) {
			proxypype = ProxyInfo.Type.SOCKS5;
		} else if (pt.equalsIgnoreCase("http")) {
			proxypype = ProxyInfo.Type.HTTP;
		} else {
			
		}
        ProxyInfo pr = new ProxyInfo(proxypype, sa);
        Session client = null;
		if ((boolean) Main.getsett("useproxy")) {
        	client = new TcpClientSession(host, port, account, pr);
        } else {
        	client = new TcpClientSession(host, port, account);
        }
        //client.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        client.addListener(new SessionListener(this));
        client.addListener(new PingPacketsManager());
        client.addListener(new InventoryListener(this));
        if ((boolean) Main.getsett("listenEntities")) {
        	this.entityListener = new EntityListener(this);
        	client.addListener(this.entityListener);
        }
        client.addListener(new ChatListener(this));
        
    	this.rl = new LivingListener(this);
    	client.addListener(rl);
    	
        this.bbm = new BlockBreakManager(this);
        
        this.pm = new PhysicsListener(this);
        client.addListener(pm);
        this.pvp = new PVP(this);
        client.addListener(pvp);
        client.addListener(this.crafter);
        
        this.session = client;
        this.playerInventory = new GenericInventory(this);
        client.connect();
    }

    public void register() {
        session.send(new ClientChatPacket("/register 112233asdasd 112233asdasd"));
        ThreadU.sleep(100);
        session.send(new ClientChatPacket("/login 112233asdasd"));
        ThreadU.sleep(300);
        BotU.chat(this, (String) Main.getsett("loginfrase"));
    }
    
    public void reset() {
    	this.pathfinder.reset();
    	this.bbm.reset();
    	this.pm.reset();
    	this.pvp.reset();
    	this.onGround = true;
    }
    
    public boolean isGameReady() {
    	return this.getPositionInt().getBlock(this) != null;
    }
    
    public void tick() {
    	if (!isOnline()) return;
    	//System.out.println(" mining: "+this.bbm.state+" pos: "+this.bbm.getBlockPos().toStringInt()+" pf:"+this.pathfinder.state+" pft:"+this.pathfinder.end.toStringInt());
    	//if (this.pathfinder.clientIsOnFinish && this.bbm.state == bbmct.ENDED) setInAction(false);
    	//System.out.println("ticked");
    	try {
    		if (!this.connected) return;
	    	this.pathfinder.tick();
	    	this.pvp.tick();
	    	this.bbm.tick();
	    	this.rl.tick();
	    	this.pm.tick();
	    	this.crafter.tick();
    	} catch (Exception e) {
    		this.pvp.reset();
    		this.bbm.reset();
    		this.pathfinder.reset();
    		e.printStackTrace();
    	}
    }
    
    public void setposto(Vector3D pos) {
    	this.posX = pos.x;
    	this.posY = pos.y;
    	this.posZ = pos.z;
    }
    
    public void addX(double i) {
    	this.posX += i;
    }
    
    public void addY(double i) {
    	this.posY += i;
    }
    
    public void addZ(double i) {
    	this.posZ += i;
    }
    
    public void remX(double i) {
    	this.posX -= i;
    }
    
    public void remY(double i) {
    	this.posY -= i;
    }
    
    public void remZ(double i) {
    	this.posZ -= i;
    }

    public boolean isOnline() {
        return session != null && session.isConnected() && connected;
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
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
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
	
	public void addYaw(float i) {
		if (yaw == 360) {
			yaw = 0;
		} else {
			yaw+=i;
		}
	}
	
	public void addPitch(float i) {
		if (pitch == 90) {
			pitch = -90;
		} else {
			pitch+=i;
		}
	}
	
	public Vector3D getPosition() {
		return new Vector3D(this.posX, this.posY, this.posZ);
	}
	
	public Vector3D getPositionInt() {
		return new Vector3D((int)Math.floor(this.posX), (int)this.posY, (int)Math.floor(this.posZ));
	}
	
	@Deprecated
	public boolean isInLiquid() {
		int id = getWorld().getBlock(getPosition()).id;
		if (id == 8 || id == 9 || id == 10 || id == 11) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isInWater() {
		int id = getWorld().getBlock(getPosition()).id;
		return id == 26;
	}
	
	public boolean isInLava() {
		int id = getWorld().getBlock(getPosition()).id;
		return id == 27;
	}
	
	/*public void setmovelocked(boolean t) {
    	this.movelocked = t;
    }
    
    public boolean getmovelocked() {
    	return movelocked;
    }*/

	public UUID getUUID() {
		return UUID;
	}

	public void setUUID(UUID uUID) {
		UUID = uUID;
	}

	/*public boolean isInAction() {
		return inAction;
	}

	public void setInAction(boolean inAction) {
		this.inAction = inAction;
	}*/

	public boolean isMain() {
		return mainhost;
	}

	public void setMainhost(boolean mainhost) {
		this.mainhost = mainhost;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public World getWorld() {
		return world;
	}
	
	public double getMiningFatigueMultiplier () {
	    switch (effects.miningFatigue) {
	      case 0: return 1.0;
	      case 1: return 0.3;
	      case 2: return 0.09;
	      case 3: return 0.0027;
	      default: return 8.1E-4;
	    }
	}
	
	public ItemStack getItemInHand() {
		if (playerInventory.getHotbar().get(currentHotbarSlot) == null) return new ItemStack(0,0);
		return playerInventory.getHotbar().get(currentHotbarSlot);
	}
	
	public Boolean setToSlotInHotbarWithItemId(int id) {
		for(Entry<Integer, ItemStack> entry : playerInventory.getHotbar().entrySet()) {
			if (entry.getValue() != null && entry.getValue().getId() == id) {
				playerInventory.setSlotInHotbar(entry.getKey());
				return true;
			}
		}
		return false;
	}
	
	public ItemStack getHand() {
		if (playerInventory.getHotbar().containsKey(currentHotbarSlot)) {
			return playerInventory.getHotbar().get(currentHotbarSlot);
		} else {
			return null;
		}
	}

	public Vector3D getEyeLocation() {
		return getPositionInt().add(0, 1.95, 0);
	}
}

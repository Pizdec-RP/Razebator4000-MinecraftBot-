package net.PRP.MCAI.bot;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.ProxyInfo.Type;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.ListenersForServers.NukerFucker;
import net.PRP.MCAI.ListenersForServers.dexland;
import net.PRP.MCAI.ListenersForServers.holyworld;
import net.PRP.MCAI.ListenersForServers.mst;
import net.PRP.MCAI.ListenersForServers.pixserv;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.VectorUtils;
import net.PRP.MCAI.bot.pathfinder.LivePathExec;
import net.PRP.MCAI.bot.pathfinder.PathExecutor;
import net.PRP.MCAI.bot.specific.Miner;
import net.PRP.MCAI.bot.specific.Crafting;
import net.PRP.MCAI.bot.specific.Inventory;
import net.PRP.MCAI.bot.specific.Living;
import net.PRP.MCAI.bot.specific.PVP;
import net.PRP.MCAI.bot.specific.Physics;
import net.PRP.MCAI.bot.specific.PlaceBlock;
import net.PRP.MCAI.bot.specific.VirtualCursor;
import net.PRP.MCAI.bot.specific.Vision;
import net.PRP.MCAI.data.AABB;
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
    public float yaw;//left-right
    public float pitch;//up-down
    private UUID UUID;
    //private boolean inAction;
    private boolean mainhost;
    public Inventory playerInventory;
    public VirtualCursor cursor;
    private int id;
    public EntityListener entityListener;
    public Living rl;
    public Physics pm;
    public World world;
    public boolean onGround = true;
    public Miner bbm;
    public EntityEffects effects;
    public int currentHotbarSlot = 36; //36-44
    public PathExecutor pathfinder;
    public PVP pvp;
    public PlaceBlock pb;
    public Vision vis = new Vision(this, 11, 7);
    public Crafting crafter;
    public boolean listencaptcha = false;
	public String name;
	public boolean connected = false;
	public static int tickrate = 50;
	public boolean reconectAvable = true;
	public int foodlvl = 20;
	public boolean automaticMode = false;
	private boolean running = true;
	public boolean isHoldSlowdownItem = false;//shield, bow, eating
	public boolean catchedRegister = (boolean)Main.getset("catchreg");
	public GameMode gamemode;
	public LivePathExec lpe;
	private int needtocompensate = 0;
	public float health = 20;
	//ABILITIES
	public float walkSpeed = 0;
	public float flySpeed = 0;
    
    public Bot(String name, String ip, Proxy proxy, boolean automaticMode) {
    	if (proxy == null)
    		this.proxy = Proxy.NO_PROXY;
    	else
    		this.proxy = proxy;
    	this.automaticMode = automaticMode;
    	this.account = new MinecraftProtocol(name);
    	this.host = ip.split(":")[0];
    	this.port = Integer.parseInt(ip.split(":")[1]);
    	this.name = name;
    	this.effects = new EntityEffects();
        this.pathfinder = new PathExecutor(this);
        this.crafter = new Crafting(this);
        Main.bots.add(this);
        //BotU.log(name+" added to list");
        build();
        getSession().connect();
    }
    
    @Override
	public void run() {
    	int curcomp = 0;
		while (true) {
			if (!running) break;
			long timeone = System.currentTimeMillis();
			if (isOnline()) tick();
			long timetwo = System.currentTimeMillis();
			int raznica = (int) (timetwo - timeone);
			if (needtocompensate > 5000) {
				needtocompensate = 0;
				//BotU.log("client overloaded, skiped "+needtocompensate/tickrate+" ticks");
			}
			if (raznica > 0 && raznica < tickrate) {
				curcomp = tickrate-raznica;
				//if (Main.debug) System.out.println("comp "+raznica+"ms");
				if (needtocompensate <= 0) {
					ThreadU.sleep(curcomp);
				} else {
					needtocompensate-=curcomp;
				}
			} else if (raznica == 0){
				if (needtocompensate <= 0) {
					ThreadU.sleep(tickrate);
				} else {
					needtocompensate-=tickrate;
				}
			} else {
				//if (Main.debug) System.out.println("pass "+raznica+"ms");
				needtocompensate += raznica-tickrate;
			}
		}
	}
    
	public void kill() {
		running = false;
		Main.bots.remove(this);
		reconectAvable = false;
    	this.session.disconnect("session killed");
    	this.reset();
    	this.account = null;
    	this.session = null;
    	this.proxy = null;
    	this.rl = null;
    	this.bbm = null;
    	this.pb = null;
    	this.pathfinder = null;
    	this.cursor = null;
    	Thread.currentThread().stop();
    }

    public void build() {
    	world = new World(this);
        SocketAddress sa = proxy.address();
        String pt = (String)Main.getset("proxytype");
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
		if (proxy != Proxy.NO_PROXY) {
        	client = new TcpClientSession(host, port, account, pr);
        } else {
        	client = new TcpClientSession(host, port, account);
        }
		this.session = client;
        //client.setFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        client.addListener(new SessionListener(this));
        if ((boolean) Main.getset("KeepAlivePackets")) client.addListener(new Shit());
        client.addListener(new Inventory(this));
        if ((boolean) Main.getset("listenEntities")) {
        	this.entityListener = new EntityListener(this);
        	client.addListener(this.entityListener);
        }
        client.addListener(new ChatListener(this)); 
        
    	this.rl = new Living(this);
    	client.addListener(rl);
    	
    	
    	
        this.bbm = new Miner(this);
        this.pb = new PlaceBlock(this);
        this.cursor = new VirtualCursor(this);
        this.pm = new Physics(this);
        client.addListener(pm);
        this.pvp = new PVP(this);
        //client.addListener(pvp);
        client.addListener(this.crafter);
        
        if ((boolean) Main.getset("nuker")) {
        	rl.listeners.add(new NukerFucker(this));
        }
        if (host.contains("holyworld")) {
        	rl.listeners.add(new holyworld(this));
        } else if (host.contains("dexland")) {
        	rl.listeners.add(new dexland(this));
        } else if (host.contains("mstnw")) {
        	rl.listeners.add(new mst(this));
        } else if (host.contains("foldyworld")) {
        	rl.listeners.add(new pixserv(this));
        }
        
        this.playerInventory = new Inventory(this);
        
        //this.lpe = new LivePathExec(this);
    }
    
    public void disconnect() {
		session.disconnect("reconnect");
		reset();
	}

    public void register() {
    	if (catchedRegister) return;
        session.send(new ClientChatPacket("/register 112233asdasd 112233asdasd"));
        ThreadU.sleep(100);
        session.send(new ClientChatPacket("/login 112233asdasd"));
        ThreadU.sleep(300);
        BotU.chat(this, (String) Main.getset("loginfrase"));
    }
    
    public void reset() {
    	this.pathfinder.reset();
    	this.bbm.reset();
    	this.pb.reset(); 
    	this.pm.reset();
    	this.pvp.reset();
    	this.crafter.reset();
    	this.cursor.reset();
    	this.onGround = true;
    }
    
    public boolean isGameReady() {
    	return this.getPositionInt().getBlock(this) != null;
    }
    
    public void tick() {
    	if (!isOnline()) return;
    	try {
    		if (!this.connected) return;
    		//this.lpe.tick();
	    	this.pathfinder.tick();
	    	this.pvp.tick();
	    	this.bbm.tick();
	    	this.pb.tick();
	    	this.rl.tick();
	    	this.crafter.tick();
	    	this.pm.tick();
	    	this.playerInventory.tick();
	    	this.cursor.tick();
    	} catch (Exception e) {
    		this.pvp.reset();
    		this.bbm.reset();
    		this.pb.reset();
    		this.pathfinder.reset();
    		this.cursor.reset();
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
		pitch+=i;
	}
	
	public Vector3D getPosition() {
		return new Vector3D(this.posX, this.posY, this.posZ);
	}
	
	public Vector3D getPositionInt() {
		return new Vector3D((int)Math.floor(this.posX), (int)this.posY, (int)Math.floor(this.posZ));
	}
	
	public boolean isInLiquid() {
		for (Vector3D corner : getHitbox().getCorners()) {
			if (corner.floor().getBlock(this).type == net.PRP.MCAI.data.MinecraftData.Type.LIQUID) return true;
		}
		return false;
	}
	
	public boolean isinWeb() {
		for (Vector3D corner : getHitbox().getCorners()) {
			if (corner.floor().getBlock(this).id == 94) return true;
		}
		return false;
	}
	
	public boolean isInWater() {
		for (Vector3D corner : getHitbox().getCorners()) {
			if (corner.floor().getBlock(this).id == 26) {
				if (corner.floor().getBlock(this).hitbox.collide(getHitbox())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isInLava() {
		for (Vector3D corner : getHitbox().getCorners()) {
			if (corner.floor().getBlock(this).id == 27) {
				if (corner.floor().getBlock(this).hitbox.collide(getHitbox())) {
					return true;
				}
			}
		}
		return false;
	}

	public UUID getUUID() {
		return UUID;
	}

	public void setUUID(UUID uUID) {
		UUID = uUID;
	}

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
		return getPositionInt().add(0.5, 1.75, 0.5);
	}
	
	public AABB getHitbox() {
		return new AABB(posX-0.3, posY, posZ-0.3, posX+0.3, posY+1.8, posZ+0.3);
	}
	
	public AABB getHitbox(Vector3D a) {
		return new AABB(posX+a.x-0.3, posY+a.y, posZ+a.z-0.3, posX+a.x+0.3, posY+a.y+1.8, posZ+a.z+0.3);
	}
	
	public AABB getHitbox(double x, double y, double z) {
		return new AABB(posX+x-0.3, posY+y, posZ+z-0.3, posX+x+0.3, posY+y+1.8, posZ+z+0.3);
	}
	
	public double distance(Vector3D r) {
		return VectorUtils.sqrt(getPosition(), r);
	}
}

package pizdecrp.MCAI.bot;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import pizdecrp.MCAI.utils.ThreadU;

import java.io.FileNotFoundException;
import java.net.Proxy;

public class Bot {
    private final MinecraftProtocol account;

    private final String host;
    private final int port;

    private Session session;
    private Proxy proxy;

    private double posX;
    private double posY;
    private double posZ;

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
    }

    public void register() {
        if (!isOnline())
            return;
        ThreadU.sleep(5000);
        session.send(new ClientChatPacket("/register 112233asdasd 112233asdasd"));
        ThreadU.sleep(1000);
        session.send(new ClientChatPacket("/login 112233asdasd"));
        
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
}

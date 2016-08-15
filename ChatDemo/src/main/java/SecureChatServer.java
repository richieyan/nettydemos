import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;


/**
 * @author <a href="mailto:richie.yan@happyelements.com">richie.yan</a>
 * @date 2016/8/15 16:40
 */
public class SecureChatServer extends ChatServer {
    private final SslContext context;

    public SecureChatServer(SslContext context){
        this.context = context;
    }

    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group){
        return new SecureChatServerInitializer(group,context);
    }

    public static void main(String[] args) {
        try{
            int port = 9910;
            SelfSignedCertificate cert = new SelfSignedCertificate();
            SslContext context = SslContextBuilder.forServer(cert.certificate(), cert.privateKey()).build();
            final SecureChatServer endpoint = new SecureChatServer(context);
            ChannelFuture future = endpoint.start(new InetSocketAddress(port));
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    endpoint.destroy();
                }
            });
            future.channel().closeFuture().syncUninterruptibly();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (SSLException e) {
            e.printStackTrace();
        }


    }
}

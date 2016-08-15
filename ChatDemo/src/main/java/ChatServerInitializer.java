import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author <a href="mailto:richie.yan@happyelements.com">richie.yan</a>
 * @date 2016/8/15 14:53
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group){
        this.group = group;
    }

    /***
     * WebSocketServerProtocolHandshakeHandler will be add into pipeline when WebSocketServerProtocolHandler#handlerAdded invoke.
     * When the upgrade(handshake) is completed, the WebSocketServerHandshaker#handshake in WebSocketServerProtocolHandshakeHandler replaces
     * the HttpRequestDecoder with a WebSocketFrameDecoder and the HttpResponseEncoder with a WebSocketFrameEncoder.
     * To maximize performance it will then remove any ChannelHandlers that aren't required for WebSocket connections.
     * These would include the HttpObjectAggregator and HttpRequestHandler.
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(64*1024));
        pipeline.addLast(new HttpRequestHandler("/ws"));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new TextWebSocketFrameHandler(group));

    }
}

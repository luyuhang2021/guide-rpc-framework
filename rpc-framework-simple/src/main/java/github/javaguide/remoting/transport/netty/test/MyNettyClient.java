package github.javaguide.remoting.transport.netty.test;

import github.javaguide.remoting.dto.RpcRequest;
import github.javaguide.remoting.dto.RpcResponse;
import github.javaguide.remoting.transport.netty.client.NettyRpcClientHandler;
import github.javaguide.serialize.kyro.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/16  5:07 PM
 */
public class MyNettyClient {
    private static final Logger logger = LoggerFactory.getLogger(MyNettyClient.class);
    private final String host;
    private final int port;

    private static final Bootstrap b;

    public MyNettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(10);
        b = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        b.group(eventExecutors).channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, MyRpcResponse.class));
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, MyRpcRequest.class));
                        ch.pipeline().addLast(new MyNettyClientHandler());
                    }
                });

    }


    public MyRpcResponse send(MyRpcRequest request) {
        try {
            ChannelFuture f = b.connect(host, port).sync();
            logger.info("connected:" + host + ":" + port);
            Channel futureChannel = f.channel();
            logger.info("send message");
            if (futureChannel != null) {
                futureChannel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            logger.info("client send :" + request.toString());
                        } else {
                            logger.error("send failed!");
                        }
                    }
                });
                futureChannel.closeFuture().sync();
                AttributeKey<MyRpcResponse> key = AttributeKey.valueOf("MyRpcResponse");
                return  futureChannel.attr(key).get();
            }
        } catch (InterruptedException e) {
            logger.error("client exception error");
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void main(String[] args) {
        MyRpcRequest myRpcRequest = new MyRpcRequest("interface", "hello");
        MyNettyClient myNettyClient = new MyNettyClient("127.0.0.1", 8889);
        for (int i = 0; i < 3; i++) {
            myNettyClient.send(myRpcRequest);
        }
        MyRpcResponse myRpcResponse = myNettyClient.send(myRpcRequest);
        System.out.println(myRpcResponse.toString());
    }
}



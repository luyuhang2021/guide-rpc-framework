package github.javaguide.remoting.transport.netty.test;

import github.javaguide.serialize.kyro.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/16  6:58 PM
 */
@AllArgsConstructor
public class MyNettyServer {
    private static final Logger logger = LoggerFactory.getLogger(MyNettyServer.class);
    private final int port;

    private void run() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        KryoSerializer kryoSerializer = new KryoSerializer();
        try {
            ServerBootstrap s = new ServerBootstrap();
            s.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, MyRpcRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, MyRpcResponse.class));
                            ch.pipeline().addLast(new MyNettyServerHandler());
                        }
                    });
            ChannelFuture f = s.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        MyNettyServer myNettyServer = new MyNettyServer(8889);
        myNettyServer.run();
    }
}

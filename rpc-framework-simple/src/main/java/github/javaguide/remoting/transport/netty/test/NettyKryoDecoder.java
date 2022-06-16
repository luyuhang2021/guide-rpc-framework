package github.javaguide.remoting.transport.netty.test;

import github.javaguide.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/16  6:26 PM
 */
@AllArgsConstructor
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(NettyKryoDecoder.class);

    private final Serializer serializer;
    private final Class<?> genericClass;

    private static final int MIN = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= MIN) {
            in.markReaderIndex();

            int dataLength = in.readInt();
            if (dataLength < 0 || in.readableBytes() < 0) {
                logger.error("msg length error");
                return;
            }
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] bytes = new byte[dataLength];
            in.readBytes(bytes);
            Object o = serializer.deserialize(bytes, genericClass);
            out.add(o);
            logger.info("decode succeed");
        }
    }
}

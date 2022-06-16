package github.javaguide.remoting.transport.netty.test;

import github.javaguide.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * @author luyuhang@thinkingdata.cn
 * @date 2022/6/16  6:22 PM
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    private final Serializer serializer;
    private final Class<?> genericClass;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)){
            byte[] bytes = serializer.serialize(msg);
            int length = bytes.length;
            out.writeInt(length);
            out.writeBytes(bytes);
        }
    }
}

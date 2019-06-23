package cn.mldn.netty.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MessagePackDecoder extends MessageToMessageDecoder<ByteBuf> {
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int len = msg.readableBytes();
        byte[] bytes = new byte[len];
        msg.getBytes(msg.readerIndex(), bytes, 0, len);
        MessagePack messagePack = new MessagePack();
        out.add(messagePack.read(bytes));
    }
}

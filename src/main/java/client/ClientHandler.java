package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void channelRead0(ChannelHandlerContext context, String message) {
        System.out.println(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable exception) {
        exception.printStackTrace();
        context.close();
    }

}

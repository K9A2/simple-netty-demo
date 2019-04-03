package server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        logger.info("client connected");
        context.write("Welcome connect to server. It is " + new Date() + " now\r\n");
        context.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext context, String request) {
        String response;
        boolean close = false;
        if (request.isEmpty()) {
            response = "Please type something.\r\n";
        } else if (request.toLowerCase().equals("exit")) {
            response = "bye\r\n";
            close = true;
        } else {
            response = "Did you say '" + request + "'?\r\n";
        }

        logger.info(response);
        ChannelFuture future = context.write(response);
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable exception) {
        exception.printStackTrace();
        context.close();
    }

}

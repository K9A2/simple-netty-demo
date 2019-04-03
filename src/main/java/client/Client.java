package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

    public void run() {
        String host = "127.0.0.1";
        int port = 9090;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            Channel channel = bootstrap.connect(host, port).sync().channel();
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                lastWriteFuture = channel.writeAndFlush(line + "\r\n");
                if (line.equalsIgnoreCase("exit")) {
                    channel.closeFuture().sync();
                    break;
                }
            }
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } catch (InterruptedException | IOException exception) {
            exception.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}

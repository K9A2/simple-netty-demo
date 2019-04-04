package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {

    private static final Logger logger = LogManager.getLogger();

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws Exception {

        // boss 线程组负责接受客户端的连接, 即负责 Accept 的操作
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // worker 线程组负责进行 socket channel 的数据读写, 即具体的数据处理工作
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 启动服务器
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    // 设置线程组
                    .group(bossGroup, workerGroup)
                    // 将 NioServerSocketChannel 注册到 Selector 上
                    .channel(NioServerSocketChannel.class)
                    // 启动 Channel 的日志输出
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 初始化 NioServerSocketChannel 对应的 Pipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    // 使用分隔符来解决粘包问题, 消息帧最大长度为 8192 字节
                                    .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                                    // 把 ByteBuf 解码为字符串
                                    .addLast(new StringDecoder())
                                    // 把 ByteByf 编码为字符串
                                    .addLast(new StringEncoder())
                                    // 最终的业务处理类
                                    .addLast(new ServerHandler());
                        }
                    })
                    // 指定 TCP 队列长度
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 指定连接为长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 使用 ChannelFuture 来保存异步操作的结果. 此处绑定到指定的端口并同步, 等待异步操作完成.
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info("Server listen at port: " + port);
            // 如果链路关闭, 则需要阻塞到所有 IO 操作之后才会退出
            future.channel().closeFuture().sync();
        } finally {
            // 释放资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}

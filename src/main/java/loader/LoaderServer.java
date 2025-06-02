package loader;

import com.couchbase.client.core.deps.io.netty.bootstrap.ServerBootstrap;
import com.couchbase.client.core.deps.io.netty.channel.ChannelFuture;
import com.couchbase.client.core.deps.io.netty.channel.ChannelInitializer;
import com.couchbase.client.core.deps.io.netty.channel.EventLoopGroup;
import com.couchbase.client.core.deps.io.netty.channel.nio.NioEventLoopGroup;
import com.couchbase.client.core.deps.io.netty.channel.socket.SocketChannel;
import com.couchbase.client.core.deps.io.netty.channel.socket.nio.NioServerSocketChannel;
import com.couchbase.client.core.deps.io.netty.handler.codec.http.HttpObjectAggregator;
import com.couchbase.client.core.deps.io.netty.handler.codec.http.HttpServerCodec;
import loader.handler.TaskHandler;

public class LoaderServer {

    public static void main(String[] args) {
        int port = 8080;
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(65536));
                        ch.pipeline().addLast(new TaskHandler());
                    }
                });

            ChannelFuture f = bootstrap.bind(port).sync();
            System.out.println("HugeGraph loader server started on port: " + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

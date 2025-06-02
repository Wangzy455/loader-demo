package loader.handler;

import com.couchbase.client.core.deps.io.netty.buffer.Unpooled;
import com.couchbase.client.core.deps.io.netty.channel.ChannelHandlerContext;
import com.couchbase.client.core.deps.io.netty.channel.SimpleChannelInboundHandler;
import com.couchbase.client.core.deps.io.netty.handler.codec.http.DefaultFullHttpResponse;
import com.couchbase.client.core.deps.io.netty.handler.codec.http.FullHttpRequest;
import com.couchbase.client.core.deps.io.netty.handler.codec.http.FullHttpResponse;
import com.couchbase.client.core.deps.io.netty.handler.codec.http.HttpHeaderNames;
import com.couchbase.client.core.deps.io.netty.handler.codec.http.HttpMethod;
import com.couchbase.client.core.deps.io.netty.handler.codec.http.HttpResponseStatus;
import com.couchbase.client.core.deps.io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import loader.common.TaskMetadata;
import loader.manger.TaskManager;
import loader.task.DataImportTask;
import loader.util.SqlUtil;

public class TaskHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 提交任务  http://localhost:8080/submit
        if (req.method() == HttpMethod.POST && req.uri().equals("/submit")) {
            String content = req.content().toString(StandardCharsets.UTF_8);
            String taskId = UUID.randomUUID().toString();
            DataImportTask task = new DataImportTask(taskId, content);

            TaskManager.submit(task);

            String json = String.format("{\"status\":\"success\",\"taskId\":\"%s\"}", taskId);
            FullHttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(json.getBytes(StandardCharsets.UTF_8))
            );
            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            ctx.writeAndFlush(res);
        }
        // 根据ID获取任务 http://localhost:8080/status/{taskId}
        else if (req.method() == HttpMethod.GET && req.uri().startsWith("/status/")) {
            String uri = req.uri();
            String[] parts = uri.split("/");

            if (parts.length == 3) {
                String taskId = parts[2];
                Optional<TaskMetadata> meta = SqlUtil.load(taskId);

                if (meta.isPresent()) {
                    String json = meta.get().toJson();
                    FullHttpResponse res = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(json, StandardCharsets.UTF_8)
                    );
                    res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
                    ctx.writeAndFlush(res);
                } else {
                    FullHttpResponse res = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NOT_FOUND,
                        Unpooled.copiedBuffer(("任务不存在: " + taskId).getBytes(StandardCharsets.UTF_8))
                    );
                    ctx.writeAndFlush(res);
                }
            } else {
                FullHttpResponse res = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST,
                    Unpooled.copiedBuffer("请求路径错误，应为 /tasks/{taskId}".getBytes(StandardCharsets.UTF_8))
                );
                ctx.writeAndFlush(res);
            }
        }
        // 取消任务接口 http://localhost:8080/cancel/{taskId}
        else if (req.method() == HttpMethod.POST && req.uri().startsWith("/cancel/")) {
            String[] parts = req.uri().split("/");
            if (parts.length == 3) {
                String taskId = parts[2];

                Optional<TaskMetadata> meta = SqlUtil.load(taskId);
                if (meta.isPresent()) {
                    TaskManager.cancelTask(taskId);
                    FullHttpResponse res = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(("任务已取消: " + taskId).getBytes(StandardCharsets.UTF_8))
                    );
                    ctx.writeAndFlush(res);
                } else {
                    FullHttpResponse res = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NOT_FOUND,
                        Unpooled.copiedBuffer(("任务不存在: " + taskId).getBytes(StandardCharsets.UTF_8))
                    );
                    ctx.writeAndFlush(res);
                }
            } else {
                FullHttpResponse res = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST,
                    Unpooled.copiedBuffer("请求格式错误，应为 /cancel/{taskId}".getBytes(StandardCharsets.UTF_8))
                );
                ctx.writeAndFlush(res);
            }
        }
        // 查询所有任务 http://localhost:8080/tasks
        else if (req.method() == HttpMethod.GET && req.uri().equals("/tasks")) {
            List<TaskMetadata> tasks = SqlUtil.listAll();
            StringBuilder jsonBuilder = new StringBuilder("[");
            for (int i = 0; i < tasks.size(); i++) {
                jsonBuilder.append(tasks.get(i).toJson());
                if (i < tasks.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("]");

            String json = jsonBuilder.toString();
            FullHttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(json, StandardCharsets.UTF_8)
            );
            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            ctx.writeAndFlush(res);
        }
        else {
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
        }
    }
}

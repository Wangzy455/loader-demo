package loader.store;

import java.util.Map;

/**
 * 模拟写入HStore的客户端
 */
public class HStoreClient {
    public void writeVertex(Map<String, Object> vertex) {
        System.out.println("模拟向HStore中写入:" + vertex);
    }
}

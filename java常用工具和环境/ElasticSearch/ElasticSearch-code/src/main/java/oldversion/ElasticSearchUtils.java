package oldversion;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther Tom
 * @Date 2020-03-22 14:13
 * @描述 TODO
 */
public class ElasticSearchUtils {

    private static TransportClient client;

    private static List<TransportAddress> addresses = new ArrayList<>();

    static {
        try {
            addresses.add(new TransportAddress(InetAddress.getByName("hadoop1"),9200));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例模式获取对象
     * @return
     */
    public static TransportClient getClint(){
        if (client == null){
              synchronized (ElasticSearchUtils.class){
                  if (client == null ){
                      client = new PreBuiltTransportClient(Settings.builder().put("cluster.name","mycluster").build());
                  }
                  client.addTransportAddresses(addresses.toArray(new TransportAddress[addresses.size()]));
              }
            }

        return client;
    }
}

package oldversion;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;

/**
 * @Auther Tom
 * @Date 2020-03-22 17:51
 * @描述 高级用法
 */
public class ElasticSearchHight {
    public static void main(String[] args) {
        TransportClient client = ElasticSearchUtils.getClint();

        //查询
        searchData(client);

        //分页

        //排序

        //高亮显示

        //聚合





    }

    private static void searchData(TransportClient client) {
//        SearchResponse response = client.prepareSearch()

    }

}

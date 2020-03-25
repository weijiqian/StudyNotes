package newversion;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther Tom
 * @Date 2020-03-23 19:08
 * @描述
 */
public class SearchBase {

    public static String index = "bigdata";
    public static String type = "student";

    public static void main(String[] args) {

        //初始化
        //Low Level Client init
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost("hadoop1", 9200, "http"));
        //High Level Client init
        RestHighLevelClient client =
                new RestHighLevelClient(restClientBuilder);


        String name = "saveDataByMap";
        switch (name){
            case "saveDataByMap":
                //保存数据
                saveDataByMap(client);
                break;

        }


    }

    /**
     * @MethodName: saveDataByMap
     * @Param:  * @param client
     * @Return: void
     * @Author: Tom
     * @Date:  2020-03-23  19:14
     * @Description: 保存数据
    **/
    private static void saveDataByMap(RestHighLevelClient client) {
        // map json 都可以保存
        Map<String,String> jsonMap = new HashMap<>();
        jsonMap.put("name","xiao xiao");
        jsonMap.put("age","39");
        jsonMap.put("gender","F");
        IndexRequest request = new IndexRequest(index);
        request.id("1");
        request.source(jsonMap);
    }

}

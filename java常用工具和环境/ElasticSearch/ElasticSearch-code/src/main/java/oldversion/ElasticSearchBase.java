package oldversion;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther Tom
 * @Date 2020-03-22 14:11
 * @描述 TODO
 */
public class ElasticSearchBase {

    private static TransportClient client;
    private static String INDEX = "bigdata";  //数据库名
    private static String TYPE = "student";  //表名

    public static void main(String[] args) {
        client = ElasticSearchUtils.getClint();

        String name = "addDataByJSON";
        switch (name){
            case "addDataByJSON":
                //新增数据
                addDataByJSON(client);
                break;
            case "getData":
                //查询数据
                getData(client);
                break;
            case "updateData":
                //更新数据
                updateData(client);
                break;
            case "deleteData":
                //删除数据
                deleteData(client);
                break;



        }





    }

    private static void deleteData(TransportClient client) {
        DeleteResponse response = client.prepareDelete(INDEX,TYPE,"2").get();
        System.out.println(response.getVersion());

    }

    private static void updateData(TransportClient client) {
        Map<String,String> doc = new HashMap<>();
        doc.put("age","33");
        UpdateResponse response = client.prepareUpdate(INDEX,TYPE,"2").setDoc(doc).get();
    }

    /**
     * @MethodName: addData
     * @Param:  * @param
     * @Return: void
     * @Author: Tom
     * @Date:  2020-03-22  16:14
     * @Description: 添加数据
    **/
    private static void addDataByJSON(TransportClient client) {
        UserBean user = new UserBean("zhang shuai","29","F");
        String source = new JSONObject(user).toString();
        IndexResponse response = client.prepareIndex(INDEX, TYPE)
                .setSource(source, XContentType.JSON)//必须要指定XContentType
                .get();
        System.out.println(response.getVersion());

    }

    /**
     * 查询数据
     * @param client
     */
    private static void getData(TransportClient client){
        GetResponse getResponse = client.prepareGet(INDEX,TYPE,"1").get();
        System.out.println(getResponse.getSource());

    }


}

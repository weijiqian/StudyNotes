package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @Auther Tom
 * @Date 2020-03-28 21:57
 * @描述 工具类
 */
public class HBaseTools {
    public static Configuration conf = new Configuration();
    private static Connection conn = null;
    private static ExecutorService pool = Executors.newFixedThreadPool(200);

    static {
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.zookeeper.quorum", "dmp01,dmp02,dmp03,dmp04,dmp05");
        conf.setLong(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, 120000);
        try {
            conn = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Table openTable(String tableName) {
        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName), pool);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

    public static void closeTable(Table table) {
        if (table != null) {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeConn() {
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void putColumnDatas(Table table, String rowKey,
                                      String familyName, Map<String, byte[]> columnDatas) {
        Put put = new Put(rowKey.getBytes());
        for (Map.Entry<String, byte[]> columnData : columnDatas.entrySet()) {
            put.addColumn(familyName.getBytes(),
                    columnData.getKey().getBytes(), columnData.getValue());
        }
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void putFamilyDatas(Table table, String rowKey,
                                      Map<String, Map<String, byte[]>> familyDatas) {
        Put put = new Put(rowKey.getBytes());
        for (Map.Entry<String, Map<String, byte[]>> familyData : familyDatas
                .entrySet()) {
            String familyName = familyData.getKey();
            Map<String, byte[]> columnDatas = familyData.getValue();
            for (Map.Entry<String, byte[]> columnData : columnDatas.entrySet()) {
                put.addColumn(familyName.getBytes(), columnData.getKey()
                        .getBytes(), columnData.getValue());
            }
        }
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void putRowDatas(Table table,
                                   Map<String, Map<String, Map<String, byte[]>>> rowDatas) {
        List<Put> puts = new ArrayList<Put>();
        for (Map.Entry<String, Map<String, Map<String, byte[]>>> rowData : rowDatas
                .entrySet()) {
            String rowKey = rowData.getKey();
            if (rowKey != null) {
                Map<String, Map<String, byte[]>> familyDatas = rowData
                        .getValue();
                Put put = new Put(rowKey.getBytes());
                for (Map.Entry<String, Map<String, byte[]>> familyData : familyDatas
                        .entrySet()) {
                    String familyName = familyData.getKey();
                    Map<String, byte[]> columnDatas = familyData.getValue();
                    for (Map.Entry<String, byte[]> columnData : columnDatas
                            .entrySet()) {
                        put.addColumn(familyName.getBytes(), columnData
                                .getKey().getBytes(), columnData.getValue());
                    }
                }
                puts.add(put);
            }
        }
        try {
            table.put(puts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getFamilyDatas(Table table, String rowkey, String family) {
        Map<String, String> datas = new HashMap<String, String>();
        Get get = new Get(rowkey.getBytes());
        get.addFamily(family.getBytes());
        try {
            Result result = table.get(get);
            List<Cell> cells = result.listCells();
            for (Cell cell : cells) {
                String key = new String(CellUtil.cloneQualifier(cell));
                String value = new String(CellUtil.cloneValue(cell), "UTF-8");
                datas.put(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public static String getValueData(Table table, String rowkey, String family, String key) {
        String value = "";
        Get get = new Get(rowkey.getBytes());
        get.addFamily(family.getBytes());
        try {
            Result result = table.get(get);
            value = Bytes.toString(result.getValue(family.getBytes(), key.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static List<String> scanValueDatas(Table table, String family, String key, int limit) {
        List<String> values = new ArrayList<String>();
        Scan scan = new Scan() ;
        Filter filter = new PageFilter(limit);
        scan.setFilter(filter);
        try {
            ResultScanner results = table.getScanner(scan);
            for (Result res : results) {
                values.add(Bytes.toString(res.getValue(family.getBytes(), key.getBytes())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return values;
    }

}

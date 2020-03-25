package springdata;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.activation.FileTypeMap;

/**
 * @Auther Tom
 * @Date 2020-03-23 21:49
 * @描述 TODO
 */
public class StudentBean {

    //@Field 每个文档的字段配置（是否分词、分词器,是否存储、 ,类型）
    /**
     *      index：是否设置分词
     *     analyzer：存储时使用的分词器
     *     searchAnalyze：搜索时使用的分词器
     *     store：是否存储
     *     type: 数据类型
     */

    @Field(index = true,analyzer = "ik_smart",store = true,type = FieldType.Text)
    private String name;

    @Field(index = false,store = true,type = FieldType.Text)
    private String age;


    @Field(index = false,store = true,type = FieldType.Text)
    private String gender;

    public StudentBean(String name, String age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "StudentBean{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}

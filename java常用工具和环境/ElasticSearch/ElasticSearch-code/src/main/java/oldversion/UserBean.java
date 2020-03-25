package oldversion;

/**
 * @Auther Tom
 * @Date 2020-03-22 16:27
 * @描述 oldversion.UserBean
 */
public class UserBean {

    private String name;
    private String age;
    private String gender;


    public UserBean(String name, String age, String gender) {
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
}

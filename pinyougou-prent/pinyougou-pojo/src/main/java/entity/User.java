package entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ClassName: User
 * Description:
 * date: 2019/7/28 19:17
 *
 * @author hxq
 * @since JDK 1.8
 */
public class User implements Serializable {
    private String sex;
    private Map<String,Object> map;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}

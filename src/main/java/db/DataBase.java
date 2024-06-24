package db;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import model.User;

public class DataBase {
    private static Map<String, User> users = Maps.newHashMap();{
        User user1 = new User("abc", "pass", "abc", "abc@naver.com");
        User user2 = new User("bbb", "pass", "bbb", "bbb@naver.com");
        User user3 = new User("ccc", "pass", "ccc", "ccc@naver.com");
        users.put("abc", user1);
        users.put("bbb", user2);
        users.put("ccc", user3);
    }

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}

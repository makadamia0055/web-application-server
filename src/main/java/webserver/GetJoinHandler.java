package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;

import java.util.Map;
import java.util.Optional;

public class GetJoinHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private HttpMethod httpMethod = HttpMethod.GET;

    private String mappedUrl = "/user/create";

    private String userId;

    private String password;

    private String name;

    public boolean isMapping(HttpRequestClass httpRequestClass){
        if(httpRequestClass.getPath().equals(mappedUrl)&&httpRequestClass.getMethod().equals(this.httpMethod)){
            return true;
        }
        return false;
    }
    public String handle(HttpRequestClass httpRequestClass){
        String paramStr = httpRequestClass.getParam();
        Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(paramStr);

        this.userId = Optional.ofNullable(queryStringMap.get("userId")).orElseThrow(() -> new IllegalArgumentException("쿼리 파라미터에 userId가 없습니다."));
        this.password = Optional.ofNullable(queryStringMap.get("password")).orElseThrow(() -> new IllegalArgumentException("쿼리 파라미터에 password가 없습니다."));
        this.name = Optional.ofNullable(queryStringMap.get("name")).orElseThrow(() -> new IllegalArgumentException("쿼리 파라미터에 name이 없습니다."));

        User joinUser = new User(userId, password, name, "default@default.com");
        log.info(joinUser.toString());
        return joinUser.toString();
    }

}

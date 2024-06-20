package webserver;

import model.User;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;

import java.util.Map;
import java.util.Optional;

public class GetJoinHandler extends AbstractHandler{


    public GetJoinHandler(String mappedUrl){
        super();
        this.httpMethod = HttpMethod.GET;
        this.mappedUrl = mappedUrl;
    }

    @Override
    public String handle(HttpRequestClass httpRequestClass){

        Map<String, String> queryStringMap =httpRequestClass.getParams().orElseThrow(()-> new IllegalArgumentException());
        String userId = Optional.ofNullable(queryStringMap.get("userId")).orElseThrow(() -> new IllegalArgumentException("쿼리 파라미터에 userId가 없습니다."));
        String password = Optional.ofNullable(queryStringMap.get("password")).orElseThrow(() -> new IllegalArgumentException("쿼리 파라미터에 password가 없습니다."));
        String name = Optional.ofNullable(queryStringMap.get("name")).orElseThrow(() -> new IllegalArgumentException("쿼리 파라미터에 name이 없습니다."));

        User joinUser = new User(userId, password, name, "default@default.com");
        log.info(joinUser.toString());
        return "join.html";
    }

}

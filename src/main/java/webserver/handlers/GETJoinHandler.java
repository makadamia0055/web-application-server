package webserver.handlers;

import model.User;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;
import webserver.WebServer;

import java.util.Map;
import java.util.Optional;

public class GETJoinHandler extends AbstractHandler{


    public GETJoinHandler(String mappedUrl){
        super();
        this.httpMethod = HttpMethod.GET;
        this.mappedUrl = mappedUrl;
    }

    @Override
    public HandlerResponse handle(HttpRequestClass httpRequestClass){

        Map<String, String> queryStringMap =httpRequestClass.getParams();
        String userId = Optional.ofNullable(queryStringMap.get("userId")).orElseThrow(() -> new IllegalArgumentException("쿼리 파라미터에 userId가 없습니다."));
        String password = Optional.ofNullable(queryStringMap.get("password")).orElseThrow(() -> new IllegalArgumentException("쿼리 파라미터에 password가 없습니다."));
        String name = Optional.ofNullable(queryStringMap.get("name")).orElseThrow(() -> new IllegalArgumentException("쿼리 파라미터에 name이 없습니다."));

        User joinUser = new User(userId, password, name, "default@default.com");
        WebServer.db.addUser(joinUser);
        log.info(joinUser.toString());
        this.handlerResponse.setViewPath("redirect:/index.html");
        return this.handlerResponse;
    }

}

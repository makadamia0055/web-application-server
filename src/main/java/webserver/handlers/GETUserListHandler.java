package webserver.handlers;

import model.User;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;
import webserver.WebServer;

import java.util.Collection;
import java.util.Map;

public class GETUserListHandler extends AbstractHandler{

    public GETUserListHandler(String mappedUrl){
        super();
        this.httpMethod = HttpMethod.GET;
        this.mappedUrl = mappedUrl;
    }

    @Override
    public HandlerResponse handle(HttpRequestClass httpRequestClass) {
        Map<String, String> cookies = httpRequestClass.getCookies();

        if(cookies.get("logined")==null||!Boolean.parseBoolean(cookies.get("logined"))){
            this.handlerResponse.setViewPath("redirect:/user/login.html");
            return handlerResponse;
        }
        Map<String, String> parameterMap = handlerResponse.getParameterMap();
        StringBuilder sb = new StringBuilder();
        for (User user : WebServer.db.findAll()) {
            sb.append(user.getUserId()+":"+user.getName()+":"+user.getEmail()+";");
        }
        String string = sb.toString();

        parameterMap.put("users", string);
        handlerResponse.setViewPath("/user/list.html");
        return handlerResponse;
    }
}

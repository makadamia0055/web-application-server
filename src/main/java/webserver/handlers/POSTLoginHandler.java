package webserver.handlers;

import model.User;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;
import webserver.WebServer;

import java.util.Map;

public class POSTLoginHandler extends AbstractHandler{

    public POSTLoginHandler(String mappedUrl){
        super();
        this.httpMethod = HttpMethod.POST;
        this.mappedUrl = mappedUrl;
    }
    @Override
    public HandlerResponse handle(HttpRequestClass httpRequestClass) {
        Map<String, String> params = httpRequestClass.getParams().orElseThrow(() -> new IllegalArgumentException("파라미터가 없음"));
        if(!params.containsKey("userId")||!params.containsKey("password")){
            log.error("파라미터 파싱 에러");
            return new HandlerResponse("redirect:/user/login_failed.html");
        }

        //유저 존재 검증

        User findUser = WebServer.db.findUserById(params.get("userId"));
        if(findUser==null){
            log.error("유저 없음");

            HandlerResponse handlerResponse = new HandlerResponse("redirect:/user/login_failed.html");
            handlerResponse.getParameterMap().put("logined", "false");
            return handlerResponse;
        }

        // 유저 비번 검증
        if(findUser.getPassword().equals(params.get("password"))){
            log.info(findUser.getUserId() + " : 로그인");
            HandlerResponse handlerResponse = new HandlerResponse("redirect:/index.html");
            handlerResponse.getParameterMap().put("logined", "true");
            handlerResponse.getParameterMap().put("userId", findUser.getUserId());

            return handlerResponse;

        }else {
            HandlerResponse handlerResponse = new HandlerResponse("redirect:/user/login_failed.html");
            handlerResponse.getParameterMap().put("logined", "false");
            return handlerResponse;
        }



    }
}

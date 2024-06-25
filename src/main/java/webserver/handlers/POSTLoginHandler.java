package webserver.handlers;

import model.User;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;
import webserver.WebServer;

import java.util.Map;

public class POSTLoginHandler extends AbstractHandler{

    public POSTLoginHandler(String mappedUrl){
        super(HttpMethod.POST, mappedUrl);
    }
    @Override
    public HandlerResponse handle(HttpRequestClass httpRequestClass) {
        Map<String, String> params = httpRequestClass.getParams();
        if(!params.containsKey("userId")||!params.containsKey("password")){
            log.error("파라미터 파싱 에러");
            this.handlerResponse.setViewPath("redirect:/user/login_failed.html");
            return this.handlerResponse;
        }

        //유저 존재 검증

        User findUser = WebServer.db.findUserById(params.get("userId"));
        if(findUser==null){
            log.error("유저 없음");

            this.handlerResponse.setViewPath("redirect:/user/login_failed.html");
            this.handlerResponse.getParameterMap().put("logined", "false");
            return handlerResponse;
        }

        // 유저 비번 검증
        if(findUser.getPassword().equals(params.get("password"))){
            log.info(findUser.getUserId() + " : 로그인");
            this.handlerResponse.setViewPath("redirect:/index.html");
            this.handlerResponse.getParameterMap().put("logined", "true");
            this.handlerResponse.getParameterMap().put("userId", findUser.getUserId());

            return this.handlerResponse;

        }else {
            this.handlerResponse.setViewPath("redirect:/user/login_failed.html");
            this.handlerResponse.getParameterMap().put("logined", "false");
            return this.handlerResponse;
        }



    }
}

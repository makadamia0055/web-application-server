package webserver.handlers;

import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;

public class GETLoginHandler extends AbstractHandler{

    public GETLoginHandler(String mappedURL){
        super();
        this.mappedUrl = mappedURL;
        this.httpMethod = HttpMethod.GET;
    }
    @Override
    public HandlerResponse handle(HttpRequestClass httpRequestClass) {

        HandlerResponse handlerResponse1 = new HandlerResponse();
        handlerResponse1.setViewPath("/user/login.html");
        return handlerResponse1;
    }
}

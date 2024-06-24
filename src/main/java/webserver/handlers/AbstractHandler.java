package webserver.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;
import webserver.RequestHandler;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractHandler {

    static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    HandlerResponse handlerResponse;
    HttpMethod httpMethod;

    String mappedUrl;

    AbstractHandler(){
        this.handlerResponse = new HandlerResponse();

    }


    public boolean isMapping(HttpRequestClass httpRequestClass){
        if(httpRequestClass.getPath().equals(mappedUrl)&&httpRequestClass.getMethod().equals(this.httpMethod)){
            return true;
        }
        return false;
    }

    public Optional<Map<String, String>> getCookies(HttpRequestClass httpRequestClass){
        return Optional.ofNullable(HttpRequestUtils.parseCookies(httpRequestClass.getParams().get("Cookie")));

    }

    public abstract HandlerResponse handle(HttpRequestClass httpRequestClass);

    public HandlerResponse run(HttpRequestClass httpRequestClass){
        Map<String, String> cookies = getCookies(httpRequestClass).get();
        this.handlerResponse.getParameterMap().putAll(cookies);
        return handle(httpRequestClass);
    }

}

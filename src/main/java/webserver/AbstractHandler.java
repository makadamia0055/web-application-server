package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;

public abstract class AbstractHandler {

    static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    HttpMethod httpMethod;

    String mappedUrl;

    public boolean isMapping(HttpRequestClass httpRequestClass){
        if(httpRequestClass.getPath().equals(mappedUrl)&&httpRequestClass.getMethod().equals(this.httpMethod)){
            return true;
        }
        return false;
    }

    public abstract String handle(HttpRequestClass httpRequestClass);
}
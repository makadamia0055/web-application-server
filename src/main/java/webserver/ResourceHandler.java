package webserver;

import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;

public class ResourceHandler extends AbstractHandler{

    public ResourceHandler(String mappedUrl){
        super();
        this.httpMethod = HttpMethod.GET;
        this.mappedUrl = mappedUrl;
//        log.info(mappedUrl+" : ResourceMapping 생성");
    }

    @Override
    public String handle(HttpRequestClass httpRequestClass) {
        if(mappedUrl.substring(1).contains("/")){
            return mappedUrl;
        }
        return mappedUrl.substring(1);
    }
}

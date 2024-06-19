package webserver;

import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;

public class POSTJoinHandler extends AbstractHandler{

    public POSTJoinHandler(String mappedUrl){
        super();
        this.httpMethod = HttpMethod.POST;
        this.mappedUrl = mappedUrl;
    }
    @Override
    public String handle(HttpRequestClass httpRequestClass) {
        return null;
    }
}

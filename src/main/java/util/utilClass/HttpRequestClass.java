package util.utilClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.util.StringTokenizer;

public class HttpRequestClass {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private HttpMethod method;

    private String url;
    private String path;
    private String paramStr;

    private String httpVersion;

    public HttpRequestClass(String httpHeader){
        StringTokenizer st = new StringTokenizer(httpHeader);
        this.method = HttpMethod.valueOf(st.nextToken());
        this.url = st.nextToken();
        this.httpVersion = st.nextToken();

        if(url.contains("/")){
            String rawPath = url.substring(url.indexOf("/"));
            if(rawPath.contains("?")){
                int index = rawPath.indexOf("?");
                this.path = rawPath.substring(0, index);
                this.paramStr = rawPath.substring(index+1);
            }else {
                this.path = rawPath;
            }

        }

    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getPath(){
        return path;
    }
    public String getParam(){
        return paramStr;
    }

    @Override
    public String toString() {
        return "HttpRequestClass{" +
                "method=" + method +
                ", url='" + url + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                '}';
    }
}

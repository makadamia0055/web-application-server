package util.utilClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import webserver.RequestHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

public class HttpRequestClass {
    private HttpMethod method;

    private String url;
    private String path;
    private String version;
    private Map<String, String> headers;

    private Map<String, String> params = new HashMap<>();
    private String body;

    public HttpRequestClass(String method, String url, String version, Map<String, String> headers, String body) {
        this.method = HttpMethod.valueOf(method);
        this.url = url;

        this.version = version;
        this.headers = headers;
        this.body = body;
        setPathAndParams();
    }


    public HttpMethod getMethod() {
        return method;
    }


    public String getUrl(){
        return url;
    }
    private void setPathAndParams(){
        String rawPath = url.substring(url.indexOf("/"));

        if (this.method.equals(HttpMethod.GET)&&rawPath.contains("?")){
            this.path = rawPath.substring(0, rawPath.indexOf("?"));
            String paramStr = rawPath.substring(rawPath.indexOf("?"));
            setParams(paramStr);
        }else if(this.method.equals(HttpMethod.POST)){
            this.path = rawPath;
            setParams(this.body);
        }else{
            this.path = rawPath;
        }

    }
    private void setParams(String paramStr){
        //? 먼저 포함된 문자열
        String substring = paramStr.contains("?")?paramStr.substring(paramStr.indexOf("?") + 1):paramStr;
        String[] splits = substring.split("&");
        for (String split : splits) {
            String[] subSplit = split.split("=");
            params.put(subSplit[0], subSplit[1]);

        }

    }
    public Map<String, String> getCookies(){
        String cookieStr = headers.get("Cookie");
        return HttpRequestUtils.parseCookies(cookieStr);
    }

    public Map<String, String> getParams(){
        return params;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }


}
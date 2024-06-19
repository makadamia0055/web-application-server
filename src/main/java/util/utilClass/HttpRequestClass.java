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
    private String path;
    private String version;
    private Map<String, String> headers;
    private String body;

    public HttpRequestClass(String method, String path, String version, Map<String, String> headers) {
        this.method = HttpMethod.valueOf(method);
        this.path = path;
        this.version = version;
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMethod() {
        return method;
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
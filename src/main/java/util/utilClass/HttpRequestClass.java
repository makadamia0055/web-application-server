package util.utilClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.util.StringTokenizer;

public class HttpRequestClass {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private HttpMethod method;

    private String url;

    private String httpVersion;

    public HttpRequestClass(String httpHeader){
        StringTokenizer st = new StringTokenizer(httpHeader);
        this.method = HttpMethod.valueOf(st.nextToken());
        this.url = st.nextToken();
        this.httpVersion = st.nextToken();

    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
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

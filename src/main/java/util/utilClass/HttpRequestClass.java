package util.utilClass;

import java.util.StringTokenizer;

public class HttpRequestClass {
    private HttpMethod method;

    private String url;

    private String httpVersion;

    public HttpRequestClass(String httpHeader){
        StringTokenizer st = new StringTokenizer(httpHeader);
        st.nextToken();
        this.method = HttpMethod.GET;
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

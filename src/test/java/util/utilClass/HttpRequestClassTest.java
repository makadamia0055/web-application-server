package util.utilClass;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.WebServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map.Entry;

public class HttpRequestClassTest{
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    @Test
    public void GET클래스생성테스트및파라미터테스트() throws IOException {
        String httpGetMessage = "GET localhost:8080/user/create?userId=id&password=password&name=name HTTP/1.1";
        BufferedReader br = new BufferedReader(new StringReader(httpGetMessage));
        HttpRequestClass httpRequestClass = HttpRequestParser.extractHttpRequest(br).orElseThrow();

        log.info(httpRequestClass.getMethod() +" ,"+ httpRequestClass.getUrl()+" ," +httpRequestClass.getPath());

        for (Entry<String, String> stringStringEntry : httpRequestClass.getParams().entrySet()) {
            log.info(stringStringEntry.getKey()+" ,"+stringStringEntry.getValue());
        }

    }

    @Test
    public void POST클래스생성테스트및파라미터테스트() throws IOException {
        String httpGetMessage = "POST /user/create HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Content-Length: 36\r\n" +
                "\r\n" +
                "userId=id&password=password&name=name";

        BufferedReader br = new BufferedReader(new StringReader(httpGetMessage));
        HttpRequestClass httpRequestClass = HttpRequestParser.extractHttpRequest(br).orElseThrow();

        log.info(httpRequestClass.getMethod() +" ,"+ httpRequestClass.getUrl()+" ," +httpRequestClass.getPath());

        for (Entry<String, String> stringStringEntry : httpRequestClass.getParams().entrySet()) {
            log.info(stringStringEntry.getKey()+" ,"+stringStringEntry.getValue());
        }

    }
}
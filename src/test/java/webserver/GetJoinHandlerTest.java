package webserver;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.utilClass.HttpRequestClass;

public class GetJoinHandlerTest {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);


    @Test
    public void getJoin의Handle작동테스트(){

        String header = "GET /user/create?userId=javajigi&password=password&name=JaeSung HTTP/1.1";

        HttpRequestClass httpRequestClass = new HttpRequestClass(header);

        GetJoinHandler getJoinHandler = new GetJoinHandler("/user/create");

        log.info("isMapping :" + getJoinHandler.isMapping(httpRequestClass));

        log.info("handle : " + getJoinHandler.handle(httpRequestClass));


    }



}
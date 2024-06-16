package webserver;

import junit.framework.TestCase;
import org.junit.Test;

public class HandlerMapperTest {
    private HandlerMapper handlerMapper = HandlerMapper.getInstance();

    @Test
    public void 핸들러URL테스트() throws Exception{
        System.out.println(handlerMapper.getMapping("/css.style.css").orElseThrow(()-> new Exception("오류 발생")));
        System.out.println(handlerMapper.getMapping("/index.html").orElseThrow(()-> new Exception("오류 발생")));



    }

}
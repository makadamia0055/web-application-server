package webserver;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Map;

public class HandlerMapperTest {
    private HandlerMapper handlerMapper = HandlerMapper.getInstance();

    @Test
    public void 핸들러URL테스트() throws Exception{
       /* System.out.println(handlerMapper.getMapping("/css.style.css").orElseThrow(()-> new Exception("오류 발생")));
        System.out.println(handlerMapper.getMapping("/index.html").orElseThrow(()-> new Exception("오류 발생")));

*/

    }
    @Test
    public void 파라미터파싱테스트(){
        Map<String, String> stringStringMap = handlerMapper.parseQueryString("q=Java&lang=en");
        stringStringMap.entrySet().forEach(entry-> System.out.println(entry.getKey()+":"+entry.getValue()));
    }

}
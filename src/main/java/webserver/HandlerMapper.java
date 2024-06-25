package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;
import webserver.handlers.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class HandlerMapper {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static final HandlerMapper instance = new HandlerMapper();

    private Map<HttpMethod, List<AbstractHandler>> urlViewMapper = new HashMap<>();
    private String staticResourcePath;

    private HandlerMapper(){
        init();
    }

    private void init(){
        addViewPath();
        setStaticResourcePath();

    }

    // resources의 디렉토리, 파일들 추가하는
    private void setStaticResourcePath() {
        staticResourcePath = Paths.get(System.getProperty("user.dir"), "target", "classes").toString();
        log.info("Root context for resources: {}", staticResourcePath);
    }

    public String getStaticResourcePath(){
        return staticResourcePath;
    }

    public static HandlerMapper getInstance(){
        return instance;
    }



    private void addViewPath() {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            urlViewMapper.put(httpMethod, new ArrayList<>());
        }
        urlViewMapper.get(HttpMethod.GET).add(new GETJoinHandler("/user/create"));
        urlViewMapper.get(HttpMethod.POST).add(new POSTJoinHandler("/user/create"));
        urlViewMapper.get(HttpMethod.POST).add(new POSTLoginHandler("/user/login"));
        urlViewMapper.get(HttpMethod.GET).add(new GETUserListHandler("/user/list"));
        urlViewMapper.get(HttpMethod.GET).add(new GETLoginHandler("/user/login"));

        log.info("View paths initialized for GET and POST methods.");
    }


    public Optional<HandlerResponse> getMapping(HttpRequestClass httpRequestClass) {
        String url = httpRequestClass.getPath();
        HttpMethod httpMethod = httpRequestClass.getMethod();

        log.info("Request received - URL: {}, Method: {}", url, httpMethod);

        if (urlViewMapper.containsKey(httpMethod)) {
            AbstractHandler abstractHandler = urlViewMapper.get(httpMethod).stream()
                    .filter(handler -> handler.isMapping(httpRequestClass))
                    .findAny()
                    .orElse(null);
            if (abstractHandler != null) {
                return Optional.ofNullable(abstractHandler.run(httpRequestClass));
            }
        }
        // 정적 리소스 경로에서 파일 찾기
        File staticFile = new File(staticResourcePath, url);
        if(staticFile.exists()&&staticFile.isFile()){
            return Optional.ofNullable(new HandlerResponse(url));
        }


        return Optional.empty();
    }






}

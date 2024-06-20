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

        /*File file = new File(rootContext);
        String[] filesAndDirectories = file.list();
        if (filesAndDirectories != null) {
            resourcePathList.addAll(Arrays.stream(filesAndDirectories).collect(Collectors.toList()));
        } else {
            log.warn("No files or directories found in: {}", rootContext);
        }

        // 자원 매핑 넣어주기
        List<ResourceHandler> resourceHandlers = resourcePathList.stream()
                .map(str -> new ResourceHandler("/" + str))
                .peek(resourceHandler -> log.info(resourceHandler.mappedUrl))
                .collect(Collectors.toList());

        urlViewMapper.get(HttpMethod.GET).addAll(resourceHandlers);
        log.info("Resource handlers added: {}", resourceHandlers);
    }
*/

    private void addViewPath() {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            urlViewMapper.put(httpMethod, new ArrayList<>());
        }
        urlViewMapper.get(HttpMethod.GET).add(new GETJoinHandler("/user/create"));
        urlViewMapper.get(HttpMethod.POST).add(new POSTJoinHandler("/user/create"));
        urlViewMapper.get(HttpMethod.POST).add(new POSTLoginHandler("/user/login"));

        log.info("View paths initialized for GET and POST methods.");
    }


    public static HandlerMapper getInstance(){
        return instance;
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
                return Optional.ofNullable(abstractHandler.handle(httpRequestClass));
            }
        }
        // 정적 리소스 경로에서 파일 찾기
        File staticFile = new File(staticResourcePath, url);
        if(staticFile.exists()&&staticFile.isFile()){
            return Optional.ofNullable(new HandlerResponse(staticFile.getPath()));
        }


        return Optional.empty();
    }




//    private Object[] parseQueryStringToParameter(Method method, String url) {
//        String split = url.split("\\?")[1];
//        Map<String, String> stringStringMap = parseQueryString(split);
//
//        Map<Class, Object> parameterInstanceMap = new HashMap<>();
//        Class<?>[] parameterTypes = method.getParameterTypes();
//
//        for (Entry<String, String> stringStringEntry : stringStringMap.entrySet()) {
//            for (Class<?> parameterType : parameterTypes) {
//                Arrays.stream(parameterType.getFields())
//                        .map(field->field.getName())
//                        .filter(fieldName-> fieldName.equals(stringStringEntry.getKey()))
//                        .findAny()
//                        .ifPresent(fieldName-> {
//                            try {
//                                parameterInstanceMap.putIfAbsent(parameterType, parameterType.getConstructor().newInstance());
//                            } catch (Exception e) {
//
//                            }
//                        });
//
//
//            }
//
//
//        }
//
//
//        return null;
//
//    }

//    public Map<String, String> parseQueryString(String url) {
//        Map<String, String> queryStringMap = new HashMap<>();
//
//        StringTokenizer st = new StringTokenizer(url, "&");
//
//        while(st.hasMoreTokens()){
//            String token = st.nextToken();
//            if(token.contains("=")){
//                String[] splitToken = token.split("=");
//                queryStringMap.put(splitToken[0], splitToken[1]);
//            }
//
//        }
//        return queryStringMap;
//    }

}

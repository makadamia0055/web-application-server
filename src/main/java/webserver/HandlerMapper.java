package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.utilClass.HttpMethod;
import util.utilClass.HttpRequestClass;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HandlerMapper {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static final HandlerMapper instance = new HandlerMapper();

    private Map<HttpMethod, List<AbstractHandler>> urlViewMapper = new HashMap<>();
    private List<String> resourcePathList = new ArrayList<>();

    private HandlerMapper(){
        init();
    }

    private void init(){
        addResourcePath();
        addViewPath();

    }

    // resources의 디렉토리, 파일들 추가하는
    private void addResourcePath() {

        String rootContext = System.getProperty("user.dir")+"/target/classes";

        File file = new File(rootContext);
        String[] direcotries = file.list();
        if(direcotries!=null){
            resourcePathList.addAll(Arrays.stream(direcotries).collect(Collectors.toList()));

        }

    }

    private void addViewPath(){
        for (HttpMethod httpMethod : HttpMethod.values()) {
            urlViewMapper.put(httpMethod, new ArrayList<>());
        }
        urlViewMapper.get(HttpMethod.GET).add(new GetJoinHandler("/user/create"));


    }

    public static HandlerMapper getInstance(){
        return instance;
    }

    public Optional<String> getMapping(HttpRequestClass httpRequestClass){
        String url = httpRequestClass.getUrl();
        HttpMethod httpMethod = httpRequestClass.getMethod();


        if(!urlViewMapper.containsKey(httpMethod)){
            return Optional.empty();
        }

        AbstractHandler abstractHandler = urlViewMapper.get(httpMethod).stream()
                .filter(handler -> handler.isMapping(httpRequestClass))
                .findAny().orElseThrow(() -> new IllegalArgumentException(httpRequestClass.getPath()+"에 대한 핸들러가 없습니다."));

        return Optional.ofNullable(abstractHandler.handle(httpRequestClass));


    }

    private Object[] parseQueryStringToParameter(Method method, String url) {
        String split = url.split("\\?")[1];
        Map<String, String> stringStringMap = parseQueryString(split);

        Map<Class, Object> parameterInstanceMap = new HashMap<>();
        Class<?>[] parameterTypes = method.getParameterTypes();

        for (Entry<String, String> stringStringEntry : stringStringMap.entrySet()) {
            for (Class<?> parameterType : parameterTypes) {
                Arrays.stream(parameterType.getFields())
                        .map(field->field.getName())
                        .filter(fieldName-> fieldName.equals(stringStringEntry.getKey()))
                        .findAny()
                        .ifPresent(fieldName-> {
                            try {
                                parameterInstanceMap.putIfAbsent(parameterType, parameterType.getConstructor().newInstance());
                            } catch (Exception e) {

                            }
                        });


            }


        }


        return null;

    }

    public Map<String, String> parseQueryString(String url) {
        Map<String, String> queryStringMap = new HashMap<>();

        StringTokenizer st = new StringTokenizer(url, "&");

        while(st.hasMoreTokens()){
            String token = st.nextToken();
            if(token.contains("=")){
                String[] splitToken = token.split("=");
                queryStringMap.put(splitToken[0], splitToken[1]);
            }

        }
        return queryStringMap;
    }

}

package webserver;

import java.util.*;

public class HandlerMapper {

    private static final HandlerMapper instance = new HandlerMapper();

    private Map<String, String> urlMapper = new HashMap<>();
    private List<String> resourcePathList = new ArrayList<>();

    private HandlerMapper(){
        init();
    }

    private void init(){
        addResourcePath();
        addViewPath();

    }

    private void addResourcePath() {
        resourcePathList.add("css");
        resourcePathList.add("js");
        resourcePathList.add("fonts");
        resourcePathList.add("images");
    }

    private void addViewPath(){
        urlMapper.put("/index.html", "index.html");
    }

    public static HandlerMapper getInstance(){
        return instance;
    }

    public Optional<String> getMapping(String url){
        for(String str : resourcePathList){
            if(url.startsWith("/"+str)){
                String rst = url.replaceFirst("/", "").toString();
                return Optional.of(rst);
            }
        }

        return Optional.ofNullable(urlMapper.get(url));
    }

}
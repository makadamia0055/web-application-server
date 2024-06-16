package webserver;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.stream.Collectors;

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

        String rootContext = System.getProperty("user.dir")+"/target/classes";

        File file = new File(rootContext);
        String[] direcotries = file.list();
        if(direcotries!=null){
            resourcePathList.addAll(Arrays.stream(direcotries).collect(Collectors.toList()));

        }

    }

    private void addViewPath(){

    }

    public static HandlerMapper getInstance(){
        return instance;
    }

    public Optional<String> getMapping(String url){
        String parsedUrl = url.replaceFirst("/", "");


        for(String str : resourcePathList){
            if(parsedUrl.startsWith(str)){
                return Optional.of(parsedUrl);
            }
        }

        return Optional.ofNullable(urlMapper.get(parsedUrl));
    }

}

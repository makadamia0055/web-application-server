package webserver.handlers;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HandlerResponse {
    private String viewPath;


    private Map<String, String> parameterMap = new HashMap<>();


    public HandlerResponse(){
    }
    public HandlerResponse(String viewPath) {
        this.viewPath = viewPath;
    }

    public void setViewPath(String viewPath){
        this.viewPath = viewPath;
    }

    public String getViewPath() {
        return viewPath;
    }

    public Map<String, String> getParameterMap() {
        return parameterMap;
    }




}

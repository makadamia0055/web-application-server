package webserver;

public class Cookies {
    private String name;
    private String value;
    private String path ="/";

    public Cookies(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public void setPath(String path){
        this.path = path;
    }

    @Override
    public String toString() {
        return "Set-Cookie"+ name +"="+ value +";"
                +"Path=" + path +";" ;
    }
}

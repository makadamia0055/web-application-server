package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.utilClass.HttpRequestClass;
import util.utilClass.HttpRequestParser;
import webserver.handlers.HandlerResponse;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private String staticResourcePath = Paths.get(System.getProperty("user.dir"), "target", "classes").toString();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequestClass httpRequestClass = HttpRequestParser.extractHttpRequest(br).orElseThrow(()->new IllegalArgumentException("HttpRequest Parse Fail"));

            // 매핑 정보 관리 및 매핑 작업을 담당하는 HandlerMapper 클래스
            HandlerMapper handlerMapper = HandlerMapper.getInstance();

            // urlMapper에서 매핑되는 url을 찾고 없으면 디폴트 값 전달
            HandlerResponse handlerResponse = handlerMapper.getMapping(httpRequestClass).orElseThrow();

            // 리다이렉션 처리
            String viewPath = handlerResponse.getViewPath();
            if(viewPath.contains("redirect:")){
                String location = viewPath.substring(viewPath.indexOf(":") + 1);
                String contextPath = "http://localhost:8080";
                sendRedirectionResponse(dos, contextPath, location, handlerResponse);
                return ;
            }

            // 매핑 처리 결과로 리턴된 ViewPath에 해당하는 파일을 찾아 리턴
            File requestedFile = new File(staticResourcePath + viewPath);
            log.info(requestedFile.getPath());
            // 파일이 없을 시 404 에러 리턴
            if(!requestedFile.exists()||!requestedFile.isFile()){
                sendErrorResponse(dos, 404, "Not Found");
                return ;
            }

            // viewPath의 view 파일을 읽어옴.
            // 템플릿 작업을 위해 String으로 읽어오고, 해당 문자열 내에 템플릿 문자열이 존재하는지 확인
            byte[] body = viewTemplate(requestedFile, handlerResponse);

            // 콘텐츠 타입 확인
            String contentType = getContentType(viewPath);
            sendResponse(dos, 200, contentType, body, handlerResponse);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 뷰 템플릿 작업 수행함.
    // html 파일 내에 <for></for> 태그가 있을 경우
    // 하드 코딩한 작업을 수행함.
    private byte[] viewTemplate(File requestedFile, HandlerResponse handlerResponse) throws IOException {
        if(!requestedFile.toPath().toString().endsWith(".html")){
            log.info(requestedFile.toPath().toString());
            return Files.readAllBytes(requestedFile.toPath());
        }
        String bodyStr =Files.readString(requestedFile.toPath());
        log.info(bodyStr);

        if(bodyStr.contains("<for>")&&bodyStr.contains("</for>")){
            log.info("body str parsing");
            String[] split1 = bodyStr.split("<for>");
            String bodyBefore = split1[0];
            String[] split2 = split1[1].split("</for>");
            String forStr = split2[0];
            String bodyPost = split2[1];

            String string = handlerResponse.getParameterMap().get("users");
            log.info("users :"+string);
            if(string!=null){
                List<String> stringList = Arrays.stream(string.split(";"))
                        .map(str -> str.split(":"))
                        .map(strs -> forStr.replace("for:id", strs[0]).replace("for:name", strs[1]).replace("for:email", strs[2]))
                        .collect(Collectors.toList());
                StringBuilder sb2 = new StringBuilder();
                for (int i = 0; i < stringList.size() ; i++) {
                    String replace = stringList.get(i).replace("for:index", String.valueOf(i + 1));
                    sb2.append(replace);

                }
                String fullStr = bodyBefore + sb2.toString() + bodyPost;
                log.info("fullStr" + fullStr);
                return fullStr.getBytes();
            }

        }
        return bodyStr.getBytes();
    }

    private void sendRedirectionResponse(DataOutputStream dos, String contextPath, String location, HandlerResponse handlerResponse) throws IOException {
        dos.writeBytes("HTTP/1.1 " + "302 Found" +"\r\n");
        dos.writeBytes("Location: " +contextPath+ location+ " \r\n");
        log.info(handlerResponse.getViewPath());
        setCookies(dos, handlerResponse);

        dos.writeBytes("\r\n");
        dos.flush();
    }

    private static void setCookies(DataOutputStream dos, HandlerResponse handlerResponse) throws IOException {
        for (Entry<String, String> entry : handlerResponse.getParameterMap().entrySet()) {
            log.info(entry.getKey()+":"+entry.getValue());

            dos.writeBytes("Set-Cookie: " + entry.getKey()+"="+entry.getValue()+"; Path=/; Max-Age=600; \r\n");
        }
    }


    private void sendErrorResponse(DataOutputStream dos, int statusCode, String message) throws IOException{
        byte[] body = message.getBytes();
        sendResponse(dos, statusCode, "text/plain", body);
        dos.flush();
    }
    private String getContentType(String fileName){
        if(fileName.endsWith(".html")){
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";

        }
        // 다른 Content-type 추가 가능
        return "application/octet-stream";
    }

    private void sendResponse(DataOutputStream dos, int statusCode, String contentType, byte[] body, HandlerResponse handlerResponse) throws IOException{
        dos.writeBytes("HTTP/1.1" + statusCode + " \r\n");
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
        dos.writeBytes("Content-length: " + body.length + "\r\n");
        setCookies(dos, handlerResponse);

        dos.writeBytes("\r\n");
        dos.write(body);
        dos.flush();
    }

    private void sendResponse(DataOutputStream dos, int statusCode, String contentType, byte[] body) throws IOException{
        dos.writeBytes("HTTP/1.1" + statusCode + " \r\n");
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
        dos.writeBytes("Content-length: " + body.length + "\r\n");

        dos.writeBytes("\r\n");
        dos.write(body);
        dos.flush();
    }



}

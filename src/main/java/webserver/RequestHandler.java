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



            // 커넥션에서 가져온 아웃풋 스트림으로 DataOutputStream 선언

            // urlMapper 생성(이후 필드로 옮기기)
            // 서버 init 작업하면서 매핑 정보 자동 입력할 수 있도록.
            HandlerMapper handlerMapper = HandlerMapper.getInstance();
//            log.info(httpRequestClass.getPath());


            // urlMapper에서 매핑되는 url을 찾고 없으면 디폴트 값 전달
            HandlerResponse handlerResponse = handlerMapper.getMapping(httpRequestClass).orElseThrow();


            String mappedTemplate = handlerResponse.getViewPath();
            if(mappedTemplate.contains("redirect:")){
                String location = mappedTemplate.substring(mappedTemplate.indexOf(":") + 1);
                String contextPath = "http://localhost:8080";
                sendRedirectionResponse(dos, contextPath, location, handlerResponse);
                return ;
            }

            File requestedFile = new File(staticResourcePath + mappedTemplate);
            log.info(requestedFile.getPath());
            if(!requestedFile.exists()||!requestedFile.isFile()){
                sendErrorResponse(dos, 404, "Not Found");
                return ;
            }

            // 전달 받은 bodyString 변환
//            byte[] body = Files.readAllBytes(requestedFile.toPath());

            byte[] body = viewTemplate(requestedFile, handlerResponse);

            String contentType = getContentType(mappedTemplate);
            sendResponse(dos, 200, contentType, body, handlerResponse);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ClasspathFileReader;
import util.utilClass.HttpRequestClass;
import util.utilClass.HttpRequestParser;

import javax.swing.text.html.Option;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

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
            log.info(httpRequestClass.getPath());


            // urlMapper에서 매핑되는 url을 찾고 없으면 디폴트 값 전달
            String mappedTemplate = handlerMapper.getMapping(httpRequestClass).orElseThrow();
            log.info("mappedTemplete :" + mappedTemplate);
            if(mappedTemplate.contains("redirect:")&&mappedTemplate.startsWith("redirect:")){
                String location = mappedTemplate.substring(mappedTemplate.indexOf(":") + 1);
                String contextPath = "http://localhost:8080";
                sendRedirectionResponse(dos, contextPath, location);
                return ;
            }

//            ClasspathFileReader classpathFileReader = new ClasspathFileReader(mappedTemplate);
            File requestedFile = new File(mappedTemplate);
            if(!requestedFile.exists()||!requestedFile.isFile()){
                sendErrorResponse(dos, 404, "Not Found");
                return ;
            }


            // 전달 받은 bodyString 변환
            byte[] body = Files.readAllBytes(requestedFile.toPath());


            String contentType = getContentType(mappedTemplate);
            sendResponse(dos, 200, contentType, body);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRedirectionResponse(DataOutputStream dos, String contextPath, String location) throws IOException {
        dos.writeBytes("HTTP/1.1 " + "302 Found" +"\r\n");
        dos.writeBytes("Location: " +contextPath+ location+ " \r\n");
        dos.writeBytes("\r\n");
        dos.flush();
    }

//    private static Optional<HttpRequestClass> extracted(BufferedReader br) throws IOException {
//        try{
////            StringBuilder headerBuilder = new StringBuilder();
////            String line;
////            while (!(line = br.readLine()).isEmpty()) {
////                headerBuilder.append(line).append("\n");
////            }
////
////            String headers = headerBuilder.toString().trim();
////            if (headers.isEmpty()) {
////                return Optional.empty();
////            }
////            log.info(headers);
//            HttpRequestClass httpRequestClass = HttpRequestParser.extractHttpRequest(br).orElseThrow();
//
//            // 헤더와 바디를 분리
//
//
//
//            return Optional.of(httpRequestClass);
//        }catch (IOException e){
//            e.printStackTrace();
//            return Optional.empty();
//        }

//
//    }
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

    private void sendResponse(DataOutputStream dos, int statusCode, String contentType, byte[] body) throws IOException{
        dos.writeBytes("HTTP/1.1" + statusCode + " \r\n");
        dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
        dos.writeBytes("Content-length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body);
        dos.flush();
    }


  /*  private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }*/

    /*private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }*/
}

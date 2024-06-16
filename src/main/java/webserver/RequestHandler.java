package webserver;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ClasspathFileReader;
import util.utilClass.HttpRequestClass;

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

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpRequestClass httpRequestClass = extracted(br).orElseThrow(() -> new IllegalArgumentException());


            DataOutputStream dos = new DataOutputStream(out);
            // 커넥션에서 가져온 아웃풋 스트림으로 DataOutputStream 선언

            // urlMapper 생성(이후 필드로 옮기기)
            // 서버 init 작업하면서 매핑 정보 자동 입력할 수 있도록.
            Map<String, String> urlMapper = new HashMap<>();
            urlMapper.put("index.html", "index.html");
            urlMapper.put("/css/styles.css", "css/styles.css");
            urlMapper.put("/js/scripts.js", "js/scripts.js");

            // urlMapper에서 매핑되는 url을 찾고 없으면 디폴트 값 전달
            String mappedTemplate = urlMapper.getOrDefault(httpRequestClass.getUrl(), "index.html");

            ClasspathFileReader classpathFileReader = new ClasspathFileReader(mappedTemplate);

            // 전달 받은 bodyString 변환
            byte[] body = classpathFileReader.readFile().getBytes();


             // HTTP의 body에 전달할 텍스트 선언
            response200Header(dos, body.length); // HTTP에 맞는 헤더 설정하는 메서드 호출
            responseBody(dos, body); // 위 response200Header에 설정한 길이 만큼의 body byte를 출력 스트림으로 내보냄
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private static Optional<HttpRequestClass> extracted(BufferedReader br) throws IOException {
        StringBuffer sb = new StringBuffer();

        String line = br.readLine();
        HttpRequestClass header = new HttpRequestClass(line);

        while(!"".equals(line)){
            line = br.readLine();
            if(line==null){
                return Optional.empty();
            }
            log.info(line);
            sb.append(line);

        }
        return Optional.ofNullable(header);


    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

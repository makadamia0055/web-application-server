package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            byte[] body = "".getBytes();
            if(httpRequestClass.getUrl().equals("/index.html")){
                body = "Hello World".getBytes();
            }

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

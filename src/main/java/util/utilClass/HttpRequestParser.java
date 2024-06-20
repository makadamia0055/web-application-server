package util.utilClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpRequestParser {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public static Optional<HttpRequestClass> extractHttpRequest(BufferedReader br) throws IOException{
        try{
            String requestLine = br.readLine();

            if(requestLine==null||requestLine.isEmpty()){
                return Optional.empty();
            }

            // 요청 라인 공백으로 분리하여 메서드, 경로, 버전 추출
            String[] requestLineParts = requestLine.split(" ");
            if(requestLineParts.length<3){
                return Optional.empty();
            }
            String method = requestLineParts[0];
            String url = requestLineParts[1];
            String version = requestLineParts[2];

            // 헤더를 읽어 Map로 정리
            Map<String, String> headers = new HashMap<>();
            String line;
            while((line=br.readLine())!=null&&!line.isEmpty()){
                String[] headerParts = line.split(":", 2);

                if(headerParts.length==2){
                    headers.put(headerParts[0].trim(), headerParts[1].trim());
                }
            }


            // Content-Length 헤더가 있으면, 해당 길이만큼 바디를 읽음
            String body = null;
            String contentLengthHeader = headers.get("Content-Length");
            if(contentLengthHeader != null){
                int contentLength = Integer.parseInt(contentLengthHeader);
                char[] bodyChars = new char[contentLength];
                int read =  br.read(bodyChars, 0, contentLength);
                body = new String(bodyChars, 0, read);
                log.info(body);

            }
//            else{
//                // Content-length가 없는 경우 바디 전체를 읽기
////                body = br.lines().collect(Collectors.joining("\n"));
////                log.info(body);
//
//            }
            HttpRequestClass httpRequestClass = new HttpRequestClass(method, url, version, headers, body);

            return Optional.of(httpRequestClass);


        }catch (IOException e){
            e.printStackTrace();
            return Optional.empty();
        }


    }
}

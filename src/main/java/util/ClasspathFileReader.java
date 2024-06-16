package util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ClasspathFileReader {


    String fileName;

    public ClasspathFileReader(String fileName){
        this.fileName = fileName;
    }

    public byte[] readFile(){
        try(InputStream inputStream = ClasspathFileReader.class.getClassLoader().getResourceAsStream(fileName);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream()){
            if(inputStream==null){
                throw new IOException("File not found" + fileName);
            }
            byte[] data = new byte[1024];

            int bytesRead;
            while((bytesRead = inputStream.read(data, 0, data.length)) !=-1){
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        }catch(IOException e){
           e.printStackTrace();
           return null;
        }
    }



}

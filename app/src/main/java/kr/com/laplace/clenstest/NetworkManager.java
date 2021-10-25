package kr.com.laplace.clenstest;

import android.content.ContentValues;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class NetworkManager {
  public String request(String _url, ContentValues _params, String filepath){
    // HttpURLConnection 참조 변수.
    HttpURLConnection urlConn = null;
    // URL 뒤에 붙여서 보낼 파라미터.
    StringBuffer sbParams = new StringBuffer();

    String strParams;
    streamChecker outputStream = null;

    InputStream inputStream = null;
    String twoHyphens = "--";
    String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
    String lineEnd = "\r\n";
    String result = "";

    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;

    int maxBufferSize = 1 * 1024 * 1024;

    String urlTo = "http://175.192.247.184:8080/image_processing/";


    /**
     * 1. StringBuffer에 파라미터 연결
     * */
    // 보낼 데이터가 없으면 파라미터를 비운다.
    if (_params == null)
      sbParams.append("");
      // 보낼 데이터가 있으면 파라미터를 채운다.
    else {
      // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
      boolean isAnd = false;
      // 파라미터 키와 값.
      String key;
      String value;

      for(Map.Entry<String, Object> parameter : _params.valueSet()){
        key = parameter.getKey();
        value = parameter.getValue().toString();

        // 파라미터가 두개 이상일때, 파라미터 사이에 &를 붙인다.
        if (isAnd)
          sbParams.append("&");

        sbParams.append(key).append("=").append(value);

        // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
        if (!isAnd)
          if (_params.size() >= 2)
            isAnd = true;
      }
    }

    /**
     * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
     * */
    try{
      URL url = new URL(urlTo);
      urlConn = (HttpURLConnection) url.openConnection();

      // [2-1]. urlConn 설정.
      urlConn.setRequestMethod("POST");
      urlConn.setReadTimeout(50000);
      urlConn.setConnectTimeout(50000);
      urlConn.setDoOutput(true);
      urlConn.setUseCaches(false);
      //urlConn.setRequestProperty("ENCTYPE","multipart/form-data");
      urlConn.setRequestProperty("Connection", "keep-alive");
      urlConn.setRequestProperty("Accept", "text/html,application/xhtml+html,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
      urlConn.setRequestProperty("Cache-Control","max-age=0");
      urlConn.setRequestProperty("Accept-Language","ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
      urlConn.setRequestProperty("Accept-Encoding", "gzip,deflate");
      urlConn.setRequestProperty("Origin","null");
      //urlConn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
      urlConn.setRequestProperty("Keep-Alive", "300");
      //urlConn.setRequestProperty("Referer" ,"http://my.server.com/test/index.html");
      urlConn.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
      urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

      String delimiter = twoHyphens + boundary + lineEnd;

      StringBuffer postString = new StringBuffer();


      outputStream = new streamChecker(urlConn.getOutputStream());

      //여기부터 파일
      File file = new File(filepath);
      postString.append(delimiter);
      postString.append("Content-Disposition: form-data; name=\"" + "file" + "\";filename=\"" + file.getName() + "\"" + /*lineEnd + "Content-Type:image/jpeg" +*/ lineEnd + lineEnd);
      outputStream.write(postString.toString().getBytes());
      //outputStream.write("Content-Disposition: form-data; name=\"" + "file" + "\";filename=\"" + file.getName() + "\"" + lineEnd);
      Log.d("getName",file.getName());
      FileInputStream fileInputStream = new FileInputStream(file);
      //bytesAvailable = fileInputStream.available();
      //bufferSize = Math.min(bytesAvailable, maxBufferSize);
      buffer = new byte[5 * 1024 * 1024/*bufferSize*/];
      //bytesRead = fileInputStream.read(buffer, 0, bufferSize);
      //Log.d("bytesRead", Integer.toString(bytesRead));
      int length = -1;
      while ((length = fileInputStream.read(buffer)) != -1/*bytesRead > 0*/) {
        Log.d("bytesRead", Integer.toString(length));
        outputStream.write(buffer, 0, length);

        /*Log.d("bytesRead", Integer.toString(bytesRead));
        outputStream.write(buffer, 0, bufferSize);
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);*/
      }
      outputStream.writeBytes(lineEnd);
      outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


      outputStream.flush();
      outputStream.close();

      if(outputStream==null){
        Log.d("Network","Null outputStream");
      }
      // [2-3]. 연결 요청 확인.
      // 실패 시 null을 리턴하고 메서드를 종료.
      if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK){
        Log.d("NetworkError","Connection Failed");
        return null;
      }
      Log.d("Network","Connection Successed");

      // [2-4]. 읽어온 결과물 리턴.
      // 요청한 URL의 출력물을 BufferedReader로 받는다.
      BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

      // 출력물의 라인과 그 합에 대한 변수.
      String line;
      String page = "";

      // 라인을 받아와 합친다.
      while ((line = reader.readLine()) != null) {
        page += line;
      }
      return page;

    } catch (MalformedURLException e) { // for URL.
      e.printStackTrace();
    } catch (IOException e) { // for openConnection().
      e.printStackTrace();
      Log.d("NetworkError","Connect Fail");
    } finally {
      if (urlConn != null)
        urlConn.disconnect();
    }
    return null;
  }

  public static byte binaryStringToByte(String s) {
    byte ret = 0, total = 0;
    for (int i = 0; i < 8; ++i) {
      ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
      total = (byte) (ret | total);
    }
    return total;
  }

  public class streamChecker extends DataOutputStream{
    private byte[] data = null;

    public streamChecker(OutputStream delegateStream) {
      super(delegateStream);
    }

    @Override
    public void write(byte[] s, int off, int len) {
      // Store the bytes internally
      Log.d("DataOutputStream", s.toString());
      // Pass off to delegate
      try{
        super.write(s, off, len);
      }catch(IOException e){
        e.printStackTrace();
      }

    }
  }
}

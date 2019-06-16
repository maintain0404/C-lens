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
  /*



  URL url;				//접속대상 서버주소를 가진 객체
  HttpURLConnection conn;	//통신을 담당하는 객체
  DataOutputStream outputStream;
  StringBuffer sbParams;
  BufferedReader buffer=null;

  String twoHyphens = "--";
  String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
  String lineEnd = "\r\n";


  //필요한 객체 초기화

  public NetworkManager(String filePath) {
    try {
      url = new URL("http://192.168.0.18:8080/index2.jsp");
      conn = (HttpURLConnection)url.openConnection();
      sbParams = new StringBuffer();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String request(ContentValues _params) {
    if (_params == null) {
      sbParams.append("");
    }//보낼 데이터가 없는 경우, 파라미터 비움 있으면 else
    else {

    }

    String data = "";
    try {
      File file = new File(filepath);

      conn.setRequestMethod("POST");//메소드 설정
      conn.setRequestProperty("Connection", "Keep-Alive");
      conn.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
      conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
      conn.setRequestProperty("Cookie", CookieManager.getInstance().getCookie("http://192.168.0.18:8080/index2.jsp")); //쿠키를 실어 보낸다

      outputStream = new DataOutputStream(conn.getOutputStream());
      outputStream.writeBytes(twoHyphens + boundary + lineEnd);
      //outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
      outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
      outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
      outputStream.writeBytes(lineEnd);

      Iterator<String> keys = params.keySet().iterator();

      while (keys.hasNext()) {

        String key = keys.next();

        String value = params.get(key);



        outputStream.writeBytes(twoHyphens + boundary + lineEnd);

        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);

        outputStream.writeBytes("Content-Type: text/plain" + lineEnd);

        outputStream.writeBytes(lineEnd);

        outputStream.writeBytes(value);

        outputStream.writeBytes(lineEnd);

      }

      outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);



      inputStream = conn.getInputStream();



      result = this.convertStreamToString(inputStream);
      fileInputStream.close();
      inputStream.close();
      outputStream.flush();
      outputStream.close();



      return result;
    } catch (Exception e) {

      e.printStackTrace();

    }
    return result;

  }

*/
      /*String strParams = sbParams.toString();
      OutputStream os = conn.getOutputStream();
      os.write(strParams.getBytes("UTF-8"));//출력스트림에 출력
      os.flush();//출력스트림을 비우기 버퍼링된 모든 출력바이트 강제로 실행
      os.close();//출력시스템 닫고 자원 해제


      //요청 확인하고 실패 시 null 리턴하고 종료
      if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        return null;
      }

      //결과물 리턴
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

      String line;
      String page = "";

      //라인 합치기
      while ((line = reader.readLine()) != null) {
        page += line;
      }
      return page;

    } catch(MalformedURLException e){//URL에서 나옴
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
      Log.d("Network","연결실패");
    } catch (IOException e) {//OpenConnection에서 나옴
      e.printStackTrace();
    } finally{
      if(conn != null){
        conn.disconnect();
      }
    }

    return null;
      *//*

      conn.connect();			//웹서버에 요청하는 시점
      InputStream is = conn.getInputStream();	//웹서버로부터 전송받을 데이터에 대한 스트림 얻기

      //1byte기반의 바이트스트림이므로 한글이 깨진다.
      //따라서 버퍼처리된 문자기반의 스트림으로 업그레이드 해야 된다.

      buffer = new BufferedReader(new InputStreamReader(is));
      //스트림을 얻어왔으므로, 문자열로 반환
      StringBuffer str = new StringBuffer();
      String d=null;

      while( (d=buffer.readLine()) != null){
        str.append(d);
      }
      data = str.toString();
    } catch (IOException e) {
      e.printStackTrace();
    } finally{
      if(buffer!=null){
        try {
          buffer.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return data;
  }*/
      //https://hyunssssss.tistory.com/305 [현's 블로그]


/*
package kr.com.laplace.clenstest;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Network {
  public String request(String _url, ContentValues _params){

    // HttpURLConnection 참조 변수.
    HttpURLConnection urlConn = null;
    // URL 뒤에 붙여서 보낼 파라미터.
    StringBuffer sbParams = new StringBuffer();


     //1. StringBuffer에 파라미터 연결


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

    //2. HttpURLConnection을 통해 web의 데이터를 가져온다.

    try{
      URL url = new URL(_url);
      urlConn = (HttpURLConnection) url.openConnection();

      // [2-1]. urlConn 설정.
      urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
      urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
      urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

      // [2-2]. parameter 전달 및 데이터 읽어오기.
      String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
      OutputStream os = urlConn.getOutputStream();
      os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
      os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
      os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

      // [2-3]. 연결 요청 확인.
      // 실패 시 null을 리턴하고 메서드를 종료.
      if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
        return null;

      // [2-4]. 읽어온 결과물 리턴.
      // 요청한 URL의 출력물을 BufferedReader로 받는다.
      BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

      // 출력물의 라인과 그 합에 대한 변수.
      String line;
      String page = "";

      // 라인을 받아와 합친다.
      while ((line = reader.readLine()) != null){
        page += line;
      }

      return page;

    } catch (MalformedURLException e) { // for URL.
      e.printStackTrace();
    } catch (IOException e) { // for openConnection().
      e.printStackTrace();
    } finally {
      if (urlConn != null)
        urlConn.disconnect();
    }

    return null;
  }
}*/

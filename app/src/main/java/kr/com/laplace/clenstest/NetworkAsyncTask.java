package kr.com.laplace.clenstest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class NetworkAsyncTask extends AsyncTask<String, Void, String>{
/*
 * Params - 비동기 작업시 필요한 데이터 자료형
 * 			ex)웹사이트에 데이터 요청시 전송할 파라미터값(ID,PW 등....)
 *
 * Progress - 비동기 방식의 요청이 진행될때 사용될 데이터 자료형.
 * 			  숫자형 자료형을 많이 사용한다.
 *
 * Result - 웹서버로부터 가져오게 될 데이터에 알맞는 자료형을 개발자가 결정
 * 			주로 JSON, XML등을 가져오게 되므로 String을 많이 사용한다.
 */
  MainActivity context;
  ProgressDialog dialog;
  NetworkManager load;//접속과 요청을 담당하는 객체 선언



  String url;

  public NetworkAsyncTask(Context context, String url_get) {
    this.context = (MainActivity)context;
    load = new NetworkManager();
    url = url_get;
    Log.d("ImageUrl", url);
  }

  //백그라운드 작업 수행전에 해야할 업무등을 이 메서드에 작성하며 되는데,
  //이 메서드는 UI쓰레드에 의해 작동하므로 UI를 제어할 수 있다.
  //따라서 이 타이밍에 진행바를 보여주는 작업등을 할 수 있다.
  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    dialog = new ProgressDialog(context);
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setMessage("로딩중입니다..");
    dialog.setCancelable(false);
    Log.d("Loading","lasdlkajsdf");

    dialog.show();

  }

  //비동기방식으로 작동할 메서드이며, 주로 메인쓰레드와는 별도로
  //웹사이트의 연동이나 지연이 발생하는 용도로 사용하면 된다.
  //사실상 개발자가 정의 쓰레드에서 run메서드와 비슷하다
  //  'String...' 가변형 파라미터로 파라미터 개수 상관없이 넣을 수 있다.
  protected String doInBackground(String... params) {
    //웹서버에 요청시도
    String data = load.request(url, null, url);
    return data;
  }

  //백그라운드 메서드가 업무수행을 마칠때 호출되는 메서드.
  //UI쓰레드에 의해 호출되므로, UI쓰레드를 제어할 수 있다.
  //따라서 진행바를 그만 나오게 할 수 있다.
  protected void onPostExecute(String data){
    super.onPostExecute(data);
    if(data == null){
      Log.d("NullData","get null data");
      dialog.dismiss();
      Toast.makeText(context, "네트워크 오류를 정보를 받아오지 못했습니다.", Toast.LENGTH_LONG).show();
      return;
    }else if(data == "false"){
      Toast.makeText(context, "네트워크 문제입니다. 다시 시도해주세요", Toast.LENGTH_LONG).show();
    }else{
      Log.d("Data", data);
      //this.dialog.dismiss();

      Intent intent = new Intent();
      intent.setClass(context, ResultActivity.class);
      //Intent intent = new Intent(this, ResultActivity.class); 로도 선언 가능
      intent.putExtra("image", url);
      intent.putExtra("data", data);
      context.startActivity(intent);
    }
    dialog.dismiss();
  }

  /*protected void onPostExecute(String result) {
    super.onPostExecute(result);
    dialog.dismiss();
    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

    System.out.println("a");

    //최종적으로 웹서버로부터 데이터를 완전히 가져온 시점은 이 메서드
    ArrayList<DataVo> dataList = context.adapter.lst;

    dataList.removeAll(dataList);

    try {
      JSONObject o = new JSONObject(result);
      //JSONArray array = new JSONArray(result);
      JSONArray array = o.getJSONArray("a");

      DataVo dataVo = null;

      JSONObject obj = null;

      System.out.println("1");

      for(int i=0;i<array.length();i++){

        obj = array.getJSONObject(i);

        dataVo = new DataVo();



        System.out.println("2obj.getString(name):"+obj.getString("name"));

        dataVo.setName(obj.getString("name"));

        dataVo.setAge(obj.getInt("age"));

        dataVo.setIsBool(obj.getString("isBool"));



        dataList.add(dataVo);



        context.listView.invalidateViews();

      }

      System.out.println("3");

      context.listView.invalidate();



      System.out.println("array.length():"+array.length());

    } catch (JSONException e) {

      e.printStackTrace();

    }
  }*/
}
// [현's 블로그]출처: https://hyunssssss.tistory.com/305
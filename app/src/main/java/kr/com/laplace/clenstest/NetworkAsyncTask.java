package kr.com.laplace.clenstest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;


public class NetworkAsyncTask extends AsyncTask<String, Void, String> {
  /*
   * Params - 비동기 작업시 필요한 데이터 자료형
   *
   * Progress - 비동기 방식의 요청이 진행될때 사용될 데이터 자료형.
   *
   * Result - 웹서버로부터 가져오게 될 데이터에 알맞는 자료형
   */
  ResultActivity context;
  ProgressDialog dialog;
  NetworkManager load;//접속과 요청을 담당하는 객체 선언


  String url;

  public NetworkAsyncTask(Context context, String url_get) {
    this.context = (ResultActivity) context;
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
    Log.d("Loading", "lasdlkajsdf");

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
  @Override
  protected void onPostExecute(String data) {
    super.onPostExecute(data);
    if (data == null || data == "false") {
      Log.d("NullData", "get null data");
      dialog.dismiss();
      Toast.makeText(context, "네트워크 오류를 정보를 받아오지 못했습니다.", Toast.LENGTH_LONG).show();
      Intent intent = new Intent();
      intent.setClass(this.context, MainActivity.class);
    } else {
      Log.d("Data", data);
      //this.dialog.dismiss();
      parseJsonAndSet(data);
    }
    dialog.dismiss();
  }

  public void parseJsonAndSet(String PrdDataJson) {
    String name;
    String low_price;
    String score;
    String link;
    String prd_name;
    String thumbnail_link;
    JSONArray coordinates;
    Bitmap thumbnail;
    int[] coordinate = new int[]{0, 0, 0, 0};

    Log.d("ItemAdd", "start");
    try {
      JSONArray jarray = new JSONObject(PrdDataJson).getJSONArray("products");
      if(jarray.length() == 0){
        Toast.makeText(this.context, "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setClass(this.context, MainActivity.class);
        this.context.startActivity(intent);
      }
      for (int i = 0; i < jarray.length(); i++) {
        Log.d("ItemAdd", Integer.toString(i));
        JSONObject jObject = jarray.getJSONObject(i);

        name = jObject.optString("search_word");
        low_price = jObject.optString("low_price");
        score = jObject.optString("score");
        link = jObject.optString("link");
        thumbnail_link = jObject.optString("thumbnail");
        prd_name = jObject.optString("product_name");
        coordinates = jObject.getJSONArray("coordinate");
        for (int j = 0; j < coordinates.length(); j++) {
          coordinate[j] = coordinates.getInt(j);
        }

        if (score == "null") {
          score = "";
        }
        if (low_price == "null") {
          continue;
        }
        if (prd_name.length() > 23){
          prd_name = prd_name.substring(0, 22) + "...";
        }
        thumbnailASync async = new thumbnailASync(thumbnail_link, new SearchResult(name, low_price, score, link, prd_name));
        async.execute();
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Log.d("ItemAdd", "end");
  }

  public class thumbnailASync extends AsyncTask<String, Bitmap, Bitmap>{
    String url;
    SearchResult res;

    public thumbnailASync(String Url, SearchResult res){
      this.url = Url;
      this.res = res;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
      Bitmap thumbnail;
      try{
        thumbnail = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
      } catch(Exception e){
        e.printStackTrace();
        return null;
      }
      return thumbnail;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      super.onPostExecute(bitmap);
      if(bitmap != null){
        res.setThummbnail(bitmap);
      }
      context.adapter.addItem(res);
      context.adapter.notifyDataSetChanged();
    }
  }
}
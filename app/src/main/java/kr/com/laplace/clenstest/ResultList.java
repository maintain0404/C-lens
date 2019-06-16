package kr.com.laplace.clenstest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.com.laplace.clenstest.R;
import kr.com.laplace.clenstest.SearchResult;


public class ResultList extends Fragment {
  private RecyclerView recyclerView;
  View resultview;
  ResultAdapter adapter;
  Bitmap bitmap;
  String PrdDataJson;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return resultview = inflater.inflate(R.layout.fragment_result_list, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
    recyclerView = (RecyclerView) resultview.findViewById(R.id.recyclerView);

    LinearLayoutManager layoutManager =
      new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new ResultAdapter();
    recyclerView.setAdapter(adapter);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(getArguments() != null) {
      bitmap = BitmapFactory.decodeFile(getArguments().getString("image"));
      PrdDataJson = getArguments().getString("PrdDataJson");

      setPrdData();
    }else{
      Log.e("ResultList","Null bundle");
    }
  }

  public void addData(SearchResult data){
    adapter.addItem(data);
  }

  public void setPrdData(){
    String name;
    String low_price;
    String score;
    String link;
    String prd_name;
    JSONArray coordinates;
    int[] coordinate = new int[]{0,0,0,0};

    Log.d("ItemAdd","start");
    try {
      if(adapter == null){
        adapter = new ResultAdapter();
      }
      JSONArray jarray = new JSONObject(PrdDataJson).getJSONArray("products");
      for (int i = 0; i < jarray.length(); i++) {
        Log.d("ItemAdd",Integer.toString(i));
        JSONObject jObject = jarray.getJSONObject(i);

        name = jObject.optString("search_word");
        low_price = jObject.optString("low_price");
        score = jObject.optString("score");
        link = jObject.optString("link");
        prd_name = jObject.optString("product_name");
        coordinates = jObject.getJSONArray("coordinate");
        for(int j = 0; j < coordinates.length(); j++){
          coordinate[j] = coordinates.getInt(j);
        }
        Bitmap thumbnail = cropByCrd(coordinate);

        if(score == "null"){
          score = "";
        }
        if(low_price =="null"){
          low_price = "최저가 없음";
        }
        addData(new SearchResult(name, low_price, score, link, thumbnail, prd_name));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Log.d("ItemAdd","end");
  }

  public Bitmap cropByCrd(int[] crd){
    Bitmap croped = Bitmap.createBitmap(bitmap, crd[0], crd[1], crd[2] - crd[0], crd[3] - crd[1]);

    return croped;
  }

}

package kr.com.laplace.clenstest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {
  RecyclerView recyclerView;
  ResultAdapter adapter;
  String PrdDataJson;
  String imageUrl;
  Bitmap bitmap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    recyclerView = (RecyclerView) findViewById(R.id.recycler);

    Intent intent = getIntent();
    PrdDataJson = intent.getExtras().getString("data");
    imageUrl = intent.getExtras().getString("image");
    bitmap = BitmapFactory.decodeFile(imageUrl);
    Log.d("imageUrl", imageUrl);

    LinearLayoutManager layoutManager =
      new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new ResultAdapter();
    setPrdData();

    recyclerView.setAdapter(adapter);

    /*Button change_to_image = findViewById(R.id.change_to_image);
    change_to_image.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        changeToImage();
      }
    });*/
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }


  public void setPrdData() {
    String name;
    String low_price;
    String score;
    String link;
    String prd_name;
    JSONArray coordinates;
    int[] coordinate = new int[]{0, 0, 0, 0};

    Log.d("ItemAdd", "start");
    try {
      JSONArray jarray = new JSONObject(PrdDataJson).getJSONArray("products");
      for (int i = 0; i < jarray.length(); i++) {
        Log.d("ItemAdd", Integer.toString(i));
        JSONObject jObject = jarray.getJSONObject(i);

        name = jObject.optString("search_word");
        low_price = jObject.optString("low_price");
        score = jObject.optString("score");
        link = jObject.optString("link");
        prd_name = jObject.optString("product_name");
        coordinates = jObject.getJSONArray("coordinate");
        for (int j = 0; j < coordinates.length(); j++) {
          coordinate[j] = coordinates.getInt(j);
        }
        Bitmap thumbnail = cropByCrd(coordinate);

        if (score == "null") {
          score = "";
        }
        if (low_price == "null") {
          low_price = "최저가 없음";
        }
        adapter.addItem(new SearchResult(name, low_price, score, link, thumbnail, prd_name));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Log.d("ItemAdd", "end");
  }

  public Bitmap cropByCrd(int[] crd) {
    Bitmap croped = Bitmap.createBitmap(bitmap, crd[0], crd[1], crd[2] - crd[0], crd[3] - crd[1]);

    return croped;
  }

  /*public void changeToImage() {
    Log.d("imageUrlonResult", imageUrl);
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), ImageActivity.class);
    intent.putExtra("image", imageUrl);
    intent.putExtra("data", PrdDataJson);

    *//*ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
    byte[] byteArr = stream.toByteArray();
    intent.putExtra("byteImage", byteArr);
*//*
    startActivity(intent);
  }*/

  @Override
  public void onBackPressed() {
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), MainActivity.class);
    startActivity(intent);
  }
}
    /*Gson gson = new Gson();
    Type searchResultType = new TypeToken<products>() {}.getType();
    products product_list = gson.fromJson(PrdDataJson, searchResultType);
    Log.d("MovieData", PrdDataJson);
    if(product_list == null){
      Log.d("ItemAdd", "product_list is null");
    }else if(product_list.ProductList == null){
      Log.d("ItemAdd", "ProductList is null");
    }else{
      Log.d("product_listSIze", Integer.toString(product_list.ProductList.size()));
      for(int i = 0; i < product_list.ProductList.size(); i++) {
        ProductInfo prd = product_list.ProductList.get(i);
        adapter.addItem(new SearchResult(prd.search_word, prd.low_price, prd.score, prd.link));
        Log.d("ItemAdd", prd.search_word);
      }
    }*/


  /*ResultList result_list;
  ResultImage result_image;
  String PrdDataJson;
  String image;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    Intent intent = getIntent();
    PrdDataJson = intent.getExtras().getString("data");
    image = intent.getExtras().getString("image");

    result_image = new ResultImage();
    Bundle image_bundle = new Bundle(1);
    image_bundle.putString("image", image);
    result_image.setArguments(image_bundle);

    result_list = new ResultList();
    Bundle list_bundle = new Bundle();
    list_bundle.putString("PrdDataJson",PrdDataJson);
    list_bundle.putString("image", image);
    result_list.setArguments(list_bundle);

    //Intent intent = getIntent();
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    fragmentTransaction.replace(R.id.container, result_list);
    fragmentTransaction.commit();

    BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
    bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
          case R.id.tab1:
            Toast.makeText(getApplicationContext(), "첫 번째 탭 선택됨", Toast.LENGTH_LONG).show();
            getSupportFragmentManager().beginTransaction()
              .replace(R.id.container, result_list).commit();

            return true;
          case R.id.tab2:
            Toast.makeText(getApplicationContext(), "두 번째 탭 선택됨", Toast.LENGTH_LONG).show();
            getSupportFragmentManager().beginTransaction()
              .replace(R.id.container, result_image).commit();

            return true;
        }

        return false;
      }
    });
  }
*/


/*public class ResultActivity extends AppCompatActivity {
  ViewPager pager;
  ResultImage result_image;
  ResultList result_list;
  String PrdDataJson;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    pager = findViewById(R.id.pager);
    pager.setOffscreenPageLimit(2);

    PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
    FragmentManager manager = getSupportFragmentManager();

    result_list = (ResultList) manager.findFragmentById(R.id.listFragment);
    adapter.addItem(result_list);

    Intent intent = getIntent();

    result_image = (ResultImage) manager.findFragmentById(R.id.imageFragment);
    Bitmap bitmap = BitmapFactory.decodeFile(intent.getExtras().getString("image"));
    if(result_image == null){
      Log.d("Null","result_image null");
    }
    //result_image.setImage(bitmap);
    adapter.addItem(result_image);

    PrdDataJson = intent.getExtras().getString("data");
    setPrdData();

    pager.setAdapter(adapter);
  }

  class PagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> items = new ArrayList<Fragment>();
    public PagerAdapter(FragmentManager fm) {
      super(fm);
    }

    public void addItem(Fragment item){
      items.add(item);
    }

    @Override
    public Fragment getItem(int position) {
      return items.get(position);
    }

    @Override
    public int getCount() {
      return items.size();
    }
  }

  public void setPrdData(){
    Gson gson = new Gson();
    Type searchResultType = new TypeToken<products>() {}.getType();
    products product_list = gson.fromJson(PrdDataJson, searchResultType);
    //Log.d("MovieData", MovieDataJson);
    if(product_list == null){
      Log.d("MovieData", "moviewList is null");
    }else if(product_list.ProductList == null){
      Log.d("MovieData", "boxOfficeResult is null");
    }
    for(int i = 0; i < product_list.ProductList.size(); i++){
      ProductInfo prd = product_list.ProductList.get(i);
      result_list.addData(new SearchResult(prd.search_word, prd.low_price, prd.score, prd.link));
    }
  }*/
  /*@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    Intent intent = getIntent();

    ImageView imageView = (ImageView)findViewById(R.id.imageView);
    Bitmap bitmap = BitmapFactory.decodeFile(intent.getExtras().getString("image"));
    imageView.setImageBitmap(bitmap);
  }*/


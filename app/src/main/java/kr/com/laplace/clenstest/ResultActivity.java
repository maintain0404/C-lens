package kr.com.laplace.clenstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class ResultActivity extends AppCompatActivity {
  RecyclerView recyclerView;
  ResultAdapter adapter;
  String imageUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_result);

    recyclerView = (RecyclerView) findViewById(R.id.recycler);


    LinearLayoutManager layoutManager =
      new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);

    adapter = new ResultAdapter();

    recyclerView.setAdapter(adapter);

    Intent intent = getIntent();
    imageUrl = intent.getExtras().getString("imageUrl");
    Log.d("imageUrl", imageUrl);

    NetworkAsyncTask load = new NetworkAsyncTask(this, imageUrl);
    load.execute();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), MainActivity.class);
    startActivity(intent);
  }
}

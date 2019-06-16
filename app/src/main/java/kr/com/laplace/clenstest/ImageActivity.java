package kr.com.laplace.clenstest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {
  String PrdDataJson;
  String imageUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image);

    Intent intent = getIntent();
    imageUrl = intent.getExtras().getString("image");
    PrdDataJson = intent.getExtras().getString("data");
    Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);

    ImageView imageview = (ImageView)findViewById(R.id.imageView);
    imageview.setImageBitmap(bitmap);

    Button change_to_list = findViewById(R.id.change_to_list);
    change_to_list.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        changeToList();
      }
    });
  }

  public void changeToList(){
    Log.d("imageUrlonImage",imageUrl);
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), ResultActivity.class);
    intent.putExtra("image", imageUrl);
    intent.putExtra("data", PrdDataJson);
    startActivity(intent);
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), MainActivity.class);
    startActivity(intent);
  }
}

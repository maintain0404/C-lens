package kr.com.laplace.clenstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    Thread time = new Thread();
    try{
      time.sleep(1000);
    }catch(InterruptedException e){

    }
    Intent intent = new Intent();
    intent.setClass(this, MainActivity.class);
    startActivity(intent);
  }
}

package kr.com.laplace.clenstest;

/*
 *  https://github.com/josnidhin/Android-Camera-Example에 있는 코드를 수정했습니다.
 */
//https://webnautes.tistory.com/82


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity
  implements ActivityCompat.OnRequestPermissionsResultCallback {

  private static final String TAG = "android_camera_example";
  private static final int PERMISSIONS_REQUEST_CODE = 100;
  String[] REQUIRED_PERMISSIONS  = {Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE};
  private static final int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK; // Camera.CameraInfo.CAMERA_FACING_FRONT

  private SurfaceView surfaceView;
  private CameraPreview mCameraPreview;
  private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
  // (참고로 Toast에서는 Context가 필요했습니다.)

  //이미지 경로 담아올 uri
  Uri imageUri;
  String imageUrl;

  ProgressDialog dialog;

  private int GET_GALLERY_IMAGE = 200;

  MainActivity main = this;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 상태바를 안보이도록 합니다.
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);

    // 화면 켜진 상태를 유지합니다.
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.activity_main);

    mLayout = findViewById(R.id.layout_main);
    surfaceView = findViewById(R.id.camera_preview_main);

    ProgressDialog dialog = new ProgressDialog(this);

    // 런타임 퍼미션 완료될때 까지 화면에서 보이지 않게 해야합니다.
    surfaceView.setVisibility(View.GONE);

    ImageButton capture = findViewById(R.id.button_main_capture);
    capture.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        String url = mCameraPreview.takePicture();
      }
    });

    ImageButton gallery = findViewById(R.id.button_main_gallery);
    gallery.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
      }
    });



    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

      int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
      int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


      if ( cameraPermission == PackageManager.PERMISSION_GRANTED
        && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
        startCamera();


      }else {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
          || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

          Snackbar.make(mLayout, "이 앱을 실행하려면 카메라와 외부 저장소 접근 권한이 필요합니다.",
            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

            @Override
            public void onClick(View view) {

              ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                PERMISSIONS_REQUEST_CODE);
            }
          }).show();


        } else {
          // 2. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
          // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
          ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
            PERMISSIONS_REQUEST_CODE);
        }

      }

    } else {

      final Snackbar snackbar = Snackbar.make(mLayout, "디바이스가 카메라를 지원하지 않습니다.",
        Snackbar.LENGTH_INDEFINITE);
      snackbar.setAction("확인", new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          snackbar.dismiss();
        }
      });
      snackbar.show();
    }
  }
  void startCamera(){
    // Create the Preview view and set it as the content of this Activity.
    mCameraPreview = new CameraPreview(this, this, CAMERA_FACING, surfaceView);

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

    if ( requestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

      boolean check_result = true;

      for (int result : grandResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          check_result = false;
          break;
        }
      }

      if ( check_result ) {

        startCamera();
      }
      else {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
          || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

          Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

            @Override
            public void onClick(View view) {

              finish();
            }
          }).show();

        }else {

          Snackbar.make(mLayout, "설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

            @Override
            public void onClick(View view) {

              finish();
            }
          }).show();
        }
      }
    }
  }

  public void getImageResult(String imageUrl){
    Intent intent = new Intent();
    intent.setClass(this, ResultActivity.class);
    intent.putExtra("imageUrl", imageUrl);
    startActivity(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

      Uri selectedImageUri = data.getData();
      imageUrl = getPath(selectedImageUri);
      Log.d("imageUrlformUri", imageUrl);
      Intent intent = new Intent();
      intent.setClass(this, ResultActivity.class);
      intent.putExtra("imageUrl", imageUrl);
      startActivity(intent);
    }
  }

  @Override
  public void onBackPressed() {
    moveTaskToBack(true);

    finish();

    android.os.Process.killProcess(android.os.Process.myPid());
  }

  //실제 갤러리에서 가져온 이미지 결로 리턴
  //https://trend21c.tistory.com/1468 참조
  public String getPath(Uri uri) {
    String[] projection = {MediaStore.Images.Media.DATA};
    Cursor cursor = managedQuery(uri, projection, null, null, null);
    startManagingCursor(cursor);
    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    cursor.moveToFirst();
    return cursor.getString(columnIndex);
  }
}



package kr.com.laplace.clenstest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import kr.com.laplace.clenstest.R;

public class ResultImage extends Fragment {
  View fragment_view;
  ImageView imageview;
  Bitmap image;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    fragment_view = inflater.inflate(R.layout.fragment_result_image, container, false);
    // Inflate the layout for this fragment
    return fragment_view;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    imageview = (ImageView) getView().findViewById(R.id.image_result);
    if (getArguments() != null){
      image = BitmapFactory.decodeFile(getArguments().getString("image"));
      imageview.setImageBitmap(image);
    }else{
      Log.e("ResultImage","Null bundle");
    }
  }


  /*public void createImage(Bitmap bitmap){
    ImageView imageview = new ImageView(getActivity());
    imageview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    imageview.setId(R.id.result_image_view);
    imageview.setImageBitmap(bitmap);
*/
  //xml로 정적 렌더링이 안 되서 만드는 동적 렌더링 함수
}

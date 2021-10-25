package kr.com.laplace.clenstest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder>{
  ArrayList<SearchResult> items = new ArrayList<SearchResult>();

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    View itemView = inflater.inflate(R.layout.result_item, viewGroup, false);

    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
    SearchResult item = items.get(position);
    viewHolder.setItem(item);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  public void addItem(SearchResult item){
    items.add(item);
  }

  public void setItems(ArrayList<SearchResult> items){
    this.items = items;
  }

  public SearchResult getItem(int position){
    return items.get(position);
  }

  public void setItem(int position, SearchResult item){
    items.set(position, item);
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    TextView textView1;
    TextView textView2;
    ImageView imageview;
    String url;

    public ViewHolder(View itemView) {
      super(itemView);

      textView1 = itemView.findViewById(R.id.name);
      textView2 = itemView.findViewById(R.id.low_price);
      imageview = itemView.findViewById(R.id.thumbnail);
    }

    public void setItem(SearchResult item) {
      textView1.setText(item.getCorrect_name());
      textView2.setText(item.getLow_price());
      imageview.setImageBitmap(item.getThumbnail());
      url = item.getLink();
      if (url != null) {
        imageview.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            v.getContext().startActivity(intent);
          }
        });
      }
    }
  }
}

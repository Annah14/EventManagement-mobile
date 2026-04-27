package com.example.annaheventsls;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setLayoutManager(new GridLayoutManager(this, 2));

        List<GalleryItem> items = new ArrayList<>();
        items.add(new GalleryItem("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=1200&q=80", "Wedding Design"));
        items.add(new GalleryItem("https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=1200&q=80", "Venue Styling"));
        items.add(new GalleryItem("https://images.unsplash.com/photo-1504805572947-34fad45aed93?auto=format&fit=crop&w=1200&q=80", "Gala Excellence"));
        items.add(new GalleryItem("https://images.unsplash.com/photo-1528605248644-14dd04022da1?auto=format&fit=crop&w=1200&q=80", "Social Events"));
        items.add(new GalleryItem("https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=1200&q=80", "Luxury Decor"));
        items.add(new GalleryItem("https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=1200&q=80", "Corporate Setup"));

        GalleryAdapter adapter = new GalleryAdapter(items);
        rvGallery.setAdapter(adapter);
    }

    private static class GalleryItem {
        String url;
        String caption;
        GalleryItem(String url, String caption) { this.url = url; this.caption = caption; }
    }

    private class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
        private List<GalleryItem> items;
        GalleryAdapter(List<GalleryItem> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GalleryItem item = items.get(position);
            holder.tvCaption.setText(item.caption);
            Glide.with(GalleryActivity.this).load(item.url).into(holder.ivImage);

            holder.itemView.setOnClickListener(v -> showFullImage(item));
        }

        private void showFullImage(GalleryItem item) {
            android.app.Dialog dialog = new android.app.Dialog(GalleryActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_full_image);
            
            ImageView ivFull = dialog.findViewById(R.id.ivFullImage);
            TextView tvFullCaption = dialog.findViewById(R.id.tvFullCaption);
            View btnClose = dialog.findViewById(R.id.btnFullClose);

            tvFullCaption.setText(item.caption);
            Glide.with(GalleryActivity.this).load(item.url).into(ivFull);

            btnClose.setOnClickListener(v2 -> dialog.dismiss());
            dialog.show();
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvCaption;
            ViewHolder(View v) {
                super(v);
                ivImage = v.findViewById(R.id.ivGalleryImage);
                tvCaption = v.findViewById(R.id.tvGalleryCaption);
            }
        }
    }
}

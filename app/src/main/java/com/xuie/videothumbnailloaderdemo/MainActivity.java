package com.xuie.videothumbnailloaderdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.xuie.videothumbnailloader.VideoThumbnailLoader;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private List<Media> medias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(recyclerView, "请同意存储查看权限", Snackbar.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x12);
            }
        } else {
            getMedias();
        }
    }

    private void getMedias() {
        medias = MediaSource.getInstance().getVideoList();
        Log.d(TAG, "medias.size():" + medias.size());
        recyclerView.setAdapter(new MyAdapter());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0x12) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMedias();
            } else {
                finish();
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            VideoThumbnailLoader.get().display(
                    medias.get(position).getPath(),
                    holder.image,
                    getWindowManager().getDefaultDisplay().getWidth(),
                    (int) dpToPx(144),
                    null);
            holder.title.setText(medias.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return medias != null ? medias.size() : 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;

            MyViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                title = itemView.findViewById(R.id.title);
            }
        }
    }

    public static float dpToPx(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

}

package com.songoku.songokuwallpaperpro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.songoku.songokuwallpaperpro.Utils.HtmlImageHome;
import com.songoku.songokuwallpaperpro.adapter.GridViewAdapter;
import com.songoku.songokuwallpaperpro.object.ObjectImage;

import java.util.ArrayList;
import java.util.List;

import static com.songoku.songokuwallpaperpro.Utils.CommonVL.OBJECT_IMAGE;
import static com.songoku.songokuwallpaperpro.Utils.CommonVL.WALL_PAPER_URL;

public class MainActivity extends AppCompatActivity {

    GridViewAdapter adapter;
    GridView gridView;
    List<ObjectImage> objectImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gridView);

        objectImageList = new ArrayList<>();
        adapter = new GridViewAdapter(MainActivity.this, R.layout.row_gridview, objectImageList);
        gridView.setAdapter(adapter);

        for (int i = 1; i <= 79; i++) {
            String fileName = null;
            if (i < 10) {
                fileName = "0" + i;
            } else {
                fileName = i + "";
            }
            objectImageList.add(new ObjectImage(fileName + ".jpg.tbn", fileName + ".jpg", false));
        }

        adapter.notifyDataSetChanged();

        new HtmlImageHome(new HtmlImageHome.ShareArrWallpaper() {
            @Override
            public void WallArrr(ArrayList<ObjectImage> arrImg) {
                objectImageList.addAll(arrImg);
                adapter.notifyDataSetChanged();
            }
        }, MainActivity.this).execute(WALL_PAPER_URL + "search.php?search=dragon+ball");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ObjectImage mObjectImage = (ObjectImage) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(MainActivity.this, PreviewPictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(OBJECT_IMAGE, mObjectImage);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}

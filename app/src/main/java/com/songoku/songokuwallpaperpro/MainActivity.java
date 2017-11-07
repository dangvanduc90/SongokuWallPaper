package com.songoku.songokuwallpaperpro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.songoku.songokuwallpaperpro.Utils.HtmlImageHome;
import com.songoku.songokuwallpaperpro.adapter.GridViewAdapter;
import com.songoku.songokuwallpaperpro.object.ObjectImage;

import java.util.ArrayList;
import java.util.List;

import static com.songoku.songokuwallpaperpro.Utils.CommonVL.MSG_NO;
import static com.songoku.songokuwallpaperpro.Utils.CommonVL.MSG_YES;
import static com.songoku.songokuwallpaperpro.Utils.CommonVL.OBJECT_IMAGE;
import static com.songoku.songokuwallpaperpro.Utils.CommonVL.WALL_PAPER_URL;

public class MainActivity extends AppCompatActivity {

    GridViewAdapter adapter;
    GridView gridView;
    List<ObjectImage> objectImageList;
    boolean isLoading = false;
    mHandle handle = new mHandle();
    LinearLayout loadMore;
    int page = 1;
    int lastPage = 54;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        loadMore = findViewById(R.id.loadMore);

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
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (i + i1 == i2 && !isLoading) {
                    if (page <= lastPage) {
                        isLoading = true;
                        loadMore.setVisibility(View.VISIBLE);
                        new HtmlImageHome(new HtmlImageHome.ShareArrWallpaper() {
                            @Override
                            public void WallArrr(ArrayList<ObjectImage> arrImg) {
                                objectImageList.addAll(arrImg);
                                adapter.notifyDataSetChanged();
                                isLoading = false;
                                loadMore.setVisibility(View.INVISIBLE);
                            }
                        }, MainActivity.this).execute(WALL_PAPER_URL + "search.php?search=dragon+ball&page=" + page);
                        page++;
//                    Thread thread = new ThreadData();
//                    thread.start();
                    } else {
                        Toast.makeText(MainActivity.this, "It's over", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public class mHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NO:
                    loadMore.setVisibility(View.VISIBLE);
                    isLoading = true;
                    break;
                case MSG_YES:
                    adapter.AddListItemAdapter((List<ObjectImage>) msg.obj);
                    adapter.notifyDataSetChanged();
                    isLoading = false;
                    break;
            }
        }
    }

    public class ThreadData extends Thread {
        @Override
        public void run() {
            handle.sendEmptyMessage(MSG_NO);
            List<ObjectImage> dataList;
            dataList = getMoreDataList();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (dataList.size() > 0) {
                Message message = handle.obtainMessage(MSG_YES, dataList);
                handle.sendMessage(message);
            } else {
                Message message = handle.obtainMessage(MSG_NO, dataList);
                handle.sendMessage(message);
            }
        }
    }

    public List<ObjectImage> getMoreDataList() {
        final List<ObjectImage> list = new ArrayList<>();

        if (page <= lastPage) {
            new HtmlImageHome(new HtmlImageHome.ShareArrWallpaper() {
                @Override
                public void WallArrr(ArrayList<ObjectImage> arrImg) {
                    list.addAll(arrImg);
                    Log.d("size1", 1 +"");
                    adapter.notifyDataSetChanged();
                }
            }, MainActivity.this).execute(WALL_PAPER_URL + "search.php?search=dragon+ball&page=" + page);
            page++;
        }
        Log.d("size1", 2 +"");
        return list;
    }
}

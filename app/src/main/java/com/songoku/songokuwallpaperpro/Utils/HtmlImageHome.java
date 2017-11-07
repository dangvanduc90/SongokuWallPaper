package com.songoku.songokuwallpaperpro.Utils;

import android.content.Context;
import android.os.AsyncTask;

import com.songoku.songokuwallpaperpro.object.ObjectImage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class HtmlImageHome extends AsyncTask<String, Void, ArrayList<ObjectImage>> {

    private ArrayList<ObjectImage> arrWall;
    private ShareArrWallpaper mShareArrSams;
    private Context mContext;

    public HtmlImageHome(ShareArrWallpaper mShareArrSams, Context mContext) {
        this.mShareArrSams = mShareArrSams;
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        arrWall = new ArrayList<>();
    }

    @Override
    protected ArrayList<ObjectImage> doInBackground(String... params) {
        String link = params[0];
        try {
            Document doc = Jsoup.connect(link).get();
            Elements elements = doc.select("div.thumb-container div.boxgrid");
            for (int i = 0; i < elements.size(); i++) {
                Elements titleImg = ((Element) elements.get(i)).select("a");
                String linkimg = titleImg.select("img").attr("src");
                String linkdownload = titleImg.attr("href");
//                Log.e("linkimg", linkimg + "===" + linkdownload);
                arrWall.add(new ObjectImage(linkimg, linkdownload, true));
            }
            return arrWall;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<ObjectImage> itemSamses) {
        super.onPostExecute(itemSamses);
        if (itemSamses != null) {
            mShareArrSams.WallArrr(itemSamses);
        }
    }

    public interface ShareArrWallpaper{
        void WallArrr(ArrayList<ObjectImage> arrImg);
    }

}

package com.songoku.songokuwallpaperpro.Utils;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseImageHome extends AsyncTask<String, Void, String> {

    private Context mContext;
    private LoadImage mImage;

    public ParseImageHome(Context mContext, LoadImage mImage) {
        this.mContext = mContext;
        this.mImage = mImage;
    }

    @Override
    protected String doInBackground(String... strings) {
        String linkImg = null;
        try {
            Elements elements = Jsoup.connect(strings[0]).get().select("div.center");
            for (int i = 0; i < elements.size(); i++) {
                Element e = elements.get(i);
                Elements img = e.select("img");
                linkImg = img.attr("src");
                if (!linkImg.equals("")) {
                    return linkImg;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mImage.loadDataImg(s);
    }

    public interface LoadImage{
        void loadDataImg(String httpImg);
    }
}

package com.coddect.whyareyouhere.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coddect.whyareyouhere.R;
import com.coddect.whyareyouhere.animation.HeightAnimation;
import com.coddect.whyareyouhere.recycler.ItemListElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends Activity
{
    Task task;

    @BindView(R.id.detail_item_name_viewer) TextView itemNameViewer;
    @BindView(R.id.detail_item_id_viewer) TextView itemIdViewer;
    @BindView(R.id.detail_where_lost_viewer) TextView whereLostViewer;
    @BindView(R.id.detail_when_lost_viewer) TextView whenLostViewer;
    @BindView(R.id.detail_where_take_viewer) TextView whereTakeViewer;

    @BindView(R.id.detail_item_image_viewer_layout) RelativeLayout itemImageViewerLayout;
    @BindView(R.id.detail_item_image_viewer) ImageView itemImageViewer;

    String baseUrl;

    Bitmap itemImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setFlag();
        setUI();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (task != null)
            task.cancel(true);
    }

    public void setFlag()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void setUI()
    {
        Intent intent = getIntent();

        itemNameViewer.setText("물품 이름 : " + intent.getStringExtra("itemName"));
        itemIdViewer.setText("물품 ID : " + intent.getStringExtra("itemId"));
        whereLostViewer.setText(intent.getStringExtra("whereLost"));
        whenLostViewer.setText(intent.getStringExtra("whenLost"));
        whereTakeViewer.setText(intent.getStringExtra("whereTake"));

        baseUrl = "http://openapi.seoul.go.kr:8088/52564b777879796d383372636c6e73/xml/SearchLostArticleImageService/1/5/" + intent.getStringExtra("itemId");

        task = new Task();
        task.execute();
    }
    public void getImageS(Document document)
    {
        NodeList nodeList = document.getElementsByTagName("row");

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);
            Element fstElmnt = (Element) node;
            NodeList nameList  = fstElmnt.getElementsByTagName("IMAGE_URL");
            Element nameElement = (Element) nameList.item(0);
            nameList = nameElement.getChildNodes();

            try
            {
                String urlString = nameList.item(0).getNodeValue();

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();

                itemImage = BitmapFactory.decodeStream(is);
            }
            catch(NullPointerException ignored)
            {

            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public void getImage() throws IOException, XmlPullParserException
    {
        URL url = new URL(baseUrl);
        InputStream is = url.openStream();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new InputStreamReader(is, "UTF-8"));

        int eventType = parser.getEventType();

        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                case XmlPullParser.START_TAG:

                    String startTag = parser.getName();

                    if (startTag.equals("IMAGE_URL"))
                    {
                        URL imageUrl = new URL(parser.nextText());
                        HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is2 = conn.getInputStream();

                        itemImage = BitmapFactory.decodeStream(is2);
                    }

                    break;

                case XmlPullParser.END_TAG:

                    break;
            }

            eventType = parser.next();
        }
    }

    private class Task extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                getImage();
            }
            catch (IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {

                    }
                });
            }
            catch (XmlPullParserException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(DetailActivity.this, "불러오는 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Void)
        {
            if (itemImage == null)
            {
                HeightAnimation animation = new HeightAnimation(itemImageViewerLayout, 60, 0);
                animation.setInterpolator(new FastOutSlowInInterpolator());
                animation.setDuration(400);
                itemImageViewerLayout.startAnimation(animation);
            }
            else
            {
                itemImageViewer.setImageBitmap(itemImage);

                HeightAnimation animation = new HeightAnimation(itemImageViewerLayout, 60, 250);
                animation.setInterpolator(new FastOutSlowInInterpolator());
                animation.setDuration(450);
                itemImageViewerLayout.startAnimation(animation);

                itemImageViewer.animate()
                        .alpha(1f)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(450)
                        .setStartDelay(0)
                        .withLayer()
                        .withEndAction(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                itemImageViewer.setAlpha(1f);
                            }
                        });
            }
        }
    }
}

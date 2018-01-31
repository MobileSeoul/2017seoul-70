package com.coddect.whyareyouhere.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.coddect.whyareyouhere.R;
import com.coddect.whyareyouhere.animation.HeightAnimation;
import com.coddect.whyareyouhere.recycler.ItemListAdapter;
import com.coddect.whyareyouhere.recycler.ItemListClickListener;
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultActivity extends Activity
{
    ItemListAdapter adapter;
    ResultActivity.Task task;

    @BindView(R.id.result_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.result_guide_view) LinearLayout guideView;
    @BindView(R.id.result_load_view) LinearLayout loadView;

    @BindView(R.id.result_selected_location_viewer) TextView selectedLocationViewer;
    @BindView(R.id.result_selected_item_viewer) TextView selectedItemViewer;

    @BindView(R.id.result_search_menu_toggle) ImageButton searchMenuToggle;
    @BindView(R.id.result_search_menu) LinearLayout searchMenu;

    @BindView(R.id.location_bus) Button busButton;
    @BindView(R.id.location_subway) Button subwayButton;
    @BindView(R.id.location_taxi) Button taxiButton;

    @BindView(R.id.item_phone) Button phoneButton;
    @BindView(R.id.item_wallet) Button walletButton;
    @BindView(R.id.item_shoppingbag) Button shoppingbagButton;
    @BindView(R.id.item_bag) Button bagButton;
    @BindView(R.id.item_backpack) Button backpackButton;
    @BindView(R.id.item_document) Button documentButton;
    @BindView(R.id.item_clothes) Button clothesButton;
    @BindView(R.id.item_book) Button bookButton;
    @BindView(R.id.item_file) Button fileButton;
    @BindView(R.id.item_etc) Button etcButton;

    String selectedLocationShortened, selectedLocation, selectedItem;
    String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ButterKnife.bind(this);

        setFlag();
        set();
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
    public void set()
    {
        selectedLocationShortened = "";
        selectedLocation = "";
        selectedItem = "";
    }

    public void openMenu(View v)
    {
        if (searchMenu.getAlpha() == 1f)
        {
            HeightAnimation animation = new HeightAnimation(searchMenu, 220, 1);
            animation.setInterpolator(new FastOutSlowInInterpolator());
            animation.setDuration(450);
            searchMenu.startAnimation(animation);

            searchMenu.animate()
                    .alpha(0f)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(450)
                    .setStartDelay(0)
                    .withLayer()
                    .withEndAction(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            searchMenu.setAlpha(0f);
                            searchMenuToggle.setImageResource(R.drawable.up);
                        }
                    });
        }
        else
        {
            HeightAnimation animation = new HeightAnimation(searchMenu, 1, 220);
            animation.setInterpolator(new FastOutSlowInInterpolator());
            animation.setDuration(450);
            searchMenu.startAnimation(animation);

            searchMenu.animate()
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
                            searchMenu.setAlpha(1f);
                            searchMenuToggle.setImageResource(R.drawable.down);
                        }
                    });
        }
    }
    public void locationBus(View v)
    {
        PopupMenu menu = new PopupMenu(this, v);
        menu.getMenu().add("일반버스");
        menu.getMenu().add("마을버스");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                if (menuItem.getTitle().toString().equals("일반버스"))
                {
                    busButton.setSelected(true);
                    subwayButton.setSelected(false);
                    taxiButton.setSelected(false);

                    selectedLocation = menuItem.getTitle().toString();
                    selectedLocationShortened = "b1";

                    search();
                }
                else if (menuItem.getTitle().toString().equals("마을버스"))
                {
                    busButton.setSelected(true);
                    subwayButton.setSelected(false);
                    taxiButton.setSelected(false);

                    selectedLocation = menuItem.getTitle().toString();
                    selectedLocationShortened = "b2";

                    search();
                }
                return false;
            }
        });
        menu.show();
    }
    public void locationSubway(View v)
    {
        PopupMenu menu = new PopupMenu(this, v);
        menu.getMenu().add("1~4호선");
        menu.getMenu().add("5~8호선");
        menu.getMenu().add("9호선");
        menu.getMenu().add("코레일");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                if (menuItem.getTitle().toString().equals("1~4호선"))
                {
                    busButton.setSelected(false);
                    subwayButton.setSelected(true);
                    taxiButton.setSelected(false);

                    selectedLocation = menuItem.getTitle().toString();
                    selectedLocationShortened = "s1";

                    search();
                }
                else if (menuItem.getTitle().toString().equals("5~8호선"))
                {
                    busButton.setSelected(false);
                    subwayButton.setSelected(true);
                    taxiButton.setSelected(false);

                    selectedLocation = menuItem.getTitle().toString();
                    selectedLocationShortened = "s2";

                    search();
                }
                else if (menuItem.getTitle().toString().equals("9호선"))
                {
                    busButton.setSelected(false);
                    subwayButton.setSelected(true);
                    taxiButton.setSelected(false);

                    selectedLocation = menuItem.getTitle().toString();
                    selectedLocationShortened = "s4";

                    search();
                }
                else if (menuItem.getTitle().toString().equals("코레일"))
                {
                    busButton.setSelected(false);
                    subwayButton.setSelected(true);
                    taxiButton.setSelected(false);

                    selectedLocation = menuItem.getTitle().toString();
                    selectedLocationShortened = "s3";

                    search();
                }
                return false;
            }
        });
        menu.show();
    }
    public void locationTaxi(View v)
    {
        PopupMenu menu = new PopupMenu(this, v);
        menu.getMenu().add("법인택시");
        menu.getMenu().add("개인택시");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                if (menuItem.getTitle().toString().equals("법인택시"))
                {
                    busButton.setSelected(false);
                    subwayButton.setSelected(false);
                    taxiButton.setSelected(true);

                    selectedLocation = menuItem.getTitle().toString();
                    selectedLocationShortened = "t1";

                    search();
                }
                else if (menuItem.getTitle().toString().equals("개인택시"))
                {
                    busButton.setSelected(false);
                    subwayButton.setSelected(false);
                    taxiButton.setSelected(true);

                    selectedLocation = menuItem.getTitle().toString();
                    selectedLocationShortened = "t2";

                    search();
                }
                return false;
            }
        });
        menu.show();
    }
    public void itemPhone(View v)
    {
        phoneButton.setSelected(true);
        walletButton.setSelected(false);
        shoppingbagButton.setSelected(false);
        bagButton.setSelected(false);
        backpackButton.setSelected(false);
        documentButton.setSelected(false);
        clothesButton.setSelected(false);
        bookButton.setSelected(false);
        fileButton.setSelected(false);
        etcButton.setSelected(false);

        selectedItem = ((Button)v).getText().toString();

        search();
    }
    public void itemWallet(View v)
    {
        phoneButton.setSelected(false);
        walletButton.setSelected(true);
        shoppingbagButton.setSelected(false);
        bagButton.setSelected(false);
        backpackButton.setSelected(false);
        documentButton.setSelected(false);
        clothesButton.setSelected(false);
        bookButton.setSelected(false);
        fileButton.setSelected(false);
        etcButton.setSelected(false);

        selectedItem = ((Button)v).getText().toString();

        search();
    }
    public void itemShoppingbag(View v)
    {
        phoneButton.setSelected(false);
        walletButton.setSelected(false);
        shoppingbagButton.setSelected(true);
        bagButton.setSelected(false);
        backpackButton.setSelected(false);
        documentButton.setSelected(false);
        clothesButton.setSelected(false);
        bookButton.setSelected(false);
        fileButton.setSelected(false);
        etcButton.setSelected(false);

        selectedItem = ((Button)v).getText().toString();

        search();
    }

    public void itemBag(View v)
    {
        phoneButton.setSelected(false);
        walletButton.setSelected(false);
        shoppingbagButton.setSelected(false);
        bagButton.setSelected(true);
        backpackButton.setSelected(false);
        documentButton.setSelected(false);
        clothesButton.setSelected(false);
        bookButton.setSelected(false);
        fileButton.setSelected(false);
        etcButton.setSelected(false);

        selectedItem = ((Button)v).getText().toString();

        search();
    }
    public void itemBackpack(View v)
    {
        phoneButton.setSelected(false);
        walletButton.setSelected(false);
        shoppingbagButton.setSelected(false);
        bagButton.setSelected(false);
        backpackButton.setSelected(true);
        documentButton.setSelected(false);
        clothesButton.setSelected(false);
        bookButton.setSelected(false);
        fileButton.setSelected(false);
        etcButton.setSelected(false);

        selectedItem = ((Button)v).getText().toString();

        search();
    }
    public void itemDocument(View v)
    {
        phoneButton.setSelected(false);
        walletButton.setSelected(false);
        shoppingbagButton.setSelected(false);
        bagButton.setSelected(false);
        backpackButton.setSelected(false);
        documentButton.setSelected(true);
        clothesButton.setSelected(false);
        bookButton.setSelected(false);
        fileButton.setSelected(false);
        etcButton.setSelected(false);

        selectedItem = ((Button)v).getText().toString();

        search();
    }
    public void itemClothes(View v)
    {
        phoneButton.setSelected(false);
        walletButton.setSelected(false);
        shoppingbagButton.setSelected(false);
        bagButton.setSelected(false);
        backpackButton.setSelected(false);
        documentButton.setSelected(false);
        clothesButton.setSelected(true);
        bookButton.setSelected(false);
        fileButton.setSelected(false);
        etcButton.setSelected(false);

        selectedItem = ((Button)v).getText().toString();

        search();
    }
    public void itemBook(View v)
    {
        phoneButton.setSelected(false);
        walletButton.setSelected(false);
        shoppingbagButton.setSelected(false);
        bagButton.setSelected(false);
        backpackButton.setSelected(false);
        documentButton.setSelected(false);
        clothesButton.setSelected(false);
        bookButton.setSelected(true);
        fileButton.setSelected(false);
        etcButton.setSelected(false);

        selectedItem = ((Button)v).getText().toString();

        search();
    }
    public void itemFile(View v)
    {
        phoneButton.setSelected(false);
        walletButton.setSelected(false);
        shoppingbagButton.setSelected(false);
        bagButton.setSelected(false);
        backpackButton.setSelected(false);
        documentButton.setSelected(false);
        clothesButton.setSelected(false);
        bookButton.setSelected(false);
        fileButton.setSelected(true);
        etcButton.setSelected(false);

        selectedItem = ((Button)v).getText().toString();

        search();
    }
    public void itemEtc(View v)
    {
        phoneButton.setSelected(false);
        walletButton.setSelected(false);
        shoppingbagButton.setSelected(false);
        bagButton.setSelected(false);
        backpackButton.setSelected(false);
        documentButton.setSelected(false);
        clothesButton.setSelected(false);
        bookButton.setSelected(false);
        fileButton.setSelected(false);
        etcButton.setSelected(true);

        selectedItem = ((Button)v).getText().toString();

        search();
    }

    public void search()
    {
        updateUI(selectedLocation, selectedItem);

        if (selectedItem.length() != 0 && selectedLocation.length() != 0 && selectedLocationShortened.length() != 0)
        {
            baseUrl =  "http://openapi.seoul.go.kr:8088/464d62696b79796d35316b71567a4f/xml/SearchLostArticleService/1/1000/" + selectedItem + "/" + selectedLocationShortened + "/";

            setList();
        }
    }
    public void updateUI(String selectedLocation, String selectedItem)
    {
        //장소
        if (selectedLocation.length() != 0)
            selectedLocationViewer.setText(selectedLocation);

        else
            selectedLocationViewer.setText("선택되지 않음");

        //물품
        if (selectedItem.length() != 0)
            selectedItemViewer.setText(selectedItem);

        else
            selectedItemViewer.setText("선택되지 않음");
    }
    public void setList()
    {
        task = new ResultActivity.Task();
        task.execute();

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addOnItemTouchListener(new ItemListClickListener(this, new ItemListClickListener.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(View view, final int position)
                    {
                        ItemListElement element = adapter.itemList.get(position);

                        Intent intent = new Intent(ResultActivity.this, DetailActivity.class);
                        intent.putExtra("itemName", element.itemName);
                        intent.putExtra("itemId", element.itemId);
                        intent.putExtra("whereLost", element.whereLost);
                        intent.putExtra("whenLost", element.whenLost);
                        intent.putExtra("whereTake", element.whereTake);
                        startActivity(intent);
                    }
                })
        );
    }

    public List<ItemListElement> addToList() throws IOException, XmlPullParserException
    {
        List<ItemListElement> result = new ArrayList<ItemListElement>();

        URL url = new URL(baseUrl);
        InputStream is = url.openStream();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new InputStreamReader(is, "UTF-8"));

        int eventType = parser.getEventType();

        ItemListElement listElement = null;

        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            switch (eventType)
            {
                case XmlPullParser.START_TAG:

                    String startTag = parser.getName();

                    if (startTag.equals("row"))
                        listElement = new ItemListElement();

                    if (startTag.equals("GET_NAME"))
                        listElement.itemName = parser.nextText();

                    if (startTag.equals("ID"))
                        listElement.itemId = parser.nextText();

                    if (startTag.equals("GET_POSITION"))
                        listElement.whereLost = "습득 장소 : " + parser.nextText();

                    if (startTag.equals("GET_DATE"))
                        listElement.whenLost = "습득 날짜 : " + parser.nextText();

                    if (startTag.equals("TAKE_PLACE"))
                        listElement.whereTake = "수령 장소 : " + parser.nextText();

                    break;

                case XmlPullParser.END_TAG:

                    String endTag = parser.getName();

                    if (endTag.equals("row"))
                        result.add(listElement);

                    break;
            }

            eventType = parser.next();
        }
        return result;
    }

    private class Task extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            guideView.setVisibility(View.GONE);
            loadView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                adapter = new ItemListAdapter(addToList());
            }
            catch (IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(ResultActivity.this, "불러오는 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(ResultActivity.this, "불러오는 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            recyclerView.setAdapter(adapter);

            guideView.setVisibility(View.GONE);
            loadView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}

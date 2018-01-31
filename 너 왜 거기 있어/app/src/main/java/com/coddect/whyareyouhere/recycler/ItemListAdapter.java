package com.coddect.whyareyouhere.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coddect.whyareyouhere.R;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>
{
    public List<ItemListElement> itemList;

    public ItemListAdapter(List<ItemListElement> itemList)
    {
        this.itemList = itemList;
    }
    @Override
    public int getItemCount()
    {
        return itemList.size();
    }
    @Override
    public void onBindViewHolder(final ItemListViewHolder itemListViewHolder, int i)
    {
        ItemListElement element = itemList.get(i);
        itemListViewHolder.itemNameViewer.setText(element.itemName);
        itemListViewHolder.itemIdViewer.setText(element.itemId);
        itemListViewHolder.whereLostViewer.setText(element.whereLost);
        itemListViewHolder.whereTakeViewer.setText(element.whereTake);
        itemListViewHolder.whenLostViewer.setText(element.whenLost);
    }
    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_layout, viewGroup, false);

        return new ItemListViewHolder(itemView);
    }
    public static class ItemListViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView itemNameViewer, itemIdViewer, whereLostViewer, whereTakeViewer, whenLostViewer;

        public ItemListViewHolder(View v)
        {
            super(v);

            itemNameViewer =  (TextView)v.findViewById(R.id.list_item_name_viewer);
            itemIdViewer = (TextView)v.findViewById(R.id.list_item_id_viewer);
            whereLostViewer =  (TextView)v.findViewById(R.id.list_where_lost_viewer);
            whereTakeViewer = (TextView)v.findViewById(R.id.list_where_take_viewer);
            whenLostViewer =  (TextView)v.findViewById(R.id.list_when_lost_viewer);
        }
    }
}

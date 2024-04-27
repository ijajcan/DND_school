package com.example.dndschool;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class WifiListAdapter extends android.widget.BaseAdapter {
    Context context;
    ArrayList<String> wifiList;
    LayoutInflater inflater;
    public WifiListAdapter(Context context, ArrayList<String> wifiList) {
        this.context = context;
        this.wifiList = wifiList;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.wifi_list_item, null);
        TextView wifiName = convertView.findViewById(R.id.name);
        ImageButton buttonRemove = convertView.findViewById(R.id.remove);

        wifiName.setText(wifiList.get(position));
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiList.remove(position);
                notifyDataSetChanged();
                updateService();
            }
        });
        return convertView;
    }
    private void updateService() {
        // Update the wifiList in the NetworkMonitorService
        Intent updateIntent = new Intent(context, NetworkMonitorService.class);
        updateIntent.setAction(NetworkMonitorService.ACTION_UPDATE_WIFI_LIST);
        updateIntent.putStringArrayListExtra("wifiList", wifiList);
        context.startService(updateIntent);
    }
}

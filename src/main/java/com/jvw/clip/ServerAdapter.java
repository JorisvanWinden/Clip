package com.jvw.clip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jvw.sqlitehelper.lib.DatabaseManager;
import com.jvw.sqlitehelper.lib.Table;

/**
 * Created by Joris on 22-4-14.
 */
public class ServerAdapter extends BaseAdapter {

    private Context context;
    private Table<Server> data;

    public ServerAdapter(Context context) {
        this.context = context;
        this.data = DatabaseManager.getDatabase(context, "servers.db").getTable("servers", Server.class);
    }

    public void add(Server server) {
        data.add(server);
        notifyDataSetChanged();
    }

    public void remove(Server server) {
        data.remove(server);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.getCount();
    }

    @Override
    public Server getItem(int position) {
        return data.getAll().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        // may produce some errors
        // UPDATE it did
        // no errors when null is passed in as parent ghehehe
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.manage_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.manage_name_textview);
            holder.ip_port = (TextView) v.findViewById(R.id.manage_ip_port_textview);
            holder.remove = (ImageButton) v.findViewById(R.id.manage_remove_button);
            v.setTag(holder);
        }
        final Server server = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();

        holder.name.setText(server.getName());
        holder.ip_port.setText(server.getIp() + ":" + server.getPort());
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(server);
            }
        });
        return v;
    }

    private static class ViewHolder {
        public TextView name;
        public TextView ip_port;
        public ImageButton remove;
    }
}

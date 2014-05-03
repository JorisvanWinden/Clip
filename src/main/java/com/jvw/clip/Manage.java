package com.jvw.clip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class Manage extends ActionBarActivity {

    private ServerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ServerAdapter(this);
        ListView list = (ListView) findViewById(R.id.manage_list_view);
        list.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_dest, null);
                final EditText ipEdit = (EditText) v.findViewById(R.id.add_dest_ip_edittext);
                final EditText nameEdit = (EditText) v.findViewById(R.id.add_dest_name_edittext);
                final EditText portEdit = (EditText) v.findViewById(R.id.add_dest_port_edittext);
                builder.setTitle("Add ip address");
                builder.setView(v);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int port = Integer.parseInt(portEdit.getText().toString());
                            String name = nameEdit.getText().toString();
                            String ip = ipEdit.getText().toString();
                            if (ip.equals("") || name.equals("")) {
                                throw new IllegalArgumentException();
                            }
                            adapter.add(new Server(name, ip, port));
                        } catch (Exception e) {
                            Toast.makeText(Manage.this, "You didn't fill in all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

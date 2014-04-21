package com.jvw.clip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class Manage extends ActionBarActivity {

	ServerDataBase data;
	private ViewGroup layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_manage);

		layout = (ViewGroup) findViewById(R.id.manage_layout);
		data = new ServerDataBase(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		for (Server server : data.getAll()) {
			addView(server.getName(), server.getIp(), server.getPort());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.manage, menu);
		return true;
	}

	private void addItem(String name, String ip, int port) {
		addView(name, ip, port);
		data.addServer(new Server(name, ip, port));
	}

	private void addView(final String name, final String ip, final int port) {
		final ViewGroup view = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.manage_item, layout, false);
		((TextView) view.findViewById(R.id.manage_name_textview)).setText(name);
		((TextView) view.findViewById(R.id.manage_ip_port_textview)).setText(ip + ":" + port);
		view.findViewById(R.id.manage_remove_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = layout.indexOfChild(view);
				layout.removeViewAt(position);
				data.removeServer(name, ip, port);
			}
		});
		layout.addView(view, 0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.manage_add:
				final View v = getLayoutInflater().inflate(R.layout.dialog_add_dest, null);
				final EditText ipEdit = (EditText) v.findViewById(R.id.add_dest_ip_edittext);
				final EditText nameEdit = (EditText) v.findViewById(R.id.add_dest_name_edittext);
				final EditText portEdit = (EditText) v.findViewById(R.id.add_dest_port_edittext);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
						String name = nameEdit.getText().toString();
						String ip = ipEdit.getText().toString();
						int port = 60607;
						try {
							port = Integer.parseInt(portEdit.getText().toString());
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						addItem(name, ip, port);
					}
				});
				builder.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}

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

	private ViewGroup layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage);

		layout = (ViewGroup) findViewById(R.id.manage_layout);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.manage, menu);
		return true;
	}

	private void addItem(String name) {
		final ViewGroup view = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.manage_item, layout, false);
		((TextView) view.findViewById(R.id.manage_name_textview)).setText(name);
		view.findViewById(R.id.manage_delete_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layout.removeView(view);
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
						int port = Integer.parseInt(portEdit.getText().toString());
						addItem(name);
					}
				});
				builder.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}

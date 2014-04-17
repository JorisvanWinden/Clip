package com.jvw.clip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Main extends ActionBarActivity implements View.OnClickListener {

	private ClipboardManager clipBoard;
	private ArrayAdapter<String> spinnerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    Button send = (Button) findViewById(R.id.clip_send_button);
	    Spinner dest = (Spinner) findViewById(R.id.clip_dest_spinner);
	    spinnerData = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	    clipBoard = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);

	    dest.setAdapter(spinnerData);
	    spinnerData.add("192.168.1.39");
	    send.setOnClickListener(this);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clip_add_menu:
				final View v = getLayoutInflater().inflate(R.layout.dialog_add_dest, null);
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
						EditText edit = (EditText) v.findViewById(R.id.add_dest_input_edittext);
						spinnerData.add(edit.getText().toString());
					}
				});
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		new SendClipboardTask().execute();
	}

	private class SendClipboardTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (clipBoard.hasPrimaryClip()) {
					if (clipBoard.getPrimaryClip().getItemCount() > 0) {
						ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
						String s = item.getText().toString();
						send(s);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private void send(String s) throws IOException {
			Socket socket = new Socket("192.168.1.39", 60607);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(s);
			out.flush();
			out.close();
		}
	}
}

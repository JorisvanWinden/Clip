package com.jvw.clip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class Main extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

	private ClipboardManager clipBoard;
	private ArrayAdapter<DestinationListItem> spinnerData;
	private Spinner spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button send = (Button) findViewById(R.id.clip_send_button);
		spinner = (Spinner) findViewById(R.id.clip_dest_spinner);
		spinnerData = new ArrayAdapter<DestinationListItem>(this, R.layout.activity_main_spinner);
		clipBoard = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);

		spinner.setAdapter(spinnerData);
		spinner.setOnItemSelectedListener(this);
		spinnerData.add(new DestinationListItem("Pc Joris", "192.168.1.39"));
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
				final EditText edit = (EditText) v.findViewById(R.id.add_dest_input_edittext);
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
						spinnerData.add(new DestinationListItem("Dinges1", edit.getText().toString()));
					}
				});
				builder.show();
				return true;
			case R.id.clip_test_connection:
				Intent intent = new Intent(this, Test.class);
				List<String> dataList = new ArrayList<String>();
				for (int i = 0; i < spinnerData.getCount(); i++) {
					dataList.add(spinnerData.getItem(i).getIp());
				}
				String[] data = dataList.toArray(new String[dataList.size()]);
				intent.putExtra("ClipServers", data);
				startActivity(intent);


			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		new SendClipboardTask(this).execute();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private class SendClipboardTask extends AsyncTask<Void, Void, Boolean> {

		private Activity activity;
		private String dest;

		public SendClipboardTask(Activity a) {
			this.activity = a;
			this.dest = spinnerData.getItem(spinner.getSelectedItemPosition()).getIp();
			Toast.makeText(activity, "Sending to " + dest, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean response = false;
			try {
				if (clipBoard.hasPrimaryClip() && clipBoard.getPrimaryClip() != null) {
					if (clipBoard.getPrimaryClip().getItemCount() > 0) {
						ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
						String s = "";
						if (item.getText() != null) {
							s = item.getText().toString();
						}

						response = send(s);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			if (aBoolean) {
				Toast.makeText(activity, "Clipboard sent!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(activity, "Unable to connect to " + dest, Toast.LENGTH_SHORT).show();
			}
		}

		private boolean send(String s) throws IOException {
			boolean result = false;
			try {
				Socket socket = new Socket(dest, 60607);
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				DataInputStream in = new DataInputStream(socket.getInputStream());
				out.writeUTF(s);
				out.flush();
				result = in.readBoolean();
				out.close();
				in.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return result;
		}

	}
}

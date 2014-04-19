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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;


public class Main extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

	public static final int UNABLE_TO_CONNECT = 0;
	public static final int CLIPBOARD_EMPTY = 1;
	public static final int cLIPBOARD_SENT = 2;
	private ClipboardManager clipBoard;
	private ArrayAdapter<DestinationListItem> spinnerData;
	private Spinner spinner;
	private RelativeLayout infoLayout;
	private TextView nameInfo;
	private TextView ipInfo;
	private TextView portInfo;

	public static boolean send(String dest, int port, int timeout, String msg) {
		boolean result = false;
		try {
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(dest, port));
			Thread.sleep(timeout);
			if (!channel.finishConnect()) return false;
			channel.configureBlocking(true);
			Socket socket = channel.socket();
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			result = in.readBoolean();
			out.writeUTF(msg);
			in.close();
			out.close();
			socket.close();
			channel.close();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button send = (Button) findViewById(R.id.clip_send_button);
		Button test = (Button) findViewById(R.id.clip_info_test_button);
		spinner = (Spinner) findViewById(R.id.clip_dest_spinner);
		infoLayout = (RelativeLayout) findViewById(R.id.clip_info_layout);
		nameInfo = (TextView) findViewById(R.id.clip_info_name_textview);
		ipInfo = (TextView) findViewById(R.id.clip_info_ip_textview);
		portInfo = (TextView) findViewById(R.id.clip_info_port_textview);

		spinnerData = new ArrayAdapter<DestinationListItem>(this, R.layout.activity_main_spinner);
		clipBoard = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);
		spinner.setAdapter(spinnerData);
		spinner.setOnItemSelectedListener(this);
		spinnerData.add(new DestinationListItem("Pc Joris", "192.168.1.39"));
		send.setOnClickListener(this);
		test.setOnClickListener(this);
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
				final EditText ipEdit = (EditText) v.findViewById(R.id.add_dest_ip_edittext);
				final EditText nameEdit = (EditText) v.findViewById(R.id.add_dest_name_edittext);
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
						spinnerData.add(new DestinationListItem(nameEdit.getText().toString(), ipEdit.getText().toString()));
					}
				});
				builder.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.clip_send_button:
				new SendClipboardTask(this).execute();
				break;
			case R.id.clip_info_test_button:
				new TestDestinationTask(this).execute(spinnerData.getItem(spinner.getSelectedItemPosition()).getIp());
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		DestinationListItem item = spinnerData.getItem(position);
		nameInfo.setText("Name: " + item.getName());
		ipInfo.setText("IP: " + item.getIp());
		portInfo.setText("Port: " + "60607");
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		infoLayout.setVisibility(View.INVISIBLE);
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
			if (clipBoard.hasPrimaryClip() && clipBoard.getPrimaryClip() != null) {
				if (clipBoard.getPrimaryClip().getItemCount() > 0) {
					ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
					String s = "";
					if (item.getText() != null) {
						s = item.getText().toString();
					}

					return send(dest, 60607, 2000, s);
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			if (aBoolean) {
				Toast.makeText(activity, "Clipboard sent!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(activity, "Unable to connect to " + dest, Toast.LENGTH_SHORT).show();
			}
		}

	}

	private class TestDestinationTask extends AsyncTask<String, Void, Boolean> {

		private Activity activity;

		public TestDestinationTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String dest = params[0];
			return send(dest, 60607, 2000, "Test");
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			if (aBoolean) {
				builder.setTitle("Server is up and running!");
				builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
			} else {
				builder.setTitle("Unable to connect to server");
				builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						spinnerData.remove(spinnerData.getItem(spinner.getSelectedItemPosition()));
						spinnerData.notifyDataSetChanged();
					}
				});
				builder.setNegativeButton("Keep", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
			}

			builder.show();
		}
	}
}

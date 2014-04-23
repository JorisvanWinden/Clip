package com.jvw.clip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class Main extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TaskCallback {

	public static final String SEND_TASK = "send";
	public static final String TEST_TASK = "test";
	private ClipboardManager clipBoard;
	private ArrayAdapter<Server> spinnerData;
	private Spinner spinner;
	private RelativeLayout infoLayout;
	private TextView nameInfo;
	private TextView ipInfo;
	private TextView portInfo;
	private ServerDataBase data;

	public static Result send(String ip, int port, int timeout, String msg) {
		try {
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(false);
			try {
				channel.connect(new InetSocketAddress(ip, port));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return Result.INVALID_PORT_IP;
			}
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!channel.finishConnect()) return Result.UNABLE_TO_CONNECT;
			channel.configureBlocking(true);
			Socket socket = channel.socket();
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(msg);
			out.close();
			socket.close();
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Result.CLIPBOARD_SENT;
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

		data = new ServerDataBase(this);
		spinnerData = new ArrayAdapter<Server>(this, R.layout.spinner_item);
		clipBoard = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);

		spinner.setAdapter(spinnerData);
		spinner.setOnItemSelectedListener(this);
		send.setOnClickListener(this);
		test.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String name = extras.getString(SendWidgetConfigureActivity.NAME, "");
			String ip = extras.getString(SendWidgetConfigureActivity.IP, "");
			int port = extras.getInt(SendWidgetConfigureActivity.PORT, 0);
			if (!(name.equals("") || ip.equals("") || port == 0)) {
				new SendTask(new Server(name, ip, port), this, SEND_TASK).execute();
			} else {
				Log.d("CLIP", "wtf is going on");
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		spinnerData.clear();
		// reload server spinner
		for (Server server : data.getAll()) {
			spinnerData.add(server);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clip_manage:
				Intent intent = new Intent(this, Manage.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		Server server = spinnerData.getItem(spinner.getSelectedItemPosition());
		switch (v.getId()) {
			case R.id.clip_send_button:
				new SendTask(server, this, SEND_TASK).execute();
				break;
			case R.id.clip_info_test_button:
				new SendTask(server, this, TEST_TASK).execute();
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Server item = spinnerData.getItem(position);
		nameInfo.setText("Name: " + item.getName());
		ipInfo.setText("IP: " + item.getIp());
		portInfo.setText("Port: " + item.getPort());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		infoLayout.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onPreExecute(SendTask task) {
		if (task.getTag().equals(SEND_TASK)) {
			Crouton.makeText(this, "Sending clipboard to " + task.getServer().getName(), Style.INFO).show();
		}
	}

	@Override
	public Result doInBackground(SendTask task) {
		if (task.getTag().equals(SEND_TASK)) {
			if (clipBoard.hasPrimaryClip() && clipBoard.getPrimaryClip() != null) {
				if (clipBoard.getPrimaryClip().getItemCount() > 0) {
					ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
					String msg = "";
					if (item.getText() != null) {
						msg = item.getText().toString();
					}
					return Main.send(task.getServer().getIp(), task.getServer().getPort(), 2000, msg);
				}
			}
			return Result.CLIPBOARD_EMPTY;
		} else if (task.getTag().equals(TEST_TASK)) {
			return Main.send(task.getServer().getIp(), task.getServer().getPort(), 2000, "Test");
		} else {
			return Result.UNABLE_TO_CONNECT;
		}
	}

	@Override
	public void onPostExecute(SendTask task, Result result) {
		if (task.getTag().equals(SEND_TASK)) {
			if (result == Result.CLIPBOARD_SENT) {
				Crouton.makeText(this, "Clipboard sent!", Style.CONFIRM).show();
			} else if (result == Result.UNABLE_TO_CONNECT) {
				Crouton.makeText(this, "Unable to connect to " + task.getServer().getIp(), Style.ALERT).show();
			} else if (result == Result.CLIPBOARD_EMPTY) {
				Crouton.makeText(this, "Clipboard is empty", Style.ALERT).show();
			} else if (result == Result.INVALID_PORT_IP) {
				Crouton.makeText(this, "Invalid port number or ip address", Style.ALERT).show();
			}

		} else if (task.getTag().equals(TEST_TASK)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			if (result == Result.CLIPBOARD_SENT) {
				builder.setTitle("Server is up and running!");

			} else {
				builder.setTitle("Unable to connect");
			}
			builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.show();
		}
	}
}

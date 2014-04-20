package com.jvw.clip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;


public class Main extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

	private ClipboardManager clipBoard;
	private ArrayAdapter<DestinationListItem> spinnerData;
	private Spinner spinner;
	private RelativeLayout infoLayout;
	private TextView nameInfo;
	private TextView ipInfo;
	private TextView portInfo;

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
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			boolean result = in.readBoolean();
			out.writeUTF(msg);
			in.close();
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

		spinnerData = new ArrayAdapter<DestinationListItem>(this, R.layout.activity_main_spinner);
		clipBoard = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);
		spinner.setAdapter(spinnerData);
		spinner.setOnItemSelectedListener(this);
		spinnerData.add(new DestinationListItem("Pc Joris", "192.168.1.39", 60607));
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
						spinnerData.add(new DestinationListItem(name, ip, port));
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
				new SendToClipboardTask(this, spinnerData.getItem(spinner.getSelectedItemPosition())).execute(clipBoard);

				break;
			case R.id.clip_info_test_button:
				new TestDestinationTask(this, spinnerData.getItem(spinner.getSelectedItemPosition())).execute(clipBoard);
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		DestinationListItem item = spinnerData.getItem(position);
		nameInfo.setText("Name: " + item.getName());
		ipInfo.setText("IP: " + item.getIp());
		portInfo.setText("Port: " + item.getPort());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		infoLayout.setVisibility(View.INVISIBLE);
	}

}

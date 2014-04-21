package com.jvw.clip;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			in.readBoolean();
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

		data = new ServerDataBase(this);
		spinnerData = new ArrayAdapter<Server>(this, R.layout.spinner_item);
		clipBoard = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);
		spinner.setAdapter(spinnerData);
		spinner.setOnItemSelectedListener(this);

		send.setOnClickListener(this);
		test.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		spinnerData.clear();
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
		Server item = spinnerData.getItem(position);
		nameInfo.setText("Name: " + item.getName());
		ipInfo.setText("IP: " + item.getIp());
		portInfo.setText("Port: " + item.getPort());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		infoLayout.setVisibility(View.INVISIBLE);
	}

}

package com.jvw.clip;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Main extends ActionBarActivity implements View.OnClickListener {

	private ClipboardManager clipBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    Button send = (Button) findViewById(R.id.clip_send_button);
	    clipBoard = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);
	    send.setOnClickListener(this);
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

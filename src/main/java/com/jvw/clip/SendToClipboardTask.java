package com.jvw.clip;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Joris on 20-4-14.
 */
public class SendToClipboardTask extends AsyncTask<ClipboardManager, Void, Result> {
	private Activity activity;
	private Server destination;

	public SendToClipboardTask(Activity a, Server destination) {
		this.activity = a;
		this.destination = destination;
		Toast.makeText(activity, "Sending clipboard to " + destination.getName(), Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Result doInBackground(ClipboardManager... params) {
		ClipboardManager clipBoard = params[0];
		if (clipBoard.hasPrimaryClip() && clipBoard.getPrimaryClip() != null) {
			if (clipBoard.getPrimaryClip().getItemCount() > 0) {
				ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
				String msg = "";
				if (item.getText() != null) {
					msg = item.getText().toString();
				}

				return Main.send(destination.getIp(), destination.getPort(), 2000, msg);
			}
		}
		return Result.CLIPBOARD_EMPTY;
	}

	@Override
	protected void onPostExecute(Result result) {
		if (result == Result.CLIPBOARD_SENT) {
			Toast.makeText(activity, "Clipboard sent!", Toast.LENGTH_SHORT).show();
		} else if (result == Result.UNABLE_TO_CONNECT) {
			Toast.makeText(activity, "Unable to connect to " + destination.getIp(), Toast.LENGTH_SHORT).show();
		} else if (result == Result.CLIPBOARD_EMPTY) {
			Toast.makeText(activity, "Clipboard is empty", Toast.LENGTH_SHORT).show();
		} else if (result == Result.INVALID_PORT_IP) {
			Toast.makeText(activity, "Invalid port number or ip address", Toast.LENGTH_SHORT).show();
		}
	}
}

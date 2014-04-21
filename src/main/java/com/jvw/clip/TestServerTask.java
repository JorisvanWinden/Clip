package com.jvw.clip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.AsyncTask;

/**
 * Created by Joris on 20-4-14.
 */
public class TestServerTask extends AsyncTask<ClipboardManager, Void, Result> {
	private Activity activity;
	private Server destination;

	public TestServerTask(Activity activity, Server destination) {
		this.activity = activity;
		this.destination = destination;
	}

	@Override
	protected Result doInBackground(ClipboardManager... params) {
		return Main.send(destination.getIp(), destination.getPort(), 2000, "Test");
	}

	@Override
	protected void onPostExecute(Result result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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

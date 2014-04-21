package com.jvw.clip;

/**
 * Created by Joris on 21-4-14.
 */
public interface TaskCallback {
	public void onPreExecute(SendTask task);

	public Result doInBackground(SendTask task);

	public void onPostExecute(SendTask task, Result result);
}

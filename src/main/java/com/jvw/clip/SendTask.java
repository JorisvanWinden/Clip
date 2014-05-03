package com.jvw.clip;

import android.os.AsyncTask;

/**
 * Created by Joris on 21-4-14.
 */
public class SendTask extends AsyncTask<Void, Void, Result> {
    private final String tag;
    private Server server;
    private TaskCallback callback;

    public SendTask(Server server, TaskCallback callback, String tag) {
        this.server = server;
        this.callback = callback;
        this.tag = tag;
    }

    @Override
    protected void onPreExecute() {
        callback.onPreExecute(this);
    }

    @Override
    protected Result doInBackground(Void... params) {
        return callback.doInBackground(this);
    }

    @Override
    protected void onPostExecute(Result result) {
        callback.onPostExecute(this, result);
    }

    public String getTag() {
        return tag;
    }

    public Server getServer() {
        return server;
    }
}

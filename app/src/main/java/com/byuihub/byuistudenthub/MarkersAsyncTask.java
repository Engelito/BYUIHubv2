package com.byuihub.byuistudenthub;

/**
 * Created by josed on 7/13/2017.
 */

abstract class MarkersAsyncTask {
    protected abstract void onPreExecute();

    protected abstract Void doInBackground(String... params);

    protected abstract void onProgressUpdate(String... values);

    protected abstract void onPostExecute(Void result);

    public abstract void execute();
}

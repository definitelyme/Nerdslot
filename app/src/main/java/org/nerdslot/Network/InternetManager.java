package org.nerdslot.Network;

import android.os.AsyncTask;
import android.util.Log;

import org.nerdslot.Foundation.Nerdslot;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Checks if user is Connected to the Internet
 *
 * @return boolean
 */
public class InternetManager extends AsyncTask<Void, Integer, Boolean> {
    private static final String INTERNET_MANAGER_TAG = "connection-tag";
    private InternetConnectionListener connectionListener;

    final public void setInternetConnectionListener(InternetConnectionListener listener) {
        this.connectionListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (ConnectionManager.isConnectionAvailable(Nerdslot.getContext())) {

            for (int i = 0; i <= 100; i += 5) {

                try {
                    HttpsURLConnection urlConn = (HttpsURLConnection) new URL("https://clients3.google.com/generate_204").openConnection();
                    urlConn.setRequestProperty("User-Agent", "Android");
                    urlConn.setRequestProperty("Connection", "close");
                    urlConn.setConnectTimeout(2000);
                    urlConn.connect();

                    return (urlConn.getResponseCode() == 204 && urlConn.getContentLength() == 0);
                } catch (IOException e) {
                    Log.i(INTERNET_MANAGER_TAG, e.getMessage(), e);
                }

                publishProgress(i);
            }
        }

        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (connectionListener != null) {
            if (result) connectionListener.internetAvailable();
            else connectionListener.internetNotAvailable();
        }
    }

    public interface InternetConnectionListener {
        void internetAvailable();

        void internetNotAvailable();
    }
}

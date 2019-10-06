package org.nerdslot.Foundation.Helper;

import android.os.AsyncTask;

import org.apache.commons.io.FileUtils;
import org.nerdslot.Fragments.RootInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadTaskManager extends AsyncTask<String, Integer, Void> implements RootInterface {
    private File target;

    public DownloadTaskManager(File target) {
        this.target = target;
    }

    @Override
    protected Void doInBackground(String... uris) {
        try {
            HttpsURLConnection urlConn = (HttpsURLConnection) new URL(uris[0]).openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();

            InputStream in = urlConn.getInputStream();
            FileUtils.copyInputStreamToFile(in, target);
            in.close();
        } catch (IOException e) {
            sendResponse(e.getLocalizedMessage(), e);
        }

        return null;
    }

    private void writeToFile(InputStream inputStream) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(target)) {
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}

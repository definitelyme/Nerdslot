package org.nerdslot.Foundation.Helper;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.nerdslot.Fragments.RootInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadTaskManager extends AsyncTask<String, Integer, Void> implements RootInterface {
    private File folder, target;
    private CompleteListener completeListener;

    public DownloadTaskManager(Context context, String folderPath, String fileName) {
        this.folder = new File(getActivityFromContext(context).getCacheDir(), folderPath);
        this.target = new File(this.folder, fileName);
    }

    public DownloadTaskManager(File target) {
        this.target = target;
    }

    @Override
    protected Void doInBackground(String... uris) {
        try {
            if (folder != null && !folder.exists()) folder.mkdirs();

            HttpsURLConnection urlConn = (HttpsURLConnection) new URL(uris[0]).openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();

            InputStream in = urlConn.getInputStream();
            writeToFile(in);
        } catch (IOException e) {
            sendResponse(e.getLocalizedMessage(), e);
        }

        return null;
    }

    private void writeToFile(InputStream inputStream) throws IOException {
        FileUtils.copyInputStreamToFile(inputStream, target);

        try (FileOutputStream outputStream = new FileOutputStream(target)) {
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            completeListener.postDownload(true);
        }

        inputStream.close();
    }

    public void setCompleteListener(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public interface CompleteListener {
        default void postDownload(boolean status) {
            postDownload(status, null);
        }

        void postDownload(boolean status, @Nullable Object extras);
    }
}

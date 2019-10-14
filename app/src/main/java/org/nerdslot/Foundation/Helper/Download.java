package org.nerdslot.Foundation.Helper;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.Nerdslot;
import org.nerdslot.Fragments.RootInterface;

import java.io.File;

public class Download implements RootInterface {
    private AppCompatActivity activity;
    private File folder, file;
    private CompleteListener completeListener;

    public Download(@NonNull String fullPath){
        activity = getActivityFromContext(Nerdslot.getContext());
        this.file = new File(fullPath);
    }

    public Download(@NotNull Context context, @NonNull String folderPath, @NonNull String fileName) {
        activity = getActivityFromContext(context);
        this.folder = new File(activity.getCacheDir(), folderPath);
        this.file = new File(this.folder, fileName);
    }

    public File getFile(){
        return file;
    }

    public void setCompleteListener(CompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public void startDownload(@NonNull String uriString) {
        StorageReference reference = FirebaseStorage.getInstance().getReference(uriString);

        int imageSize = Integer.parseInt(String.valueOf(file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));

        if (!folder.exists() || imageSize < 1) { // If folder doesn't or file size is less then 1
            folder.mkdirs();
            reference.getFile(file)
                    .addOnSuccessListener(taskSnapshot -> completeListener.postDownload(true, file))
                    .addOnFailureListener(e -> completeListener.postDownload(false, e)); // Download Cover Image
            return;
        }

        completeListener.postDownload(true, file);
    }

    public interface CompleteListener{
        default void postDownload(boolean status) {
            postDownload(status, null);
        }
        void postDownload(boolean status, @Nullable Object extras);
    }
}

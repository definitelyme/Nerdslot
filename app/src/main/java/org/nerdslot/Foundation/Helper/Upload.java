package org.nerdslot.Foundation.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mertakdut.Reader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.FireUtil;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.Models.Issue.Magazine;

public class Upload implements RootInterface {
    private Fragment fragment;
    private Activity activity;
    private String fileExtension;
    private String currentNode;
    private Reader reader;

    public Upload(@NonNull Context context) {
        this.activity = (Activity) context;
    }

    public Upload(@NonNull Fragment context, @NotNull MIME_TYPE mimeType) {
        this.fragment = context;

        setFileExtension(mimeType);
        startIntent();
    }

    private void setFileExtension(@NotNull MIME_TYPE mimeType) {
        switch (mimeType) {
            case JPG:
                fileExtension = MIME_TYPE.JPG.toString();
                break;
            case PNG:
                fileExtension = MIME_TYPE.PNG.toString();
                break;
            case IMAGE:
                fileExtension = MIME_TYPE.IMAGE.toString();
                break;
            case EPUB:
                fileExtension = MIME_TYPE.EPUB.toString();
                break;
            case PDF:
                fileExtension = MIME_TYPE.PDF.toString();
                break;
            case DOC:
                fileExtension = MIME_TYPE.DOC.toString();
                break;
            case TXT:
                fileExtension = MIME_TYPE.TXT.toString();
                break;
            case ZIP:
                fileExtension = MIME_TYPE.ZIP.toString();
                break;
            default:
                fileExtension = "*/*";
        }
    }

    private String getFileExtension(@NotNull MIME_TYPE mimeType) {
        switch (mimeType){
            case PNG:
                return ".png";
            case JPG:
                return ".jpg";
            case DOC:
                return ".doc";
            case EPUB:
                return ".epub";
            case PDF:
                return ".pdf";
            case TXT:
                return ".txt";
            case ZIP:
                return ".zip";
            default:
                return ".file";
        }
    }

    private void startIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(fileExtension);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        fragment.startActivityForResult(Intent.createChooser(intent, "Choose File"), SELECT_FILE_REQUEST_CODE);
    }

    public void magazine__(String title, Uri uri) {
        currentNode = new Issue().getNode();

        DatabaseReference magazineRef = FireUtil.databaseReference(new Magazine());
        String key = magazineRef.push().getKey();
        Magazine magazine = new Magazine.Builder()
                .setId(key)
                .setTitle(title)
                .setSlug(new Slugify.Builder().make(title))
                .build();

        StorageReference uploadReference = FireUtil.storageReference(magazine).child(key).child(title + getFileExtension(MIME_TYPE.EPUB));

        StorageMetadata magazineMetadata = new StorageMetadata.Builder()
                .setContentType(fileExtension)
                .setCustomMetadata("Nerdslot", "Magazines")
                .build();

        UploadTask uploadTask = uploadReference.putFile(uri, magazineMetadata);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            sendResponse("File Uploaded!");
        }).addOnFailureListener(e -> {
            sendResponse(e.getMessage(), e);
        });
    }
}

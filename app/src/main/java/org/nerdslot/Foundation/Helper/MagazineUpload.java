package org.nerdslot.Foundation.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Fragments.RootInterface;

public class MagazineUpload implements RootInterface {
    private Activity activity;
    private String fileExtension;

    public MagazineUpload(@NonNull Context context, @NotNull MIME_TYPE mimeType) {
        this.activity = ((AppCompatActivity) context);

        setFileExtension(mimeType);
        startIntent();
    }

    private void startIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(fileExtension);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, fileExtension);
        activity.startActivityForResult(Intent.createChooser(intent, "Choose File"), SELECT_FILE_REQUEST_CODE);
    }

    private void setFileExtension(@NotNull MIME_TYPE mimeType) {
        switch (mimeType) {
            case JPG:
                fileExtension = MIME_TYPE.JPG.toString();
            case PNG:
                fileExtension = MIME_TYPE.PNG.toString();
            case EPUB:
                fileExtension = MIME_TYPE.EPUB.toString();
            case PDF:
                fileExtension = MIME_TYPE.PDF.toString();
            case DOC:
                fileExtension = MIME_TYPE.DOC.toString();
            case TXT:
                fileExtension = MIME_TYPE.TXT.toString();
            case ZIP:
                fileExtension = MIME_TYPE.ZIP.toString();
            default:
                fileExtension = "*/*";
        }
    }
}

package org.nerdslot.Foundation.Helper;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Issue.Featured;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.Models.Issue.Magazine;
import org.nerdslot.Models.User.User;

import java.util.ArrayList;

public class Upload implements RootInterface {
    public Uri epubUri;
    public ArrayList<Uri> imageUris;
    private Fragment fragment;
    private String fileExtension;
    private String mime;
    private String magazineSessionKey;
    private String epubStringUri;
    private String imageStringUri;
    private String issueImageUri;
    private String issueId;
    private boolean isUpdating;
    private ProgressListener progressListener;
    private CompleteListener completeListener;

    public Upload(@NonNull Fragment context) {
        this.fragment = context;
    }

    public void __construct(@NotNull MIME_TYPE mimeType) {
        setMimeType(mimeType);
        startIntent();

        imageUris = new ArrayList<>();

        if (magazineSessionKey == null || magazineSessionKey.equals("")) {
            DatabaseReference magazineRef = new Reference.Builder()
                    .setNode(Magazine.class)
                    .getDatabaseReference();
            magazineSessionKey = magazineRef.push().getKey();
        }
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setOnCompleteListener(CompleteListener listener) {
        this.completeListener = listener;
    }

    /**
     * Returns the Mime Type to String
     *
     * @return String File Mime-Type
     */
    public String getMimeType() {
        return mime;
    }

    private void setMimeType(@NotNull MIME_TYPE mimeType) {
        switch (mimeType) {
            case JPG:
                mime = MIME_TYPE.JPG.toString();
                setExtension(mimeType);
                break;
            case PNG:
                mime = MIME_TYPE.PNG.toString();
                setExtension(mimeType);
                break;
            case IMAGE:
                mime = MIME_TYPE.IMAGE.toString();
                setExtension(mimeType);
                break;
            case EPUB:
                mime = MIME_TYPE.EPUB.toString();
                setExtension(mimeType);
                break;
            case PDF:
                mime = MIME_TYPE.PDF.toString();
                setExtension(mimeType);
                break;
            case DOC:
                mime = MIME_TYPE.DOC.toString();
                setExtension(mimeType);
                break;
            case TXT:
                mime = MIME_TYPE.TXT.toString();
                setExtension(mimeType);
                break;
            case ZIP:
                mime = MIME_TYPE.ZIP.toString();
                setExtension(mimeType);
                break;
            default:
                mime = "*/*";
                setExtension(mimeType);
        }
    }

    private void startIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mime);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        fragment.startActivityForResult(Intent.createChooser(intent, "Choose File"), SELECT_FILE_REQUEST_CODE);
    }

    @Important
    public void magazine__() {
        setMimeType(MIME_TYPE.EPUB); // Set the File Extension

        StorageReference uploadReference = new Reference.Builder()
                .setNode(Magazine.class)
                .setNode(getMagazineSessionKey())
                .setNode(getMagazineSessionKey() + getExtension())
                .getStorageReference(); // Get Storage Reference to insert Epub File

        StorageMetadata magazineMetadata = new StorageMetadata.Builder()
                .setContentType(getMimeType())
                .setCustomMetadata("Nerdslot", "Magazines")
                .build(); // Create Metadata

        uploadReference.putFile(epubUri, magazineMetadata)
                .addOnSuccessListener(snapshot -> {
                    if (!isUpdating) completeListener.postUpload(true, imageStringUri);
                })
                .addOnFailureListener(e -> {
                    sendResponse(e.getMessage(), e);
                    completeListener.postUpload(false);
                });

        epubStringUri = uploadReference.toString();
        updateMagazine(); // This is always called last
    }

    @Important
    public void cover__(String id) {
        issueId = id;
        int counter = 0;
        boolean isLast;
        StringBuilder builder = new StringBuilder(imageUris.size());

        for (Uri imageUri : imageUris) {
            setMimeType(MIME_TYPE.JPG); // Set the File Extension

            StorageReference uploadReference = new Reference.Builder()
                    .setNode(Magazine.class)
                    .setNode(getMagazineSessionKey())
                    .setNode(MAGAZINE_COVER_NODE)
                    .setNode(counter == 0
                            ? getMagazineSessionKey() + getExtension()
                            : getMagazineSessionKey() + STRING_URI_INCREMENTER + counter + getExtension())
                    .getStorageReference(); // Get Storage Reference to insert image

            StorageMetadata coverMetadata = new StorageMetadata.Builder()
                    .setContentType(getMimeType())
                    .setCustomMetadata("Nerdslot", "Magazines")
                    .setCustomMetadata("Type", "magazine-image")
                    .build(); // Create Metadata

            uploadReference.putFile(imageUri, coverMetadata);

            counter++; // Update counter
            isLast = counter == imageUris.size(); // set isLast

            String uri = uploadReference.toString();

            imageStringUri = builder.append(uri).append(!isLast
                    ? STRING_URI_SEPERATOR
                    : "").toString();

            // Update Issue Image Uri
            if (issueImageUri == null) {
                issueImageUri = uri;
                updateIssueNode(issueId, ISSUE_IMAGE_NODE, issueImageUri); // Update the Issue's Cover Image to the First image
            }
        }
    }

    @Important
    public void slider__() {
        for (Uri imageUri : imageUris) {
            String key = new Reference.Builder()
                    .setNode(Featured.class)
                    .getDatabaseReference().push().getKey();

            setMimeType(MIME_TYPE.JPG); // Set the File Extension

            StorageReference uploadReference = new Reference.Builder()
                    .setNode(Magazine.class)
                    .setNode(Featured.class)
                    .setNode(key + getExtension())
                    .getStorageReference(); // Get Storage Reference to insert image

            StorageMetadata coverMetadata = new StorageMetadata.Builder()
                    .setContentType(getMimeType())
                    .setCustomMetadata("type", "featured-image")
                    .build(); // Create Metadata

            uploadReference.putFile(imageUri, coverMetadata);

            String uri = uploadReference.toString();

            completeListener.postUpload(true, key, uri);
        }

        // Note: I'm calling false here to inform the
        completeListener.postUpload(false);
        reset(); // Should be called here
    }

    @Important
    public void updateIssue(@NonNull Issue issue) {
        int counter = 0;
        boolean isLast, isLastOldImg;
        isUpdating = issueId == null; // TRUE
        setMimeType(MIME_TYPE.JPG); // Set the File Extension
        setMagazineSessionKey(issue.getMagazine().getId());

        StringBuilder builder = new StringBuilder(imageUris.size());
        for (Uri uri : imageUris) {
            if (uri.toString().contains(STRING_URI_INCREMENTER)) {
                String[] split = uri.toString().split(STRING_URI_INCREMENTER);
                String[] split2 = split[split.length - 1].split(getExtension()); // 1.jpg
                if (split2.length > 0) {
                    int val = Integer.parseInt(split2[0]); // 1
                    if (val == counter) counter++;
                    else if (val > counter) counter = val + 1;
                }
            }
        }

        StringBuilder oldImages = new StringBuilder();
        int lastCount = counter - 1;

        for (Uri imgUri : imageUris) {
            if (imgUri.toString().contains(STRING_APP_STORAGE_ROOT)) {
                isLastOldImg = imgUri.toString().contains(STRING_URI_INCREMENTER + lastCount) || counter < 1;
                counter = counter < 1 ? counter + 1 : 0;
                oldImages.append(imgUri).append(isLastOldImg ? "" : STRING_URI_SEPERATOR);
            }
            if (!imgUri.toString().contains(STRING_APP_STORAGE_ROOT)) {
                StorageReference uploadReference = new Reference.Builder()
                        .setNode(Magazine.class)
                        .setNode(getMagazineSessionKey())
                        .setNode(MAGAZINE_COVER_NODE)
                        .setNode(counter == 0
                                ? getMagazineSessionKey() + getExtension()
                                : getMagazineSessionKey() + STRING_URI_INCREMENTER + counter + getExtension())
                        .getStorageReference(); // Get Storage Reference to insert image

                StorageMetadata coverMetadata = new StorageMetadata.Builder()
                        .setContentType(getMimeType())
                        .setCustomMetadata("Nerdslot", "Magazines")
                        .setCustomMetadata("Type", "magazine-image")
                        .build(); // Create Metadata

                uploadReference.putFile(imgUri, coverMetadata);

                counter++; // Update counter
                isLast = counter == imageUris.size(); // set isLast

                String uri = uploadReference.toString();

                imageStringUri = builder.append(uri).append(!isLast
                        ? STRING_URI_SEPERATOR : "").toString();
            }
        }

        imageStringUri = oldImages.append(oldImages == null
                ? "" : STRING_URI_SEPERATOR).append(imageStringUri == null
                ? "" : imageStringUri).toString();

        issueImageUri = imageStringUri.split(STRING_URI_SEPERATOR)[0];

        epubStringUri = epubUri.toString();

        if (epubUri.toString().contains(STRING_APP_STORAGE_ROOT)) {
            updateMagazine();
            return;
        }

        magazine__();
    }

    @Important
    public void user_image__(@NotNull User user, Uri uri) {
        setMimeType(MIME_TYPE.JPG); // Set the File Extension

        StorageReference uploadReference = new Reference.Builder()
                .setNode(User.class)
                .setNode(user.getUid())
                .setNode(getMagazineSessionKey() + getExtension())
                .getStorageReference(); // Get Storage Reference to insert image

        StorageMetadata coverMetadata = new StorageMetadata.Builder()
                .setContentType(getMimeType())
                .setCustomMetadata("type", user.getName() + "'s - profile photo")
                .build(); // Create Metadata

        uploadReference.putFile(uri, coverMetadata);

        new Reference.Builder()
                .setNode(User.class).setNode(user.getUid()).setNode(USER_PROFILE_PHOTO_NODE)
                .getDatabaseReference()
                .setValue(uploadReference.toString());
        reset();
    }

    private void updateMagazine() {
        DatabaseReference reference = new Reference.Builder()
                .setNode(Magazine.class).setNode(getMagazineSessionKey()).getDatabaseReference();

        Magazine magazine = new Magazine.Builder()
                .setId(getMagazineSessionKey())
                .setMagazineUri(epubStringUri)
                .setImages(imageStringUri)
                .setImageCount(imageUris.size())
                .build();

        reference.setValue(magazine);
        if (isUpdating) { // If issueId == null, call listener and pass updated magazine
            completeListener.postUpload(true, issueImageUri, magazine);
            return;
        }
        updateIssueNode(issueId, "magazine", magazine);
        reset(); // Must be called here
    }

    private void updateIssueNode(String id, String node, Object data) {
        new Reference.Builder()
                .setNode(Issue.class)
                .setNode(id)
                .setNode(node)
                .getDatabaseReference().setValue(data);
    }

    private void reset() {
        epubUri = null;
        fileExtension = null;
        mime = null;
        magazineSessionKey = null;
        epubStringUri = null;
        imageStringUri = null;
        issueImageUri = null;
        issueId = null;
        imageUris.clear();
    }

    @Exclude
    public String getMagazineSessionKey() {
        return magazineSessionKey;
    }

    private void setMagazineSessionKey(String magazineSessionKey) {
        this.magazineSessionKey = magazineSessionKey;
    }

    @Exclude
    private String getExtension() {
        return fileExtension;
    }

    private void setExtension(@NotNull MIME_TYPE mimeType) {
        switch (mimeType) {
            case PNG:
                fileExtension = ".png";
                break;
            case JPG:
            case IMAGE:
                fileExtension = ".jpg";
                break;
            case DOC:
                fileExtension = ".doc";
                break;
            case EPUB:
                fileExtension = ".epub";
                break;
            case PDF:
                fileExtension = ".pdf";
                break;
            case TXT:
                fileExtension = ".txt";
                break;
            case ZIP:
                fileExtension = ".zip";
                break;
            default:
                fileExtension = ".file";
        }
    }

    public interface ProgressListener {
        void progressUpdate(int progress);
    }

    public interface CompleteListener {
        default void postUpload(boolean status) {
            postUpload(status, (String) null);
        }

        void postUpload(boolean status, @Nullable Object... values);
    }
}

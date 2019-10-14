package org.nerdslot.Models.src;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.nerdslot.Foundation.FireUtil;
import org.nerdslot.Foundation.Helper.Pluralizer;
import org.nerdslot.Foundation.Helper.Slugify;

import java.util.Locale;

public abstract class Model implements ModelInterface, ValueEventListener {
    /**
     * If model should return with relationships
     *
     * @var string
     */
    @Exclude
    protected boolean related = false;
    /**
     * Subclass of the Model Object
     *
     * @var string
     */
    @Exclude
    private Class<? extends Model> subClass = this.getClass();
    /**
     * Node for this model Class<? extends Model>
     *
     * @var string
     */
    @Exclude
    private String node = Pluralizer.build(
            new Slugify.Builder().input(getSubClass().getSimpleName().toLowerCase()).seperator("-").make()
    );
    /**
     * Total number of models in the node
     *
     * @var string
     */
    @Exclude
    private long count;
    /**
     * Query to the current node
     *
     * @var string
     */
    @Exclude
    private Query query;
    /**
     * When the model was created
     *
     * @var string
     */
    private long created_at = System.currentTimeMillis();
    /**
     * When the model was updated
     *
     * @var string
     */
    private long updated_at = System.currentTimeMillis();
    /**
     * When the model was deleted
     *
     * @var string
     */
    private long deleted_at;

    /**
     * Abstract model constructor
     * This Constructor may not be called outside its subclasses,
     * because of its abstract nature, it defines default methods
     * which getAllIssues subclasses automatically inherit.
     * <p></p>
     */
    protected Model() {
        query = FireUtil.databaseReference(getNode());
        query.addListenerForSingleValueEvent(this);
    }

    public void removeValueEventListener() {
        query.removeEventListener(this);
    }

    @Exclude
    public String getNode() {
        return node;
    }

    public String created_at() {
        return DateFormatUtils.format(created_at, "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
    }

    public String updated_at() {
        return DateFormatUtils.format(updated_at, "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
    }

    public String deleted_at() {
        return DateFormatUtils.format(deleted_at, "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public long getDeleted_at() {
        return deleted_at;
    }

    @Exclude
    public long count() {
        return count;
    }

    private Class<? extends Model> getSubClass() {
        return subClass;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        count = dataSnapshot.getChildrenCount();
    }
}

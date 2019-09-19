package org.nerdslot.Models.src;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.nerdslot.Foundation.FireUtil;
import org.nerdslot.Foundation.Helper.Slugify;

import java.util.ArrayList;
import java.util.Date;

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
    private String node = new Slugify.Builder().input(getSubClass().getSimpleName().toLowerCase()).seperator("-").make();
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
    private Date created_at;
    /**
     * When the model was updated
     *
     * @var string
     */
    private Date updated_at;
    /**
     * When the model was deleted
     *
     * @var string
     */
    private Date deleted_at;

    /**
     * Abstract model constructor
     * This Constructor may not be called outside its subclasses,
     * because of its abstract nature, it defines default methods
     * which all subclasses automatically inherit.
     * <p></p>
     */
    protected Model() {
        query = FireUtil.databaseReference(getNode());
        query.addListenerForSingleValueEvent(this);
    }

    public void removeValueEventListener(){
        query.removeEventListener(this);
    }

    @Exclude
    public String getNode() {
        return node;
    }

    @Exclude
    public long count(){
        return count;
    }

    private Class<? extends Model> getSubClass() {
        return subClass;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        count = dataSnapshot.getChildrenCount();
//        dataSnapshot.getValue(Category.class);
//        dataSnapshot.getValue(getSubClass());
//        removeValueEventListener();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.i(TAG, "onCancelled: Database Reference: Action Cancelled! - " + databaseError.getMessage(), databaseError.toException());
    }

    public static class QueryBuilder extends Model implements ValueEventListener {
        private ArrayList<Model> arrayList;
        private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        public QueryBuilder() {
            databaseReference.addListenerForSingleValueEvent(this);
        }

        public ArrayList<Model> all(){
            arrayList = new ArrayList<>();
            databaseReference.child(this.getNode());
            return arrayList;
        }
    }
}

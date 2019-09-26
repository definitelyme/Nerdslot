package org.nerdslot.Foundation;

import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.Helper.Pluralizer;
import org.nerdslot.Foundation.Helper.Slugify;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.src.Model;

public class Reference implements RootInterface {

    public Reference() {
        //
    }

    public static class Builder {
        private String node;

        public Builder setNode(@NotNull Class<? extends Model> child) {
            String thisNode = this.node;
            String newNode = Pluralizer.build(
                    new Slugify.Builder().input(child.getSimpleName().toLowerCase()).seperator("-").make()
            );

            this.node = thisNode != null && !thisNode.equals("") && !TextUtils.isEmpty(thisNode)
                    ? thisNode.concat(String.format("/%s", newNode))
                    : newNode;

            return this;
        }

        public Builder setNode(@NotNull String node) {
            String thisNode = this.node;

            this.node = thisNode != null && !thisNode.equals("") && !TextUtils.isEmpty(thisNode)
                    ? thisNode.concat(String.format("/%s", node))
                    : node;

            return this;
        }

        public DatabaseReference getDatabaseReference() {
            String thisNode = this.node;

            DatabaseReference databaseReference;

            if (thisNode == null)
                databaseReference = FireUtil.databaseReference();
            else
                databaseReference = FireUtil.databaseReference().child(thisNode);

            return databaseReference;
        }

        public StorageReference getStorageReference() {
            String thisNode = this.node;

            StorageReference storageReference;

            if (thisNode == null)
                storageReference = FireUtil.storageReference();
            else
                storageReference = FireUtil.storageReference().child(thisNode);

            return storageReference;
        }
    }
}

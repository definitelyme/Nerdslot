package org.nerdslot.Foundation.Helper;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.nerdslot.Foundation.FireUtil;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Category;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.Models.src.Model;

import java.util.concurrent.ThreadLocalRandom;

public class Faker implements RootInterface {
    private DatabaseReference databaseReference;
    private String currentNode;
    private String[] category_ids = {"category-1", "category-2"};

    public Faker() {
        databaseReference = FireUtil.firebaseDatabase.getReference();
    }

    public void fakeCategories(String node) {
        currentNode = node;
        createCategory(category_ids[0], "Magazines", "Valour Max Issues");
        createCategory(category_ids[1], "Books", "Enjoy the best of Nerdslot Books.");
    }

    public void fakeIssues(String node) {
        databaseReference.child(node);
        currentNode = node;
        createIssue("ValourMax Issue #12", "Bella Victor, a.k.a The Chief Efulefu Officer is the cover of this month's issue. He is The Social Media guru, who built Socialander, a top notch digital marketing firm. ValourMax issue #12 is here to teach you a lot about handling challenges, working withRelationships your theme and so on. All aimed at making you The Better You.",
                "NGN", "1300", true, 3.5);
        createIssue("The Subtle Art of not Giving a F*CK", null, "NGN", "5400", false, 4.5);
        createIssue("Words of Radiance", "Physical properties of reservoir fluids are determined in the laboratory, either from bottomhole samples or from recombined surface separator samples. Frequently, however, this information is not available.",
                "NGN", "1000", true, 3.5);
        createIssue("Cold as Winter's Heart", "No part of this publication may be reproduced, stored in a retrieval system, or transmitted in any form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the prior written permission of the publisher.",
                "NGN", "500", false, 3.0);
    }

    public String returnNode(@NonNull Model model){
        return model.getNode();
    }

    private void createCategory(String key, String name, String description) {
        Category category = new Category.Builder()
                .setId(key)
                .setName(name)
                .setDescription(description)
                .build();

        if (currentNode.equals(new Category().getNode())) databaseReference.child(currentNode).child(key).setValue(category);
    }

    private void createIssue(String title, String description, String currency, String price, boolean is_featured, double rateCount) {
        String key = databaseReference.push().getKey();
        int randomInt = ThreadLocalRandom.current().nextInt(0, 1);
        Issue issue = new Issue.Builder()
                .setId(key)
                .setTitle(title)
                .setDescription(description)
                .setCategory_id(category_ids[randomInt])
                .setCurrency(currency)
                .setPrice(price)
                .setFeatured(is_featured)
                .setRateCount(rateCount)
                .build();

        databaseReference.child(currentNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 10){
                    databaseReference.child(currentNode).child(key).setValue(issue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.bypriyan.m24.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.model.ModelUsers;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterParticipentAdd extends RecyclerView.Adapter<AdapterParticipentAdd.MyHolder> {

    Context context;
    ArrayList<ModelUsers> usersList;
    String groupId, myRole;

    public AdapterParticipentAdd(Context context, ArrayList<ModelUsers> usersList, String groupId, String myRole) {
        this.context = context;
        this.usersList = usersList;
        this.groupId = groupId;
        this.myRole = myRole;
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_participent_add, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterParticipentAdd.MyHolder holder, int position) {

        ModelUsers modelUsers = usersList.get(position);
        String name = modelUsers.getName();
        String email= modelUsers.getEmail();
        String image = modelUsers.getProfileImage();
        String uid = modelUsers.getUid();

        holder.name.setText(name);
        holder.email.setText(email);

        try {

            Glide
                    .with(context)
                    .load(image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(holder.profile);
        }catch (Exception e){

        }

        checkIfAlreadyExist(modelUsers, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
                reference.child(groupId).child(Constant.KEY_PARTICIPENTS).child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String hisPreviousRole = ""+snapshot.child(Constant.KEY_ROLE).getValue();

                                    String [] options;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Options");
                                    if(myRole.equals("creator")){
                                        if(hisPreviousRole.equals("admin")){

                                            options = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    if(i==0){
                                                        removeAdmin(modelUsers);
                                                    }else {
                                                        removeParticipenta(modelUsers);

                                                    }

                                                }
                                            }).create().show();

                                        }
                                        else if(hisPreviousRole.equals(Constant.KEY_PARTICIPENTS)) {

                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    if(i==0){
                                                        makeAdmin(modelUsers);
                                                    }else {
                                                        removeParticipenta(modelUsers);

                                                    }

                                                }
                                            }).create().show();

                                        }

                                    }
                                    else if(myRole.equals("admin")){
                                        if(hisPreviousRole.equals("creator")){
                                            Toast.makeText(context, "Creator of group...", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(hisPreviousRole.equals("admin")){
                                            options = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    if(i==0){
                                                        removeAdmin(modelUsers);
                                                    }else {
                                                        removeParticipenta(modelUsers);

                                                    }

                                                }
                                            }).create().show();
                                        }
                                        else if(hisPreviousRole.equals(Constant.KEY_PARTICIPENTS)){
                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    if(i==0){
                                                        makeAdmin(modelUsers);
                                                    }else {
                                                        removeParticipenta(modelUsers);

                                                    }

                                                }
                                            }).create().show();
                                        }

                                    }
                                }
                                else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Add Participant")
                                            .setMessage("Add this user in this group?")
                                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    addParticepant(modelUsers);

                                                }
                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            dialogInterface.dismiss();

                                        }
                                    }).create().show();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
            }
        });

    }

    private void addParticepant(ModelUsers modelUsers) {

        String timeStamp = ""+System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_UID, modelUsers.getUid());
        hashMap.put(Constant.KEY_ROLE, Constant.KEY_PARTICIPENTS);
        hashMap.put(Constant.KEY_TIMESTAMP, timeStamp);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS).child(modelUsers.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Added Successfully..", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void makeAdmin(ModelUsers modelUsers) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_ROLE, "admin");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS).child(modelUsers.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "The user is now Admin..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void removeParticipenta(ModelUsers modelUsers) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS).child(modelUsers.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "User removed successfully..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeAdmin(ModelUsers modelUsers) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_ROLE, Constant.KEY_PARTICIPENTS);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS).child(modelUsers.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "The user is no longer Admin..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkIfAlreadyExist(ModelUsers modelUsers, MyHolder holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_GROUPS);
        reference.child(groupId).child(Constant.KEY_PARTICIPENTS).child(modelUsers.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            String hisRole = ""+snapshot.child(Constant.KEY_ROLE).getValue();
                            holder.status.setText(hisRole);

                        }else{
                            holder.status.setText(" ");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        private CircleImageView profile;
        TextView name, email, status;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.imageProfile);
            name = itemView.findViewById(R.id.textName);
            email = itemView.findViewById(R.id.textEmail);
            status = itemView.findViewById(R.id.status);
        }
    }

}

package com.bypriyan.m24.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.bypriyan.m24.activity.ChatActivity;
import com.bypriyan.m24.model.ModelUsers;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {

    Context context;
    List<ModelUsers> usersList;
    FirebaseAuth firebaseAuth;
    String myUid;

    public AdapterUser(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getCurrentUser().getUid();
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterUser.MyHolder holder, int position) {

        String userUid = usersList.get(position).getUid();
        String userImage = usersList.get(position).getProfileImage();
        String email = usersList.get(position).getEmail();
        String name = usersList.get(position).getName();

        holder.email.setText(email);
        holder.name.setText(name);

        try {

            Glide
                    .with(context)
                    .load(userImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(holder.profile);
        }catch (Exception e){

        }


        holder.blockIv.setImageResource(R.drawable.unblocked);

        checkHisBlocked(userUid, holder, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iamBlockedOrNot(userUid, name, userImage);
            }
        });

        holder.blockIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(usersList.get(position).isBlocked()){
                unblockUser(userUid);
            }else{
                blockUser(userUid);

            }
            }
        });

    }


    private void checkHisBlocked(String userUid, MyHolder holder, int position) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(myUid).child(Constant.KEY_BLOCKEDUSERS).orderByChild(Constant.KEY_UID).equalTo(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                holder.blockIv.setImageResource(R.drawable.block);
                                usersList.get(position).setBlocked(true);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }


    private void iamBlockedOrNot(String hisUid, String name, String image){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(hisUid).child(Constant.KEY_BLOCKEDUSERS).orderByChild(Constant.KEY_UID).equalTo(myUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                Toast.makeText(context, "You're blocked by the user, can't send message", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(Constant.KEY_NAME, name);
                        intent.putExtra(Constant.KEY_IMAGE, image);
                        intent.putExtra(Constant.KEY_UID, hisUid);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


    }

    private void blockUser(String hisUid) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_UID, hisUid);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(myUid).child(Constant.KEY_BLOCKEDUSERS).child(hisUid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Blocked successfully...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(context, " "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void unblockUser(String hisUid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(myUid).child(Constant.KEY_BLOCKEDUSERS).orderByChild(Constant.KEY_UID).equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()){

                            if(ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Unblocked user...", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

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
        ImageView blockIv;
        TextView name, email;

        public MyHolder(@NonNull  View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.imageProfile);
            name = itemView.findViewById(R.id.textName);
            email = itemView.findViewById(R.id.textEmail);
            blockIv = itemView.findViewById(R.id.blockIv);
        }
    }

}

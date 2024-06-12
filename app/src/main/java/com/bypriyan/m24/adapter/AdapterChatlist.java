package com.bypriyan.m24.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder> {

    Context context;
    List<ModelUsers> usersList;
    HashMap<String, String> lastMessageMap;

    public AdapterChatlist(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);

        return new MyHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterChatlist.MyHolder holder, int position) {

        String hisUid = usersList.get(position).getUid();
        String image = usersList.get(position).getProfileImage();
        String name = usersList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);

        holder.nameUser.setText(name);
        if(lastMessage == null || lastMessage.equals("default")){
            holder.lastMessageUser.setVisibility(View.GONE);
        }else{
            holder.lastMessageUser.setVisibility(View.VISIBLE);
            holder.lastMessageUser.setText(lastMessage);
        }

        holder.profileImageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, FullimageView.class);
//                intent.putExtra("image", image);
//                context.startActivity(intent);
            }
        });

        try{
            Glide
                    .with(context)
                    .load(image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(holder.profileImageUser);

        }catch (Exception e){

        }

        if(usersList.get(position).getOnlineStatus().equals("online")){
            holder.onlineStatus.setImageResource(R.drawable.circle_online);
        }else{
            holder.onlineStatus.setImageResource(R.drawable.circle_offline);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, Chat.class);
//                intent.putExtra(Constant.KEY_UID, hisUid);
//                intent.putExtra(Constant.KEY_NAME, name);
//                intent.putExtra(Constant.KEY_IMAGE, image);
//                context.startActivity(intent);
                iamBlockedOrNot(hisUid, name, image);

            }
        });


    }

    private void iamBlockedOrNot(String hisUid, String name, String image){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = user.getUid();

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
                     intent.putExtra(Constant.KEY_UID, hisUid);
                        intent.putExtra(Constant.KEY_NAME, name);
                     intent.putExtra(Constant.KEY_IMAGE, image);
                     context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


    }

    public void setLastMessageMap (String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImageUser;
        ImageView onlineStatus;
        TextView nameUser, lastMessageUser;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            profileImageUser = itemView.findViewById(R.id.profileImageUser);
            onlineStatus = itemView.findViewById(R.id.onlineStatus);
            nameUser = itemView.findViewById(R.id.nameUser);
            lastMessageUser = itemView.findViewById(R.id.lastMessageUser);
        }
    }

}

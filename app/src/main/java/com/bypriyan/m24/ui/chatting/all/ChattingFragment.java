package com.bypriyan.m24.ui.chatting.all;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bypriyan.m24.R;
import com.bypriyan.m24.adapter.AdapterChatlist;
import com.bypriyan.m24.databinding.FragmentChattingBinding;
import com.bypriyan.m24.databinding.FragmentCommunityBinding;
import com.bypriyan.m24.model.ModelChat;
import com.bypriyan.m24.model.ModelChatlist;
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

import java.util.ArrayList;
import java.util.List;


public class ChattingFragment extends Fragment {

    private FragmentChattingBinding binding;
    List<ModelChatlist> chatlistsList;
    List<ModelUsers> usersList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatlist adapterChatlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChattingBinding.inflate(getLayoutInflater());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatlistsList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATLIST).child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chatlistsList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelChatlist chatlist = ds.getValue(ModelChatlist.class);
                    chatlistsList.add(chatlist);

                }
                loadChats();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }

    private void loadChats() {

        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelUsers users = ds.getValue(ModelUsers.class);

                    for(ModelChatlist chatlist: chatlistsList){
                        if(users.getUid() != null && users.getUid().equals(chatlist.getId())){
                            usersList.add(users);
                            break;

                        }
                    }
                    //adapter
                    adapterChatlist = new AdapterChatlist(getContext(),usersList);
                    binding.recyclearView.setAdapter(adapterChatlist);

                    for(int i=0; i< usersList.size(); i++){
                        lastMessage(usersList.get(i).getUid());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void lastMessage(String userId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_CHATS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat == null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String reciver = chat.getReciver();

                    if(sender == null || reciver== null){
                        continue;
                    }

                    if(chat.getReciver().equals(currentUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReciver().equals(userId) && chat.getSender().equals(currentUser.getUid())){

                        if(chat.getType().equals("image")){
                            theLastMessage = "send a photo";
                        }else{
                            theLastMessage = chat.getMessage();
                        }

                    }
                }

                adapterChatlist.setLastMessageMap(userId, theLastMessage);
                adapterChatlist.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}
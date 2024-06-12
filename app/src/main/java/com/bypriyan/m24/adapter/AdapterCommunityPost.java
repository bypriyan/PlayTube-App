package com.bypriyan.m24.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.m24.R;
import com.bypriyan.m24.model.ModelComment;
import com.bypriyan.m24.model.ModelCommunityPost;
import com.bypriyan.m24.utility.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCommunityPost extends RecyclerView.Adapter<AdapterCommunityPost.HolderCommunityPost> {

    Context context;
    ArrayList<ModelCommunityPost> postList;
    String myUid;
    private DatabaseReference likeRef;
    private DatabaseReference postsRef;
    public AlertDialog alertDialog;
    private AdapterComment adapterComment;
    private boolean mProcessLike = false;
    private FirebaseUser user;
    private ExecutorService executorService;
    private ArrayList<ModelComment> commentArrayList;

    public AdapterCommunityPost(Context context, ArrayList<ModelCommunityPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef = FirebaseDatabase.getInstance().getReference().child(Constant.KEY_LIKES);
        postsRef = FirebaseDatabase.getInstance().getReference().child(Constant.KEY_COMMUNITY_POSTS);
        this.user = user = FirebaseAuth.getInstance().getCurrentUser();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @NotNull
    @Override
    public HolderCommunityPost onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_community_posts, parent, false);

        return new HolderCommunityPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterCommunityPost.HolderCommunityPost holder, int position) {
        ModelCommunityPost modelCommunityPost = postList.get(position);
        final String uid = modelCommunityPost.getUid();
        String uName = modelCommunityPost.getName();
        String uDp = modelCommunityPost.getImage();
        String pId = modelCommunityPost.getPostId();
        String pTitle = modelCommunityPost.getTitle();
        String pDescriptio = modelCommunityPost.getPostDescription();
        String pTime = modelCommunityPost.getPostTime();
        String pImage = modelCommunityPost.getPostImage();
        String pLiked = modelCommunityPost.getPostLikes();
        String commentCount = modelCommunityPost.getPostComments();

        //

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(Long.parseLong(pTime));
        String postTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        //

        holder.userName.setText(uName);
        holder.dateTime.setText(postTime);
        holder.pTitle.setText(pTitle);
        holder.pDescription.setText(pDescriptio);
        holder.pLikes.setText(pLiked +" Like");
        holder.pComments.setText(commentCount+" Comments");

        setLikes(holder, pId);

        Glide
                .with(context)
                .load(uDp)
                .centerCrop()
                .placeholder(R.drawable.ic_person)
                .into(holder.profileImage);


        if(pImage.equals("noImage")){

            holder.postImage.setVisibility(View.GONE);
            holder.imageBg.setVisibility(View.GONE);

        }else{

            holder.postImage.setVisibility(View.VISIBLE);
            holder.imageBg.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .load(pImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image)
                    .into(holder.postImage);
        }


        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOptions(holder.moreBtn, uid, myUid, pId, pImage);
            }
        });

        holder.LikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pLikes = Integer.parseInt(postList.get(holder.getAdapterPosition()).getPostLikes());
                mProcessLike = true;
                String postIde = postList.get(position).getPostId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(mProcessLike){
                            if(snapshot.child(postIde).hasChild(myUid)){
                                postsRef.child(postIde).child(Constant.KEY_POST_LIKES).setValue(""+(pLikes-1));
                                likeRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }else{
                                postsRef.child(postIde).child(Constant.KEY_POST_LIKES).setValue(""+(pLikes+1));
                                likeRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike = false;

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        });

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCommentBottomSheet(pId);
            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareTextOnly(pTitle, pDescriptio);

            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                loadComments(pId, holder);
            }
        });

    }

    private void loadComments(String pId, AdapterCommunityPost.HolderCommunityPost holder) {
        commentArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_COMMUNITY_POSTS);
        reference.child(pId).child(Constant.KEY_COMMENT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                commentArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelComment modelComment = ds.getValue(ModelComment.class);
                    commentArrayList.add(modelComment);
                }
                adapterComment = new AdapterComment(context, commentArrayList);
                holder.pComments.setText(""+commentArrayList.size());
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void openCommentBottomSheet(String postId) {
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_comments, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        ImageView close = view.findViewById(R.id.close);
        ImageView backBtn = view.findViewById(R.id.backBtn);
        RecyclerView recyclearComments = view.findViewById(R.id.recyclearComments);
        EditText commentEd = view.findViewById(R.id.commentEd);
        TextView applyBtn = view.findViewById(R.id.applyBtn);

        alertDialog = builder.create();
        recyclearComments.setAdapter(adapterComment);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        applyBtn.setOnClickListener(v -> {
            if (commentEd.getText().toString().isEmpty()) {
                commentEd.setError("Empty");
                commentEd.requestFocus();
            } else {
                String comment = commentEd.getText().toString().trim();
                uploadComment(comment, postId, commentEd);
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void uploadComment(String comment, String pId, EditText commentEd) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_COMMENT_TEXT, ""+comment);
        hashMap.put(Constant.KEY_COMMENT_TIMESTAMP, ""+timestamp);
        hashMap.put(Constant.KEY_COMMENT_USER_UID, ""+user.getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_COMMUNITY_POSTS);
        reference.child(pId).child(Constant.KEY_COMMENT).child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        commentEd.setText("");
                        alertDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void shareImageAndText(String pTitle, String pDescriptio, Bitmap bitmap) {

        String shareBody = pTitle+"\n"+pDescriptio;
        Uri uri = shaveImageToShare(bitmap);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        shareIntent.setType("image/png");
        context.startActivity(Intent.createChooser(shareIntent,"Share Via"));

    }

    private Uri shaveImageToShare(Bitmap bitmap) {

        File imageFolder = new File(context.getCacheDir(),"images");
        Uri uri = null;

        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_images.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.bypriyan.infomate.fileprovider", file);

        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return uri;


    }

    private void shareTextOnly(String pTitle, String pDescriptio) {

        String shareBody = pTitle+"\n"+pDescriptio;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(shareIntent,"Share Via"));

    }

    private void setLikes(@NotNull HolderCommunityPost holder, String postKey) {

    likeRef.addValueEventListener(new ValueEventListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

            if(snapshot.child(postKey).hasChild(myUid)){
                holder.LikeBtn.setImageResource(R.drawable.liked);
            }
//            else{
//                holder.LikeBtn.setColorFilter(R.color.green);
//                holder.LikeBtn.setBackgroundColor(R.color.red);
//            }
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });

    }

    private void showMoreOptions(ImageView moreBtn, String uid, String myUid, String pId, String pImage) {

        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0 ,"Delete");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                if(id==0){

                    beginDelete(pId, pImage);
                }
                return false;
            }
        });
        popupMenu.show();

    }

    private void beginDelete(String pId, String pImage) {

        if(pImage.equals("noImage")){
            deleteWithoutImage(pId);
        }else{

            deleteWithImage(pId, pImage);
        }

    }

    private void deleteWithImage(String pId, String pImage) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Query fQuery = FirebaseDatabase.getInstance().getReference(Constant.KEY_COMMUNITY_POSTS);
                fQuery.orderByChild(Constant.KEY_POST_ID).equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context, "Post Deleted successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
            progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteWithoutImage(String pId) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");

        Query fQuery = FirebaseDatabase.getInstance().getReference(Constant.KEY_COMMUNITY_POSTS);
        fQuery.orderByChild(Constant.KEY_POST_ID).equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Post Deleted successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }


    class HolderCommunityPost extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView userName, dateTime, pTitle, pDescription, pLikes, pComments;
        ImageView moreBtn, LikeBtn, commentBtn, shareBtn, postImage;
        MaterialCardView imageBg;
        LinearLayout profileLayout;

      public HolderCommunityPost(@NonNull @NotNull View itemView) {
          super(itemView);
          profileImage = itemView.findViewById(R.id.profileImage);
          userName = itemView.findViewById(R.id.userName);
          dateTime = itemView.findViewById(R.id.dateTime);
          pTitle = itemView.findViewById(R.id.pTitle);
          pLikes = itemView.findViewById(R.id.pLikes);
          pDescription = itemView.findViewById(R.id.pDescription);
          moreBtn = itemView.findViewById(R.id.moreBtn);
          LikeBtn = itemView.findViewById(R.id.likeBtn);
          commentBtn = itemView.findViewById(R.id.commentBtn);
          shareBtn = itemView.findViewById(R.id.shareBtn);
          postImage = itemView.findViewById(R.id.pImage);

          imageBg = itemView.findViewById(R.id.imageBg);
          pComments = itemView.findViewById(R.id.pComments);
          profileLayout = itemView.findViewById(R.id.profileLayout);

      }
  }

}

package com.bypriyan.m24.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.bypriyan.m24.R;
import com.bypriyan.m24.other.SliderItems;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class SliderAdapterViewPager extends RecyclerView.Adapter<SliderAdapterViewPager.SliderViewPagerViewHolder>{

    private ArrayList<SliderItems> sliderImages;
    private ViewPager2 viewPager2;
    private Context context;

    public SliderAdapterViewPager(ArrayList<SliderItems> sliderImages, ViewPager2 viewPager2, Context context) {
        this.sliderImages = sliderImages;
        this.viewPager2 = viewPager2;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public SliderViewPagerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new SliderViewPagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_slider_item_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SliderAdapterViewPager.SliderViewPagerViewHolder holder, int position) {
       SliderItems sliderItems = sliderImages.get(position);
       int animations = sliderItems.getAnimations();
       String textTitle = sliderItems.getTextTitle();
       String textDescription = sliderItems.getTextDescription();

       try {
           holder.textTitle.setText(textTitle);
           holder.textDescription.setText(textDescription);
           holder.lottieAnimation.setAnimation(animations);
       }catch (Exception e){

       }

    }

    @Override
    public int getItemCount() {
        return sliderImages.size();
    }

    public class SliderViewPagerViewHolder extends RecyclerView.ViewHolder{

        private LottieAnimationView lottieAnimation;
        private TextView textTitle, textDescription;

        SliderViewPagerViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            lottieAnimation = itemView.findViewById(R.id.lottieAnimation);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);

        }
    }

}

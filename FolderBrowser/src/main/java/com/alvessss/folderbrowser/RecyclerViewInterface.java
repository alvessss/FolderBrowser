package com.alvessss.folderbrowser;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

@SuppressWarnings("all")
class RecyclerViewInterface extends RecyclerView.Adapter<RecyclerViewInterface.CustomHolder> {
   private final ArrayList<DataBody> recyclerViewData;

   RecyclerViewInterface(ArrayList<DataBody> recyclerViewDataPtr) {
      recyclerViewData = recyclerViewDataPtr;
   }

   @NonNull @Override
   public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
         .inflate(0, parent, false);

      return new CustomHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull CustomHolder holder, int position) {
      holder.nameView.setText(recyclerViewData.get(position).name);
      holder.pathView.setText(recyclerViewData.get(position).path);
      holder.iconView.setImageDrawable(recyclerViewData.get(position).icon);
   }

   @Override
   public int getItemCount() {
      return recyclerViewData.size();
   }

   static class DataBody {
      String name;
      String path;
      Drawable icon;
   }

   static class CustomHolder extends RecyclerView.ViewHolder {
      final TextView nameView;
      final TextView pathView;
      final ImageView iconView;

      public CustomHolder(@NonNull View itemView) {
         super(itemView);
         nameView = (TextView)itemView.findViewById(0);
         pathView = (TextView)itemView.findViewById(0);
         iconView = (ImageView)itemView.findViewById(0);
      }
   }
}
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
      return new CustomHolder(
         LayoutInflater.from(parent.getContext())
            .inflate(R.layout.folder_browser_item, parent, false)
      );
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
         nameView = (TextView)itemView.findViewById(R.id.text_view_for_inode_name);
         pathView = (TextView)itemView.findViewById(R.id.text_view_for_inode_path);
         iconView = (ImageView)itemView.findViewById(R.id.image_view_for_inode_icon);
      }
   }
}
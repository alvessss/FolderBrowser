package com.alvessss.folderbrowser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

@SuppressWarnings("all")
class RecyclerViewInterface extends RecyclerView.Adapter<RecyclerViewInterface.CustomHolder> {
   public static final int SPAN_COUNT = 4;
   private final ArrayList<DataBody> recyclerViewData;
   private final RecyclerView recyclerView;

   RecyclerViewInterface(Context context) {
      recyclerViewData = new ArrayList<>();
      // recyclerView = ((AppCompatActivity)context).findViewById(R.id.recycler_view_for_directory_content);
      recyclerView = (RecyclerView) ((AppCompatActivity)context).findViewById(R.id.recycler_view_for_directory_content);
      recyclerView.setVisibility(RecyclerView.VISIBLE);

      GridLayoutManager layoutManager = new GridLayoutManager(context, SPAN_COUNT);
      RelativeLayout.LayoutParams params = new
         RelativeLayout.LayoutParams(
         RelativeLayout.LayoutParams.MATCH_PARENT,
         RelativeLayout.LayoutParams.WRAP_CONTENT
      );

      recyclerView.setAdapter((RecyclerView.Adapter)this);
      recyclerView.setLayoutParams(params);
      recyclerView.setLayoutManager(layoutManager);
      recyclerView.setVisibility(View.VISIBLE);
   }

   public RecyclerView getRecyclerViewObj() {
      return recyclerView;
   }

   public void updateScreen() {
      notifyDataSetChanged();
   }

   public void addItem(DataBody dataBody) {
      recyclerViewData.add(dataBody);
   }

   public void removeItem(int index) {
      if (index <= recyclerViewData.size()){
         recyclerViewData.remove(index);
      }
   }

   public void setItem(DataBody dataBody, int index) {
      recyclerViewData.set(index, dataBody);
   }

   public void addArray(DataBody[] dataBodyArray) {
      for (int i = 0; i < dataBodyArray.length; i++) {
         recyclerViewData.add(dataBodyArray[i]);
      }
   }

   public void clear() {
      recyclerViewData.clear();
      notifyDataSetChanged();
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
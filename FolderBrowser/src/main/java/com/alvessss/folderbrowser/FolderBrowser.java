package com.alvessss.folderbrowser;


import java.io.File;
import java.util.ArrayList;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.graphics.drawable.Drawable;

@SuppressWarnings("all")
public class FolderBrowser
{
   private final AppCompatActivity activity;
   private final RecyclerViewData recyclerViewData;
   private final FileSupport[] supportedFiles;

   public FolderBrowser(final AppCompatActivity activity,
      final RecyclerViewData recyclerViewData, final FileSupport[] supportedFiles)
   {
      this.activity = activity;
      this.recyclerViewData = recyclerViewData;

      // TODO: validations on array:
      this.supportedFiles = supportedFiles;

      if (this.activity == null)
      {
         DEBUG.throwError("ACTIVITY CANNOT BE NULL!");
         return;
      }

      if (!this.recyclerViewData.checkFields())
      {
         DEBUG.throwError("RECYCLER VIEW DATA > MISSING SOME ID'S... bye");
         return;
      }

      this.recyclerViewData.setAdapter();
      this.recyclerViewData.setView(this.activity);
   }

   public static class RecyclerViewData
   {
      public int id;
      public int itemLayoutId;
      public int columns = 6;

      public int defaultFileIcon;
      public int defaultDirectoryIcon;

      public int textViewForInodeName;
      public int imageViewForInodeIcon;
      public int textViewForInodePath;

      private RecyclerView recyclerView;
      private Adapter adapter;
      private ArrayList<InodeModel> inodeModel;

      private boolean setAdapter()
      {
         adapter = new Adapter();
         inodeModel = new ArrayList<>();
         return true;
      }

      private boolean setView(final AppCompatActivity viewActivity)
      {
         recyclerView = (RecyclerView) viewActivity.findViewById(id);
         recyclerView.setAdapter(adapter);
         return DEBUG.checkView(recyclerView, id, true);
      }

      private boolean checkFields()
      {
         return
            DEBUG.checkId(id) &&
               DEBUG.checkId(itemLayoutId) &&
               DEBUG.checkId(defaultFileIcon) &&
               DEBUG.checkId(defaultDirectoryIcon) &&
               DEBUG.checkId(textViewForInodeName) &&
               DEBUG.checkId(imageViewForInodeIcon);
      }

      private class InodeModel
      {
         String inodeName;
         Drawable inodeIcon;
      }

      private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
      {
         @Override public void onBindViewHolder(Adapter.ViewHolder viewHolder, int position)
         {
            viewHolder.textView.setText(inodeModel.get(position).inodeName);
            viewHolder.imageView.setImageDrawable(inodeModel.get(position).inodeIcon);
         }

         @Override public Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
         {
            View itemView = LayoutInflater.from(viewGroup.getContext())
               .inflate(itemLayoutId, viewGroup, false);

            return new Adapter.ViewHolder(itemView);
         }

         @Override public int getItemCount()
         {
            return inodeModel.size();
         }

         class ViewHolder extends RecyclerView.ViewHolder
         {
            TextView textView;
            ImageView imageView;

            ViewHolder(View itemView)
            {
               super(itemView);

               boolean logging = true;

               textView = itemView.findViewById(textViewForInodeName);
               if (!DEBUG.checkView(textView, textViewForInodeName, logging))
               {
                  DEBUG.throwError("textView is null");
               }

               imageView = itemView.findViewById(imageViewForInodeIcon);
               if (!DEBUG.checkView(imageView, imageViewForInodeIcon, logging))
               {
                  DEBUG.throwError("imageView is null");
               }
            }
         }
      }
   }

   // class: DirectoryContent:
      // class: Inode
         // method: Inode[] getFiles
         // method: Inode[] getDirs
         // member: Inode[] childInodes

   private static class Inode
   {
      String path;
      String name;
      Type type;
      Inode[] childs;

      // TODO: testing
      static Inode[] getTreeFrom(String inodePath)
      {
         File rootFile = new File(inodePath);
         if (rootFile == null)
         {
            return null;
         }

         File[] listedFiles = rootFile.listFiles();
         if (listedFiles.length == 0)
         {
            return null;
         }

         ArrayList<Inode> tempInodeArr = new ArrayList<>();
         for (int i = 0; i < listedFiles.length; i++)
         {
            Inode currInode = new Inode();

            currInode.name = listedFiles[0].getName();
            currInode.path = listedFiles[0].getAbsolutePath();
            currInode.type = listedFiles[0].isFile() ? Type.FILE : Type.DIRECTORY;
            currInode.childs = getTreeFrom(currInode.path);

            tempInodeArr.add(currInode);
         }

         return tempInodeArr.toArray(new Inode[tempInodeArr.size()]);
      }

      static enum Type
      {
         FILE(0), DIRECTORY(1);
         int val;
         Type(int value)
         {
            val = value;
         }
      }
   }

   private static abstract class DEBUG
   {
      static void throwError(String msg)
      {
         new RuntimeException(msg);
      }

      static boolean checkId(int viewId)
      {
         if (viewId == 0)
         {
            return false;
         }
         return true;
      }

      static boolean checkView(View view, int id, boolean log)
      {
         if (view == null)
         {
            if (log)
            {
               Log.d("VIEW NOT FOUND", "id: " + Integer.toString(id));
            }
            return false;
         }

         return true;
      }
   }
}

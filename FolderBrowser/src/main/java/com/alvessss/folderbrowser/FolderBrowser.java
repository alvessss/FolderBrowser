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

   public static class Inode
   {
      public String path;
      public String name;
      public Type type;
      public Inode[] childs;

      private Inode()
      {
         ;
      }

      public static Inode getInode(String inotePath, boolean recursive)
      {
         File file = new File(inotePath);
         if (file == null) return  null;

         Inode inode = new Inode();
         inode.name = file.getName();
         inode.path = file.getAbsolutePath();
         inode.type = file.isFile() ? Type.FILE : Type.DIRECTORY;
         if (recursive)
         {
            inode.childs = getInodeTree(inode.path);
         }
         else
         {
            inode.childs = new Inode[0];
         }

         return inode;
      }

      public static Inode[] getInodeTree(String inodePath)
      {
         File root = new File(inodePath);
         if (root == null) return new Inode[0];

         File[] childs = root.listFiles();
         if (childs == null) return new Inode[0];

         Inode[] tree = new Inode[childs.length];
         int index = 0;
         for (File child : childs)
         {
            Inode inode = new Inode();
            inode.name = child.getName();
            inode.path = child.getAbsolutePath();
            inode.type = child.isFile() ? Type.FILE : Type.DIRECTORY;
            inode.childs = getInodeTree(inode.path);
            tree[index++] = inode;
         }

         return tree;
      }

      public static void extractPathsFromInodeTree(ArrayList<String> ptr, Inode inodeTree)
      {
         for (Inode inode : inodeTree.childs)
         {
            ptr.add(inode.path);
            extractPathsFromInodeTree(ptr, inode);
         }
      }

      public static enum Type
      {
         FILE(0), DIRECTORY(1);
         int val;
         Type(int value)
         {
            val = value;
         }
      }
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

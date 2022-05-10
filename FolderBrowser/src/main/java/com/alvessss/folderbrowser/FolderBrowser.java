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

   /**
    * A Inode abstracts a amount of real data in the disk to
    * a simple structure containing, basically, a name, a path,
    * a type (File or Directory), and its data (obviously). If
    * a inode is Directory-Type, its data is the paths of its childs.
    *
    * Our Inode is even simple than that, it have no data. If the inode is
    * a File the field Childs must be a array with length 0, if is a Directory,
    * Childs must be a array of the child inodes.
    *
    * "Why it have no data?" > Because you could easily retrieve its data with
    * the java.io.File library passing the Inode.path to it.
    */
   public static class Inode
   {
      private String path;
      private String name;
      private Type type;
      private Inode[] childs;

      private Inode()
      {
         ;
      }

      /**
       * Return a inode getted by the inodePath.
       * @param inodePath The path of the inode in the filesystem.
       * @param recursive If true, get all the child inodes.
       * @return A Inode Object, or null if the inodePath cannot be resolved.
       */
      public static Inode getInode(String inodePath, boolean recursive)
      {
         File file = new File(inodePath);
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

      /**
       * Get all the childs from a specific inode root.
       * @param inodePath the path of the root inode.
       * @return A array of inodes getted from the root inode, or null if the
       * root inode does not have childs or does not exists.
       */
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

      /**
       * Extract only the path field of a Inode and pass it
       * to the ArrayList pointer. If the Inode have childs,
       * get the path of all of them.
       * @param ptr The ArrayList to put the paths.
       * @param inodeTree the Inode to extract the paths.
       */
      public static void extractPathsFromInodeTree(ArrayList<String> ptr, Inode inodeTree)
      {
         for (Inode inode : inodeTree.childs)
         {
            ptr.add(inode.path);
            extractPathsFromInodeTree(ptr, inode);
         }
      }

      public String getName()
      {
         return name;
      }

      public String getPath()
      {
         return path;
      }

      public Type getType()
      {
         return type;
      }

      public Inode[] getChilds()
      {
         return childs;
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

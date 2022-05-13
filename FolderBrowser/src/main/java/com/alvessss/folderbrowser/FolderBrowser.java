package com.alvessss.folderbrowser;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;
import androidx.annotation.NonNull;
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
   private final AppCompatActivity parentActivity;
   private final FileSupport[] supportedFiles;
   private final RecyclerViewHandler recyclerViewHandler;

   private Inode root;
   private Inode defaultRoot;

   private Theme theme;

   public FolderBrowser(@NonNull final AppCompatActivity activity,
      @NonNull final RecyclerViewData recyclerViewData,
      final FileSupport[] supportedFiles)
   {
     parentActivity = activity;

     if (!recyclerViewData.checkFields())
     {
        DEBUG.warning("Resource IDS cannot be null");
        DEBUG.throwError("Set properly the RecyclerViewData! Bye.");
     }

      recyclerViewHandler = new RecyclerViewHandler(recyclerViewData);

     if (supportedFiles == null)
     {
        this.supportedFiles = new FileSupport[0];
     }
     else
     {
        this.supportedFiles = supportedFiles;
     }

   }

   private void setRecyclerView(RecyclerViewData rvdata)
   {
      ;
   }

   private void setTheme(Theme theme)
   {
      if  (!theme.checkFields())
      {
         DEBUG.warning("Resource IDS cannot be null");
         DEBUG.throwError("Set properly the Theme fields! Bye.");
      }

      this.theme = theme;
   }

   public static class Theme
   {
      private static final Theme DEFAULT;
      static
      {
         DEFAULT = new Theme();
         DEFAULT.name = "Ocean";
         DEFAULT.folderColor = R.color.ocean_blue_foreground;
         DEFAULT.iconColor = R.color.ocean_white_foreground;
         DEFAULT.backgroundColor = R.color.ocean_blue_background;
      }

      private int folderColor = DEFAULT.folderColor;
      private int iconColor = DEFAULT.iconColor;;
      private int backgroundColor = DEFAULT.backgroundColor;
      private String name = DEFAULT.name;

      public void setFolderColor(int resourceId)
      {
         folderColor = resourceId;
      }

      public void setIconColor(int resourceId)
      {
         iconColor = resourceId;
      }

      public void setBackgroundColor(int resourceId)
      {
         backgroundColor = resourceId;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      private boolean checkFields()
      {
         return DEBUG.checkId(folderColor) &&
            DEBUG.checkId(iconColor) &&
            DEBUG.checkId(backgroundColor) &&
            name != null;
      }
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
         FILE(0),
         DIRECTORY(1);

         int val;
         Type(int value)
         {
            val = value;
         }
      }
   }

   public static class RecyclerViewData
   {
      private static final int COLUMNS = 4;
      private static final int ID = R.id.recycler_view_for_directory_content;
      private static final int ITEM = R.layout.folder_browser_item;
      private static final int TEXT_VIEW_FOR_INODE_NAME = R.id.text_view_for_inode_name;
      private static final int IMAGE_VIEW_FOR_INODE_ICON = R.id.image_view_for_inode_icon;
      private static final int TEXT_VIEW_FOR_CURRENT_PATH = R.id.text_view_for_current_path;
      private static final int DEFAULT_ICON_FOR_FILE = R.drawable.default_icon_for_file;
      private static final int DEFAULT_ICON_FOR_DIRECTORY = R.drawable.default_icon_for_directory;

      private int columns = COLUMNS;
      private int fileIcon = DEFAULT_ICON_FOR_FILE;
      private int directoryIcon = DEFAULT_ICON_FOR_DIRECTORY;

      public void setColumns(int columns)
      {
         this.columns = columns;
      }

      public void setFileIcon(int resourceId)
      {
         this.fileIcon = resourceId;
      }

      public void setDirectoryIcon(int resourceId)
      {
         this.directoryIcon = resourceId;
      }

      private boolean checkFields()
      {
         return DEBUG.checkId(fileIcon) &&
            DEBUG.checkId(directoryIcon) &&
            columns >= 1;
      }
   }

   private class RecyclerViewHandler
   {
      RecyclerView recyclerView;
      Adapter adapter;
      ArrayList<InodeModel> inodeData = new ArrayList<>();

      final RecyclerViewData recyclerViewData;

      RecyclerViewHandler(@NonNull final RecyclerViewData recyclerViewData)
      {
         this.recyclerViewData = recyclerViewData;
      }

      class InodeModel
      {
         String inodeName;
         Drawable inodeIcon;
      }

      private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
      {
         @Override public void onBindViewHolder(Adapter.ViewHolder viewHolder, int position)
         {
            viewHolder.textView.setText(inodeData.get(position).inodeName);
            viewHolder.imageView.setImageDrawable(inodeData.get(position).inodeIcon);
         }

         @Override public Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
         {
            View itemView = LayoutInflater.from(viewGroup.getContext())
               .inflate(RecyclerViewData.ITEM, viewGroup, false);

            return new Adapter.ViewHolder(itemView);
         }

         @Override public int getItemCount()
         {
            return inodeData.size();
         }

         class ViewHolder extends RecyclerView.ViewHolder
         {
            TextView textView;
            ImageView imageView;

            ViewHolder(View itemView)
            {
               super(itemView);

               boolean logging = true;

               textView = itemView.findViewById(RecyclerViewData.TEXT_VIEW_FOR_INODE_NAME);
               if (!DEBUG.checkView(textView, RecyclerViewData.TEXT_VIEW_FOR_INODE_NAME, logging))
               {
                  DEBUG.throwError("textView is null");
               }

               imageView = itemView.findViewById(RecyclerViewData.IMAGE_VIEW_FOR_INODE_ICON);
               if (!DEBUG.checkView(imageView, RecyclerViewData.IMAGE_VIEW_FOR_INODE_ICON, logging))
               {
                  DEBUG.throwError("imageView is null");
               }
            }
         }
      }
   }

   private static abstract class DEBUG
   {
      static final String TAG = "FolderBrowser says";
      static void throwError(String msg)
      {
         new RuntimeException(msg);
      }

      static void warning(String msg)
      {
         Log.i(TAG, msg);
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

package com.alvessss.folderbrowser;

import java.io.File;
import java.util.ArrayList;

import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.graphics.drawable.Drawable;

// TODO: set RecyclerView callbacks

@SuppressWarnings("all")
public class FolderBrowser
{
   private final ViewGroup container;
   private final AppCompatActivity parentActivity;
   private final FileSupport[] supportedFiles;
   private final RecyclerViewHandler recyclerViewHandler;

   private Inode rootInode;
   private Inode currentInode;

   private ImageView lastHighlightedFileIcon;

   private Theme theme;

   private final View.OnClickListener onClickFolder =
      new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            TextView tvFolderPath = view.findViewById(RecyclerViewData.TEXT_VIEW_FOR_INODE_PATH);
            String folderPath = tvFolderPath.getText().toString();
            changeDirectoryTo(folderPath);
         }
   };

   private final View.OnClickListener onClickFile =
      new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            resetHighlightedFileIcons(lastHighlightedFileIcon);
            TextView tvFilePath = view.findViewById(RecyclerViewData.TEXT_VIEW_FOR_INODE_PATH);
            String filePath = tvFilePath.getText().toString();
            ImageView iconFile = view.findViewById(RecyclerViewData.IMAGE_VIEW_FOR_INODE_ICON);
            highlightFileIcon(iconFile);
         }
   };

   public FolderBrowser(
      @NonNull final ViewGroup container,
      @NonNull final AppCompatActivity activity,
      @NonNull final RecyclerViewData recyclerViewData,
      final FileSupport[] supportedFiles)
   {
     this.container = container;
     parentActivity = activity;

     if (!recyclerViewData.checkFields())
     {
        DEBUG.warning("tip 1: Resource IDS cannot be null");
        DEBUG.warning("tip 2: recyclerViewData.columns must be equal or greater than 1");
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

   public void startSearch(Inode choosenInode)
   {
      View folderBrowserView = LayoutInflater.from(parentActivity)
         .inflate(R.layout.folder_browser_layout, container, false);

      new Handler().post(()->container.addView(folderBrowserView));
      changeDirectoryTo(rootInode.path);
   }

   public void setTheme(Theme theme)
   {
      if  (!theme.checkFields())
      {
         DEBUG.warning("tip 1: Resource IDS cannot be null");
         DEBUG.warning("tip 2: theme.name cannot be null");
         DEBUG.throwError("Set properly the Theme fields! Bye.");
      }

      this.theme = theme;
   }

   private void setRecyclerView(RecyclerViewData rvdata)
   {
      ;
   }

   private void changeDirectoryTo(String newDirectory)
   {
      currentInode = Inode.getInode(newDirectory, false);
      ArrayList<RecyclerViewHandler.InodeModel> newInodeData = new ArrayList<>();
      RecyclerViewHandler.InodeModel model;
      FileSupport fileSupport;

      for (Inode child : currentInode.childs)
      {
         model = recyclerViewHandler.new InodeModel();
         fileSupport = FileSupport.classify(supportedFiles, currentInode.name);

         if (fileSupport != null)
         {
            model.inodeIcon = ResourcesCompat.getDrawable(parentActivity.getResources(), fileSupport.icon, null);
         }
         else
         {
            if (child.isFile())
            {
               model.inodeIcon = ResourcesCompat.getDrawable(parentActivity.getResources(), RecyclerViewData.DEFAULT_ICON_FOR_FILE, null);
            }
            else
            {
               model.inodeIcon = ResourcesCompat.getDrawable(parentActivity.getResources(), RecyclerViewData.DEFAULT_ICON_FOR_DIRECTORY, null);
            }
         }

         model.inodeName = child.name;
         model.inodePath = child.path;
         model.type = child.type;
         newInodeData.add(model);
      }

      assert recyclerViewHandler != null;
      recyclerViewHandler.inodeData = newInodeData;
      recyclerViewHandler.adapter.notifyDataSetChanged();
   }

   private void highlightFileIcon(ImageView ivFileIcon)
   {
      ivFileIcon.setColorFilter(theme.clickedIconColor);
      lastHighlightedFileIcon = ivFileIcon;
   }

   private void resetHighlightedFileIcons(ImageView highlightedFileIcon)
   {
      if (highlightedFileIcon != null)
      {
         highlightedFileIcon.setColorFilter(theme.iconColor);
      }
   }

   public static class FileSupport
   {
      public String name;
      public int icon;
      public String[] extensions;

      public static FileSupport[] getArray(int n)
      {
         FileSupport[] fileSupportArray = new FileSupport[n];
         for (FileSupport fs : fileSupportArray)
         {
            fs = new FileSupport();
         }

         return fileSupportArray;
      }

      public static FileSupport classify(FileSupport[] supportedFiles, String fileName)
      {
         for (FileSupport supportedFile : supportedFiles)
         {
            for (String supportedExtension : supportedFile.extensions)
            {
               if (supportedExtension.equals(getExt(fileName)));
               {
                  return supportedFile;
               }
            }
         }

         return null;
      }

      public static String getExt(String filePath)
      {
         int strLength = filePath.lastIndexOf(".");
         if (strLength == 0)
         {
            return null;
         }

         return filePath.substring(strLength + 1).toLowerCase();
      }

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
         DEFAULT.clickedIconColor = R.color.ocean_gray_foreground;
         DEFAULT.backgroundColor = R.color.ocean_blue_background;
      }

      private int folderColor = DEFAULT.folderColor;
      private int iconColor = DEFAULT.iconColor;
      private int clickedIconColor = DEFAULT.clickedIconColor;
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

      public boolean isFile()
      {
         return type == Type.FILE;
      }

      public boolean isDirectory()
      {
         return type == Type.DIRECTORY;
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
      private static final int TEXT_VIEW_FOR_INODE_PATH = R.id.text_view_for_inode_path;
      private static final int TEXT_VIEW_FOR_DIRECTORY_PATH = R.id.text_view_for_directory_path;
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
         String inodePath;
         Inode.Type type;
      }

      private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
      {
         @Override public void onBindViewHolder(Adapter.ViewHolder viewHolder, int position)
         {
            viewHolder.textViewInodeName.setText(inodeData.get(position).inodeName);
            viewHolder.imageViewInodeIcon.setImageDrawable(inodeData.get(position).inodeIcon);
            viewHolder.textViewInodePath.setText(inodeData.get(position).inodePath);

            if (inodeData.get(position).type == Inode.Type.FILE)
            {
               viewHolder.itemView.setOnClickListener(onClickFile);
            }
            else
            {
               viewHolder.itemView.setOnClickListener(onClickFolder);
            }
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
            TextView textViewInodeName;
            ImageView imageViewInodeIcon;
            TextView textViewInodePath;

            ViewHolder(View itemView)
            {
               super(itemView);

               boolean logging = true;

               textViewInodeName = itemView.findViewById(RecyclerViewData.TEXT_VIEW_FOR_INODE_NAME);
               if (!DEBUG.checkView(textViewInodeName, RecyclerViewData.TEXT_VIEW_FOR_INODE_NAME, logging))
               {
                  DEBUG.throwError("textViewInodeName is null");
               }

               imageViewInodeIcon = itemView.findViewById(RecyclerViewData.IMAGE_VIEW_FOR_INODE_ICON);
               if (!DEBUG.checkView(imageViewInodeIcon, RecyclerViewData.IMAGE_VIEW_FOR_INODE_ICON, logging))
               {
                  DEBUG.throwError("imageViewInodeIcon is null");
               }

                textViewInodePath = itemView.findViewById(RecyclerViewData.TEXT_VIEW_FOR_INODE_PATH);
               if (!DEBUG.checkView(textViewInodePath, RecyclerViewData.TEXT_VIEW_FOR_INODE_PATH, logging))
               {
                  DEBUG.throwError("textViewInodePath is null");
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

package com.alvessss.folderbrowser;

import java.util.ArrayList;

import android.os.Environment;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

@SuppressWarnings("all")
public class FolderBrowser
{
   // TODO 1: Apply clean code;
   // TODO 2: Make the icon's sizes be dynnamic according with the size of its parentContainer;
   // TODO 3: Implement the navigation through directories;
   // TODO 4: Apply tests;

   private static final int LAYOUT = R.layout.folder_browser_layout;
   private static final int RETURN_BUTTON = R.id.button_view_for_previous_directory;

   private final ViewGroup parentContainer;
   private final AppCompatActivity parentActivity;

   private final FileSupport[] supportedFiles;
   private final RecyclerViewHandler recyclerViewHandler;

   private Inode rootInode = Inode.getInode(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), true
   );
   private Inode currentInode = rootInode;
   private Inode previousInode = rootInode;

   private Theme theme;

   private FloatingActionButton returnButton;

   private final View.OnClickListener onClickFolder =
      new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            TextView tvFolderPath = view.findViewById(RecyclerViewData.TEXT_VIEW_FOR_INODE_PATH);
            String folderPath = tvFolderPath.getText().toString();
            previousInode = currentInode;
            changeDirectoryTo(folderPath);
         }
      };

   private final View.OnClickListener onClickFile =
      new View.OnClickListener()
      {
         @Override
         public void onClick(View view)
         {
            TextView tvFilePath = view.findViewById(RecyclerViewData.TEXT_VIEW_FOR_INODE_PATH);
            String filePath = tvFilePath.getText().toString();
            ImageView iconFile = view.findViewById(RecyclerViewData.IMAGE_VIEW_FOR_INODE_ICON);
         }
      };

   private final View.OnClickListener onClickReturn =
      new View.OnClickListener() {
         @Override
         public void onClick(View view)
         {
            changeDirectoryTo(currentInode.getParent());
         }
      };

   public FolderBrowser(
      @NonNull final ViewGroup parentContainer,
      @NonNull final AppCompatActivity activity,
      @NonNull final RecyclerViewData recyclerViewData,
      final FileSupport[] supportedFiles)
   {
     this.parentContainer = parentContainer;
     parentActivity = activity;

     if (!recyclerViewData.checkFields())
     {
        DEBUG.warning("tip 1: Resource IDS cannot be null");
        DEBUG.warning("tip 2: recyclerViewData.columns must be equal or greater than 1");
        DEBUG.throwError("Set properly the RecyclerViewData! Bye.");
     }

      recyclerViewHandler = new RecyclerViewHandler(activity, recyclerViewData);

     if (supportedFiles == null)
     {
        this.supportedFiles = new FileSupport[0];
     }
     else
     {
        this.supportedFiles = supportedFiles;
     }

   }

   public void setRootInode(String path)
   {
      rootInode = Inode.getInode(path, false);
   }

   public void startSearch(Inode choosenInode)
   {
      View folderBrowserView = LayoutInflater.from(parentActivity)
         .inflate(FolderBrowser.LAYOUT, parentContainer, false);

      parentContainer.addView(folderBrowserView);

      RecyclerView recyclerView = folderBrowserView.findViewById(recyclerViewHandler.recyclerViewData.ID);

      returnButton = parentContainer.findViewById(RETURN_BUTTON);
      returnButton.setOnClickListener(onClickReturn);

      recyclerView.setAdapter(recyclerViewHandler.adapter);

      changeDirectoryTo(rootInode.getPath());
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
      currentInode = Inode.getInode(newDirectory, true);
      ArrayList<RecyclerViewHandler.InodeModel> newInodeData = new ArrayList<>();
      RecyclerViewHandler.InodeModel model;
      FileSupport fileSupport;

      for (Inode child : currentInode.getChilds())
      {
         model = recyclerViewHandler.new InodeModel();
         fileSupport = FileSupport.classify(supportedFiles, child.getName());

         if (fileSupport != null)
         {
            model.inodeIcon = ResourcesCompat.getDrawable(parentActivity.getResources(), fileSupport.icon, null);
         }
         else
         {
            if (child.isFile() )
            {
               model.inodeIcon = ResourcesCompat.getDrawable(parentActivity.getResources(), RecyclerViewData.DEFAULT_ICON_FOR_FILE, null);
            }
            else
            {
               model.inodeIcon = ResourcesCompat.getDrawable(parentActivity.getResources(), RecyclerViewData.DEFAULT_ICON_FOR_DIRECTORY, null);
            }
         }

         Log.i(DEBUG.TAG, child.getName());
         model.inodeName = child.getName();
         model.inodePath = child.getPath();
         model.type = child.getType();
         newInodeData.add(model);
      }

      assert recyclerViewHandler != null;
      recyclerViewHandler.inodeData = newInodeData;
      recyclerViewHandler.adapter.notifyDataSetChanged();
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

   public static class RecyclerViewData
   {
      private static final int UNDEFINED = 0;
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

      public void setColumns(int n)
      {
         columns = n;
      }

      public void setFileIcon(int resourceId)
      {
         fileIcon = resourceId;
      }

      public void setDirectoryIcon(int resourceId)
      {
         directoryIcon = resourceId;
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
      // TODO: Inflate the RecyclerView's layout and add it to activity before call findviewbyid();

      RecyclerView recyclerView;
      Adapter adapter = new Adapter();
      ArrayList<InodeModel> inodeData = new ArrayList<>();

      final RecyclerViewData recyclerViewData;

      RecyclerViewHandler(@NonNull AppCompatActivity activity, @NonNull final RecyclerViewData recyclerViewData)
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

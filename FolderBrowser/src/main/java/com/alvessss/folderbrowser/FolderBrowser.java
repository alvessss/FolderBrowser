package com.alvessss.folderbrowser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@SuppressWarnings("all")
public class FolderBrowser
{
   private AppCompatActivity parentActivity;
   private Context parentContext;

   private int recyclerViewID;
   private int itemLayoutID;

   private int itemFilenameID;
   private int itemFileIconID;

   private ArrayList<_ItemFields> itemFieldsArr = new ArrayList<>();

   private int defaultFileIcon;
   private int defaultDirectoryIcon;

   @Nullable private FileSupport[] trackedFiles;

   private RecyclerView recyclerView;
   private _RvAdapter adapter = new _RvAdapter(itemFieldsArr);

   private GridLayoutManager gridLayoutManager;
   private int gridLayoutColumns;

   public static void childPaths(String root,
                                 ArrayList<String> pathsArr, String targetMimeType)
   {
      String currentMimeType = "";
      File currentDir = new File(root);
      File[] dirFiles = currentDir.listFiles();
      if (currentDir.exists() == false || dirFiles == null)
      {
         return;
      }

      for (int i = 0; i < dirFiles.length; i++){
         File currentInode = dirFiles[i];
         if (!currentInode.canRead())
         {
            continue;
         }

         else if (currentInode.isDirectory())
         {
            childPaths(currentInode.getAbsolutePath(), pathsArr, targetMimeType);
         }

         if (currentInode.isFile())
         {
            currentMimeType = FolderBrowser.getMimeType(currentInode);
            if (currentMimeType == null || !currentMimeType.equals(targetMimeType))
            {
               continue;
            }
            pathsArr.add(currentInode.getAbsolutePath());
         }
      }
   }

   public static String getMimeType(File file)
   {
      String mimeType = null;
      try
      {
         mimeType = Files.probeContentType(Paths.get(file.toURI()));
      } catch (IOException | NullPointerException e)
      {
         e.printStackTrace();
      }
      return mimeType;
   }

   public static Builder setNew()
   {
      return new Builder();
   }

   private FolderBrowser()
   {
      ;
   }

   public static class Builder
   {
      private FolderBrowser privateInstance = new FolderBrowser();

      private ArrayList<FileSupport> tempTrackedFiles = new ArrayList<>();

      private boolean checkRecyclerView = false;
      private boolean checkItem = false;
      private boolean checkDefIcons = true;

      public Builder setContext(@NonNull Context context)
      {
         privateInstance.parentContext = context;
         privateInstance.parentActivity = (AppCompatActivity) context;
         return this;
      }

      public Builder setRecyclerView(int id)
      {
         return setRecyclerView(id, 6);
      }

      public Builder setRecyclerView(int id, int columns)
      {
         privateInstance.recyclerViewID = id;
         privateInstance.recyclerView = privateInstance.parentActivity
            .findViewById(privateInstance.recyclerViewID);

         if (privateInstance.recyclerView == null)
         {
            new RuntimeException("Recycler view not found");
         }

         privateInstance.gridLayoutManager = new GridLayoutManager(
            privateInstance.parentActivity,
            privateInstance.gridLayoutColumns = columns
         );

         checkRecyclerView = true;
         return this;
      }

      public Builder setRecyclerViewItem(int layoutId, int fileNameId, int fileIconId)
      {
         privateInstance.itemLayoutID = layoutId;
         privateInstance.itemFilenameID = fileNameId;
         privateInstance.itemFileIconID = fileIconId;

         checkItem = true;
         return this;
      }

      public Builder setDefaultIcons(int fileIcon, int directoryIcon)
      {
         privateInstance.defaultFileIcon = fileIcon;
         privateInstance.defaultDirectoryIcon = directoryIcon;

         checkDefIcons = true;
         return this;
      }

      public Builder setNewSupportForFile(FileSupport fileSupport)
      {
         tempTrackedFiles.add(fileSupport);
         return this;
      }

      public FolderBrowser build()
      {
         if (checkRecyclerView && checkItem && checkDefIcons)
         {
            privateInstance.trackedFiles = tempTrackedFiles.toArray(
               new FileSupport[tempTrackedFiles.size()]);

            return privateInstance;
         }
         else
         {
            new RuntimeException("Missing settings. See the docs > https://google.com");
            return null;
         }
      }

      private Builder(){};
   }

   private class _ItemFields
   {
      public String filename;
      public Drawable icon;
   }

   private class _RvAdapter extends
      RecyclerView.Adapter<_RvAdapter.Holder>
   {
      private ArrayList<_ItemFields> itemFields;

      public _RvAdapter(ArrayList<_ItemFields> itemFields)
      {
         this.itemFields = itemFields;
      }

      @NonNull @Override
      public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
      {
         View view = LayoutInflater.from(parentContext)
            .inflate(recyclerViewID, parent, false);

         return new Holder(view);
      }

      @Override
      public void onBindViewHolder(@NonNull Holder holder, int position)
      {
         holder.filename.setText(itemFields.get(position).filename);
         holder.icon.setImageDrawable(itemFields.get(position).icon);
      }

      @Override
      public int getItemCount()
      {
         return itemFields.size();
      }

      public class Holder extends
         RecyclerView.ViewHolder
      {
         TextView filename;
         ImageView icon;

         public Holder(@NonNull View itemView)
         {
            super(itemView);

            filename = itemView.findViewById(itemFilenameID);
            icon = itemView.findViewById(itemFileIconID);
         }
      }
   }
}

package com.alvessss.folderbrowser;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
   public static final int DEFAULT_COLUMNS = 6;

   public static final String PERMISSION_READ = Manifest.permission.READ_EXTERNAL_STORAGE;
   public static final String PERMISSION_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

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

   private boolean _needPermision = true;
   private ActivityResultLauncher<String> permissionLauncher =
      parentActivity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
         if (isGranted)
         {
            _start();
         }
         else
         {
            _permissionNotGranted();
         }
      });

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

   private FolderBrowser() // PRIVATE CONSTRUCTOR OF BUILDER
   {
      ;
   }

   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   private void _start()
   {
      if (!_checkPermission(PERMISSION_READ))
      {
         permissionLauncher.launch(PERMISSION_READ);
         return;
      }

      if (!_checkPermission(PERMISSION_WRITE))
      {
         permissionLauncher.launch(PERMISSION_WRITE);
         return;
      }
   }

   private boolean _checkPermission(String permission)
   {
      if (ContextCompat.checkSelfPermission(parentContext, permission) == PackageManager.PERMISSION_DENIED)
      {
         return false;
      }

      return true;
   }

   private void _permissionNotGranted()
   {
      ;
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public static class Builder
   {
      private FolderBrowser privateInstance = new FolderBrowser();

      private ArrayList<FileSupport> tempTrackedFiles = new ArrayList<>();

      public Builder setContext(@NonNull Context context)
      {
         privateInstance.parentContext = context;
         privateInstance.parentActivity = (AppCompatActivity) context;
         return this;
      }

      public Builder setRecyclerView(int id)
      {
         return setRecyclerView(id, FolderBrowser.DEFAULT_COLUMNS);
      }

      public Builder setRecyclerView(int id, int columns)
      {
         privateInstance.recyclerViewID = id;
         privateInstance.gridLayoutColumns = 6;
         return this;
      }

      public Builder setRecyclerViewItem(int layoutId, int fileNameId, int fileIconId)
      {
         privateInstance.itemLayoutID = layoutId;
         privateInstance.itemFilenameID = fileNameId;
         privateInstance.itemFileIconID = fileIconId;

         return this;
      }

      public Builder setDefaultIcons(int fileIcon, int directoryIcon)
      {
         privateInstance.defaultFileIcon = fileIcon;
         privateInstance.defaultDirectoryIcon = directoryIcon;

         return this;
      }

      public Builder setNewSupportForFile(FileSupport fileSupport)
      {
         tempTrackedFiles.add(fileSupport);
         return this;
      }

      public Builder needPermission(boolean value)
      {
         privateInstance._needPermision = true;
         return this;
      }

      public FolderBrowser build()
      {
         if (_setup())
         {
            return privateInstance;
         }
         else
         {
            new RuntimeException("Missing some settings. Check the log above!");
            return null;
         }
      }

      private Builder() // PRIVATE CONSTRUCTOR OF BUILDER
      {
         ;
      }

      private boolean _setup()
      {
         if ( _checkContext() &&
            _checkRecylerViewAndGridLayout() &&
            _checkRecyclerViewItem() &&
            _checkDefaultIcons() )
         {
            _setTempTrackedFiles();
            return true;
         }

         return false;
      }

      private void _setTempTrackedFiles()
      {
         privateInstance.trackedFiles = tempTrackedFiles.toArray(new FileSupport[tempTrackedFiles.size()]);
      }

      private boolean _checkContext()
      {
         if (privateInstance.parentActivity == null ||
            privateInstance.parentContext == null)
         {
            return false;
         }
         return true;
      }

      private boolean _checkRecylerViewAndGridLayout()
      {
         privateInstance.recyclerView = privateInstance.parentActivity
            .findViewById(privateInstance.recyclerViewID);

         privateInstance.gridLayoutManager = new GridLayoutManager(
            privateInstance.parentContext, (privateInstance.gridLayoutColumns)
         );

         if (privateInstance.recyclerView == null ||
            privateInstance.gridLayoutManager == null)
         {
            return false;
         }

         return true;
      }

      private boolean _checkRecyclerViewItem()
      {
         if ( _checkId(privateInstance.itemLayoutID) &&
            _checkId(privateInstance.itemFilenameID) &&
            _checkId(privateInstance.itemFileIconID) )
         {
            return true;
         }

         return false;
      }

      private boolean _checkDefaultIcons()
      {
         if (_checkId(privateInstance.defaultFileIcon)
            && _checkId(privateInstance.defaultDirectoryIcon))
         {
            return true;
         }
         return false;
      }

      private boolean _checkId(int id)
      {
         if (id != 0)
         {
            return true;
         }

         return false;
      }

      // unused
      private boolean _checkLayout(Context context, int layoutId)
      {

         final View testView;
         try
         {
            testView = LayoutInflater.from(context)
               .inflate(layoutId, null);
            return true;
         }
         catch (Resources.NotFoundException e)
         {
            e.printStackTrace();
            return false;
         }
      }

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

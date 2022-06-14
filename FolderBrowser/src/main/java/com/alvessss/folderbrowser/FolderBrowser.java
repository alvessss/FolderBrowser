package com.alvessss.folderbrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.io.File;

@SuppressWarnings("all")
public class FolderBrowser {
   // Tag for debug.
   private static final String TAG = "FolderBrowser";
   private static final int FILE_ICON_ID = R.drawable.default_icon_for_file;
   private static final int DIRECTORY_ICON_ID = R.drawable.default_icon_for_directory;

   // From the calling context.
   private Context context;
   private AppCompatActivity appCompatActivity;
   private ViewGroup parentViewGroup;

   // Navigation logic.
   private String root;
   private Inode currentInode;
   private Mode mode;

   RecyclerViewInterface recyclerViewInterface;

   /* private constructor */
   private FolderBrowser() {
   }

   public void launch() {
      ViewGroup folderBrowserContent = (ViewGroup) LayoutInflater
         .from(context)
         .inflate(R.layout.folder_browser_layout, null);

      folderBrowserContent.setVisibility(ViewGroup.VISIBLE);
      parentViewGroup.addView(folderBrowserContent);
      recyclerViewInterface = new RecyclerViewInterface(context);
   }

   public void release() {
      recyclerViewInterface.clear();
      parentViewGroup.removeView(recyclerViewInterface.getRecyclerViewObj());
   }

   public void restart() {
      restart();
   }

   public void start() {
      start(root);
   }

   public void restart(String path) {
      start(path);
   }

   public void start(String path) {
      String[] listedPaths = listDirectory(path);
      File[] childrenFiles = new File[listedPaths.length];
      RecyclerViewInterface.DataBody[] inodeDataBody = new RecyclerViewInterface.DataBody[childrenFiles.length];

      for (int i = 0; i < childrenFiles.length; i++) {
         childrenFiles[i] = new File(listedPaths[i]);
         inodeDataBody[i] = new RecyclerViewInterface.DataBody();
         inodeDataBody[i].name = childrenFiles[i].getName();
         inodeDataBody[i].path = childrenFiles[i].getPath();
         inodeDataBody[i].icon = childrenFiles[i].isFile() ?
            (ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_icon_for_file, null)) :
            (ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_icon_for_directory, null));
      }

      recyclerViewInterface.clear();
      recyclerViewInterface.addArray(inodeDataBody);
      recyclerViewInterface.updateScreen();
   }

   private String[] listDirectory(String path) {
      java.io.File rootFile = new java.io.File(path);
      if (rootFile == null) return new String[0];

      java.io.File[] listedFiles = rootFile.listFiles();
      if (listedFiles.length == 0) return new String[0];

      String[] listedPaths = new String[listedFiles.length];
      for (int i = 0; i < listedFiles.length; i++) {
         listedPaths[i] = listedFiles[i].getAbsolutePath();
      }

      return listedPaths;
   }

   public enum Mode {
      DIRECTORY(0), FILE(1);

      public int val;
      Mode(int val){
         this.val = val;
      };
   }

   public static class Builder {
      private static final String TAG = "FolderBrowser.Builder";
      private static final String VALIDATION_OK = "ALL FIELDS ALRIGHT :)";

      public final FolderBrowser building = new FolderBrowser();

      public Builder(final ViewGroup containerView) {
         building.context = containerView.getContext();
         building.appCompatActivity = (AppCompatActivity) building.context;
         building.parentViewGroup = containerView;
      }

      public Builder setRoot(String root) {
         building.root = root;
         return this;
      }

      public FolderBrowser build() {
         return building;
      }
   }
}
package com.alvessss.folderbrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

@SuppressWarnings("all")
public class FolderBrowser extends Filesystem {
   private static final String DEBUG_TAG = "FolderBrowser";

   private final Context context;
   private final AppCompatActivity appCompatActivity;
   private final ViewGroup parentViewGroup;

   private ViewGroup folderBrowserContentViewGroup;
   private RecyclerViewInterface recyclerViewInterface;

   public FolderBrowser(Context context, ViewGroup container, Callback onDoneCallback) {
      super(onDoneCallback);
      this.context = context;
      this.appCompatActivity = (AppCompatActivity) context;
      this.parentViewGroup = container;
   }

   public void launch() {
      // Inflate Folder-Browser's layout.
      folderBrowserContentViewGroup = (ViewGroup) LayoutInflater
         .from(context)
         .inflate(R.layout.folder_browser_layout, null);
      folderBrowserContentViewGroup.setVisibility(ViewGroup.VISIBLE);
      parentViewGroup.addView(folderBrowserContentViewGroup);

      // Init recycler view adapter.
      recyclerViewInterface = new RecyclerViewInterface(context);

      setViews(this);
   }

   public void release() {
      recyclerViewInterface.clear();
      parentViewGroup.removeView(folderBrowserContentViewGroup);
   }

   public void restart() {
      restart(getRoot().getPath());
   }

   public void start() {
      start(getRoot().getPath());
   }

   public void restart(String directoryPath) {
      start(directoryPath);
   }

   public RecyclerViewInterface getRecyclerViewInterface() {
      return recyclerViewInterface;
   }

   public AppCompatActivity getAppCompatActivity() {
      return appCompatActivity;
   }

   public void start(String directoryPath) {
      Directory directory = new Directory(new java.io.File(directoryPath));

      // Update the current inode.
      setCurrentInode((Inode) directory);
      getDirectoryPathView().setText(directory.getPath());

      // List the children of the target directory.
      String[] listedPaths = Directory.listDirectory(directory.getPath());
      java.io.File[] childrenFiles = new java.io.File[listedPaths.length];
      RecyclerViewInterface.DataBody[] inodeDataBody = new RecyclerViewInterface.DataBody[childrenFiles.length];

      // Fill the data array with the children's data.
      for (int i = 0; i < childrenFiles.length; i++) {
         childrenFiles[i] = new java.io.File(listedPaths[i]);
         inodeDataBody[i] = new RecyclerViewInterface.DataBody();
         inodeDataBody[i].name = childrenFiles[i].getName();
         inodeDataBody[i].path = childrenFiles[i].getPath();
         inodeDataBody[i].icon = childrenFiles[i].isFile() ?
            (ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_icon_for_file, null)) :
            (ResourcesCompat.getDrawable(context.getResources(), R.drawable.default_icon_for_directory, null));
      }

      // Reset the recycler view data if any.
      recyclerViewInterface.clear();

      // Add the new data and update the screen.
      recyclerViewInterface.addArray(inodeDataBody);
      recyclerViewInterface.updateScreen();
   }

}
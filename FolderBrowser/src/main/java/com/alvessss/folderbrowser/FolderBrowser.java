package com.alvessss.folderbrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

@SuppressWarnings("all")
public class FolderBrowser extends Filesystem {
   private static final String DEBUG_TAG = "FolderBrowser";

   private boolean launched = false;

   private final Context parentActivityContext;
   private final AppCompatActivity parentAppCompatActivity;
   private final ViewGroup parentViewGroup;

   private ViewGroup folderBrowserContentViewGroup;
   private RecyclerViewInterface recyclerViewInterface;

   public FolderBrowser(Context context, ViewGroup container, Callback onDoneCallback) {
      super(onDoneCallback);
      this.parentActivityContext = context;
      this.parentAppCompatActivity = (AppCompatActivity) context;
      this.parentViewGroup = container;
   }

   public void start(String directoryPath) {
      if (!launched) {
         launch();
      }

      Directory directory = new Directory(new java.io.File(directoryPath));

      // Update the current inode.
      super.setCurrentInode((Inode) directory);
      super.getDirectoryPathView().setText(directory.getPath());

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
            (ResourcesCompat.getDrawable(parentActivityContext.getResources(), R.drawable.default_icon_for_file, null)) :
            (ResourcesCompat.getDrawable(parentActivityContext.getResources(), R.drawable.default_icon_for_directory, null));
      }

      // Reset the recycler view data if any.
      recyclerViewInterface.clear();

      // Add the new data and update the screen.
      recyclerViewInterface.addArray(inodeDataBody);
      recyclerViewInterface.updateScreen();
   }

   public void restart(String directoryPath) {
      start(directoryPath);
   }

   public RecyclerViewInterface getRecyclerViewInterface() {
      return recyclerViewInterface;
   }

   public AppCompatActivity getParentAppCompatActivity() {
      return parentAppCompatActivity;
   }

   private void launch() {
      launched = true;
      inflateLayout();
      setRecyclerView();
      super.setFolderBrowserCallbacks(this);
   }

   private void inflateLayout() {
      folderBrowserContentViewGroup = (ViewGroup) LayoutInflater
         .from(parentActivityContext)
         .inflate(R.layout.folder_browser_layout, null);
      folderBrowserContentViewGroup.setVisibility(ViewGroup.VISIBLE);
      parentViewGroup.addView(folderBrowserContentViewGroup);
   }

   private void setRecyclerView() {
      recyclerViewInterface = new RecyclerViewInterface(parentActivityContext);
   }

   public void release() {
      launched = false;
      recyclerViewInterface.clear();
      parentViewGroup.removeView(folderBrowserContentViewGroup);
   }
}
package com.alvessss.folderbrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("all")
public class FolderBrowser extends Filesystem {
   private static final String DEBUG_TAG = FolderBrowser.class.getSimpleName();

   private boolean launched = false;

   private final Context parentActivityContext;
   private final AppCompatActivity parentAppCompatActivity;
   private final ViewGroup parentViewGroup;

   private ViewGroup folderBrowserContentViewGroup;
   private RecyclerViewInterface recyclerViewInterface;

   public FolderBrowser(
      @NonNull Context context,
      @NonNull ViewGroup container,
      @NonNull Callback onDoneCallback) {

      // The Filesustem class requires a callback telling
      // what to do when the final user click in the "done" button.
      super(onDoneCallback);

      this.parentActivityContext = context;
      this.parentAppCompatActivity = (AppCompatActivity) context;
      this.parentViewGroup = container;
   }

   public void start(String directoryPath) {
      if (!launched) {
         launch();
      }

      super.setCurrentInode(directoryPath);
      super.getDirectoryPathView().setText(directoryPath);

      // List the children of the target directory.
      String[] listedPaths = Directory.listDirectory(directoryPath);
      if (listedPaths.length == 0) {
         return;
      }

      // Reset the recycler view data if any.
      recyclerViewInterface.clear();

      // Add the listed files in the Recycler View
      recyclerViewInterface.addArray(
         RecyclerViewInterface.DataBody.getFrom(listedPaths, parentActivityContext)
      );
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

   public boolean isLaunched() {
      return launched;
   }

   private void launch() {
      launched = true;
      inflateLayout();
      setRecyclerView();
      super.setFolderBrowserCallbacks(this);
   }

   public void release() {
      launched = false;
      recyclerViewInterface.clear();
      parentViewGroup.removeView(folderBrowserContentViewGroup);
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
}
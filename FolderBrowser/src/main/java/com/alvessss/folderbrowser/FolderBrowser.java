package com.alvessss.folderbrowser;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

@SuppressWarnings("all")
public class FolderBrowser {
   // Debug tag.
   private static final String TAG = "FolderBrowser";

   // Icons for Directory and File.
   public static final int FILE_ICON_ID = R.drawable.default_icon_for_file; // TODO: Put in the File class;
   public static final int DIRECTORY_ICON_ID = R.drawable.default_icon_for_directory; // TODO: Put in the Directory class;

   // Default colors for the icons.
   public static final int FILE_COLOR = R.color.ocean_white_foreground; // TODO: Put in the File class;
   public static final int DIRECTORY_COLOR = R.color.ocean_blue_foreground; // TODO: Put in the Directory class;

   // Colors when the icons are selected.
   public static final int FILE_HIGHLIGHT_COLOR = R.color.ocean_gray_foreground; // TODO: Put in the File class;
   public static final int DIRECTORY_HIGHLIGHT_COLOR = R.color.ocean_gray_foreground; // TODO: Put in the Directory class;

   // Root of the system.
   private static final String SYSTEM_ROOT = Environment
      .getRootDirectory()
      .getAbsolutePath();

   // Objects getted from the calling class:
   private Context context;   // To use resources of the Client.
   private AppCompatActivity appCompatActivity; // idem
   private ViewGroup parentViewGroup; // To put our screen (view) inside the Client's screen (view).

   // TODO: Put the fields above in a class called Filesystem and create an instancie in this class;
   // Main fields. Setteds in the Builder/Constructor (just once):
   private Directory root; // The root directory (SYSTEM_ROOT will be used if it is not setted).
   private Inode currentInode; // The current directory/file (The file that is clicked or the directory that is opened).
   private Inode previousInode; // The previous currentInode.
   private RunnableCallback onDoneCallback; // Will perform when the Final-User click on "done" to select a directory/file.
   private TextView directoryPath; // Text view to show the path of the current directory to the Final-User.

   // Auxiliar objects
   private ViewGroup folderBrowserContentViewGroup; // The View-Group where we'll put all the data.
   private RecyclerViewInterface recyclerViewInterface; // All the recycler view stuff to print the directories/files on the screen.

   // Static functions. // TODO: Put in the Inode class;
   public static void changeIconColor(View recyclerViewItemView, int NEW_COLOR) {
      ((ImageView)recyclerViewItemView
         .findViewById(R.id.image_view_for_inode_icon)) // we have always to search the imageView cuz the RecyclerView is dynamic.
         .setColorFilter(ContextCompat.getColor(recyclerViewItemView.getContext(), NEW_COLOR),
            android.graphics.PorterDuff.Mode.MULTIPLY);
   }

   // TODO: Put in the Inode class;
   private static void highlightInode(View recyclerViewItemView, Inode inode) {
      if (inode.isFile()) {
         FolderBrowser.changeIconColor(recyclerViewItemView, FILE_HIGHLIGHT_COLOR);
         return;
      }

      if (inode.isDirectory()) {
         FolderBrowser.changeIconColor(recyclerViewItemView, DIRECTORY_HIGHLIGHT_COLOR);
         return;
      }

      // Here could be more cases
   }
   //

   private FolderBrowser() {
   }

   // TODO: Put in the Filesystem class;
   public Inode getSelectedFile() {
      return getSelectedInode();
   }

   // TODO: Put in the Filesystem class;
   public Inode getSelectedInode() {
      return currentInode;
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

      // Set pending on-click-listeners.
      setOnClickListenersOfNavigationButtons();
   }

   public void release() {
      recyclerViewInterface.clear();
      parentViewGroup.removeView(folderBrowserContentViewGroup);
   }

   public void restart() {
      restart(root);
   }

   public void start() {
      start(root);
   }

   public void restart(Directory directory) {
      start(directory);
   }

   public void start(Directory directory) {
      // Update the current inode.
      currentInode = (Inode) directory;
      directoryPath.setText(currentInode.getPath());

      // List the children of the target directory.
      String[] listedPaths = listDirectory(directory);
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

   // TODO: Put in the Filesytem class;
   private void setOnClickListenersOfNavigationButtons() {
      // 1: Set return-button's callback.
      View.OnClickListener returnButtonListener = view -> {
         // Check if the currentInode is child of the setted root.
         if (Inode.isChildOf(currentInode.getPath(), root.getPath())) {
            restart(
               new Directory(
                  new java.io.File(
                     currentInode.getParent()
                  )
               )
            );
         }
      };
      ((AppCompatActivity)context)
         .findViewById(R.id.button_view_for_previous_directory)
         .setOnClickListener(returnButtonListener);

      // 2: Set select-button's callback.
      recyclerViewInterface.setOnClickItem(inodeView -> {
         // Set the previousInode with the currentInode before
         // it change.
         previousInode = currentInode;

         // Path of the selected inode.
         String newPath = ((TextView)inodeView
            .findViewById(R.id.text_view_for_inode_path))
               .getText()
               .toString();

         // Remove the highlight of the previous selected inode if any.
         if (previousInode != null) {
            if (previousInode.isFile()) {
               FolderBrowser.changeIconColor(
                  previousInode.getInodeView(), FILE_COLOR
               );
            }

            else if (previousInode.isDirectory()) {
               // Don't need to remove the highlight of directories because
               // the data of the previous directory is no longer on the screen
               // and the inode data has reseted.
            }
         }

         // Create a new inode object with newPath.
         java.io.File inodeSourceFile = new java.io.File(newPath);
         Inode selectedInode = new Inode(inodeSourceFile);

         // If it is directory highlight the directory and restart the FolderBrowser with it's data.
         if (selectedInode.isDirectory()) {
            previousInode = selectedInode;
            FolderBrowser.changeIconColor(inodeView, DIRECTORY_HIGHLIGHT_COLOR);
            restart(new Directory(inodeSourceFile));
         }

         // If it is file highlight the file and update the currentInode object.
         else if (selectedInode.isFile()) {
            FolderBrowser.changeIconColor(inodeView, FILE_HIGHLIGHT_COLOR);
            // Save the inodeView to remove the highlight later.
            selectedInode.setInodeView(inodeView);
            currentInode = selectedInode;
         }
      });

      // 3: Set done-button's callback.
      ((AppCompatActivity) context)
         .findViewById(R.id.button_view_for_select_file)
         .setOnClickListener(view -> {
            if (onDoneCallback != null){
               onDoneCallback.run();
            } else {
               Log.w(TAG, "onDoneCallback is null, nothing todo.");
            }
         });

      // 4: Set directory's path display.
      this.directoryPath = ((AppCompatActivity) context)
         .findViewById(R.id.text_view_for_directory_path);
   }

   // TODO: Put in the Directory class;
   private static String[] listDirectory(Directory directoryPath) {
      java.io.File rootFile = new java.io.File(directoryPath.getPath());
      if (rootFile == null) return new String[0];

      java.io.File[] listedFiles = rootFile.listFiles();
      if (listedFiles == null) return new String[0];
      if (listedFiles.length == 0) return new String[0];

      String[] listedPaths = new String[listedFiles.length];
      for (int i = 0; i < listedFiles.length; i++) {
         listedPaths[i] = listedFiles[i].getAbsolutePath();
         Log.v(TAG, "listing/" + i + ": " + listedPaths[i]);
      }

      return listedPaths;
   }

   public interface RunnableCallback {
      void run();
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
         building.root = new Directory(
            new java.io.File(root)
         );
         return this;
      }

      public Builder setOnDoneCallback(RunnableCallback callback) {
         building.onDoneCallback = callback;
         return this;
      }

      public FolderBrowser build() {
         if (building.root == null) {
            setRoot(SYSTEM_ROOT);
         }

         if (building.onDoneCallback == null) {
            //
         }

         return building;
      }
   }
}
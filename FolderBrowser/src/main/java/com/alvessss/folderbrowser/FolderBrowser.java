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

import java.io.File;

@SuppressWarnings("all")
public class FolderBrowser {
   // Tag for debug.
   private static final String TAG = "FolderBrowser";

   // Icons for Directory and File.
   public static final int FILE_ICON_ID = R.drawable.default_icon_for_file;
   public static final int DIRECTORY_ICON_ID = R.drawable.default_icon_for_directory;

   // Default colors
   public static final int FILE_COLOR = R.color.ocean_white_foreground;
   public static final int DIRECTORY_COLOR = R.color.ocean_blue_foreground;
   public static final int FILE_HIGHLIGHT_COLOR = R.color.ocean_gray_foreground;
   public static final int DIRECTORY_HIGHLIGHT_COLOR = R.color.ocean_gray_foreground;

   // System root
   private static final String systemRoot = Environment
      .getRootDirectory()
      .getAbsolutePath();

   // From the calling context.
   private Context context;
   private AppCompatActivity appCompatActivity;
   private ViewGroup parentViewGroup;

   // Navigation logic.
   private Directory root;
   private Inode currentInode;
   private Inode previousInode;
   private Mode mode; // TODO

   ViewGroup folderBrowserContentViewGroup;
   RecyclerViewInterface recyclerViewInterface;

   public static void changeIconColor(View inodeView, int NEW_COLOR) {
      ((ImageView)inodeView
         .findViewById(R.id.image_view_for_inode_icon))
         .setColorFilter(ContextCompat.getColor(inodeView.getContext(), NEW_COLOR),
            android.graphics.PorterDuff.Mode.MULTIPLY);
   }

   private static void highlightInode(View inodeView, Inode inode) {
      if (inode.isFile()) {
         FolderBrowser.changeIconColor(inodeView, FILE_HIGHLIGHT_COLOR);
         return;
      }

      if (inode.isDirectory()) {
         FolderBrowser.changeIconColor(inodeView, DIRECTORY_HIGHLIGHT_COLOR);
         return;
      }
   }

   /* private constructor */
   private FolderBrowser() {
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

   private void setOnClickListenersOfNavigationButtons() {
      // Set return button.
      View.OnClickListener returnButtonListener = view -> {
         // Check if the currentInode is child of the setted root.
         if (Directory.isChildOf((Directory) currentInode, root, systemRoot)) {
            Log.v(TAG, "is child of");
            restart(
               new Directory(
                  new java.io.File(currentInode.getParent())
               )
            );
         }
      };
      ((AppCompatActivity)context)
         .findViewById(R.id.button_view_for_previous_directory)
         .setOnClickListener(returnButtonListener);

      // Set select button.
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
   }

   private static String[] listDirectory(Directory directoryPath) {
      java.io.File rootFile = new java.io.File(directoryPath.getPath());
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
         building.root = new Directory(
            new java.io.File(root)
         );
         return this;
      }

      public FolderBrowser build() {
         return building;
      }
   }
}
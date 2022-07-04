package com.alvessss.folderbrowser;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

@SuppressWarnings("all")
class Filesystem {
   public static final String DEBUG_TAG = "FB.Filesystem";

   public static final String SYSTEM_ROOOT = Environment
      .getRootDirectory()
      .getAbsolutePath();

   public static final int RETURN_BUTTON = R.id.button_view_for_previous_directory;
   public static final int INODE_PATH = R.id.text_view_for_inode_path;
   public static final int DIRECTORY_PATH = R.id.text_view_for_directory_path;
   public static final int DONE_BUTTON = R.id.button_view_for_select_file;

   private Directory root;
   private Inode currentInode;
   private Inode previousInode;
   private TextView directoryPath;
   private final Callback callbackForOnDoneButton;

   Filesystem(Callback onDoneCallback) {
      callbackForOnDoneButton = onDoneCallback;
      setRoot(SYSTEM_ROOOT);
   }

   public void setRoot(String rootPath) {
      if (rootPath == null) {
         Log.w(DEBUG_TAG, "Passed null as rootPath");
      }
      root = new Directory(new java.io.File(rootPath));
   }

   public Directory getRoot() {
      return root;
   }

   public Inode getCurrentInode() {
      return currentInode;
   }

   public Inode getPreviousInode() {
      return previousInode;
   }

   public TextView getDirectoryPathView() {
      return directoryPath;
   }

   void setFolderBrowserCallbacks(FolderBrowser folderBrowser) {
      setButtonsCallbacks(folderBrowser);
      setDirectoryPathDisplay(folderBrowser);
   }

   void setCurrentInode(Inode inode) {
      currentInode = inode;
   }

   void setCurrentInode(String inodePath) {
      currentInode = new Inode(
         new java.io.File(inodePath)
      );
   }

   private void setButtonsCallbacks(FolderBrowser folderBrowser) {
      setReturnButton(folderBrowser);
      setOnInodeClickAction(folderBrowser);
      setCallbackForOnDoneButton(folderBrowser);
   }

   private void setReturnButton(FolderBrowser folderBrowser) {
      /* Set the callback of the Return Button */
      View.OnClickListener returnButtonCallback =
         view -> {
            // Check if the currentInode is child of the setted root.
            if (Inode.isChildOf(currentInode.getPath(), root.getPath())) {
               folderBrowser.restart(currentInode.getParent());
           }
      };
      folderBrowser.getParentAppCompatActivity()
         .findViewById(RETURN_BUTTON)
         .setOnClickListener(returnButtonCallback);
   }

   private void setOnInodeClickAction(FolderBrowser folderBrowser) {
      /* Set the action to perfom when the Final-User click on a file/directory */
      folderBrowser.getRecyclerViewInterface()
         .setOnClickItem(inodeView -> {
            previousInode = currentInode;
            String newInodePath = ((TextView)inodeView
               .findViewById(INODE_PATH)).getText().toString();

            // remove the highlight of the previous selected inode if any.
            if (previousInode != null) {
               if (previousInode.isFile()) {
                  Inode.changeIconColor(
                     previousInode.getInodeView(), File.FILE_COLOR
                  );
               }
               else if (previousInode.isDirectory()) {
                  // Don't need to remove the highlight of directories
                  // because the data of the previous directory is no longer
                  // on the screen and the inode data has reseted.
               }
            }

            java.io.File inodeSourceFile = new java.io.File(newInodePath);
            Inode selectedInode = new Inode(inodeSourceFile);

            if (selectedInode.isDirectory()) {
               previousInode = selectedInode;
               Inode.changeIconColor(inodeView, Directory.HIGHLIGHTED_DIRECTORY_COLOR);
               folderBrowser.restart(newInodePath);
            }

            else if (selectedInode.isFile()) {
               Inode.changeIconColor(inodeView, File.HIGHLIGHTED_FILE_COLOR);
               selectedInode.setInodeView(inodeView);
               currentInode = selectedInode;
            }
         });
   }

   private void setCallbackForOnDoneButton(FolderBrowser folderBrowser) {
      folderBrowser.getParentAppCompatActivity()
         .findViewById(DONE_BUTTON)
         .setOnClickListener(
            view -> {
               if (callbackForOnDoneButton != null) {
                  callbackForOnDoneButton.run();
               } else {
                  Log.w(DEBUG_TAG, "callBackForOnDoneButton is null, nothing todo.");
               }
            }
         );
   }

   private void setDirectoryPathDisplay(FolderBrowser folderBrowser) {
      directoryPath = folderBrowser.getParentAppCompatActivity()
         .findViewById(DIRECTORY_PATH);
   }

   public interface Callback {
      public void run();
   }
}

package com.alvessss.folderbrowser;

import android.os.Environment;

@SuppressWarnings("all")
public class DirectoryNavigation {
   private static final String systemRoot = Environment.
      getExternalStorageDirectory().getAbsolutePath();

   // Recycler-view pointer to put the directories data.
   private RecyclerViewInterface recyclerViewInterface;

   // Default and current directory
   private Directory rootDirectory;
   private Directory currentDirectory;

   public DirectoryNavigation() {
   }

   DirectoryNavigation(Directory rootDirectory, RecyclerViewInterface recyclerViewInterface) {
      setRootDirectory(rootDirectory);
      setRecyclerViewInterface(recyclerViewInterface);
   }

   public void setRootDirectory(Directory rootDirectory) {
      this.rootDirectory = rootDirectory;
   }

   public void setRecyclerViewInterface(RecyclerViewInterface recyclerViewInterface) {
      this.recyclerViewInterface = recyclerViewInterface;
   }

   public void setCurrentDirectory(Directory currentDirectory) {
      if (Directory.isChildOf(currentDirectory.getPath(), rootDirectory.getPath())) {
         this.currentDirectory = currentDirectory;
      }
   }
}

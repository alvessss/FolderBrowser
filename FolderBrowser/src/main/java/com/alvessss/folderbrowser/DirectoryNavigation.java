package com.alvessss.folderbrowser;

import android.os.Environment;

@SuppressWarnings("all")
class DirectoryNavigation {
   private static final String systemRoot = Environment.
      getExternalStorageDirectory().getAbsolutePath();

   // Recycler-view pointer to put the directories data.
   private RecyclerViewInterface recyclerViewInterface;

   // Default and current directory
   private Directory rootDirectory;
   private Directory currentDirectory;

   DirectoryNavigation() {
   }

   DirectoryNavigation(Directory rootDirectory, RecyclerViewInterface recyclerViewInterface) {
      setRootDirectory(rootDirectory);
      setRecyclerViewInterface(recyclerViewInterface);
   }

   void setRootDirectory(Directory rootDirectory) {
      this.rootDirectory = rootDirectory;
   }

   void setRecyclerViewInterface(RecyclerViewInterface recyclerViewInterface) {
      this.recyclerViewInterface = recyclerViewInterface;
   }

   void setCurrentDirectory(Directory currentDirectory) {
      if (currentDirectory.isChildOf(rootDirectory, systemRoot)) {
         this.currentDirectory = currentDirectory;
      }
   }
}

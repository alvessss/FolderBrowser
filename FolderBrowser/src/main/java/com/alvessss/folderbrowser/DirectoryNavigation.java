package com.alvessss.folderbrowser;

@SuppressWarnings("all")
class DirectoryNavigation {
   // recycler view to put the directories data.
   private RecyclerViewInterface recyclerViewInterface;

   // default and current directory
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
      // if (currentDirectory.isChildOf(rootDirectory)) {
      this.currentDirectory = currentDirectory;
      // }
   }
}

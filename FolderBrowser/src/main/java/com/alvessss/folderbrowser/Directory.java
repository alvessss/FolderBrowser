package com.alvessss.folderbrowser;

import androidx.annotation.NonNull;

import java.io.File;

@SuppressWarnings("all")
public class Directory extends Inode {
   private static final String TAG = "FolderBrowser.Directory";

   private final String[] childs;

   public Directory(@NonNull File sourceFile) throws RuntimeException {
      super(sourceFile);
      childs = getChildsFrom(sourceFile);
   }

   public static @NonNull String[] getChildsFrom(@NonNull File sourceFile) {
      File[] sourceFileChilds = sourceFile.listFiles();

      if (sourceFileChilds == null) {
         return new String[0];
      }

      int i = 0; String[] childs = new String[sourceFileChilds.length];
      for (File child : sourceFileChilds) {
         childs[i++] = child.getAbsolutePath();
      }

      return childs;
   }

   @Override
   public boolean isDirectory() {
      return true;
   }

   @Override
   public boolean isFile() {
      return false;
   }

   public String[] getChilds() {
      return childs;
   }
}

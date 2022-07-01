package com.alvessss.folderbrowser;

import androidx.annotation.NonNull;

import java.io.File;

@SuppressWarnings("all")
public class Directory extends Inode {
   public static final String TAG = "FB.Directory";

   public static final int DIRECTORY_ICON = R.drawable.default_icon_for_directory;
   public static final int DIRECTORY_COLOR = R.color.ocean_blue_foreground;
   public static final int HIGHLIGHTED_DIRECTORY_COLOR = R.color.ocean_gray_foreground;

   private final String[] childs;

   public static String[] listDirectory(String directoryPath) {
      java.io.File sourceFile = new java.io.File(directoryPath);
      if (sourceFile == null) return new String[0];

      java.io.File[] listedFiles = sourceFile.listFiles();
      if (listedFiles == null) return new String[0];
      if (listedFiles.length == 0) return new String[0];

      String[] listedPaths = new String[listedFiles.length];
      for (int i = 0; i < listedFiles.length; i++) {
         listedPaths[i] = listedFiles[i].getAbsolutePath();
      }

      return listedPaths;
   }

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

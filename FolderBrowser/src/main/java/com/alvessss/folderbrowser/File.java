package com.alvessss.folderbrowser;

import android.util.Log;

import androidx.annotation.NonNull;

@SuppressWarnings("all")
public class File extends Inode {
   private static final String TAG = "FolderBrowser.File";

   private final String extension;

   public File(@NonNull java.io.File sourceFile) throws RuntimeException {
      super(sourceFile);
      extension = getExtensionFrom(sourceFile);
   }

   public static String getExtensionFrom(@NonNull java.io.File sourceFile) {
      return null;
   }

   @Override
   public boolean isFile() {
      return true;
   }

   @Override
   public boolean isDirectory() {
      return false;
   }

   public String getExtension() {
      return extension;
   }

}

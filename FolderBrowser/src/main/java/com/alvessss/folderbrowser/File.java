package com.alvessss.folderbrowser;

import androidx.annotation.NonNull;

@SuppressWarnings("all")
public class File extends Inode {
   public static final String TAG = "FB.File";

   public static final int FILE_ICON = R.drawable.default_icon_for_file;
   public static final int FILE_COLOR = R.color.ocean_white_foreground;
   public static final int HIGHLIGHTED_FILE_COLOR = R.color.ocean_gray_foreground;

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

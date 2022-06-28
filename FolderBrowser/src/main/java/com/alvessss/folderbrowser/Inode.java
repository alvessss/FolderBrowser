package com.alvessss.folderbrowser;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

@SuppressWarnings("all")
class Inode {
   private static final String TAG = "FolderBrowser.Inode";

   // Common
   private final String name;
   private final String path;
   private final String parent;

   // Types
   private final boolean file;
   private final boolean directory;

   // Permissions
   private final boolean read;
   private final boolean write;
   private final boolean exec;

   // Extra
   private boolean highlighted = false;
   private View inodeView;

   public static boolean isFile(String inodePath) {
      return new Inode(
         new java.io.File(inodePath)
      ).isFile();
   }

   public static boolean isDirectory(String inodePath) {
      return new Inode(
         new java.io.File(inodePath)
      ).isDirectory();
   }

   public static boolean isChildOf(String targetDirectory, String rootDirectory) {
      int pathStart, pathEnd;

      pathStart = targetDirectory.indexOf(rootDirectory);
      pathEnd = rootDirectory.length();
      if (pathStart == -1) {
         return false;
      }

      else if (pathEnd >= targetDirectory.length()) {
         return false;
      }

      return true;
   }

   public Inode(@NonNull java.io.File sourceFile) {
      name = sourceFile.getName();
      path = sourceFile.getAbsolutePath();
      Log.v(TAG, "name: " + name);

      if (sourceFile.getParentFile() == null) {
         Log.v(TAG, name + "is null. Path = " + path);
         parent = null;
      } else {
         parent = sourceFile.getParentFile().getAbsolutePath();
      }

      file = sourceFile.isFile();
      directory = sourceFile.isDirectory();
      Log.v(TAG, "type: " + (file ? "file" : "directory"));

      read = sourceFile.canRead();
      Log.v(TAG, "read: " + Boolean.toString(read));

      write = sourceFile.canWrite();
      Log.v(TAG, "write: " + Boolean.toString(write));

      exec = sourceFile.canExecute();
      Log.v(TAG, "exec: " + Boolean.toString(exec));
   }

   public String getName() {
      return name;
   }

   public String getPath() {
      return path;
   }

   public String getParent() {
      return parent;
   }

   public boolean isFile() {
      return file;
   }

   public boolean isDirectory() {
      return directory;
   }

   public boolean canRead() {
      return read;
   }

   public boolean canWrite() {
      return write;
   }

   public boolean canExec() {
      return exec;
   }

   public boolean isHighlighted() {
      return highlighted;
   }

   public void isHighlited(boolean val) {
      highlighted = val;
   }

   public View getInodeView() {
      return inodeView;
   }

   public void setInodeView(View view) {
      inodeView = view;
   }

   public Directory toDirectory() {
      return new Directory(
         new java.io.File(path)
      );
   }

   public File toFile() {
      return new File(
         new java.io.File(path)
      );
   }
}
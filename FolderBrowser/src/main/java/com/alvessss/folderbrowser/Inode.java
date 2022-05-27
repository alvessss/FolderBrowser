package com.alvessss.folderbrowser;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;

@SuppressWarnings("all")
class Inode {
   private static final String TAG = "FolderBrowser.Inode";

   private final String name;
   private final String path;
   private final String parent;

   // types
   private final boolean file;
   private final boolean directory;

   // Permissions
   private final boolean read;
   private final boolean write;
   private final boolean exec;

   public Inode(@NonNull File sourceFile) {
      name = sourceFile.getName();
      path = sourceFile.getAbsolutePath();
      parent = sourceFile.getParent();
      Log.v(TAG, "name: " + name);

      file = sourceFile.isFile();
      directory = sourceFile.isDirectory();
      Log.v(TAG, "type: " + (file ? "file" : "directory"));

      read = sourceFile.canRead();
      Log.v(TAG, "read: " + Boolean.toString(read));
      write = sourceFile.canWrite();
      Log.v(TAG, "write" + Boolean.toString(write));
      exec = sourceFile.canExecute();
      Log.v(TAG, "exec" + Boolean.toString(exec));

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

}
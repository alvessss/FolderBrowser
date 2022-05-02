package com.alvessss.folderbrowser;

@SuppressWarnings("all")
public class FileSupport
{
   private final int fileIconId;
   private final String[] supportedExtensions;

   public FileSupport(int fileIconId, String... supportedExtensions)
   {
      this.fileIconId = fileIconId;
      this.supportedExtensions = supportedExtensions;
   }
}

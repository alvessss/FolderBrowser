package com.alvessss.folderbrowser;

@SuppressWarnings("all")
public class FileSupport
{
   private String name;
   private final int fileIconId;
   private final String[] supportedExtensions;

   public FileSupport(
      String name,
      int fileIconId,
      String... supportedExtensions)
   {
      this.name = name;
      this.fileIconId = fileIconId;
      this.supportedExtensions = supportedExtensions;
   }

   public String getName()
   {
      return name;
   }

   public int getFileIconId()
   {
      return fileIconId;
   }

   public String[] getSupportedExtensions()
   {
      return supportedExtensions;
   }
}

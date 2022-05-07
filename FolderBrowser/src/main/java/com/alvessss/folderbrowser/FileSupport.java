package com.alvessss.folderbrowser;

@SuppressWarnings("all")
public class FileSupport
{
   public String name;
   public int icon;
   public String[] extensions;

   public static FileSupport[] getArray(int n)
   {
      FileSupport[] fileSupportArray = new FileSupport[n];
      for (FileSupport fs : fileSupportArray)
      {
         fs = new FileSupport();
      }

      return fileSupportArray;
   }
}
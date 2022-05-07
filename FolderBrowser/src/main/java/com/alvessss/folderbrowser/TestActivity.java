package com.alvessss.folderbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

@SuppressWarnings("all")
public class TestActivity extends AppCompatActivity {
   private FolderBrowser folderBrowser;
   private static final FolderBrowser.RecyclerViewData rvDataForFolderBrowser;
   private static final FileSupport[] supportedFilesOnFolderBrowser;
   static{
      rvDataForFolderBrowser = new FolderBrowser.RecyclerViewData();
      rvDataForFolderBrowser.columns = 4;

      rvDataForFolderBrowser.id = R.id.recycler_view_for_folder_browser;
      rvDataForFolderBrowser.itemLayoutId = R.layout.recycler_view_item_for_folder_browser;

      rvDataForFolderBrowser.defaultFileIcon = R.drawable.default_icon_for_file;
      rvDataForFolderBrowser.defaultDirectoryIcon = R.drawable.default_icon_for_directory;

      rvDataForFolderBrowser.textViewForInodeName = R.id.text_view_for_inode_name;
      rvDataForFolderBrowser.imageViewForInodeIcon = R.id.image_view_for_inode_icon;
      rvDataForFolderBrowser.textViewForInodePath = R.id.text_view_for_inode_path;

      supportedFilesOnFolderBrowser = FileSupport.getArray(3);

      supportedFilesOnFolderBrowser[0].name = "JAVA";
      supportedFilesOnFolderBrowser[0].icon = R.drawable.default_icon_for_file;
      supportedFilesOnFolderBrowser[0].extensions = new String[]{".java", ".jar", ".class"};

      supportedFilesOnFolderBrowser[1].name = "PYTHON";
      supportedFilesOnFolderBrowser[1].icon = R.drawable.default_icon_for_file;
      supportedFilesOnFolderBrowser[1].extensions = new String[]{".py", ".pyw"};

      supportedFilesOnFolderBrowser[2].name = "C";
      supportedFilesOnFolderBrowser[2].icon = R.drawable.default_icon_for_file;
      supportedFilesOnFolderBrowser[2].extensions = new String[]{".c", ".h"};
   }

   @Override protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_test);
   }

   @Override protected void onStart()
   {
      super.onStart();

      folderBrowser = new FolderBrowser(this, rvDataForFolderBrowser, supportedFilesOnFolderBrowser);

      FolderBrowser.RecyclerViewData rvdata = new FolderBrowser.RecyclerViewData();
   }
}
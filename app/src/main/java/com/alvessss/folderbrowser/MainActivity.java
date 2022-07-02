package com.alvessss.folderbrowser;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
   private static final String TAG = "MainActivity";

   // Permission requester
   private final ActivityResultLauncher<String> requestPermissionLauncher =
      registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
         if (isGranted) {
            Log.v(TAG, "permission granted");
         } else {
            Log.v(TAG, "permission not granted");
         }
      });


   // FolderBrowse stuff
   private static final int FOLDER_BROWSER_CONTAINER_ID = R.id.folder_browser_container_viewgroup;
   private static final String FOLDER_BROWSER_ROOT = Environment
      .getRootDirectory()
      .getAbsolutePath();
   private FolderBrowser folderBrowser;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
   }

   @Override
   protected void onStart() {
      super.onStart();

      // set FolderBrowser
      ViewGroup folderBrowserContainerView = MainActivity.this.findViewById(FOLDER_BROWSER_CONTAINER_ID);

      folderBrowser = new FolderBrowser(this, folderBrowserContainerView, () -> {
         Log.i(TAG, folderBrowser.getCurrentInode().getName());
      });

      ((FloatingActionButton)findViewById((R.id.folder_browser_launcher_button)))
         .setOnClickListener(
            new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showFolderBrowser(view);
            }
      });

      // request permission
      requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
      requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
   }

   public void showFolderBrowser(View view) {
      folderBrowser.setRoot("/system");
      folderBrowser.start("/system/usr");
   }

}
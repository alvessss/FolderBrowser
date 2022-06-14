package com.alvessss.folderbrowser;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
   // FolderBrowse stuff
   private static final int FOLDER_BROWSER_CONTAINER_ID = R.id.folder_browser_container_viewgroup;
   private static final String FOLDER_BROWSER_ROOT = Environment
      .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
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
      folderBrowser = new FolderBrowser.Builder(folderBrowserContainerView)
         .setRoot(FOLDER_BROWSER_ROOT)
         .build();

      ((FloatingActionButton)findViewById((R.id.folder_browser_launcher_button))).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            showFolderBrowser(view);
         }
      });
   }

   public void showFolderBrowser(View view) {
      Log.i("DEBUG", "hi 1");
      folderBrowser.launch();
      folderBrowser.start(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
   }

}
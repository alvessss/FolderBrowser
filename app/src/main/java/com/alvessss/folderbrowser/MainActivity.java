package com.alvessss.folderbrowser;

import java.io.File;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
   private FolderBrowser folderBrowser;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
   }

   @Override
   protected void onStart() {
      super.onStart();

      DirectoryNavigation nav = new DirectoryNavigation();

      String myRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
      Directory dirRoot = new Directory(new File(myRoot));
      File[] myRootChildren = (new File(myRoot)).listFiles();

      Directory dirChild = null;
      for (int i = 0; i < Objects.requireNonNull(myRootChildren).length; i++) {
         if (myRootChildren[i].isDirectory()) {
            dirChild = new Directory(myRootChildren[i]);
            break;
         }
      }

      if (dirChild.isChildOf(dirRoot, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())) {
         Log.d("DEBUG", "is child of");
      }

   }
} 
package com.alvessss.folderbrowser;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

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

      FolderBrowser.Builder folderBrowserBuilder = new FolderBrowser.Builder(this);

      folderBrowser = FolderBrowser.build(folderBrowserBuilder);

   }
} 
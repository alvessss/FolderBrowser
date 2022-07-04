package com.alvessss.folderbrowser;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.SavedStateHandle;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("all")
public class FolderBrowser extends Filesystem {
   private static final String DEBUG_TAG = FolderBrowser.class.getSimpleName();

   private boolean launched = false;

   private final Context parentActivityContext;
   private final AppCompatActivity parentAppCompatActivity;
   private final ViewGroup parentViewGroup;

   private ViewGroup folderBrowserContentViewGroup;
   private RecyclerViewInterface recyclerViewInterface;

   public FolderBrowser(
      @NonNull Context context,
      @NonNull ViewGroup container,
      @NonNull Callback onDoneCallback) {

      // The Filesustem class requires a callback telling
      // what to do when the final user click in the "done" button.
      super(onDoneCallback);

      this.parentActivityContext = context;
      this.parentAppCompatActivity = (AppCompatActivity) context;
      this.parentViewGroup = container;
   }

   public void start(String directoryPath) {
      if (!launched) {
         launch();
      }

      super.setCurrentInode(directoryPath);
      super.getDirectoryPathView().setText(directoryPath);
      setClickableSpansInDirectoryPathDisplay(
         new Directory(
            new java.io.File(
               directoryPath
            )
         )
      );


      // List the children of the target directory.
      String[] listedPaths = Directory.listDirectory(directoryPath);

      // Reset the recycler view data if any.
      recyclerViewInterface.clear();

      // Add the listed files in the Recycler View
      recyclerViewInterface.addArray(
         RecyclerViewInterface.DataBody.getFrom(listedPaths, parentActivityContext)
      );
      recyclerViewInterface.updateScreen();
   }

   public void restart(String directoryPath) {
      start(directoryPath);
   }

   public RecyclerViewInterface getRecyclerViewInterface() {
      return recyclerViewInterface;
   }

   public AppCompatActivity getParentAppCompatActivity() {
      return parentAppCompatActivity;
   }

   private void setClickableSpansInDirectoryPathDisplay(Directory directory) {
      String SLASH = "/";
      TextView textView = super.getDirectoryPathView();

      ArrayList<String> paths = new ArrayList<>();
      ArrayList<String> names = new ArrayList<>();
      ArrayList<Integer> startIndexes = new ArrayList<>();
      ArrayList<Integer> endIndexes = new ArrayList<>();
      ArrayList<ClickableSpan> clickableSpans;

      int slashIndex;
      int startIndex;
      int endIndex;
      String name;
      String path;
      String currentPath = textView.getText().toString();
      while ((slashIndex = currentPath.lastIndexOf(SLASH)) != -1) {
         startIndex = slashIndex + 1;
         name = currentPath.substring(startIndex);
         endIndex = currentPath.lastIndexOf(name) + name.length();
         path = currentPath;
         currentPath = currentPath.substring(0, slashIndex);

         names.add(name);
         paths.add(path);
         startIndexes.add(startIndex);
         endIndexes.add(endIndex);
      }

      currentPath = textView.getText().toString();
      SpannableString spannableString = new SpannableString(currentPath);

      for (int i = 0; i < paths.size(); i++) {
         int finalI = i; // i hate this
         spannableString.setSpan(
            new ClickableSpan() {
               @Override
               public void onClick(@NonNull View view){
                  start(paths.get(finalI));
               }
            }, startIndexes.get(i), endIndexes.get(i), 0);
      }

      textView.setText(spannableString, TextView.BufferType.SPANNABLE);
      textView.setMovementMethod(LinkMovementMethod.getInstance());
   }

   public boolean isLaunched() {
      return launched;
   }

   private void launch() {
      launched = true;
      inflateLayout();
      setRecyclerView();
      super.setFolderBrowserCallbacks(this);
   }

   public void release() {
      launched = false;
      recyclerViewInterface.clear();
      parentViewGroup.removeView(folderBrowserContentViewGroup);
   }

   private void inflateLayout() {
      folderBrowserContentViewGroup = (ViewGroup) LayoutInflater
         .from(parentActivityContext)
         .inflate(R.layout.folder_browser_layout, null);
      folderBrowserContentViewGroup.setVisibility(ViewGroup.VISIBLE);
      parentViewGroup.addView(folderBrowserContentViewGroup);
   }

   private void setRecyclerView() {
      recyclerViewInterface = new RecyclerViewInterface(parentActivityContext);
   }
}
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

      Directory directory = new Directory(
         new java.io.File(directoryPath)
      );

      if (!directory.canRead()) {
         Log.w(DEBUG_TAG, "You have no permission to read the directory: " + directory.getName());
         return;
      }

      super.setCurrentInode(directoryPath);
      super.getDirectoryPathView().setText(directoryPath);
      setClickableSpansInDirectoryPathDisplay(directory);


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
      /*
         This function put a click listener in each directory name in the directory path display
       */

      String SLASH = "/";
      TextView textView = super.getDirectoryPathView();

      // Lets break the path and put the useful data into these arrays
      ArrayList<String> paths = new ArrayList<>();
      ArrayList<String> names = new ArrayList<>();
      ArrayList<Integer> startIndexes = new ArrayList<>();
      ArrayList<Integer> endIndexes = new ArrayList<>();
      ArrayList<ClickableSpan> clickableSpans;
      //

      int slashIndex;
      int startIndex;
      int endIndex;
      String name;
      String path;
      String currentPath = textView.getText().toString(); // the path that we'll split

      // start from the end
      while ((slashIndex = currentPath.lastIndexOf(SLASH)) != -1) {
         // the last directory name start just after the last slash and goes to the end
         startIndex = slashIndex + 1;
         name = currentPath.substring(startIndex);
         endIndex = startIndex + name.length();

         // save the path before remove the current directory name
         path = currentPath;
         currentPath = currentPath.substring(0, slashIndex);

         names.add(name);
         paths.add(path);
         startIndexes.add(startIndex);
         endIndexes.add(endIndex);
      }

      // get the current path again to fill the spannableString
      currentPath = textView.getText().toString();
      SpannableString spannableString = new SpannableString(currentPath);

      for (int i = 0; i < paths.size(); i++) {
         int finalI = i;
         ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view){
               start(paths.get(finalI)); // each name will get your respective path
            }
         };

         // put the clickableSpan into the String and set where each word starts end ends
         spannableString.setSpan(clickableSpan, startIndexes.get(i), endIndexes.get(i), 0);
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
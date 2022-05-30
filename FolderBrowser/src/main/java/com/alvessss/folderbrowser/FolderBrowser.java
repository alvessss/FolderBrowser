package com.alvessss.folderbrowser;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

@SuppressWarnings("all")
public class FolderBrowser {
   // tag for debug.
   public static final String TAG = "FolderBrowser";

   // from parent class.
   private Context context;
   private AppCompatActivity appCompatActivity;

   private Inode currentInode;
   private Mode mode;

   /* constructor */
   public static FolderBrowser build(FolderBrowser.Builder builder) {
      FolderBrowser builtInstance = builder.getInstance();

      String buildingValidationStatus = Builder.validate(builtInstance);

      if (Objects.equals(buildingValidationStatus, Builder.VALIDATION_OK)) {
         Log.v(Builder.TAG, Builder.VALIDATION_OK);
         return builtInstance;
      }

      else {
         throw new RuntimeException(buildingValidationStatus);
      }
   }

   public void start(ViewGroup parentView) {
      // TODO
      // recyclerViewInterface.initView();
      // recyclerViewInterface.attachTo(parentView);
      // directoryNavigation.setDataSource(recyclerViewInterface);
      // directoryNavigation.setDirectory(rootDirectory);
      // directoryNavigation.update();
   }

   private void changeDirectory(String newDirectory) {
      // TODO
      // directoryNavigation.setDirectory(newDirectory);
      // directoryNavigation.update();
   }

   /* private constructor */
   private FolderBrowser() {
   }

   public enum Mode {
      DIRECTORY(0), FILE(1);

      public int val;
      Mode(int val){
         this.val = val;
      };
   }

   public static class Builder {
      private static final String TAG = "FolderBrowser.Builder";
      private static final String VALIDATION_OK = "ALL FIELDS ALRIGHT :)";

      private final FolderBrowser instance;

      public Builder(@NonNull final Context parentActivityContext) {
         instance = new FolderBrowser();
         instance.context = parentActivityContext;
         instance.appCompatActivity = (AppCompatActivity) parentActivityContext;
      }

      public static String validate(FolderBrowser folderBrowser) {
         if (folderBrowser.context == null) {
            return "The Context of the calling activity cannot be null!";
         }

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!folderBrowser.context.isUiContext()) {
               return "The Context of the calling activity must be the UI Context!";
            }
         }

         return Builder.VALIDATION_OK;
      }

      private FolderBrowser getInstance() {
         return instance;
      }
   }
}
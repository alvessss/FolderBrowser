package com.alvessss.folderbrowser;

// TODO: RecyclerView class

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class FolderBrowser {
   // from parent class
   private Context context;
   private AppCompatActivity appCompatActivity;

   private FolderBrowser builtInstance;

   private FolderBrowser() {
      // for Builder class
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
         if (folderBrowser.context == null)
            return "The Context of the calling activity cannot be null!";

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!folderBrowser.context.isUiContext())
               return "The Context of the calling activity must be the UI Context!";
         }

         return Builder.VALIDATION_OK;
      }

      private static FolderBrowser build(FolderBrowser.Builder builder) {
         FolderBrowser builtInstance = builder.instance;
         String buildingValidationStatus = Builder.validate(builtInstance);
         if (Objects.equals(buildingValidationStatus, Builder.VALIDATION_OK)) {
            Log.v(Builder.TAG, Builder.VALIDATION_OK);
            return builtInstance;
         }

         else {
            throw new RuntimeException(buildingValidationStatus);
         }
      }
   }

}
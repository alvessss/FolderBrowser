package com.alvessss.folderbrowser;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;

public class FolderBrowserTest {
   @Test
   public void retrievedInodePathShouldBeEqualToGivenFilePath() {
      File file = new File("/home");
      Inode inode = new Inode(file);
      assertEquals(file.getAbsolutePath(), inode.getPath());
   }

   @Test
   public void inodeCanOnlyBeFileOrDirectory() {
      File file = new File("/home");
      Inode inode = new Inode(file);
      assertNotEquals(inode.isFile(), inode.isDirectory());
   }

}
package com.alvessss.folderbrowser;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;

/**
 * A Inode abstracts a amount of real data in the disk to
 * a simple structure containing, basically, a name, a path,
 * a type (File or Directory), and its data (obviously). If
 * a inode is Directory-Type, its data is the paths of its childs.
 *
 * Our Inode is even simple than that, it have no data. If the inode is
 * a File the field Childs must be a array with length 0, if is a Directory,
 * Childs must be a array of the child inodes.
 *
 * "Why it have no data?" > Because you could easily retrieve its data with
 * the java.io.File library passing the Inode.path to it.
 */

@SuppressWarnings("all")
public class Inode
{
   private String path;
   private String name;
   private String parent;
   private Inode.Type type;
   private Inode[] childs;

   private Inode() {}

   /**
    * Return a inode getted by the inodePath.
    * @param inodePath The path of the inode in the filesystem.
    * @param recursive If true, get all the child inodes.
    * @return A Inode Object, or null if the inodePath cannot be resolved.
    */
   public static Inode getInode(String inodePath, boolean recursive) {
      File sourceFile = new File(inodePath);
      if (sourceFile == null)
         return  null;

      Inode inode = new Inode();
      inode.name = sourceFile.getName();
      inode.path = sourceFile.getAbsolutePath();

      if (sourceFile.isFile())
         inode.type = Type.FILE;
      else
         inode.type = Type.DIRECTORY;

      if (sourceFile.getParentFile() != null)
         inode.parent = sourceFile.getParentFile().getAbsolutePath();

      if (recursive)
         inode.childs = getInodeChilds(inode.path, true);
      else
         inode.childs = getInodeChilds(inode.path, false);

      return inode;
   }

   /**
    * If recursivity is true, return all the childs from a specific inode root, otherwise
    * return only the first childs.
    * @param inodePath the path of the root inode.
    * @return A array of inodes getted from the root inode, or null if the
    * root inode does not have childs or does not exists.
    */
   public static @NonNull Inode[] getInodeChilds(String inodePath, boolean recursivity) {
      File sourceFile = new File(inodePath);
      if (sourceFile == null)
         return new Inode[0];

      File[] sourceChilds = sourceFile.listFiles();
      if (sourceChilds == null)
         return new Inode[0];

      Inode[] childs = new Inode[sourceChilds.length];

      int i = 0;
      for (File child : sourceChilds) {
         Inode inodeChild = childs[i++] = new Inode();
         inodeChild.name = child.getName();
         inodeChild.path = child.getAbsolutePath();
         inodeChild.type = child.isFile() ? Type.FILE : Type.DIRECTORY;

         if (child.getParentFile() != null)
            inodeChild.parent = child.getParentFile().getAbsolutePath();

         if (!recursivity) {
            inodeChild.childs = new Inode[0];
            continue;
         }

         inodeChild.childs = getInodeChilds(inodeChild.path, true);
      }

      return childs;
   }

   /**
    * Extract only the path field of a Inode and pass it
    * to the ArrayList pointer. If the Inode have childs,
    * get the path of all of them.
    * @param ptr The ArrayList to put the paths.
    * @param inodeTree the Inode to extract the paths.
    */
   public static void extractPathsFromInodeTree(ArrayList<String> ptr, Inode inodeTree)
   {
      for (Inode inode : inodeTree.childs)
      {
         ptr.add(inode.path);
         extractPathsFromInodeTree(ptr, inode);
      }
   }

   public String getName()
   {
      return name;
   }

   public String getPath()
   {
      return path;
   }

   public Type getType()
   {
      return type;
   }

   public Inode[] getChilds()
   {
      return childs;
   }

   public boolean isFile()
   {
      return type == Type.FILE;
   }

   public boolean isDirectory()
   {
      return type == Type.DIRECTORY;
   }

   public String getParent() {
      return parent;
   }

   public static enum Type
   {
      FILE(0),
      DIRECTORY(1);

      int val;
      Type(int value)
      {
         val = value;
      }
   }
}

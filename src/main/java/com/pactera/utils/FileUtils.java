package com.pactera.utils;

import java.io.*;

/**
 * Created by pactera on 2017/11/22.
 */
public class FileUtils {

   public static void copyInputStreamToFile(InputStream input, File file) throws IOException {
      int index;
      byte[] bytes = new byte[1024];
      FileOutputStream downloadFile = new FileOutputStream(file);
      while ((index = input.read(bytes)) != -1) {
         downloadFile.write(bytes, 0, index);
         downloadFile.flush();
      }
      downloadFile.close();
      input.close();
   }
}

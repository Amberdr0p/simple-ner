package impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProcessingFile {
  private static BufferedReader br;


  public static void WriteToEndFile(String path, Collection<String> list) {
    Writer writer = null;
    try {
      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), "utf-8"));
      for (String line : list) {
        writer.write(line + "\r\n");
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  public static void close() {
    try {
      br.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static void readFile(String path, int countScip) throws IOException {
    try {
      br = new BufferedReader(new FileReader(path));
      int i = 0;
      for (String line; i < countScip && (line = br.readLine()) != null; i++) {
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static List<String> nextWindow(int count) throws IOException {
    int i = 0;
    List<String> res = new ArrayList<String>();
    for (String line; i < count && (line = br.readLine()) != null; i++) {
      res.add(line);
    }
    return res;
  }

}

package com.swt.helloworld.listener;

import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.item.ItemReaderException;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.transform.FlatFileFormatException;
import org.springframework.stereotype.Component;

@Component
public class SkipListener {

  private String fileName = "error/read_skipped";

  @OnSkipInRead
  public void onSkipInRead(Throwable t) {
    System.out.println("SkipListener");
    if (t instanceof ItemReaderException) {
      ItemReaderException flatFileParseE = (ItemReaderException) t;
      onSkip(flatFileParseE.getCause(), fileName);
    }
  }

  public void onSkip(Object o, String fileName) {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(fileName, true);
      fos.write(o.toString().getBytes());
      fos.write("\r\n".getBytes());
      fos.close();
    } catch (IOException e) {
      System.out.println("What? { " + e.getMessage());
      e.printStackTrace();
    }
  }

}

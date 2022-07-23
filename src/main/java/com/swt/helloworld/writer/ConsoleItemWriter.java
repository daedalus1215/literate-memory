package com.swt.helloworld.writer;

import java.util.List;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.stereotype.Component;

@Component
public class ConsoleItemWriter extends AbstractItemStreamItemWriter {

  @Override
  public void write(List items) throws Exception {
    items.stream().forEach(System.out::println);
    System.out.println(" ************** writing each chunk ************** ");
  }
}

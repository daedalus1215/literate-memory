package com.swt.helloworld.processors;

import com.swt.helloworld.model.Product;
import org.springframework.batch.item.ItemProcessor;

public class UppercaseProcessor implements ItemProcessor<Product, Product> {

  @Override
  public Product process(Product product) throws Exception {
    if (product.getProductID() != 2) {
      product.setProductDesc(product.getProductDesc().toUpperCase());
    }
    return product;
  }
}

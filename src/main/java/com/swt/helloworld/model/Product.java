package com.swt.helloworld.model;

public class Product {

  private final String productID;
  private final String productName;
  private final String ProductDesc;
  private final String price;
  private final Integer unit;

  public Product(String productID, String productName, String productDesc, String price, Integer unit) {
    this.productID = productID;
    this.productName = productName;
    ProductDesc = productDesc;
    this.price = price;
    this.unit = unit;
  }

  public String getProductID() {
    return productID;
  }

  public String getProductName() {
    return productName;
  }

  public String getProductDesc() {
    return ProductDesc;
  }

  public String getPrice() {
    return price;
  }

  public Integer getUnit() {
    return unit;
  }
}

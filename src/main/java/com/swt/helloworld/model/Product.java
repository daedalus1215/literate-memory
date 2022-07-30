package com.swt.helloworld.model;

public class Product {

  private  String productID;
  private  String productName;
  private  String price;
  private  Integer unit;
  private  String productDesc;

  public Product() {
  }

  public Product(String productID, String productName, String productDesc, String price, Integer unit) {
    this.productID = productID;
    this.productName = productName;
    this.productDesc = productDesc;
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
    return productDesc;
  }

  public String getPrice() {
    return price;
  }

  public Integer getUnit() {
    return unit;
  }

  public void setProductID(String productID) {
    this.productID = productID;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public void setUnit(Integer unit) {
    this.unit = unit;
  }

  public void setProductDesc(String productDesc) {
    this.productDesc = productDesc;
  }
}

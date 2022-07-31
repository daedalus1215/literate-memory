package com.swt.helloworld.model;

import java.math.BigDecimal;

public class Product {

  private  Integer productID;
  private  String productName;
  private  String productDesc;
  private BigDecimal price;
  private  Integer unit;

  public Product() {
  }

  public Product(Integer productID, String productName, String productDesc, BigDecimal price, Integer unit) {
    this.productID = productID;
    this.productName = productName;
    this.productDesc = productDesc;
    this.price = price;
    this.unit = unit;
  }

  public Integer getProductID() {
    return productID;
  }

  public void setProductID(Integer productID) {
    this.productID = productID;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductDesc() {
    return productDesc;
  }

  public void setProductDesc(String productDesc) {
    this.productDesc = productDesc;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Integer getUnit() {
    return unit;
  }

  public void setUnit(Integer unit) {
    this.unit = unit;
  }

  @Override
  public String toString() {
    return "Product{" +
        "productID=" + productID +
        ", productName='" + productName + '\'' +
        ", productDesc='" + productDesc + '\'' +
        ", price=" + price +
        ", unit=" + unit +
        '}';
  }
}

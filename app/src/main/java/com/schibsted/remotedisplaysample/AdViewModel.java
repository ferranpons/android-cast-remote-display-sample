package com.schibsted.remotedisplaysample;

public class AdViewModel {
  private final String id;
  private final String title;
  private final String price;
  private final String image;

  public AdViewModel(String id, String title, String price, String image) {
    this.id = id;
    this.title = title;
    this.price = price;
    this.image = image;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getPrice() {
    return price;
  }

  public String getImage() {
    return image;
  }
}

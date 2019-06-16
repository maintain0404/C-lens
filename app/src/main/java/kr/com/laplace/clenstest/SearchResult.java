package kr.com.laplace.clenstest;

import android.graphics.Bitmap;

public class SearchResult {
  String name;
  String low_price;
  String score;
  String link;
  Bitmap thumbnail;
  String correct_name;

  public SearchResult(String name, String low_price, String score, String link, Bitmap thumbnail, String correct_name) {
    this.name = name;
    this.low_price = low_price;
    this.score = score;
    this.link = link;
    this.thumbnail = thumbnail;
    this.correct_name = correct_name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLow_price() {
    return low_price;
  }

  public void setLow_price(String low_price) {
    this.low_price = low_price;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public Bitmap getThumbnail() {
    return thumbnail;
  }

  public void setThummbnail(Bitmap coordinate) {
    this.thumbnail = coordinate;
  }

  public String getCorrect_name() {
    return correct_name;
  }

  public void setCorrect_name(String correct_name) {
    this.correct_name = correct_name;
  }
}

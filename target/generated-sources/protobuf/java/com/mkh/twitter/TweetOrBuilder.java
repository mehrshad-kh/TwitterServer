// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: twitter.proto

package com.mkh.twitter;

public interface TweetOrBuilder extends
    // @@protoc_insertion_point(interface_extends:twitter.Tweet)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 id = 1;</code>
   */
  int getId();

  /**
   * <code>string text = 2;</code>
   */
  java.lang.String getText();
  /**
   * <code>string text = 2;</code>
   */
  com.google.protobuf.ByteString
      getTextBytes();

  /**
   * <code>int32 photo_id = 3;</code>
   */
  int getPhotoId();

  /**
   * <code>int32 tweet_id = 4;</code>
   */
  int getTweetId();

  /**
   * <code>.google.protobuf.Timestamp date_created = 5;</code>
   */
  boolean hasDateCreated();
  /**
   * <code>.google.protobuf.Timestamp date_created = 5;</code>
   */
  com.google.protobuf.Timestamp getDateCreated();
  /**
   * <code>.google.protobuf.Timestamp date_created = 5;</code>
   */
  com.google.protobuf.TimestampOrBuilder getDateCreatedOrBuilder();
}

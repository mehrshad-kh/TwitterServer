// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: twitter.proto

package com.mkh.twitter;

public final class TwitterProto {
  private TwitterProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_twitter_AuthResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_twitter_AuthResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_twitter_User_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_twitter_User_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_twitter_Tweet_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_twitter_Tweet_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rtwitter.proto\022\007twitter\032\037google/protobu" +
      "f/timestamp.proto\"3\n\014AuthResponse\022#\n\006res" +
      "ult\030\001 \001(\0162\023.twitter.AuthResult\"\337\002\n\004User\022" +
      "\n\n\002id\030\001 \001(\005\022\022\n\nfirst_name\030\002 \001(\t\022\021\n\tlast_" +
      "name\030\003 \001(\t\022\020\n\010username\030\004 \001(\t\022\020\n\010password" +
      "\030\005 \001(\t\022\r\n\005email\030\006 \001(\t\022\024\n\014phone_number\030\007 " +
      "\001(\t\022\022\n\ncountry_id\030\010 \001(\005\022-\n\tbirthdate\030\t \001" +
      "(\0132\032.google.protobuf.Timestamp\022\013\n\003bio\030\n " +
      "\001(\t\022\020\n\010location\030\013 \001(\t\022\017\n\007website\030\014 \001(\t\0220" +
      "\n\014date_created\030\r \001(\0132\032.google.protobuf.T" +
      "imestamp\0226\n\022date_last_modified\030\016 \001(\0132\032.g" +
      "oogle.protobuf.Timestamp\"w\n\005Tweet\022\n\n\002id\030" +
      "\001 \001(\005\022\014\n\004text\030\002 \001(\t\022\020\n\010photo_id\030\003 \001(\005\022\020\n" +
      "\010tweet_id\030\004 \001(\005\0220\n\014date_created\030\005 \001(\0132\032." +
      "google.protobuf.Timestamp*:\n\nAuthResult\022" +
      "\020\n\014INVALID_PASS\020\000\022\r\n\tNOT_FOUND\020\001\022\013\n\007GRAN" +
      "TED\020\0022x\n\007Twitter\0226\n\014Authenticate\022\r.twitt" +
      "er.User\032\025.twitter.AuthResponse\"\000\0225\n\020GetD" +
      "ailyBriefing\022\r.twitter.User\032\016.twitter.Tw" +
      "eet\"\0000\001B!\n\017com.mkh.twitterB\014TwitterProto" +
      "P\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.TimestampProto.getDescriptor(),
        });
    internal_static_twitter_AuthResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_twitter_AuthResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_twitter_AuthResponse_descriptor,
        new java.lang.String[] { "Result", });
    internal_static_twitter_User_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_twitter_User_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_twitter_User_descriptor,
        new java.lang.String[] { "Id", "FirstName", "LastName", "Username", "Password", "Email", "PhoneNumber", "CountryId", "Birthdate", "Bio", "Location", "Website", "DateCreated", "DateLastModified", });
    internal_static_twitter_Tweet_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_twitter_Tweet_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_twitter_Tweet_descriptor,
        new java.lang.String[] { "Id", "Text", "PhotoId", "TweetId", "DateCreated", });
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}

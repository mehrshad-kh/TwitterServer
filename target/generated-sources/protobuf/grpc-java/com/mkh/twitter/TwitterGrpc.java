package com.mkh.twitter;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.55.1)",
    comments = "Source: twitter.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class TwitterGrpc {

  private TwitterGrpc() {}

  public static final String SERVICE_NAME = "twitter.Twitter";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.mkh.twitter.User,
      com.mkh.twitter.AuthResponse> getAuthenticateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Authenticate",
      requestType = com.mkh.twitter.User.class,
      responseType = com.mkh.twitter.AuthResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.mkh.twitter.User,
      com.mkh.twitter.AuthResponse> getAuthenticateMethod() {
    io.grpc.MethodDescriptor<com.mkh.twitter.User, com.mkh.twitter.AuthResponse> getAuthenticateMethod;
    if ((getAuthenticateMethod = TwitterGrpc.getAuthenticateMethod) == null) {
      synchronized (TwitterGrpc.class) {
        if ((getAuthenticateMethod = TwitterGrpc.getAuthenticateMethod) == null) {
          TwitterGrpc.getAuthenticateMethod = getAuthenticateMethod =
              io.grpc.MethodDescriptor.<com.mkh.twitter.User, com.mkh.twitter.AuthResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Authenticate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.mkh.twitter.User.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.mkh.twitter.AuthResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TwitterMethodDescriptorSupplier("Authenticate"))
              .build();
        }
      }
    }
    return getAuthenticateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.mkh.twitter.User,
      com.mkh.twitter.Tweet> getGetDailyBriefingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetDailyBriefing",
      requestType = com.mkh.twitter.User.class,
      responseType = com.mkh.twitter.Tweet.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.mkh.twitter.User,
      com.mkh.twitter.Tweet> getGetDailyBriefingMethod() {
    io.grpc.MethodDescriptor<com.mkh.twitter.User, com.mkh.twitter.Tweet> getGetDailyBriefingMethod;
    if ((getGetDailyBriefingMethod = TwitterGrpc.getGetDailyBriefingMethod) == null) {
      synchronized (TwitterGrpc.class) {
        if ((getGetDailyBriefingMethod = TwitterGrpc.getGetDailyBriefingMethod) == null) {
          TwitterGrpc.getGetDailyBriefingMethod = getGetDailyBriefingMethod =
              io.grpc.MethodDescriptor.<com.mkh.twitter.User, com.mkh.twitter.Tweet>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetDailyBriefing"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.mkh.twitter.User.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.mkh.twitter.Tweet.getDefaultInstance()))
              .setSchemaDescriptor(new TwitterMethodDescriptorSupplier("GetDailyBriefing"))
              .build();
        }
      }
    }
    return getGetDailyBriefingMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TwitterStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TwitterStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TwitterStub>() {
        @java.lang.Override
        public TwitterStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TwitterStub(channel, callOptions);
        }
      };
    return TwitterStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TwitterBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TwitterBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TwitterBlockingStub>() {
        @java.lang.Override
        public TwitterBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TwitterBlockingStub(channel, callOptions);
        }
      };
    return TwitterBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TwitterFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TwitterFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TwitterFutureStub>() {
        @java.lang.Override
        public TwitterFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TwitterFutureStub(channel, callOptions);
        }
      };
    return TwitterFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void authenticate(com.mkh.twitter.User request,
        io.grpc.stub.StreamObserver<com.mkh.twitter.AuthResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAuthenticateMethod(), responseObserver);
    }

    /**
     */
    default void getDailyBriefing(com.mkh.twitter.User request,
        io.grpc.stub.StreamObserver<com.mkh.twitter.Tweet> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetDailyBriefingMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service Twitter.
   */
  public static abstract class TwitterImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return TwitterGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service Twitter.
   */
  public static final class TwitterStub
      extends io.grpc.stub.AbstractAsyncStub<TwitterStub> {
    private TwitterStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TwitterStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TwitterStub(channel, callOptions);
    }

    /**
     */
    public void authenticate(com.mkh.twitter.User request,
        io.grpc.stub.StreamObserver<com.mkh.twitter.AuthResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAuthenticateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getDailyBriefing(com.mkh.twitter.User request,
        io.grpc.stub.StreamObserver<com.mkh.twitter.Tweet> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getGetDailyBriefingMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service Twitter.
   */
  public static final class TwitterBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<TwitterBlockingStub> {
    private TwitterBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TwitterBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TwitterBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.mkh.twitter.AuthResponse authenticate(com.mkh.twitter.User request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAuthenticateMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<com.mkh.twitter.Tweet> getDailyBriefing(
        com.mkh.twitter.User request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getGetDailyBriefingMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service Twitter.
   */
  public static final class TwitterFutureStub
      extends io.grpc.stub.AbstractFutureStub<TwitterFutureStub> {
    private TwitterFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TwitterFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TwitterFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.mkh.twitter.AuthResponse> authenticate(
        com.mkh.twitter.User request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAuthenticateMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_AUTHENTICATE = 0;
  private static final int METHODID_GET_DAILY_BRIEFING = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_AUTHENTICATE:
          serviceImpl.authenticate((com.mkh.twitter.User) request,
              (io.grpc.stub.StreamObserver<com.mkh.twitter.AuthResponse>) responseObserver);
          break;
        case METHODID_GET_DAILY_BRIEFING:
          serviceImpl.getDailyBriefing((com.mkh.twitter.User) request,
              (io.grpc.stub.StreamObserver<com.mkh.twitter.Tweet>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getAuthenticateMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.mkh.twitter.User,
              com.mkh.twitter.AuthResponse>(
                service, METHODID_AUTHENTICATE)))
        .addMethod(
          getGetDailyBriefingMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              com.mkh.twitter.User,
              com.mkh.twitter.Tweet>(
                service, METHODID_GET_DAILY_BRIEFING)))
        .build();
  }

  private static abstract class TwitterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TwitterBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.mkh.twitter.TwitterProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Twitter");
    }
  }

  private static final class TwitterFileDescriptorSupplier
      extends TwitterBaseDescriptorSupplier {
    TwitterFileDescriptorSupplier() {}
  }

  private static final class TwitterMethodDescriptorSupplier
      extends TwitterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TwitterMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TwitterGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TwitterFileDescriptorSupplier())
              .addMethod(getAuthenticateMethod())
              .addMethod(getGetDailyBriefingMethod())
              .build();
        }
      }
    }
    return result;
  }
}

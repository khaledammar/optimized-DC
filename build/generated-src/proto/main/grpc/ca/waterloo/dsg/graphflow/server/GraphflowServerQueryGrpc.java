package ca.waterloo.dsg.graphflow.server;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.2.0)",
    comments = "Source: GraphflowServer.proto")
public final class GraphflowServerQueryGrpc {

  private GraphflowServerQueryGrpc() {}

  public static final String SERVICE_NAME = "GraphflowServerQuery";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ca.waterloo.dsg.graphflow.server.ServerQueryString,
      ca.waterloo.dsg.graphflow.server.ServerQueryResult> METHOD_EXECUTE_QUERY =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "GraphflowServerQuery", "ExecuteQuery"),
          io.grpc.protobuf.ProtoUtils.marshaller(ca.waterloo.dsg.graphflow.server.ServerQueryString.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(ca.waterloo.dsg.graphflow.server.ServerQueryResult.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GraphflowServerQueryStub newStub(io.grpc.Channel channel) {
    return new GraphflowServerQueryStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GraphflowServerQueryBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GraphflowServerQueryBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static GraphflowServerQueryFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GraphflowServerQueryFutureStub(channel);
  }

  /**
   */
  public static abstract class GraphflowServerQueryImplBase implements io.grpc.BindableService {

    /**
     */
    public void executeQuery(ca.waterloo.dsg.graphflow.server.ServerQueryString request,
        io.grpc.stub.StreamObserver<ca.waterloo.dsg.graphflow.server.ServerQueryResult> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_EXECUTE_QUERY, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_EXECUTE_QUERY,
            asyncUnaryCall(
              new MethodHandlers<
                ca.waterloo.dsg.graphflow.server.ServerQueryString,
                ca.waterloo.dsg.graphflow.server.ServerQueryResult>(
                  this, METHODID_EXECUTE_QUERY)))
          .build();
    }
  }

  /**
   */
  public static final class GraphflowServerQueryStub extends io.grpc.stub.AbstractStub<GraphflowServerQueryStub> {
    private GraphflowServerQueryStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GraphflowServerQueryStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GraphflowServerQueryStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GraphflowServerQueryStub(channel, callOptions);
    }

    /**
     */
    public void executeQuery(ca.waterloo.dsg.graphflow.server.ServerQueryString request,
        io.grpc.stub.StreamObserver<ca.waterloo.dsg.graphflow.server.ServerQueryResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_EXECUTE_QUERY, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GraphflowServerQueryBlockingStub extends io.grpc.stub.AbstractStub<GraphflowServerQueryBlockingStub> {
    private GraphflowServerQueryBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GraphflowServerQueryBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GraphflowServerQueryBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GraphflowServerQueryBlockingStub(channel, callOptions);
    }

    /**
     */
    public ca.waterloo.dsg.graphflow.server.ServerQueryResult executeQuery(ca.waterloo.dsg.graphflow.server.ServerQueryString request) {
      return blockingUnaryCall(
          getChannel(), METHOD_EXECUTE_QUERY, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GraphflowServerQueryFutureStub extends io.grpc.stub.AbstractStub<GraphflowServerQueryFutureStub> {
    private GraphflowServerQueryFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GraphflowServerQueryFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GraphflowServerQueryFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GraphflowServerQueryFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ca.waterloo.dsg.graphflow.server.ServerQueryResult> executeQuery(
        ca.waterloo.dsg.graphflow.server.ServerQueryString request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_EXECUTE_QUERY, getCallOptions()), request);
    }
  }

  private static final int METHODID_EXECUTE_QUERY = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GraphflowServerQueryImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GraphflowServerQueryImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXECUTE_QUERY:
          serviceImpl.executeQuery((ca.waterloo.dsg.graphflow.server.ServerQueryString) request,
              (io.grpc.stub.StreamObserver<ca.waterloo.dsg.graphflow.server.ServerQueryResult>) responseObserver);
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

  private static final class GraphflowServerQueryDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ca.waterloo.dsg.graphflow.server.GraphflowServerProto.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GraphflowServerQueryGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GraphflowServerQueryDescriptorSupplier())
              .addMethod(METHOD_EXECUTE_QUERY)
              .build();
        }
      }
    }
    return result;
  }
}

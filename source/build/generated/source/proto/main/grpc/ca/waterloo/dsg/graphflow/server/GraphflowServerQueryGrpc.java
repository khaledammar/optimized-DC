package ca.waterloo.dsg.graphflow.server;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.34.1)",
    comments = "Source: GraphflowServer.proto")
public final class GraphflowServerQueryGrpc {

  private GraphflowServerQueryGrpc() {}

  public static final String SERVICE_NAME = "GraphflowServerQuery";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ca.waterloo.dsg.graphflow.server.ServerQueryString,
      ca.waterloo.dsg.graphflow.server.ServerQueryResult> getExecuteQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteQuery",
      requestType = ca.waterloo.dsg.graphflow.server.ServerQueryString.class,
      responseType = ca.waterloo.dsg.graphflow.server.ServerQueryResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ca.waterloo.dsg.graphflow.server.ServerQueryString,
      ca.waterloo.dsg.graphflow.server.ServerQueryResult> getExecuteQueryMethod() {
    io.grpc.MethodDescriptor<ca.waterloo.dsg.graphflow.server.ServerQueryString, ca.waterloo.dsg.graphflow.server.ServerQueryResult> getExecuteQueryMethod;
    if ((getExecuteQueryMethod = GraphflowServerQueryGrpc.getExecuteQueryMethod) == null) {
      synchronized (GraphflowServerQueryGrpc.class) {
        if ((getExecuteQueryMethod = GraphflowServerQueryGrpc.getExecuteQueryMethod) == null) {
          GraphflowServerQueryGrpc.getExecuteQueryMethod = getExecuteQueryMethod =
              io.grpc.MethodDescriptor.<ca.waterloo.dsg.graphflow.server.ServerQueryString, ca.waterloo.dsg.graphflow.server.ServerQueryResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ca.waterloo.dsg.graphflow.server.ServerQueryString.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ca.waterloo.dsg.graphflow.server.ServerQueryResult.getDefaultInstance()))
              .setSchemaDescriptor(new GraphflowServerQueryMethodDescriptorSupplier("ExecuteQuery"))
              .build();
        }
      }
    }
    return getExecuteQueryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GraphflowServerQueryStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GraphflowServerQueryStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GraphflowServerQueryStub>() {
        @java.lang.Override
        public GraphflowServerQueryStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GraphflowServerQueryStub(channel, callOptions);
        }
      };
    return GraphflowServerQueryStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GraphflowServerQueryBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GraphflowServerQueryBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GraphflowServerQueryBlockingStub>() {
        @java.lang.Override
        public GraphflowServerQueryBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GraphflowServerQueryBlockingStub(channel, callOptions);
        }
      };
    return GraphflowServerQueryBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GraphflowServerQueryFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GraphflowServerQueryFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GraphflowServerQueryFutureStub>() {
        @java.lang.Override
        public GraphflowServerQueryFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GraphflowServerQueryFutureStub(channel, callOptions);
        }
      };
    return GraphflowServerQueryFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class GraphflowServerQueryImplBase implements io.grpc.BindableService {

    /**
     */
    public void executeQuery(ca.waterloo.dsg.graphflow.server.ServerQueryString request,
        io.grpc.stub.StreamObserver<ca.waterloo.dsg.graphflow.server.ServerQueryResult> responseObserver) {
      asyncUnimplementedUnaryCall(getExecuteQueryMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getExecuteQueryMethod(),
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
  public static final class GraphflowServerQueryStub extends io.grpc.stub.AbstractAsyncStub<GraphflowServerQueryStub> {
    private GraphflowServerQueryStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GraphflowServerQueryStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GraphflowServerQueryStub(channel, callOptions);
    }

    /**
     */
    public void executeQuery(ca.waterloo.dsg.graphflow.server.ServerQueryString request,
        io.grpc.stub.StreamObserver<ca.waterloo.dsg.graphflow.server.ServerQueryResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getExecuteQueryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GraphflowServerQueryBlockingStub extends io.grpc.stub.AbstractBlockingStub<GraphflowServerQueryBlockingStub> {
    private GraphflowServerQueryBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GraphflowServerQueryBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GraphflowServerQueryBlockingStub(channel, callOptions);
    }

    /**
     */
    public ca.waterloo.dsg.graphflow.server.ServerQueryResult executeQuery(ca.waterloo.dsg.graphflow.server.ServerQueryString request) {
      return blockingUnaryCall(
          getChannel(), getExecuteQueryMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GraphflowServerQueryFutureStub extends io.grpc.stub.AbstractFutureStub<GraphflowServerQueryFutureStub> {
    private GraphflowServerQueryFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GraphflowServerQueryFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GraphflowServerQueryFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ca.waterloo.dsg.graphflow.server.ServerQueryResult> executeQuery(
        ca.waterloo.dsg.graphflow.server.ServerQueryString request) {
      return futureUnaryCall(
          getChannel().newCall(getExecuteQueryMethod(), getCallOptions()), request);
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

  private static abstract class GraphflowServerQueryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GraphflowServerQueryBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ca.waterloo.dsg.graphflow.server.GraphflowServerProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GraphflowServerQuery");
    }
  }

  private static final class GraphflowServerQueryFileDescriptorSupplier
      extends GraphflowServerQueryBaseDescriptorSupplier {
    GraphflowServerQueryFileDescriptorSupplier() {}
  }

  private static final class GraphflowServerQueryMethodDescriptorSupplier
      extends GraphflowServerQueryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GraphflowServerQueryMethodDescriptorSupplier(String methodName) {
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
      synchronized (GraphflowServerQueryGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GraphflowServerQueryFileDescriptorSupplier())
              .addMethod(getExecuteQueryMethod())
              .build();
        }
      }
    }
    return result;
  }
}

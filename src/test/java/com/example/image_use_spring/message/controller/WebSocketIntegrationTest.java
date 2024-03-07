//package com.example.image_use_spring.message.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.TimeUnit;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompFrameHandler;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.util.concurrent.ListenableFuture;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//import org.springframework.web.socket.sockjs.client.Transport;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class WebSocketIntegrationTest {
//
//  @LocalServerPort
//  private int port;
//
//  private WebSocketStompClient stompClient;
//  private StompSession stompSession;
//  private final BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);
//
//  @BeforeEach
//  public void setup() throws Exception {
//    this.stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
//    this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//    String url = "ws://localhost:" + port + "/ws";
//    ListenableFuture<StompSession> stompSessionFuture = this.stompClient.connect(url, new StompSessionHandlerAdapter() {});
//    this.stompSession = stompSessionFuture.get(10, TimeUnit.SECONDS);
//  }
//
//  @AfterEach
//  public void tearDown() {
//    if (this.stompSession != null) {
//      this.stompSession.disconnect();
//    }
//    if (this.stompClient != null) {
//      this.stompClient.stop();
//    }
//  }
//
//  @Test
//  public void verifyWebSocketConnectionAndMessage() throws Exception {
//    this.stompSession.subscribe("/chat/groups/1", new DefaultStompFrameHandler());
//
//    String sampleMessage = "Hello, WebSocket!";
//    this.stompSession.send("/app/chat.send", sampleMessage);
//
//    assertEquals(sampleMessage, blockingQueue.poll(10, TimeUnit.SECONDS));
//  }
//
//  private List<Transport> createTransportClient() {
//    List<Transport> transports = new ArrayList<>();
//    transports.add(new WebSocketTransport(new StandardWebSocketClient()));
//    return transports;
//  }
//
//  private class DefaultStompFrameHandler implements StompFrameHandler {
//    @Override
//    public Type getPayloadType(StompHeaders headers) {
//      return String.class;
//    }
//
//    @Override
//    public void handleFrame(StompHeaders headers, Object payload) {
//      blockingQueue.add((String) payload);
//    }
//  }
//}

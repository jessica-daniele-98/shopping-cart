package com.shoppingcart;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public interface MongoDBContainers {

  @Container
  @ServiceConnection
  MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:8.0"));
}

package org.vertx.java.tests.core.net;

import org.junit.Test;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetClient;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.shareddata.SharedData;
import org.vertx.java.newtests.TestBase;
import vertx.tests.core.http.TLSTestParams;
import vertx.tests.core.net.CloseHandlerServer;
import vertx.tests.core.net.CloseHandlerServerCloseFromServer;
import vertx.tests.core.net.CloseSocketServer;
import vertx.tests.core.net.DrainingServer;
import vertx.tests.core.net.EchoServer;
import vertx.tests.core.net.EchoServerNoReady;
import vertx.tests.core.net.FanoutServer;
import vertx.tests.core.net.InstanceCheckServer;
import vertx.tests.core.net.PausingServer;
import vertx.tests.core.net.TLSServer;
import vertx.tests.core.net.TestClient;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class JavaNetTest extends TestBase {

  private static final Logger log = Logger.getLogger(JavaNetTest.class);

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    startApp(TestClient.class.getName());
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void testClientDefaults() throws Exception {
    startTest(getMethodName());
  }

  @Test
  public void testClientAttributes() throws Exception {
    startTest(getMethodName());
  }

  @Test
  public void testServerDefaults() throws Exception {
    startTest(getMethodName());
  }

  @Test
  public void testServerAttributes() throws Exception {
    startTest(getMethodName());
  }

  @Test
  public void testEchoBytes() throws Exception {
    startApp(EchoServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testEchoStringDefaultEncoding() throws Exception {
    startApp(EchoServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testEchoStringUTF8() throws Exception {
    startApp(EchoServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testEchoStringUTF16() throws Exception {
    startApp(EchoServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testConnectDefaultHost() throws Exception {
    startApp(EchoServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testConnectLocalHost() throws Exception {
    startApp(EchoServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testConnectInvalidPort() {
    startTest(getMethodName());
  }

  @Test
  public void testConnectInvalidHost() {
    startTest(getMethodName());
  }

  @Test
  public void testWriteWithCompletion() throws Exception {
    startApp(EchoServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testClientCloseHandlersCloseFromClient() throws Exception {
    startApp(EchoServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testClientCloseHandlersCloseFromServer() throws Exception {
    startApp(CloseSocketServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testServerCloseHandlersCloseFromClient() throws Exception {
    startApp(CloseHandlerServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testServerCloseHandlersCloseFromServer() throws Exception {
    startApp(CloseHandlerServerCloseFromServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testClientDrainHandler() throws Exception {
    startApp(PausingServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testServerDrainHandler() throws Exception {
    startApp(DrainingServer.class.getName());
    startTest(getMethodName());
  }

  @Test
  public void testReconnectAttemptsInfinite() throws Exception {
    // Start the client without the server
    startTest(getMethodName(), false);
    reconnectAttempts();
  }

  @Test
  public void testReconnectAttemptsMany() throws Exception {
    // Start the client without the server
    startTest(getMethodName(), false);
    reconnectAttempts();
  }

  @Test
  public void testReconnectAttemptsNotEnough() throws Exception {
    // Start the client without the server
    startTest(getMethodName());
  }

  void reconnectAttempts() throws Exception {
    // Wait a little while then start the server
    Thread.sleep(1000);
    startApp(EchoServerNoReady.class.getName(), false);
    waitTestComplete();
  }

  @Test
  // Client trusts all server certs
  public void testTLSClientTrustAll() throws Exception {
    testTLS(getMethodName(), false, false, true, false, false, true, true);
  }

  @Test
  // Server specifies cert that the client trusts (not trust all)
  public void testTLSClientTrustServerCert() throws Exception {
    testTLS(getMethodName(), false, true, true, false, false, false, true);
  }

  @Test
  // Server specifies cert that the client doesn't trust
  public void testTLSClientUntrustedServer() throws Exception {
    testTLS(getMethodName(), false, false, true, false, false, false, false);
  }

  @Test
  //Client specifies cert even though it's not required
  public void testTLSClientCertNotRequired() throws Exception {
    testTLS(getMethodName(), true, true, true, true, false, false, true);
  }

  @Test
  //Client specifies cert and it's not required
  public void testTLSClientCertRequired() throws Exception {
    testTLS(getMethodName(), true, true, true, true, true, false, true);
  }

  @Test
  //Client doesn't specify cert but it's required
  public void testTLSClientCertRequiredNoClientCert() throws Exception {
    testTLS(getMethodName(), false, true, true, true, true, false, false);
  }

  @Test
  //Client specifies cert but it's not trusted
  public void testTLSClientCertClientNotTrusted() throws Exception {
    testTLS(getMethodName(), true, true, true, false, true, false, false);
  }

  void testTLS(String testName, boolean clientCert, boolean clientTrust,
               boolean serverCert, boolean serverTrust,
               boolean requireClientAuth, boolean clientTrustAll,
               boolean shouldPass) throws Exception {
    //Put the params in shared-data
    TLSTestParams params = new TLSTestParams(clientCert, clientTrust, serverCert, serverTrust,
        requireClientAuth, clientTrustAll, shouldPass);
    SharedData.instance.getMap("TLSTest").put("params", params.serialize());
    startApp(TLSServer.class.getName());
    startTest(testName);
  }

  @Test
  public void testSharedServersMultipleInstances1() throws Exception {
    int numInstances = Runtime.getRuntime().availableProcessors() * 2;
    sharedServers(getMethodName(), true, numInstances, 0, 0);
  }

  @Test
  public void testSharedServersMultipleInstances2() throws Exception {
    int numInstances = Runtime.getRuntime().availableProcessors() - 1;
    sharedServers(getMethodName(), true, numInstances, 0, 0);
  }

  @Test
  public void testSharedServersMultipleInstances3() throws Exception {
    int numInstances = Runtime.getRuntime().availableProcessors() + 1;
    sharedServers(getMethodName(), true, numInstances, 0, 0);
  }

  @Test
  public void testSharedServersMultipleInstances1StartAllStopAll() throws Exception {
    int numInstances = Runtime.getRuntime().availableProcessors() * 2;
    sharedServers(getMethodName(), true, numInstances, numInstances, numInstances);
  }

  @Test
  public void testSharedServersMultipleInstances2StartAllStopAll() throws Exception {
    int numInstances = Runtime.getRuntime().availableProcessors() - 1;
    sharedServers(getMethodName(), true, numInstances, numInstances, numInstances);
  }

  @Test
  public void testSharedServersMultipleInstances3StartAllStopAll() throws Exception {
    int numInstances = Runtime.getRuntime().availableProcessors() + 1;
    sharedServers(getMethodName(), true, numInstances, numInstances,
        numInstances);
  }

  @Test
  public void testSharedServersMultipleInstances1StartAllStopSome() throws Exception {
    int numInstances = Runtime.getRuntime().availableProcessors() * 2;
    sharedServers(getMethodName(), true, numInstances, numInstances, numInstances / 2);
  }

  @Test
  public void testSharedServersMultipleInstances2StartAllStopSome() throws Exception {
    int numInstances = Runtime.getRuntime().availableProcessors() - 1;
    sharedServers(getMethodName(), true, numInstances, numInstances, numInstances / 2);
  }

  @Test
  public void testSharedServersMultipleInstances3StartAllStopSome() throws Exception {
    int numInstances = Runtime.getRuntime().availableProcessors() + 1;
    sharedServers(getMethodName(), true, numInstances, numInstances,
        numInstances / 2);
  }

  void sharedServers(String testName, boolean multipleInstances, int numInstances, int initialServers, int initialToStop) throws Exception {

    // Start an echo server on a different port to make sure shared servers work ok when there are other servers
    // on different ports

    SharedData.instance.getMap("params").put("listenport", 8181);
    startApp(EchoServer.class.getName(), true);
    SharedData.instance.getMap("params").remove("listenport");

    //We initially start then stop them to make sure the shared server cleanup code works ok

    int numConnections = 100;

    if (initialServers > 0) {

      // First start some servers
      String[] appNames = new String[initialServers];
      for (int i = 0; i < initialServers; i++) {
        appNames[i] = startApp(InstanceCheckServer.class.getName(), 1);
      }

      SharedData.instance.getSet("connections").clear();
      SharedData.instance.getSet("servers").clear();
      SharedData.instance.getSet("instances").clear();
      SharedData.instance.getMap("params").put("numConnections", numConnections);

      startTest(testName);

      assertEquals(numConnections, SharedData.instance.getSet("connections").size());
      // And make sure connection requests are distributed amongst them
      assertEquals(initialServers, SharedData.instance.getSet("instances").size());

      // Then stop some

      for (int i = 0; i < initialToStop; i++) {
        stopApp(appNames[i]);
      }
    }

    SharedData.instance.getSet("connections").clear();
    SharedData.instance.getSet("servers").clear();
    SharedData.instance.getSet("instances").clear();
    SharedData.instance.getMap("params").put("numConnections", numConnections);

    //Now start some more

    if (multipleInstances) {
      startApp(InstanceCheckServer.class.getName(), numInstances);
    } else {
      for (int i = 0; i < numInstances; i++) {
        startApp(InstanceCheckServer.class.getName(), 1);
      }
    }

    startTest(testName);

    assertEquals(numConnections, SharedData.instance.getSet("connections").size());
    // And make sure connection requests are distributed amongst them
    assertEquals(numInstances + initialServers - initialToStop, SharedData.instance.getSet("instances").size());
  }

  @Test
  public void testCreateServerNoContext() throws Exception {
    try {
      new NetServer();
      fail("Should throw exception");
    } catch (IllegalStateException e) {
      // Ok
    }
  }

  @Test
  public void testCreateClientNoContext() throws Exception {
    try {
      new NetClient();
      fail("Should throw exception");
    } catch (IllegalStateException e) {
      // Ok
    }
  }

  @Test
  public void testFanout() throws Exception {
    startApp(FanoutServer.class.getName());
    startTest(getMethodName());
  }
}

package mockserver;

import org.apache.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public class TestMockServer {

    private static ClientAndServer mockServer;
    private MockServer mock = new MockServer();

    @BeforeClass
    public static void startServer() {
        mockServer = startClientAndServer(1080);
    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }

    @Test
    public void whenPostRequestMockServer_thenServerReceived() {
        mock.createExpectationForInvalidAuth();
        mock.hitTheServerWithPostRequest();
        mock.verifyPostRequest();
    }

    @Test
    public void whenPostRequestForInvalidAuth_then401Received() {
        mock.createExpectationForInvalidAuth();
        HttpResponse response = mock.hitTheServerWithPostRequest();
        assertEquals(401, response.getStatusLine().getStatusCode());
    }

    @Test
    public void whenGetRequest_ThenForward() {
        mock.createExpectationForForward();
        mock.hitTheServerWithGetRequest();
        mock.verifyGetRequest();

    }
}

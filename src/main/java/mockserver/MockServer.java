package mockserver;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpForward;
import org.mockserver.verify.VerificationTimes;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpForward.forward;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.StringBody.exact;

class MockServer {

    private final static String LOCALHOST = "localhost";
    private final static String MOCK_SERVER_CLIENT = "127.0.0.1";
    private final static String GET = "GET";
    private final static String POST = "POST";
    private final static String URL = "http://127.0.0.1:1080";
    private final static String ESKY = "www.esky.com";
    private final static String INDEX_HTML = "/index.html";
    private final static String VALIDATE = "/validate";

    void verifyPostRequest() {
        new MockServerClient(LOCALHOST, 1080).verify(
                request()
                        .withMethod(POST)
                        .withPath(VALIDATE)
                        .withBody(exact("{username: 'user', password: 'password'}")),
                VerificationTimes.exactly(1)
        );
    }

    void verifyGetRequest() {
        new MockServerClient(LOCALHOST, 1080).verify(
                request()
                        .withMethod(GET)
                        .withPath(INDEX_HTML),
                VerificationTimes.exactly(1)
        );
    }

    HttpResponse hitTheServerWithPostRequest() {
        String url = URL + VALIDATE;
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        HttpResponse response;

        try {
            StringEntity stringEntity = new StringEntity("{username: 'user', password: 'password'}");
            post.getRequestLine();
            post.setEntity(stringEntity);
            response = client.execute(post);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    void hitTheServerWithGetRequest() {
        String url = URL + INDEX_HTML;
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(url);
        try {
            client.execute(get);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void createExpectationForInvalidAuth() {
        new MockServerClient(MOCK_SERVER_CLIENT, 1080)
                .when(request().withMethod(POST).withPath(VALIDATE), exactly(1)).respond(
                response()
                        .withStatusCode(401)
                        .withBody("{ message: 'incorrect username and password combination' }")
                        .withDelay(TimeUnit.SECONDS, 1)
        );
    }

    void createExpectationForForward() {
        new MockServerClient(MOCK_SERVER_CLIENT, 1080)
                .when(request().withMethod(GET).withPath(INDEX_HTML),
                        exactly(1)
                )
                .forward(forward().withHost(ESKY).withScheme(HttpForward.Scheme.HTTP));
    }
}

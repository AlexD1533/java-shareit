package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    private TestBaseClient client;

    private static class TestBaseClient extends BaseClient {
        public TestBaseClient(RestTemplate rest) {
            super(rest);
        }

        public ResponseEntity<Object> testGet(String path) {
            return get(path);
        }

        public ResponseEntity<Object> testGet(String path, long userId) {
            return get(path, userId);
        }

        public ResponseEntity<Object> testGet(String path, Long userId, Map<String, Object> parameters) {
            return get(path, userId, parameters);
        }

        public <T> ResponseEntity<Object> testPost(String path, T body) {
            return post(path, body);
        }

        public <T> ResponseEntity<Object> testPost(String path, long userId, T body) {
            return post(path, userId, body);
        }

        public <T> ResponseEntity<Object> testPost(String path, Long userId, Map<String, Object> parameters, T body) {
            return post(path, userId, parameters, body);
        }

        public <T> ResponseEntity<Object> testPut(String path, long userId, T body) {
            return put(path, userId, body);
        }

        public <T> ResponseEntity<Object> testPut(String path, long userId, Map<String, Object> parameters, T body) {
            return put(path, userId, parameters, body);
        }

        public <T> ResponseEntity<Object> testPatch(String path, T body) {
            return patch(path, body);
        }

        public <T> ResponseEntity<Object> testPatch(String path, long userId) {
            return patch(path, userId);
        }

        public <T> ResponseEntity<Object> testPatch(String path, long userId, T body) {
            return patch(path, userId, body);
        }

        public <T> ResponseEntity<Object> testPatch(String path, Long userId, Map<String, Object> parameters, T body) {
            return patch(path, userId, parameters, body);
        }

        public ResponseEntity<Object> testDelete(String path) {
            return delete(path);
        }

        public ResponseEntity<Object> testDelete(String path, long userId) {
            return delete(path, userId);
        }

        public ResponseEntity<Object> testDelete(String path, Long userId, Map<String, Object> parameters) {
            return delete(path, userId, parameters);
        }
    }

    @BeforeEach
    void setUp() {
        client = new TestBaseClient(restTemplate);
    }

    @Test
    void testGet_WithoutParameters() {
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testGet(path);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGet_WithUserId() {
        String path = "/test";
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testGet(path, userId);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testGet_WithParameters() {
        String path = "/test?param={param}";
        long userId = 1L;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param", "value");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testGet(path, userId, parameters);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPost_WithoutUserIdAndParameters() {
        String path = "/test";
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testPost(path, body);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPost_WithUserId() {
        String path = "/test";
        long userId = 1L;
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testPost(path, userId, body);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPost_WithParameters() {
        String path = "/test?param={param}";
        long userId = 1L;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param", "value");
        String body = "request body";

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testPost(path, userId, parameters, body);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPut_WithParameters() {
        String path = "/test?param={param}";
        long userId = 1L;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param", "value");
        String body = "request body";

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.PUT),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testPut(path, userId, parameters, body);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPut_WithoutParameters() {
        String path = "/test";
        long userId = 1L;
        String body = "request body";

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.PUT),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testPut(path, userId, body);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPatch_WithoutUserIdAndParameters() {
        String path = "/test";
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testPatch(path, body);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPatch_WithUserIdOnly() {
        String path = "/test";
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testPatch(path, userId);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPatch_WithUserIdAndBody() {
        String path = "/test";
        long userId = 1L;
        String body = "request body";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testPatch(path, userId, body);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPatch_WithParameters() {
        String path = "/test?param={param}";
        long userId = 1L;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param", "value");
        String body = "request body";

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.PATCH),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testPatch(path, userId, parameters, body);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testDelete_WithoutParameters() {
        String path = "/test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.DELETE),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testDelete(path);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testDelete_WithUserId() {
        String path = "/test";
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.DELETE),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testDelete(path, userId);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testDelete_WithParameters() {
        String path = "/test?param={param}";
        long userId = 1L;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param", "value");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.DELETE),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testDelete(path, userId, parameters);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testMakeAndSendRequest_WithHttpStatusCodeException() {
        String path = "/test";
        HttpStatusCodeException exception = org.mockito.Mockito.mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(exception.getResponseBodyAsByteArray()).thenReturn("Error".getBytes());

        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> response = client.testGet(path);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), equalTo("Error".getBytes()));
    }

    @Test
    void testDefaultHeaders_WithUserId() {
        String path = "/test";
        long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenAnswer(invocation -> {
                    HttpEntity<?> requestEntity = invocation.getArgument(2);
                    HttpHeaders actualHeaders = requestEntity.getHeaders();

                    assertThat(actualHeaders.getContentType(), equalTo(MediaType.APPLICATION_JSON));
                    assertThat(actualHeaders.getAccept(), contains(MediaType.APPLICATION_JSON));
                    assertThat(actualHeaders.getFirst("X-Sharer-User-Id"), equalTo("1"));

                    return expectedResponse;
                });

        ResponseEntity<Object> response = client.testGet(path, userId);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testDefaultHeaders_WithoutUserId() {
        String path = "/test";

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");

        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenAnswer(invocation -> {
                    HttpEntity<?> requestEntity = invocation.getArgument(2);
                    HttpHeaders actualHeaders = requestEntity.getHeaders();

                    assertThat(actualHeaders.getContentType(), equalTo(MediaType.APPLICATION_JSON));
                    assertThat(actualHeaders.getAccept(), contains(MediaType.APPLICATION_JSON));
                    assertThat(actualHeaders.getFirst("X-Sharer-User-Id"), nullValue());

                    return expectedResponse;
                });

        ResponseEntity<Object> response = client.testGet(path);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testMakeAndSendRequest_WithNullParameters() {
        String path = "/test";
        long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                eq(path),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = client.testGet(path, userId, null);
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    void testPrepareGatewayResponse_WithErrorStatusWithoutBody() {
        HttpStatusCodeException exception = org.mockito.Mockito.mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(exception.getResponseBodyAsByteArray()).thenReturn(null);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> response = client.testGet("/test");

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), nullValue());
    }
}
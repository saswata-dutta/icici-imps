import lombok.NonNull;
import lombok.experimental.UtilityClass;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@UtilityClass
public class HttpExecutor {
  private static OkHttpClient client;
  private static ExecutorService threadExecutor;
  private static final int TTL = 1;

  static {
    client =
        new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();

    threadExecutor = Executors.newSingleThreadExecutor();
  }

  public static Optional<String> apply(String baseUrl, Map<String, String> params) {

    String bodyStr =
        params.entrySet().stream()
            .map(it -> it.getKey() + "=" + it.getValue())
            .collect(Collectors.joining("&"));

    RequestBody body = RequestBody.create(MEDIA_TYPE, bodyStr);
    Request request =
        new Request.Builder()
            .addHeader("Accept", "application/xml")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .url(baseUrl)
            .post(body)
            .build();
    System.out.println("Fork Init Request ...");
    Future<Optional<String>> asyncResponse = threadExecutor.submit(() -> execute(request));
    try {
      return asyncResponse.get(TTL, TimeUnit.MINUTES);
    } catch (TimeoutException e) {
      System.err.println("Timed out Sending Request");
      e.printStackTrace();
    } catch (ExecutionException e) {
      System.err.println("Error Sending Request");
      e.printStackTrace();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Thread Interrupted");
      e.printStackTrace();
    }
    // stop the pending request
    client.dispatcher().cancelAll();
    asyncResponse.cancel(true);
    return Optional.empty();
  }

  private static Optional<String> execute(Request request) {
    System.out.println("Init Request ...");
    try (Response response = client.newCall(request).execute()) {
      System.out.println("Receive Response ...");
      if (response != null && response.isSuccessful()) {
        return Optional.ofNullable(consumeResponse(response));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  private static String consumeResponse(@NonNull Response response) throws IOException {
    String body = response.body() == null ? "" : response.body().string();

    System.out.println("Code: " + response.code());
    if (response.headers() != null) System.out.println("Headers: " + response.headers().toString());
    System.out.println("Body: " + body);
    if (response.trailers() != null)
      System.out.println("Trailers: " + response.trailers().toString());

    return body;
  }

  static final MediaType MEDIA_TYPE =
      MediaType.get("application/x-www-form-urlencoded; charset=utf-8");

  public static void shutDown() {
    System.out.println("ShutDown ...");
    threadExecutor.shutdown();
  }
}

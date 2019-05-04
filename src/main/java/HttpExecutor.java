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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@UtilityClass
public class HttpExecutor {
  private static OkHttpClient client;

  static {
    client =
        new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();
  }

  public Optional<String> apply(String baseUrl, Map<String, String> params) throws IOException {

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

    try (Response response = client.newCall(request).execute()) {
      if (response == null || !response.isSuccessful()) {
        return Optional.empty();
      }
      return Optional.ofNullable(consumeResponse(response));
    }
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
}

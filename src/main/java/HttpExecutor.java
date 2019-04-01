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
      if (!response.isSuccessful() || response.body() == null) {
        System.out.println(response.code());
        if (response.body() != null) {
          System.out.println(response.body().string());
        }
        return Optional.empty();
      }
      return Optional.ofNullable(response.body().string());
    }
  }

  static final MediaType MEDIA_TYPE =
      MediaType.get("application/x-www-form-urlencoded; charset=utf-8");
}

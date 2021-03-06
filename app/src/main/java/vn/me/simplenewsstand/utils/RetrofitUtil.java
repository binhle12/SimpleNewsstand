package vn.me.simplenewsstand.utils;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.me.simplenewsstand.BuildConfig;
import vn.me.simplenewsstand.model.ApiResponse;

public class RetrofitUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final Gson GSON = new Gson();

    public static Retrofit get() {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient client() {
        return new OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor())
                .addInterceptor(responseInterceptor())
                .addInterceptor(loggingInterceptor())
                .build();
    }

    private static Interceptor apiKeyInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url()
                        .newBuilder()
                        .addQueryParameter("api_key", BuildConfig.API_KEY)
                        .build();
                request = request.newBuilder()
                        .url(url)
                        .build();
                return chain.proceed(request);
            }
        };
    }

    private static Interceptor responseInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                ResponseBody responseBody = response.body();
                ApiResponse apiResponse = GSON.fromJson(responseBody.string(), ApiResponse.class);
                responseBody.close();

                response = response.newBuilder()
                        .body(ResponseBody.create(JSON, apiResponse.getResponse().toString()))
                        .build();
                return response;
            }
        };
    }

    private static Interceptor loggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

}

package de.danoeh.antennapod.core.service;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class BasicAuthorizationInterceptor implements Interceptor {
    private static final String TAG = "BasicAuthInterceptor";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response response = chain.proceed(request);

        if (response.code() != HttpURLConnection.HTTP_UNAUTHORIZED) {
            return response;
        }

        Request.Builder newRequest = request.newBuilder();
        if (!TextUtils.equals(response.request().url().toString(), request.url().toString())) {
            // Redirect detected. OkHTTP does not re-add the headers on redirect, so calling the new location directly.
            newRequest.url(response.request().url());

            List<String> authorizationHeaders = request.headers().values(HEADER_AUTHORIZATION);
            if (!authorizationHeaders.isEmpty() && !TextUtils.isEmpty(authorizationHeaders.get(0))) {
                // Call already had authorization headers. Try again with the same credentials.
                newRequest.header(HEADER_AUTHORIZATION, authorizationHeaders.get(0));
                return chain.proceed(newRequest.build());
            }
        }



        return chain.proceed(newRequest.build());
    }
}

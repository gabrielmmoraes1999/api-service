package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class OAuth2TokenEvents {

    private static final List<OAuth2TokenSuccessListener> successListeners = new CopyOnWriteArrayList<>();

    private OAuth2TokenEvents() {
    }

    public static void addSuccessListener(OAuth2TokenSuccessListener listener) {
        Objects.requireNonNull(listener, "listener");
        successListeners.add(listener);
    }

    public static void removeSuccessListener(OAuth2TokenSuccessListener listener) {
        successListeners.remove(listener);
    }

    public static void clearSuccessListeners() {
        successListeners.clear();
    }

    public static void fireSuccess(OAuth2TokenSuccessContext context) {
        Objects.requireNonNull(context, "context");
        for (OAuth2TokenSuccessListener listener : successListeners) {
            listener.onSuccess(context);
        }
    }

}

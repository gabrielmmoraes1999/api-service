package io.github.gabrielmmoraes1999.apiservice.security.oauth2;

@FunctionalInterface
public interface OAuth2TokenSuccessListener {

    void onSuccess(OAuth2TokenSuccessContext context);

}

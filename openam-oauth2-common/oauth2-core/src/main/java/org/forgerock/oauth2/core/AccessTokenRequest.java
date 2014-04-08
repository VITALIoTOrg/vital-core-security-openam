/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2014 ForgeRock AS.
 */

package org.forgerock.oauth2.core;

import org.forgerock.util.Reject;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.forgerock.oauth2.core.GrantType.DefaultGrantType.AUTHORIZATION_CODE;
import static org.forgerock.oauth2.core.GrantType.DefaultGrantType.CLIENT_CREDENTIALS;
import static org.forgerock.oauth2.core.GrantType.DefaultGrantType.PASSWORD;
import static org.forgerock.oauth2.core.Utils.splitScope;

/**
 * Represents a request to gain an OAuth2 Access Token.
 * <br/>
 * There is a builder object for each of the standard spec defined grant types.
 *
 * @since 12.0.0
 */
public interface AccessTokenRequest {

    public static class AuthorizationCodeAccessTokenRequest implements AccessTokenRequest {

        /**
         * AccessTokenRequest builder for creating an AccessTokenRequest for the OAuth2 Authorization Code grant type.
         *
         * @since 12.0.0
         */
        public static final class AuthorizationCodeAccessTokenRequestBuilder {

            private final GrantType grantType = AUTHORIZATION_CODE;
            private String code;
            private String redirectUri;
            private String clientId;
            private ClientCredentials clientCredentials;
            private Map<String, Object> context;

            /**
             * Constructs a new AuthorizationCodeAccessTokenRequestBuilder.
             */
            private AuthorizationCodeAccessTokenRequestBuilder() { }

            /**
             * Adds the authorization code to the builder.
             *
             * @param code The authorization code.
             * @return This builder instance.
             */
            public AuthorizationCodeAccessTokenRequestBuilder code(final String code) {
                this.code = code;
                return this;
            }

            /**
             * Adds the redirect uri to the builder.
             *
             * @param redirectUri The redirect uri.
             * @return This builder instance.
             */
            public AuthorizationCodeAccessTokenRequestBuilder redirectUri(final String redirectUri) {
                this.redirectUri = redirectUri;
                return this;
            }

            /**
             * Adds the client's identifier to the builder.
             *
             * @param clientId The client's identifier.
             * @return This builder instance.
             */
            public AuthorizationCodeAccessTokenRequestBuilder clientId(final String clientId) {
                this.clientId = clientId;
                return this;
            }

            /**
             * Adds the client's credentials to the builder.
             *
             * @param clientCredentials The client's credentials.
             * @return This builder instance.
             */
            public AuthorizationCodeAccessTokenRequestBuilder clientCredentials(
                    final ClientCredentials clientCredentials) {
                this.clientCredentials = clientCredentials;
                return this;
            }

            /**
             * Adds the {@code Map<String, Object>} containing OAuth2 Provider implementation specific context information
             * to the builder.
             *
             * @param context The {@code Map<String, Object>} containing OAuth2 Provider implementation specific context
             *                information.
             * @return This builder instance.
             */
            public AuthorizationCodeAccessTokenRequestBuilder context(final Map<String, Object> context) {
                this.context = Collections.unmodifiableMap(context);
                return this;
            }

            /**
             * Builds the AuthorizationCodeAccessTokenRequest from the provided information.
             * <br/>
             * Validates that the builder has been given all the required data to be able to build a
             * AuthorizationCodeAccessTokenRequest for the OAuth2 Authorization Code grant type.
             *
             * @return An AuthorizationCodeAccessTokenRequest instance.
             */
            public AuthorizationCodeAccessTokenRequest build() {
                Reject.ifNull(clientCredentials, "Client Authentication must be set.");
                Reject.ifTrue(clientCredentials.getClientId() == null, "Missing parameter, 'client_id'");
                Reject.ifTrue(clientCredentials.getClientId().isEmpty(), "Missing parameter, 'client_id'");
                Reject.ifTrue(code == null, "Missing parameter, 'code'");
                Reject.ifTrue(code.isEmpty(), "Missing parameter, 'code'");
                Reject.ifTrue(redirectUri == null, "Missing parameter, 'redirect_uri'");
                Reject.ifTrue(redirectUri.isEmpty(), "Missing parameter, 'redirect_uri'");

                return new AuthorizationCodeAccessTokenRequest(grantType, code, redirectUri, clientId,
                        clientCredentials, context);
            }
        }

        /**
         * Creates a new builder for creating an AccessTokenRequest for the OAuth2 Authorization Code grant type.
         *
         * @return A new instance of the AuthorizationCodeAccessTokenRequestBuilder.
         */
        public static AuthorizationCodeAccessTokenRequestBuilder createAuthorizationCodeAccessTokenRequest() {
            return new AuthorizationCodeAccessTokenRequestBuilder();
        }

        private final GrantType grantType;
        private final String code;
        private final String redirectUri;
        private final String clientId;
        private final ClientCredentials clientCredentials;
        private final Map<String, Object> context;

        /**
         * Constructs a new AuthorizationCodeAccessTokenRequest.
         *
         * @param grantType The grant type.
         * @param code The authorization code.
         * @param redirectUri The redirect uri.
         * @param clientId The client's identifier.
         * @param clientCredentials The client's credentials.
         * @param context A {@code Map<String, Object>} containing OAuth2 Provider implementation specific context
         *                information.
         */
        private AuthorizationCodeAccessTokenRequest(final GrantType grantType, final String code,
                final String redirectUri, final String clientId, final ClientCredentials clientCredentials,
                final Map<String, Object> context) {
            this.grantType = grantType;
            this.code = code;
            this.redirectUri = redirectUri;
            this.clientId = clientId;
            this.clientCredentials = clientCredentials;
            this.context = context;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public GrantType getGrantType() {
            return grantType;
        }

        /**
         * Gets the Authorization code.
         *
         * @return The authorization code.
         */
        public String getCode() {
            return code;
        }

        /**
         * Gets the redirect URI.
         *
         * @return The redirect URI.
         */
        public String getRedirectUri() {
            return redirectUri;
        }

        /**
         * Gets the client's identifier.
         *
         * @return The client's identifier.
         */
        public String getClientId() {
            return clientId;
        }

        /**
         * Gets the client's credentials.
         *
         * @return The client's credentials.
         */
        public ClientCredentials getClientCredentials() {
            return clientCredentials;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, Object> getContext() {
            return context;
        }
    }

    public static class PasswordCredentialsAccessTokenRequest implements AccessTokenRequest {

        /**
         * AccessTokenRequest builder for creating an AccessTokenRequest for the OAuth2 Password Credentials grant type.
         *
         * @since 12.0.0
         */
        public static final class PasswordCredentialsAccessTokenRequestBuilder {

            private final GrantType grantType = PASSWORD;
            private ResourceOwnerPasswordAuthenticationHandler authenticationHandler;
            private ClientCredentials clientCredentials;
            private String scope;
            private Map<String, Object> context;

            /**
             * Constructs a new PasswordCredentialsAccessTokenRequestBuilder.
             */
            private PasswordCredentialsAccessTokenRequestBuilder() { }

            /**
             * Adds the client's credentials to the builder.
             *
             * @param clientCredentials The client's credentials.
             * @return This builder instance.
             */
            public PasswordCredentialsAccessTokenRequestBuilder clientCredentials(
                    final ClientCredentials clientCredentials) {
                this.clientCredentials = clientCredentials;
                return this;
            }

            /**
             * Adds the authentication handler instance to the builder.
             *
             * @param authenticationHandler The authentication handler instance.
             * @return This builder instance.
             */
            public PasswordCredentialsAccessTokenRequestBuilder authenticationHandler(
                    final ResourceOwnerPasswordAuthenticationHandler authenticationHandler) {
                this.authenticationHandler = authenticationHandler;
                return this;
            }

            /**
             * Adds the requested scope to the builder.
             *
             * @param scope The requested scope.
             * @return This builder instance.
             */
            public PasswordCredentialsAccessTokenRequestBuilder scope(final String scope) {
                this.scope = scope;
                return this;
            }

            /**
             * Adds the {@code Map<String, Object>} containing OAuth2 Provider implementation specific context information
             * to the builder.
             *
             * @param context The {@code Map<String, Object>} containing OAuth2 Provider implementation specific context
             *                information.
             * @return This builder instance.
             */
            public PasswordCredentialsAccessTokenRequestBuilder context(final Map<String, Object> context) {
                this.context = Collections.unmodifiableMap(context);
                return this;
            }

            /**
             * Builds the PasswordCredentialsAccessTokenRequest from the provided information.
             * <br/>
             * Validates that the builder has been given all the required data to be able to build a
             * PasswordCredentialsAccessTokenRequest for the OAuth2 Password Credentials grant type.
             *
             * @return A PasswordCredentialsAccessTokenRequest instance.
             */
            public PasswordCredentialsAccessTokenRequest build() {
                Reject.ifNull(clientCredentials, "Client Authentication must be set.");
                Reject.ifTrue(clientCredentials.getClientId() == null, "Missing parameter, 'client_id'");
                Reject.ifTrue(clientCredentials.getClientId().isEmpty(), "Missing parameter, 'client_id'");
                Reject.ifNull(authenticationHandler, "Authentication Handler must be set.");
                Reject.ifTrue(authenticationHandler.getUsername() == null, "Missing parameter, 'username'");
                Reject.ifTrue(authenticationHandler.getUsername().isEmpty(), "Missing parameter, 'username'");
                Reject.ifTrue(authenticationHandler.getPassword() == null, "Missing parameter, 'password'");
                Reject.ifTrue(authenticationHandler.getPassword().length == 0, "Missing parameter, 'password'");

                return new PasswordCredentialsAccessTokenRequest(grantType, clientCredentials, authenticationHandler,
                        splitScope(scope), context);
            }
        }

        /**
         * Creates a new builder for creating an AccessTokenRequest for the OAuth2 Password Credentials grant type.
         *
         * @return A new instance of the PasswordCredentialsAccessTokenRequestBuilder.
         */
        public static PasswordCredentialsAccessTokenRequestBuilder createPasswordAccessTokenRequest() {
            return new PasswordCredentialsAccessTokenRequestBuilder();
        }

        private final GrantType grantType;
        private final Set<String> scope;
        private final ClientCredentials clientCredentials;
        private final AuthenticationHandler authenticationHandler;
        private final Map<String, Object> context;

        /**
         * Constructs a new PasswordCredentialsAccessTokenRequest.
         *
         * @param grantType The grant type.
         * @param clientCredentials The client's credentials.
         * @param authenticationHandler The authentication handler.
         * @param scope The requested scope.
         * @param context A {@code Map<String, Object>} containing OAuth2 Provider implementation specific context
         *                information.
         */
        private PasswordCredentialsAccessTokenRequest(final GrantType grantType,
                final ClientCredentials clientCredentials, final AuthenticationHandler authenticationHandler,
                final Set<String> scope, final Map<String, Object> context) {
            this.grantType = grantType;
            this.scope = scope;
            this.clientCredentials = clientCredentials;
            this.authenticationHandler = authenticationHandler;
            this.context = context;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public GrantType getGrantType() {
            return grantType;
        }

        /**
         * Gets the requested scope.
         *
         * @return The requested scope.
         */
        public Set<String> getScope() {
            return scope;
        }

        /**
         * Gets the client's credentials.
         *
         * @return The client's credentials.
         */
        public ClientCredentials getClientCredentials() {
            return clientCredentials;
        }

        /**
         * Gets the Authentication Handler.
         *
         * @return The Authentication Handler.
         */
        public AuthenticationHandler getAuthenticationHandler() {
            return authenticationHandler;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, Object> getContext() {
            return context;
        }
    }

    public static class ClientCredentialsAccessTokenRequest implements AccessTokenRequest {

        /**
         * AccessTokenRequest builder for creating an AccessTokenRequest for the OAuth2 Client Credentials grant type.
         *
         * @since 12.0.0
         */
        public static final class ClientCredentialsAccessTokenRequestBuilder {

            private final GrantType grantType = CLIENT_CREDENTIALS;
            private String scope;
            private ClientCredentials clientCredentials;
            private Map<String, Object> context;

            /**
             * Constructs a new ClientCredentialsAccessTokenRequestBuilder.
             */
            private ClientCredentialsAccessTokenRequestBuilder() { }

            /**
             * Adds the requested scope to the builder.
             *
             * @param scope The requested scope.
             * @return This builder instance.
             */
            public ClientCredentialsAccessTokenRequestBuilder scope(final String scope) {
                this.scope = scope;
                return this;
            }

            /**
             * Adds the client's credentials to the builder.
             *
             * @param clientCredentials The client's credentials.
             * @return This builder instance.
             */
            public ClientCredentialsAccessTokenRequestBuilder clientCredentials(
                    final ClientCredentials clientCredentials) {
                this.clientCredentials = clientCredentials;
                return this;
            }

            /**
             * Adds the {@code Map<String, Object>} containing OAuth2 Provider implementation specific context information
             * to the builder.
             *
             * @param context The {@code Map<String, Object>} containing OAuth2 Provider implementation specific context
             *                information.
             * @return This builder instance.
             */
            public ClientCredentialsAccessTokenRequestBuilder context(final Map<String, Object> context) {
                this.context = Collections.unmodifiableMap(context);
                return this;
            }

            /**
             * Builds the ClientCredentialsAccessTokenRequest from the provided information.
             * <br/>
             * Validates that the builder has been given all the required data to be able to build a
             * ClientCredentialsAccessTokenRequest for the OAuth2 Client Credentials grant type.
             *
             * @return A ClientCredentialsAccessTokenRequest instance.
             */
            public ClientCredentialsAccessTokenRequest build() {
                Reject.ifNull(clientCredentials, "Client Authentication must be set.");
                Reject.ifTrue(clientCredentials.getClientId() == null, "Missing parameter, 'client_id'");
                Reject.ifTrue(clientCredentials.getClientId().isEmpty(), "Missing parameter, 'client_id'");

                return new ClientCredentialsAccessTokenRequest(grantType, splitScope(scope), clientCredentials,
                        context);
            }
        }

        /**
         * Creates a new builder for creating an AccessTokenRequest for the OAuth2 Client Credentials grant type.
         *
         * @return A new instance of the ClientCredentialsAccessTokenRequestBuilder.
         */
        public static ClientCredentialsAccessTokenRequestBuilder createClientCredentialsAccessTokenRequest() {
            return new ClientCredentialsAccessTokenRequestBuilder();
        }

        private final GrantType grantType;
        private final Set<String> scope;
        private final ClientCredentials clientCredentials;
        private final Map<String, Object> context;

        /**
         * Constructs a new ClientCredentialsAccessTokenRequest.
         *
         * @param grantType The grant type.
         * @param scope The requested scope.
         * @param clientCredentials The client's credentials.
         * @param context A {@code Map<String, Object>} containing OAuth2 Provider implementation specific context
         *                information.
         */
        private ClientCredentialsAccessTokenRequest(final GrantType grantType, final Set<String> scope,
                final ClientCredentials clientCredentials, final Map<String, Object> context) {
            this.grantType = grantType;
            this.scope = scope;
            this.clientCredentials = clientCredentials;
            this.context = context;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public GrantType getGrantType() {
            return grantType;
        }


        /**
         * Gets the requested scope.
         *
         * @return The requested scope.
         */
        public Set<String> getScope() {
            return scope;
        }

        /**
         * Gets the client's credentials.
         *
         * @return The client's credentials.
         */
        public ClientCredentials getClientCredentials() {
            return clientCredentials;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, Object> getContext() {
            return context;
        }
    }

    /**
     * Gets the OAuth2 Grant Type this AccessTokenRequest is for.
     *
     * @return The grant type.
     */
    GrantType getGrantType();

    /**
     * Gets the {@code Map<String, Object>} containing OAuth2 Provider implementation specific context information.
     *
     * @return The {@code Map<String, Object>} containing OAuth2 Provider implementation specific context information.
     */
    Map<String, Object> getContext();
}
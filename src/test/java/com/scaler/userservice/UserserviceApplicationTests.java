package com.scaler.userservice;

import com.scaler.userservice.security.repositories.JpaRegisteredClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserserviceApplicationTests {

    @Autowired
    private JpaRegisteredClientRepository registeredClientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Commit
    void storeRegisteredClientIntoDB() {

        RegisteredClient oidcClient =
                RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("oidc-client")
                        .clientIdIssuedAt(Instant.now())

                        // ✅ BCrypt-encoded client secret
                        .clientSecret(passwordEncoder.encode("secret"))

                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)

                        .redirectUri("https://oauth.pstmn.io/v1/callback")
                        .postLogoutRedirectUri("https://oauth.pstmn.io/v1/callback")

                        .scope(OidcScopes.OPENID)
                        .scope(OidcScopes.PROFILE)
                        .scope("admin")
                        .scope("student")
                        .scope("mentor")

                        .clientSettings(
                                ClientSettings.builder()
                                        .requireAuthorizationConsent(true)
                                        .requireProofKey(false)
                                        .build()
                        )

                        .tokenSettings(
                                TokenSettings.builder()
                                        .authorizationCodeTimeToLive(Duration.ofMinutes(5))
                                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                                        .refreshTokenTimeToLive(Duration.ofHours(1))
                                        .reuseRefreshTokens(true)
                                        .build()
                        )

                        .build();

        registeredClientRepository.save(oidcClient);
    }
}

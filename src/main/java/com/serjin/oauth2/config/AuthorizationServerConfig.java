package com.serjin.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
/**
 *  1.Token 발급, 리프레쉬 해주는 역할, OAuth 토큰과 관련된 모든 처리
 *  2.총 세개의 Config 필요(AuthorizationServer, Resource, Security)
 */
/**
 * 1.Auth 에 대한 토큰 요청은 모두 여기서 관리해준다.
 *  /oauth/authorize
 *  /oauth/token
 *  /oauth/check_token
 *  /oauth/confirm_access
 *  /oauth/error
 * */


/**
 * 1. withClient("sejin-client")
 * 2. secret("sejin-password") // facebook 같은 곳에서 발급해준 client-id,pw 와 같은 개념
 * 3. Authentication, 이 서버가 oauth 토큰을 발급 해줄것이다.
 *    1) token 스토어를 inMemory 가 아니라 db 에서 처리 하는 방법과
 *    2) withClient/secret 여러 어플리케이션을 등록 할수 있는 방법을 고안해야한다.
 * */


@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;   // 발급 받은 oauth Token 들을 저장해준다. security config 에 bean 선언되어있음

    @Autowired
    private AuthenticationManager authenticationManager; // Spring security 의 핵심 인터페이스 중하나, 실제로 인증 역할

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
        configurer
                .inMemory()
                .withClient("sejin-client")
                .secret(passwordEncoder.encode("sejin-password"))
                .authorizedGrantTypes("password","authorization_code","refresh_token","implicit") // Grant type
                .scopes("read", "write", "trust")
                .accessTokenValiditySeconds(1*60*60) // 유효시간 1시간
                .refreshTokenValiditySeconds(6*60*60);

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);;
    }
}




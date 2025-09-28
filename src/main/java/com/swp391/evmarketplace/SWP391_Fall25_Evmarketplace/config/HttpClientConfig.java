package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(10_000);
        f.setReadTimeout(15_000);

        String proxyUrl = System.getenv("HTTPS_PROXY");
        if (proxyUrl == null || proxyUrl.isBlank()) proxyUrl = System.getenv("HTTP_PROXY");
        if (proxyUrl != null && !proxyUrl.isBlank()) {
            java.net.URI u = java.net.URI.create(proxyUrl);
            java.net.Proxy proxy = new java.net.Proxy(
                    java.net.Proxy.Type.HTTP,
                    new java.net.InetSocketAddress(u.getHost(), u.getPort() > 0 ? u.getPort() : 80)
            );
            f.setProxy(proxy);
        }
        return new RestTemplate(f);
    }
}

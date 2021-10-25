package com.jscastaneda.bancolombia.codigoton.integration.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * Clase encargado de realizar las invocaciones a las capacidades expuestas del API de Evalart APP
 *
 * @author Jonathan Castañeda <jcaatanedaesp@gmail.com>
 * @since 24/10/2021
 */
@Slf4j
@Component
public class EvalartAppRestClient {

    @Value("${application.rest.client.evalart-app.url-base}")
    private String urlBase;

    @Value("${application.rest.client.evalart-app.connection-timeout}")
    private long connectionTimeout;

    @Value("${application.rest.client.evalart-app.read-timeout}")
    private long readTimeout;

    @Value("${application.rest.client.evalart-app.path.code-decrypt}")
    private String pathCodeDecrypt;

    @Autowired
    private RestTemplateBuilder builder;

    private RestTemplate template;

    @PostConstruct
    private void init() {
        template = builder
                .setConnectTimeout(Duration.ofSeconds(connectionTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }

    /**
     * Metodo encargado de invocar a la capacidad code_decrypt
     *
     * @param codeEncrypt (Codigo encriptado)
     * @return String (Codigo desencriptado)
     */
    public String invokeCodeDecrypt(String codeEncrypt) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(urlBase)
                    .path(pathCodeDecrypt)
                    .buildAndExpand(codeEncrypt)
                    .toUriString();

            log.info("Request URL={}", url);
            String codeDecrypt = template.getForObject(url, String.class);
            log.info("Response {}", codeDecrypt);

            return codeDecrypt.replace("\"", "");
        } catch (RestClientException e) {
            log.error("Error", e);
            throw e;
        }
    }
}

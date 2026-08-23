package ru.brokilone.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class CheckClient implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CheckClient.class);

    private final RestClient restClient = RestClient.create("http://localhost:8080");

    @Override
    public void run(String... args) {
        check();
    }

    public void check() {
        try {
            restClient.get()
                    .uri("/check")
                    .retrieve()
                    .body(String.class);
            log.info("Success");
        } catch (HttpClientErrorException.NotFound e) {
            log.error("404 Not Found");
        } catch (HttpClientErrorException.BadRequest e) {
            log.error("400 Bad Request");
        } catch (Exception e) {
            log.error("Unknown error");
        }
    }
}

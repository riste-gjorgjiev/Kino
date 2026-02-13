package mk.ukim.finki.wp.kino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class KinoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KinoApplication.class, args);
    }

}

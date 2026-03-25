package mk.ukim.finki.ecommerce.ecommercelab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ECommercelabApplication {
    public static void main(String[] args) {
        SpringApplication.run(ECommercelabApplication.class, args);
    }
}
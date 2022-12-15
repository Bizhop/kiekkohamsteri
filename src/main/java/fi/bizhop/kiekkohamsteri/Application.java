package fi.bizhop.kiekkohamsteri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@SpringBootApplication
@EnableScheduling
public class Application {
    static ConfigurableApplicationContext ctx;

    public static void main(String[] args) {
        ctx = SpringApplication.run(Application.class, args);
    }

    public static void exit() { ctx.close(); }

    @Bean
    public Clock getClock() {
        return Clock.systemUTC();
    }
}

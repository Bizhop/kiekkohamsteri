package fi.bizhop.kiekkohamsteri;

import fi.bizhop.kiekkohamsteri.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration {
    final AuthService authService;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
            }
        };
    }

    @Bean
    public FilterRegistrationBean<UserFilter> userFilter() {
        var bean = new FilterRegistrationBean<UserFilter>();

        bean.setFilter(new UserFilter(authService));
        bean.addUrlPatterns("/api/ostot/*");
        bean.addUrlPatterns("/api/kiekot/*");
        bean.addUrlPatterns("/api/user/*");
        bean.addUrlPatterns("/api/stats/*");
        bean.addUrlPatterns("/api/dropdown/*");
        bean.setOrder(1);

        return bean;
    }

    @Bean
    public FilterRegistrationBean<AdminUserFilter> adminUserFilter() {
        var bean = new FilterRegistrationBean<AdminUserFilter>();

        bean.setFilter(new AdminUserFilter(authService));
        bean.addUrlPatterns("/api/molds/*");
        bean.addUrlPatterns("/api/muovit/*");
        bean.setOrder(2);

        return bean;
    }
}

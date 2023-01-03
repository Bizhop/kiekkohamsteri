package fi.bizhop.kiekkohamsteri;

import fi.bizhop.kiekkohamsteri.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    final AuthService authService;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedOrigins("*")
                .allowedHeaders("*");
    }

    @Bean
    public FilterRegistrationBean<UserFilter> userFilter() {
        var bean = new FilterRegistrationBean<UserFilter>();

        bean.setFilter(new UserFilter(authService));

        bean.addUrlPatterns("/api/v2/stats/*");
        bean.addUrlPatterns("/api/v2/dropdowns/*");
        bean.addUrlPatterns("/api/v2/user/*");
        bean.addUrlPatterns("/api/v2/groups/*");
        bean.addUrlPatterns("/api/v2/discs/*");
        bean.addUrlPatterns("/api/v2/buys/*");

        bean.setOrder(1);

        return bean;
    }

    @Bean
    public FilterRegistrationBean<AdminUserFilter> adminUserFilter() {
        var bean = new FilterRegistrationBean<AdminUserFilter>();

        bean.setFilter(new AdminUserFilter(authService));

        //V1
        bean.addUrlPatterns("/api/v2/discs/molds/*");
        bean.addUrlPatterns("/api/v2/discs/plastics/*");

        bean.setOrder(2);

        return bean;
    }
}

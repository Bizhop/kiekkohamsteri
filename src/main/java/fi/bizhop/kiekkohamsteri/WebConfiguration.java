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

        //V1
        bean.addUrlPatterns("/api/ostot/*");
        bean.addUrlPatterns("/api/kiekot/*");
        bean.addUrlPatterns("/api/user/*");
        bean.addUrlPatterns("/api/stats/*");
        bean.addUrlPatterns("/api/dropdown/*");

        //V2
        bean.addUrlPatterns("/api/v2/user/*");

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

    @Bean
    public FilterRegistrationBean<CompatibilityFilter> compatibilityFilter() {
        var bean = new FilterRegistrationBean<CompatibilityFilter>();

        bean.setFilter(new CompatibilityFilter());
        bean.addUrlPatterns("/api/kiekot/*");
        bean.addUrlPatterns("/api/muovit/*");
        bean.addUrlPatterns("/api/molds/*");
        bean.setOrder(3);

        return bean;
    }
}

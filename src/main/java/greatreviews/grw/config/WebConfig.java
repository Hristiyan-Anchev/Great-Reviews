package greatreviews.grw.config;

import greatreviews.grw.interceptors.RestrictedAddressInterceptor;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class WebConfig implements WebMvcConfigurer {
    RestrictedAddressInterceptor restrictedAddressInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(restrictedAddressInterceptor).excludePathPatterns(
                "/restricted",
                "/bootstrap/**",
                "/images/**"
        );
    }
}

package greatreviews.grw.interceptors;

import greatreviews.grw.controllers.bindings.RegisterUserBinding;
import greatreviews.grw.services.interfaces.UserMetaDataService;
import greatreviews.grw.services.interfaces.UserService;
import greatreviews.grw.services.models.UserServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.annotation.Around;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class RestrictedAddressInterceptor implements HandlerInterceptor {
    UserService userService;
    UserMetaDataService metadataService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String remoteHost = request.getRemoteHost();

        if(metadataService.hostIsBlacklisted(remoteHost)){
            response.sendRedirect("/restricted");
            return false;
        }

        return true;
    }
}

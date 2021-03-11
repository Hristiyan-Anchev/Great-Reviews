package greatreviews.grw.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import greatreviews.grw.config.converters.SubcategoriesCollectionConverter;
import greatreviews.grw.entities.SubcategoryEntity;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.util.Set;

@Configuration
public class BeansConfig {

@Bean
    public ModelMapper getModelMapper(){
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);



        return modelMapper;
    }

    @Bean
    public Gson gson(){
        return new GsonBuilder().setPrettyPrinting()
                .create();
    }

    @Bean
    public PasswordEncoder getEncoder(){
       return new Pbkdf2PasswordEncoder();
    }



}

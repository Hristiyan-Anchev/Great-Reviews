package greatreviews.grw.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import greatreviews.grw.entities.CompanyEntity;
import greatreviews.grw.entities.ReviewEntity;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.services.models.ReviewServiceModel;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Configuration
public class BeansConfig {

    @Bean
    public ModelMapper getModelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);

        configPropMappingReviewEntityReviewServiceModel(modelMapper);
        configReviewServiceModelToReviewEntity(modelMapper);
        localDateTimeToStringConverter(modelMapper);
        companyEntToCompServModelVotesMap(modelMapper);


        return modelMapper;
    }




    @Bean
    public Gson gson() {
        return new GsonBuilder().setPrettyPrinting()
                .create();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    private void configPropMappingReviewEntityReviewServiceModel(ModelMapper modelMapper) {
        modelMapper.typeMap(ReviewEntity.class, ReviewServiceModel.class)
                .addMappings(new PropertyMap<ReviewEntity, ReviewServiceModel>() {
                    @Override
                    protected void configure() {
                        map().setUserReviewsCount(
                                source.getUser().getReviews().size()
                        );

                    }
                });

    }


    private void localDateTimeToStringConverter(ModelMapper modelMapper) {
        Converter<LocalDateTime, String> formatDateToString = new Converter<LocalDateTime, String>() {
            @Override
            public String convert(MappingContext<LocalDateTime, String> context) {
                LocalDateTime sourceDate = context.getSource();
                return sourceDate.format(DateTimeFormatter.ofPattern("dd MM yyyy"));
            }
        };


        PropertyMap<ReviewEntity, ReviewServiceModel> reviewToServiceModelMap =
                new PropertyMap<ReviewEntity, ReviewServiceModel>() {
                    @Override
                    protected void configure() {
                        using(formatDateToString).map(source.getCreated()).setCreated(null);
                    }
                };

        modelMapper.addMappings(reviewToServiceModelMap);
    }

    private void companyEntToCompServModelVotesMap(ModelMapper modelMapper) {
        //converting upvotes
       Converter<Set<ReviewEntity>,Long> mapUpvotesCount =
               new Converter<Set<ReviewEntity>, Long>() {
                   @Override
                   public Long convert(MappingContext<Set<ReviewEntity>, Long> context) {
                       Long upvotesCount = context.getSource().stream().filter(r->r.getVote().equals("1"))
                               .count();

                       return upvotesCount;
                   }
               };

        //converting downvotes
        Converter<Set<ReviewEntity>,Long> mapDownvotesCount =
                new Converter<Set<ReviewEntity>, Long>() {
                    @Override
                    public Long convert(MappingContext<Set<ReviewEntity>, Long> context) {
                        Long downvotesCount = context.getSource().stream().filter(r->r.getVote().equals("-1"))
                                .count();
                        return downvotesCount;
                    }
                };


        //property maps for CompanyEntity to CompanyServiceModel properties
        PropertyMap<CompanyEntity,CompanyServiceModel> companyEntUpvotesMap =
                new PropertyMap<CompanyEntity, CompanyServiceModel>() {
                    @Override
                    protected void configure() {
                        using(mapUpvotesCount).map(source.getReviews()).setUpVotesCount(null);
                    }
                };

        PropertyMap<CompanyEntity,CompanyServiceModel> companyEntDownvotesMap =
                new PropertyMap<CompanyEntity, CompanyServiceModel>() {
                    @Override
                    protected void configure() {
                        using(mapDownvotesCount).map(source.getReviews()).setDownVotesCount(null);
                    }
                };

        modelMapper.addMappings(companyEntUpvotesMap);
        modelMapper.addMappings(companyEntDownvotesMap);

    }

    private void configReviewServiceModelToReviewEntity(ModelMapper modelMapper) {
        PropertyMap<ReviewServiceModel,ReviewEntity> serviceModelToEntityMap =
                new PropertyMap<ReviewServiceModel, ReviewEntity>() {
                    @Override
                    protected void configure() {
                        skip(destination.getId());
                    }
                };

        modelMapper.addMappings(serviceModelToEntityMap);
    }

}

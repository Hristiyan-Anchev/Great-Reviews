package greatreviews.grw.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import greatreviews.grw.config.authentication.CustomUser;
import greatreviews.grw.entities.*;
import greatreviews.grw.entities.basic.BaseEntity;
import greatreviews.grw.services.models.CompanyServiceModel;
import greatreviews.grw.services.models.ReviewServiceModel;

import org.apache.tika.Tika;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class BeansConfig {
    @Autowired
    Environment env;

    @Bean
    public ModelMapper getModelMapper() {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);

        configPropMappingReviewEntityReviewServiceModel(modelMapper);
        configReviewServiceModelToReviewEntity(modelMapper);
        localDateTimeToStringConverter(modelMapper);
        companyEntToCompServModelVotesMap(modelMapper);
        mapUserEntityToCustomUser(modelMapper);

        return modelMapper;
    }

    @Bean
    public Tika getMagic(){
        var tika = new Tika();
        return tika;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().setPrettyPrinting()
                .create();
    }

    @Bean
    public Cloudinary getCloudinaryInstance(){
        var cloudinary =new Cloudinary(ObjectUtils.asMap(
                "cloud_name",env.getProperty("cloud.name"),
                "api_key",env.getProperty("api.key"),
                "api_secret",env.getProperty("api.secret")
        ));

        return cloudinary;
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

        Converter<Set<SubcategoryEntity>,Long> firstSubcategoryConverter = new Converter<Set<SubcategoryEntity>, Long>() {
            @Override
            public Long convert(MappingContext<Set<SubcategoryEntity>, Long> context) {
                var sourceSubcategories = context.getSource();
                if(sourceSubcategories.size() >= 1){
                    return new ArrayList<>(sourceSubcategories).get(0).getId();
                }
                return null;
            }
        };

        Converter<Set<SubcategoryEntity>,Long> secondSubcategoryConverter = new Converter<Set<SubcategoryEntity>, Long>() {
            @Override
            public Long convert(MappingContext<Set<SubcategoryEntity>, Long> context) {
                var sourceSubcategories = context.getSource();
                if(sourceSubcategories.size() >= 2){
                    return new ArrayList<>(sourceSubcategories).get(1).getId();
                }
                return null;
            }
        };

        Converter<Set<SubcategoryEntity>,Long> thirdSubcategoryConverter = new Converter<Set<SubcategoryEntity>, Long>() {
            @Override
            public Long convert(MappingContext<Set<SubcategoryEntity>, Long> context) {
                var sourceSubcategories = context.getSource();
                if(sourceSubcategories.size() >= 3){
                    return new ArrayList<>(sourceSubcategories).get(2).getId();
                }
                return null;
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

        PropertyMap<CompanyEntity,CompanyServiceModel> mainCatMap = new PropertyMap<CompanyEntity, CompanyServiceModel>() {
            @Override
            protected void configure() {
                map(source.getCategory().getId()).setMainCategory(null);
            }
        };

        PropertyMap<CompanyEntity,CompanyServiceModel> subcategoriesMapping = new PropertyMap<CompanyEntity, CompanyServiceModel>() {
            @Override
            protected void configure() {
                using(firstSubcategoryConverter).map(source.getSubcategories()).setFirstSubcategory(null);
                using(secondSubcategoryConverter).map(source.getSubcategories()).setSecondSubcategory(null);
                using(thirdSubcategoryConverter).map(source.getSubcategories()).setThirdSubcategory(null);
            }
        };


        modelMapper.addMappings(subcategoriesMapping);
        modelMapper.addMappings(mainCatMap);
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

    private void mapUserEntityToCustomUser(ModelMapper modelMapper) {
        modelMapper.typeMap(UserEntity.class, CustomUser.class)
                .addMappings(
                        new PropertyMap<UserEntity, CustomUser>() {
                            @Override
                            protected void configure() {
                                map().setHasCompanies(
                                        source.getCompanies().size() > 0
                                );
                            }
                        }
                );

        //map user entity company id's to custom user

       Converter<Set<CompanyEntity>,List<Long>> companyEntityToCompanyIdListMapping =
               new Converter<Set<CompanyEntity>, List<Long>>() {
                   @Override
                   public List<Long> convert(MappingContext<Set<CompanyEntity>, List<Long>> context) {
                       return context.getSource().stream().map(BaseEntity::getId).collect(Collectors.toUnmodifiableList());
                   }
               };

       modelMapper.typeMap(UserEntity.class,CustomUser.class).addMappings(
               new PropertyMap<UserEntity, CustomUser>() {
                   @Override
                   protected void configure() {
                       using(companyEntityToCompanyIdListMapping).map(source.getCompanies()).setOwnedCompanies(null);
                   }
               }
       );


    }


}

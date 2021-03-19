package greatreviews.grw.services.implementations;


import greatreviews.grw.controllers.views.ReviewViewModel;
import greatreviews.grw.entities.ReviewEntity;
import greatreviews.grw.repositories.CompanyRepository;
import greatreviews.grw.repositories.ReviewRepository;
import greatreviews.grw.repositories.UserRepository;
import greatreviews.grw.services.interfaces.ReviewService;
import greatreviews.grw.services.models.ReviewServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewServiceImpl implements ReviewService {
    ReviewRepository reviewRepository;
    UserRepository userRepository;
    CompanyRepository companyRepository;

    ModelMapper modelMapper;

    @Override
    @Transactional
    public void addReview(ReviewServiceModel review,Long currentUserId, Long companyId) {
        ReviewEntity newReview = modelMapper.map(review,ReviewEntity.class);

        //this is needed because modelMapper maps the companyId property to the ReviewEntity's id (unwanted behaviour)
//        newReview.setId(null);

        var targetCompany = companyRepository.findCompanyEntityById(companyId).orElse(null);
        var targetUser = userRepository.findById(currentUserId).orElse(null);

        newReview.setCompany(targetCompany);
        newReview.setUser(targetUser);

        reviewRepository.saveAndFlush(newReview);
    }

    @Override
    public Long getCompanyReviewsCount(Long companyId) {
        return reviewRepository.getCompanyReviewsCount(companyId);
    }

    @Override
    public Long getCompanyUpVotesCount(Long companyId) {
        return reviewRepository.getCompanyUpVotesCount(companyId);
    }

    @Override
    public Long getCompanyDownVotesCount(Long companyId) {
        return reviewRepository.getCompanyDownVotesCount(companyId);
    }

    @Override
    public Set<ReviewServiceModel> getCompanyReviews(Long companyId) {
        Set<ReviewEntity> allCompanyReviews = reviewRepository.getCompanyReviewsById(companyId);

        Set<ReviewServiceModel> mappedReviews = modelMapper.map(allCompanyReviews,
                new TypeToken<Set<ReviewServiceModel>>() {
                }.getType());

        return mappedReviews;
    }

    @Override
    public Set<ReviewServiceModel> getLatestReviews(int reviewsCount) {

        Set<ReviewServiceModel> reviewServiceModels = reviewRepository.getLatestReviews(PageRequest.of(0, reviewsCount,Sort.by("created").descending()))
                .map(re -> {
                    return modelMapper.map(re, ReviewServiceModel.class);
                }).toSet();

        System.out.println(reviewServiceModels.toString());
        return reviewServiceModels;
    }

    @Override
    public Set<ReviewServiceModel> getUserReviewsById(Long userId) {
        Set<ReviewEntity> userReviews = reviewRepository.getReviewsByUser(userId);

        Set<ReviewServiceModel> mappedReviews = modelMapper.map(
                userReviews,
                new TypeToken<Set<ReviewServiceModel>>(){}.getType()
        );


        return mappedReviews;
    }


}

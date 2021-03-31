package greatreviews.grw.services.implementations;


import greatreviews.grw.controllers.DTO.CensorResponseDTO;
import greatreviews.grw.controllers.DTO.FlagReviewResponseDTO;
import greatreviews.grw.controllers.DTO.PublishReviewResponseDTO;
import greatreviews.grw.entities.ReviewEntity;
import greatreviews.grw.entities.UserEntity;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewServiceImpl implements ReviewService {
    public static final String CENSORED_CONTENT_TEXT = "This review has been censored due to violation of our policy.";
    public static final String CENSORED_TITLE_TEXT = "Censored review";
    ReviewRepository reviewRepository;
    UserRepository userRepository;
    CompanyRepository companyRepository;

    ModelMapper modelMapper;

    @Override
    @Transactional
    public void addReview(ReviewServiceModel review, Long currentUserId, Long companyId) {
        ReviewEntity newReview = modelMapper.map(review, ReviewEntity.class);

        //this is needed because modelMapper maps the companyId property to the ReviewEntity's id (unwanted behaviour)
//        newReview.setId(null);

        var targetCompany = companyRepository.findCompanyEntityById(companyId).orElse(null);
        var targetUser = userRepository.findById(currentUserId).orElse(null);
        var userHasPendingReviewForCompany =
                targetCompany.getReviews().stream().anyMatch(r -> {
                    return r.getUser().getId().equals(targetCompany.getId()) && !r.getIsPublished();
                });

        if (!userHasPendingReviewForCompany) {
            newReview.setCompany(targetCompany);
            newReview.setUser(targetUser);
            newReview.setIsPublished(targetUser.getRoles()
                    .stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN")));
            newReview.setIsCensored(false);
            reviewRepository.saveAndFlush(newReview);
        }
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
    public List<ReviewServiceModel> getCompanyReviews(Long companyId, Boolean published) {
        List<ReviewEntity> allCompanyReviews = reviewRepository.getCompanyReviewsById(companyId, published);

        List<ReviewServiceModel> mappedReviews = modelMapper.map(allCompanyReviews,
                new TypeToken<List<ReviewServiceModel>>() {
                }.getType());


        return alterCensoredReviews(mappedReviews);
    }

    @Override
    public List<ReviewServiceModel> getLatestReviews(int reviewsCount) {

        List<ReviewServiceModel> reviewServiceModels = reviewRepository.getLatestReviews(PageRequest.of(0, reviewsCount, Sort.by("created").descending()))
                .map(re -> {
                    return modelMapper.map(re, ReviewServiceModel.class);
                }).toList();

        System.out.println(reviewServiceModels.toString());
        return reviewServiceModels;
    }

    @Override
    public List<ReviewServiceModel> getUserReviewsById(Long userId) {
        Set<ReviewEntity> userReviews = reviewRepository.getReviewsByUser(userId);

        Set<ReviewServiceModel> mappedReviews = modelMapper.map(
                userReviews,
                new TypeToken<Set<ReviewServiceModel>>() {
                }.getType()
        );

        return this.alterCensoredReviews(mappedReviews);
    }

    @Override
    public FlagReviewResponseDTO addUserFlag(Long reviewId, Long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        var response = new FlagReviewResponseDTO(false);

        user.ifPresent(u -> {
            var flagsOwnReview = u.getReviews().stream().anyMatch(userReview -> userReview.getId().equals(reviewId));
            if (!flagsOwnReview) {
                var userHasFlaggedReview =
                        u.getFlaggedReviews().stream().anyMatch(fr -> {
                            return fr.getId().equals(reviewId);
                        });

                if (!userHasFlaggedReview) {
                    ReviewEntity review = reviewRepository.findById(reviewId).get();

                    u.getFlaggedReviews().add(review);

                    userRepository.saveAndFlush(u);
                    response.setIsFlagged(true);
                }
            }
        });

        return response;
    }

    @Override
    public Integer getUnpublishedReviewsCount() {
        return reviewRepository.countUnpublishedReviews();
    }

    @Override
    public Integer getFlaggedReviewsCount() {
        return reviewRepository.countFlaggedReviews();
    }

    @Override
    public List<ReviewServiceModel> getUnpublishedReviews() {
        List<ReviewEntity> unpublishedReviews = reviewRepository.getUnpublishedReviews();
        List<ReviewServiceModel> mappedUnpublishedReviews =
                modelMapper.map(unpublishedReviews, new TypeToken<List<ReviewServiceModel>>(){}.getType());

        return mappedUnpublishedReviews;
    }

    @Override
    public PublishReviewResponseDTO publishReview(Long reviewId) {
        Optional<ReviewEntity> targetReviewOpt = reviewRepository.findById(reviewId);

        if (targetReviewOpt.isPresent()) {
            var targetReview = targetReviewOpt.get();
            targetReview.setIsPublished(true);
            reviewRepository.saveAndFlush(targetReview);
            return new PublishReviewResponseDTO(true);
        }


        return new PublishReviewResponseDTO(false);
    }

    @Override
    public List<ReviewServiceModel> getFlaggedReviews() {
        List<ReviewEntity> flaggedReviews =
                reviewRepository.getFlaggedReviews();
        List<ReviewServiceModel> mappedFlaggedReviews =
                modelMapper.map(flaggedReviews, new TypeToken<List<ReviewServiceModel>>(){}.getType());

        return mappedFlaggedReviews;
    }

    @Override
    public CensorResponseDTO toggleReviewCensorById(Long reviewId) {
        Optional<ReviewEntity> targetReviewOpt = reviewRepository.findById(reviewId);
        var response = new CensorResponseDTO(false);

        if (targetReviewOpt.isPresent()) {
            var review = targetReviewOpt.get();

            review.setIsCensored(!review.getIsCensored());

            reviewRepository.saveAndFlush(review);

            response =  new CensorResponseDTO(review.getIsCensored());
        }

        return response;
    }


    private List<ReviewServiceModel> alterCensoredReviews(Collection<ReviewServiceModel> allReviews){
        return allReviews.stream().map(r -> {
            if(r.getIsCensored()){
                r.setContent(CENSORED_CONTENT_TEXT);
                r.setTitle(CENSORED_TITLE_TEXT);
                return r;
            }
            return r;
        }).collect(Collectors.toList());
    }
}

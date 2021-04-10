package greatreviews.grw.services.interfaces;

import greatreviews.grw.controllers.views.BlogViewModel;
import greatreviews.grw.services.models.BlogServiceModel;

import java.util.List;

public interface BlogService {
    BlogServiceModel addBlogPost(BlogServiceModel blogPost);

    List<BlogServiceModel> getLatestBlogs(Integer n);

    BlogServiceModel findPostById(Long postId);

    List<BlogServiceModel> getAllBlogs();
}

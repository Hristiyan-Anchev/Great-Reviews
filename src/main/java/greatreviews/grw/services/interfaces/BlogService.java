package greatreviews.grw.services.interfaces;

import greatreviews.grw.services.models.BlogServiceModel;

import java.util.List;

public interface BlogService {
    void addBlogPost(BlogServiceModel blogPost);

    List<BlogServiceModel> getLatestBlogs(Integer n);
}

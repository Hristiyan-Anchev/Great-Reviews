package greatreviews.grw.services.implementations;

import greatreviews.grw.controllers.views.BlogViewModel;
import greatreviews.grw.entities.BlogpostEntity;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.repositories.BlogRepository;
import greatreviews.grw.repositories.UserRepository;
import greatreviews.grw.services.interfaces.BlogService;
import greatreviews.grw.services.models.BlogServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class BlogServiceImpl implements BlogService {

    BlogRepository blogRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;


    @Override
    public BlogServiceModel addBlogPost(BlogServiceModel blogPost) {
        BlogpostEntity newBlog = modelMapper.map(blogPost,BlogpostEntity.class);
        Optional<UserEntity> authorOpt = userRepository.findById(blogPost.getAuthorId());

        if (authorOpt.isPresent()) {
            var author = authorOpt.get();
            newBlog.setAuthor(author);

           newBlog =  blogRepository.saveAndFlush(newBlog);

            return modelMapper.map(newBlog,BlogServiceModel.class);
        }

        return null;

    }

    @Override
    public List<BlogServiceModel> getLatestBlogs(Integer n) {
        List<BlogServiceModel> blogs = blogRepository.getLatestBlogs(
                PageRequest.of(0, n, Sort.by("created").descending())
        ).map(b -> modelMapper.map(b,BlogServiceModel.class)).toList();


        return blogs;
    }

    @Override
    public BlogServiceModel findPostById(Long postId) {
        Optional<BlogpostEntity> targetPostOpt = blogRepository.findById(postId);

        if (targetPostOpt.isPresent()) {
            var targetPost = targetPostOpt.get();
            var mappedPost = modelMapper.map(targetPost,BlogServiceModel.class);
            return mappedPost;
        }

        return null;
    }

    @Override
    public List<BlogServiceModel> getAllBlogs() {
        List<BlogServiceModel> allBlogs =
                modelMapper.map(blogRepository.findAll(),new TypeToken<List<BlogServiceModel>>(){}.getType());

        return allBlogs;
    }
}

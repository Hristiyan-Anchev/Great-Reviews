package greatreviews.grw.services.implementations;

import greatreviews.grw.entities.BlogpostEntity;
import greatreviews.grw.entities.UserEntity;
import greatreviews.grw.repositories.BlogRepository;
import greatreviews.grw.repositories.UserRepository;
import greatreviews.grw.services.interfaces.BlogService;
import greatreviews.grw.services.models.BlogServiceModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


public class BlogServiceTest {

    BlogService service;

    BlogRepository blogRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;

    @BeforeEach
    private void init(){
        modelMapper = new ModelMapper();
        userRepository = Mockito.mock(UserRepository.class);
        blogRepository = Mockito.mock(BlogRepository.class);



        service = new BlogServiceImpl(blogRepository,userRepository,modelMapper);
    }

    UserEntity getMockedUserEntity(){

        UserEntity mockedUserEntity = new UserEntity();
        mockedUserEntity.setId(3l);
        mockedUserEntity.setEnabled(true);
        mockedUserEntity.setUsername("test_user");
        mockedUserEntity.setEmail("test@mail.com");


        return mockedUserEntity;
    }

    @Test
    void addBlogPost_ShouldReturn_CorrectServiceModel(){
        //todo do some mocking first
        UserEntity mockedUserEntity = getMockedUserEntity();

            BlogServiceModel blogToBeAdded = new BlogServiceModel(null,"TEST_TITLE","URL_TEST",null,"Lorem ipsum",null);

            Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(mockedUserEntity));
            BlogpostEntity mockedBlogpostEntity = new BlogpostEntity();
            mockedBlogpostEntity.setId(1L);
            mockedBlogpostEntity.setImageURL("URL_TEST");
            mockedBlogpostEntity.setTitle("TEST_TITLE");
            mockedBlogpostEntity.setContent("Lorem ipsum");
            mockedBlogpostEntity.setAuthor(mockedUserEntity);

            Mockito.when(blogRepository.saveAndFlush(any())).thenReturn(mockedBlogpostEntity);

        BlogServiceModel actualBlog = service.addBlogPost(blogToBeAdded);


        BlogServiceModel expectedResult = new BlogServiceModel(1L,"TEST_TITLE","URL_TEST",null,"Lorem ipsum",3L);

        assertEquals(expectedResult.getId(),actualBlog.getId());
        assertEquals(expectedResult.getTitle(),actualBlog.getTitle());
        assertEquals(expectedResult.getImageURL(),actualBlog.getImageURL());
        assertEquals(expectedResult.getContent(),actualBlog.getContent());
        assertEquals(expectedResult.getAuthorId(),actualBlog.getAuthorId());
    }



}

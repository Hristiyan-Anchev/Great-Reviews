package greatreviews.grw.controllers;

import greatreviews.grw.controllers.DTO.CurrentUserDTO;
import greatreviews.grw.controllers.bindings.AddBlogBinding;
import greatreviews.grw.controllers.views.BlogViewModel;
import greatreviews.grw.services.interfaces.BlogService;
import greatreviews.grw.services.interfaces.FileUploadService;
import greatreviews.grw.services.models.BlogServiceModel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/blogs")
@Controller
public class BlogController {
    private static final String TRANSFORMATION_PARAMS = "w_1024,h_1024,c_fill";

    FileUploadService fileUploadService;
    ModelMapper modelMapper;
    BlogService blogService;

    @GetMapping("/home")
    public ModelAndView getBlogPage(Model model){
        var modelAndView = new ModelAndView("/blog/Blog");

        //get the four latest blogs
        List<BlogViewModel> latestBlogs = modelMapper.map(blogService.getLatestBlogs(4),new TypeToken<List<BlogViewModel>>(){}.getType());
        modelAndView.addObject("latestBlog",latestBlogs.get(0));
        modelAndView.addObject("moreFromBlog", latestBlogs.stream().skip(1).collect(Collectors.toList()));


        return modelAndView;
    }


    @GetMapping("/post")
    public ModelAndView getBlogPost(Model model,@RequestParam(name = "id") Long postId){
        var modelAndView = new ModelAndView("redirect:/");
        BlogServiceModel targetBlogPost =
                blogService.findPostById(postId);

        if(targetBlogPost != null) {
            var mappedTargetBlogPost = modelMapper.map(targetBlogPost, BlogViewModel.class);
            var moreFromBlog = modelMapper.map(blogService.getLatestBlogs(3), new TypeToken<List<BlogViewModel>>(){}.getType());

            modelAndView.setViewName("/blog/BlogPost");
            modelAndView.addObject("currentBlogPost", mappedTargetBlogPost);
            modelAndView.addObject("moreFromBlog",moreFromBlog);
        }

        return modelAndView;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/control")
    public ModelAndView getAddBlogPage(Model model){
        var modelAndView = new ModelAndView("redirect:/");

        if(model.containsAttribute("addBlogBinding")){
            modelAndView.addObject("addBlogBinding",model.getAttribute("addBlogBinding"));
        }else{
            modelAndView.addObject("addBlogBinding",new AddBlogBinding("","","", null));
        }

        if (isCurrentUserAdmin(model)) {
            modelAndView.setViewName("/blog/AddBlogPage");
        }

        return modelAndView;
    }
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView addBlog(@ModelAttribute @Valid AddBlogBinding addBlogBinding,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       Model model,
                                       @RequestParam(name = "file",required = false)
                                       MultipartFile multipartFile
                                       ){
        var modelAndView = new ModelAndView("redirect:/");
        if (isCurrentUserAdmin(model)) {
            var secureResourceURL = fileUploadService.uploadFile(multipartFile,TRANSFORMATION_PARAMS);

            if(secureResourceURL == null){
                bindingResult.rejectValue("file","error.uploading.file","Something went wrong with the file upload");
            }
            //check if errors occured
            if(bindingResult.hasErrors()){
                redirectAttributes.addFlashAttribute("addBlogBinding",addBlogBinding);
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addBlogBinding",bindingResult);
                modelAndView.setViewName("redirect:/blogs/control");
                return modelAndView;
            }

            addBlogBinding.setImageURL(secureResourceURL);

            BlogServiceModel blogPost = modelMapper.map(addBlogBinding, BlogServiceModel.class);
            blogPost.setAuthorId(
                    ((CurrentUserDTO) model.getAttribute("currentUser")).getId()
            );

            blogService.addBlogPost(blogPost);

            modelAndView.setViewName("redirect:/users/admin/panel");
        }

        return modelAndView;
    }

//======================================================================================================================

private Boolean isCurrentUserAdmin(Model model){
        CurrentUserDTO currentUser =((CurrentUserDTO) model.getAttribute("currentUser"));
        return currentUser.getRoles().contains("ROLE_ADMIN");
}

}

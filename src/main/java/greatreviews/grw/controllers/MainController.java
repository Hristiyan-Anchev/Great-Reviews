package greatreviews.grw.controllers;

import greatreviews.grw.controllers.views.ReviewViewModel;
import greatreviews.grw.services.interfaces.ReviewService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping()
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainController {

     ModelMapper modelMapper;
     ReviewService reviewService;

    @ModelAttribute("latestReviews")
     private List<ReviewViewModel> getLatestReviews(){
        List<ReviewViewModel> collect = reviewService.getLatestReviews(6).stream().map(r -> {
            ReviewViewModel mapped = modelMapper.map(r, ReviewViewModel.class);
            return mapped;
        }).collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/")
    public ModelAndView getIndex(Model model){
        var modelAndView = new ModelAndView("index");

        return modelAndView;
    }


    @GetMapping("/home")
    public ModelAndView getHome(Model model){
        var modelAndView = new ModelAndView("index");

        return modelAndView;
    }
}

package greatreviews.grw.controllers;

import greatreviews.grw.controllers.views.SubcategoryViewModel;
import greatreviews.grw.repositories.SubcategoryRepository;
import greatreviews.grw.services.interfaces.SubcategoryService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Controller
@RequestMapping("/subcategories")
public class SubcategoryController {

    SubcategoryService subcategoryService;
    ModelMapper modelMapper;

    @GetMapping("/get")
    @ResponseBody
    public List<SubcategoryViewModel> getSubcategories(@RequestParam(name = "of") Long id){
        return modelMapper.map(subcategoryService.findSubcategoriesOfMain(id),
                new TypeToken<List<SubcategoryViewModel>>(){}.getType());

    }

}

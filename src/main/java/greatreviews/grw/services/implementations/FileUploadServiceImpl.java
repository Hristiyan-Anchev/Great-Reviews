package greatreviews.grw.services.implementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import greatreviews.grw.services.interfaces.FileUploadService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class FileUploadServiceImpl implements FileUploadService {

    Cloudinary cloudinary;
    Tika tika;


    public String uploadFile(MultipartFile file,String transformations){
        String resourceSecureUrl = null;


        if(file != null && !file.isEmpty()){
            try {
                //get the file mime type
                String mimeType = tika.detect(file.getInputStream());

                //check if the file is an image
                if(!mimeType.startsWith("image/")) return null;

                //upload the image
                if(!transformations.isBlank() && transformations != null){
                    var uploadResponse = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    resourceSecureUrl = insertTransformation(transformations,
                            uploadResponse.get("secure_url").toString());
                }else{
                    var uploadResponse = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                    resourceSecureUrl = uploadResponse.get("secure_url").toString();
                }


            }catch (IOException ioe){
                System.out.println(ioe.getMessage());
                return null;
            }
        }

        return resourceSecureUrl;
    }



    private String extractPublicId(String resourceUrl){
        // todo: Util class candidate
        int lastSlashIdx = resourceUrl.lastIndexOf("/");
        int fileExtensionDotIdx = resourceUrl.lastIndexOf(".");
        String result = resourceUrl.substring(lastSlashIdx + 1,fileExtensionDotIdx);
        System.out.println(result);
        return  result;
    }

    private String insertTransformation(String transformation, String resourceUrl){
        //todo: Util class candidate

        StringBuilder sb = new StringBuilder();

        List<String> tokens = Arrays.asList(resourceUrl.split("/"));

        tokens = tokens.stream().map(token -> {
            var newToken = token;
            if(token.equals("upload")){
                newToken = token + ("/" + transformation);
            }

            return newToken;
        }).collect(Collectors.toList());

        String result = String.join("/", tokens);
        System.out.println(result);
        return result;
    }

}

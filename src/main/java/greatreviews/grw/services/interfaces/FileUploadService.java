package greatreviews.grw.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    String uploadFile(MultipartFile file, String transformations);
}

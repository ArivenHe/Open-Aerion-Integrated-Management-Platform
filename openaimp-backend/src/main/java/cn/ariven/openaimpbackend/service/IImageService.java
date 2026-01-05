package cn.ariven.openaimpbackend.service;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IImageService {
    ResponseMessage<String> uploadImage(MultipartFile file);
    void getImage(String filename, HttpServletResponse response) throws IOException;
}

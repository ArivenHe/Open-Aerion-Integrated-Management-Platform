package cn.ariven.openaimpbackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.service.IImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final IImageService imageService;

    @SaCheckRole("event-posting")
    @PostMapping("/Admin/Image/Upload")
    public ResponseMessage<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return imageService.uploadImage(file);
    }

    @GetMapping("/images/{filename:.+}")
    public void getImage(@PathVariable String filename, HttpServletResponse response) throws IOException {
        imageService.getImage(filename, response);
    }
}

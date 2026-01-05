package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.service.IImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl implements IImageService {

    private final String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

    @Override
    public ResponseMessage<String> uploadImage(MultipartFile file) {
        if (file.isEmpty()) return ResponseMessage.error("文件空");

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + filename);

            file.transferTo(dest);

            String imageUrl = "/image/" + filename;
            return ResponseMessage.success(imageUrl);

        } catch (IOException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @Override
    public void getImage(String filename, HttpServletResponse response) throws IOException {
        log.info("Attempting to retrieve image: {}", filename);
        File file = new File(uploadDir + filename);

        if (!file.exists()) {
            log.warn("Image not found: {}", filename);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = Files.probeContentType(file.toPath());
        log.debug("Detected content type: {} for file: {}", contentType, filename);
        response.setContentType(contentType);

        try (InputStream in = new FileInputStream(file)) {
            StreamUtils.copy(in, response.getOutputStream());
            log.info("Successfully served image: {}", filename);
        } catch (IOException e) {
            log.error("Error serving image: {}", filename, e);
            throw e;
        }
    }
}

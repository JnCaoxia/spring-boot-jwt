package murraco.controller.file;

import murraco.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/chunk")
    public Map<String, Object> uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileName") String fileName) throws IOException {

        return fileUploadService.uploadChunk(chunk, chunkIndex, totalChunks, fileName);
    }

    @PostMapping("/merge")
    public Map<String, Object> mergeChunks(@RequestBody Map<String, String> request) throws IOException {
        String fileName = request.get("fileName");
        return fileUploadService.mergeChunks(fileName);
    }
}

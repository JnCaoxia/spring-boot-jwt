package murraco.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;

@Service
public class FileUploadService {

    private final Path chunkStorageLocation = Paths.get("E:\\chunk-uploads");
    private final Path fileStorageLocation = Paths.get("E:\\uploads");

    public FileUploadService() {
        try {
            Files.createDirectories(this.chunkStorageLocation);
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Map<String, Object> uploadChunk(MultipartFile chunk, int chunkIndex, int totalChunks, String fileName) throws IOException {
        Path chunkFile = chunkStorageLocation.resolve(fileName + "_" + chunkIndex);
        Files.copy(chunk.getInputStream(), chunkFile, StandardCopyOption.REPLACE_EXISTING);
        return Collections.singletonMap("success", true);
    }

    public Map<String, Object> mergeChunks(String fileName) throws IOException {
        Path mergedFile = fileStorageLocation.resolve(fileName);
        try (BufferedOutputStream mergedStream = new BufferedOutputStream(Files.newOutputStream(mergedFile, StandardOpenOption.CREATE))) {
            int chunkIndex = 0;
            while (true) {
                Path chunkFile = chunkStorageLocation.resolve(fileName + "_" + chunkIndex);
                if (!Files.exists(chunkFile)) {
                    break;
                }
                Files.copy(chunkFile, mergedStream);
                chunkIndex++;
            }
        }

        // 删除临时分块文件
        int chunkIndex = 0;
        while (true) {
            Path chunkFile = chunkStorageLocation.resolve(fileName + "_" + chunkIndex);
            if (!Files.exists(chunkFile)) {
                break;
            }
            Files.delete(chunkFile);
            chunkIndex++;
        }

        return Collections.singletonMap("success", true);
    }
}

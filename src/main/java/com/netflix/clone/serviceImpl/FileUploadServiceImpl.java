package com.netflix.clone.serviceImpl;

import com.netflix.clone.service.FileUploadService;
import com.netflix.clone.util.FileHandlerUtil;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private Path videoStorageLocation;
    private Path imageStorageLocation;

    @Value("${file.upload.video-dir:uploads/videos}")
    private String videoDir;

    @Value("${file.upload.image-dir:uploads/images}")
    private String imageDir;

    @PostConstruct
    public void init(){
        this.videoStorageLocation= Paths.get(videoDir).toAbsolutePath().normalize();
        this.imageStorageLocation=Paths.get(imageDir).toAbsolutePath().normalize();

        try{
            Files.createDirectories(this.videoStorageLocation);
            Files.createDirectories(this.imageStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not Create The directory Where The Uploaded Filed will bw stored.",ex);
        }
    }


    @Override
    public String storeVideoFile(MultipartFile file) {

        return storeFile(file,videoStorageLocation);
    }

    private String storeFile(MultipartFile file, Path storageLocation) {
        String fileExtension = FileHandlerUtil.extractFileExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String filename = uuid + fileExtension;

        try{
            if (file.isEmpty()){
                throw new RuntimeException("Failed to store empty file"+ filename);
            }

            Path targetLocation = storageLocation.resolve(filename);
            Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uuid;

        } catch (IOException ex) {
            throw new RuntimeException("Failed to Stored file"+ filename,ex);
        }
    }

    @Override
    public String storeImageFile(MultipartFile file) {
        return storeFile(file, imageStorageLocation);
    }

    @Override
    public ResponseEntity<Resource> serveVedio(String uuid, String rangeHeader) {
        try{
            Path filePath = FileHandlerUtil.findFileByUuid(videoStorageLocation,uuid);
            Resource resource = FileHandlerUtil.createFullResource(filePath);

            String filename = resource.getFilename();
            String contentType = FileHandlerUtil.detectVideoContentType(filename);

            long fileLength = resource.contentLength();

            if (isFullContentRequest(rangeHeader)){
                return  buildFullVedioResponse(resource,contentType,fileLength,filename);
            }
            return buildPartialVideoResponse(filePath,rangeHeader,contentType,filename,fileLength);

        } catch (Exception e) {
           return ResponseEntity.notFound().build();
        }
    }

    private boolean isFullContentRequest(String rangeHeader) {
        return rangeHeader==null || rangeHeader.isEmpty();
    }

    private ResponseEntity<Resource> buildFullVedioResponse(Resource resource, String contentType, long fileLength, String filename) {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_LENGTH,String.valueOf(fileLength)).body(resource);
    }

    private ResponseEntity<Resource> buildPartialVideoResponse(Path filePath, String rangeHeader, String contentType, String filename, long fileLength)  throws IOException{
        long[] range = FileHandlerUtil.parseRangeHeader(rangeHeader,fileLength);
        long rangeStart = range[0];
        long rangeEnd = range[1];

        if(isValidRange(rangeStart,rangeEnd,fileLength)){
            return buildRangeNotStatisfiableResponse(fileLength);
        }

        long contentLength = rangeEnd - rangeStart + 1;
        Resource rangeResource = FileHandlerUtil.createRangeResource(filePath,rangeStart,contentLength);

        return ResponseEntity.status(206).contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCEPT_RANGES,"bytes")
                .header(HttpHeaders.CONTENT_RANGE,"bytes" + rangeStart +"-"+rangeEnd+"/"+fileLength)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                .body(rangeResource);
    }

    private ResponseEntity<Resource> buildRangeNotStatisfiableResponse(long fileLength) {
        return ResponseEntity.status(416).header(HttpHeaders.CONTENT_RANGE,"bytes */" + fileLength)
                .build();
    }

    private boolean isValidRange(long rangeStart, long rangeEnd, long fileLength) {
        return rangeStart  <= rangeEnd &&  rangeStart >= 0 && rangeEnd < fileLength;
    }

    @Override
    public ResponseEntity<Resource> serveImage(String uuid) {
        try {

            Path filePath= FileHandlerUtil.findFileByUuid(imageStorageLocation,uuid);
            Resource resource = FileHandlerUtil.createFullResource(filePath);

            String  filename= resource.getFilename();
            String contentType = FileHandlerUtil.detectVideoContentType(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\""+filename +"\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

}

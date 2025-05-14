package br.com.petsniffer.app.controller;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.annotation.security.PermitAll;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;


@RestController()
@RequestMapping()
//@Controller
public class FileUploadController {

    private final StorageService storageService;
    private AmazonS3 s3Client;
    private final String bucketName = "encontrepetsfotos";

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }


    @PermitAll
    @PostMapping("/up")

    public String handleFileBodyUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1) // Defina sua região
                .build();

        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        s3Client.putObject(bucketName, fileName, inputStream, metadata);

        // Obtenha a URL da imagem
        String imageUrl = s3Client.getUrl(bucketName, fileName).toString();


        System.out.println(file.getOriginalFilename());
        System.out.println("Passou");

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @PermitAll
    @PostMapping()
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1) // Defina sua região
                .build();

        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        s3Client.putObject(bucketName, fileName, inputStream, metadata);

        // Obtenha a URL da imagem
        String imageUrl = s3Client.getUrl(bucketName, fileName).toString();


        System.out.println(file.getOriginalFilename());
        System.out.println("Passou");

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }


    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
package br.com.petsniffer.app.controller;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import br.com.petsniffer.app.domain.pets.Pet;
import br.com.petsniffer.app.domain.pets.PetStatus;
import br.com.petsniffer.app.repositories.PetRepository;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;


//@Controller
    @RestController()
    @RequestMapping("pets")
    public class PetImageBodyController {

        private final StorageService storageService;

        @Autowired
        PetRepository petRepository;

        private AmazonS3 s3Client;
        private final String bucketName = "petsniffer-pets";

        public PetImageBodyController(StorageService storageService) {
            this.storageService = storageService;
        }

        @PermitAll
        @PostMapping("/fileup")
        public String handleFileBodyUpload(@RequestParam("file") MultipartFile file,
                                           @RequestParam("nome") String nome,
                                           @RequestParam("tipo") String tipo,
                                           @RequestParam("raca") String raca,
                                           @RequestParam("cor") String cor,
                                           @RequestParam("status") PetStatus status,
                                           @RequestParam("datahora") LocalDateTime datahora,
                                           @RequestParam("ownerid") String ownerid,
                                           @RequestParam("pais") String pais,
                                           @RequestParam("estado") String estado,
                                           @RequestParam("cidade") String cidade,
                                           @RequestParam("bairro") String bairro,
                                           @RequestParam("rua") String rua,
                                           @RequestParam("location") String location,
                                           RedirectAttributes redirectAttributes) throws IOException {

            System.out.println("@PostMapping(fileup)");
            // Gera o ID do pet
            String petId = UUID.randomUUID().toString();

            // Inicializa o cliente S3
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1) // Defina sua região
                    .build();

            // Obtém a extensão do arquivo original
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // Define o nome do arquivo como ID + extensão
            String fileName = petId + fileExtension;

            // Define o caminho da imagem no bucket S3
            //String s3Key = "in/" + fileName;
            String s3Key = fileName;

            // Envia o arquivo para o bucket S3
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            //s3Client.putObject(bucketName, s3Key, inputStream, metadata);

            try {
                s3Client.putObject(bucketName, s3Key, inputStream, metadata);
                System.out.println("Upload realizado com sucesso: " + s3Key);
                // Ou retorne uma resposta de sucesso para o frontend
            } catch (Exception e) {
                System.err.println("Erro ao fazer upload para o S3: " + e.getMessage());
                e.printStackTrace();
                // Aqui você pode lançar uma exceção customizada ou retornar erro para o frontend
            }


            // Obtém a URL da imagem
            String imageUrl = s3Client.getUrl(bucketName, s3Key).toString();

            System.out.println("String imageUrl: " + imageUrl );

            // Cria o objeto Pet
            Pet newPet = new Pet();
            newPet.setId(petId); // Define o ID gerado
            newPet.setNome(nome);
            newPet.setTipo(tipo);
            newPet.setRaca(raca);
            newPet.setCor(cor);
            newPet.setStatus(status);
            newPet.setDatahora(datahora);
            newPet.setOwnerid(ownerid);
            newPet.setPais(pais);
            newPet.setEstado(estado);
            newPet.setCidade(cidade);
            newPet.setBairro(bairro);
            newPet.setRua(rua);
            newPet.setLocation(location);
            newPet.setEmbedding(new ArrayList<>(Collections.nCopies(2048, 0.0f)));
            newPet.setFoto(imageUrl); // URL da imagem com o ID + extensão

            // Salva o novo pet no banco de dados
            petRepository.save(newPet);

            System.out.println("Pet criado com ID: " + petId);
            System.out.println("Imagem salva no S3 com o nome: " + fileName);
            System.out.println("URL da imagem: " + imageUrl);

            return "redirect:/";
        }



}
   //     @ExceptionHandler(StorageFileNotFoundException.class)
   //     public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
   //         return ResponseEntity.notFound().build();
   //     }



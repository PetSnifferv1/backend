package br.com.petsniffer.app.controller;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import br.com.petsniffer.app.domain.pets.Pet;
import br.com.petsniffer.app.domain.pets.dtos.PetRequestDTO;
import br.com.petsniffer.app.domain.pets.dtos.PetUpdateDTO;
import br.com.petsniffer.app.repositories.PetRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Transactional
@CrossOrigin(origins = "http://localhost:5173")
@RestController()
@RequestMapping("pets")
public class PetController {

    @Autowired
    PetRepository petRepository;
    private AmazonS3 s3Client;
    private final String bucketName = "petsniffer";

    @PermitAll
    @PostMapping("/create-pets")
     public ResponseEntity<Pet> postPet(@RequestBody @Valid PetRequestDTO body) {
         System.out.println("Body recebido: " + body);
         Pet newPet = new Pet(body);
         System.out.println("Create body: ' " + body);
         System.out.println("newPet.getFoto: " + newPet.getFoto());

         this.petRepository.save(newPet);

        return ResponseEntity.ok().build();}

    @PostMapping("/upload-imagem")
     public ResponseEntity<Pet> postPet(@RequestParam("imagem")  MultipartFile imagem)
                                                                   throws IOException {
        s3Client = AmazonS3ClientBuilder.standard()
                 .withRegion(Regions.US_EAST_1) // Defina sua região
                 .build();

        String fileName = imagem.getOriginalFilename();
        InputStream inputStream = imagem.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imagem.getSize());

        s3Client.putObject(bucketName, fileName, inputStream, metadata);

        // Obtenha a URL da imagem
        String imageUrl = s3Client.getUrl(bucketName, fileName).toString();

        return ResponseEntity.ok().build();}



    @GetMapping("/my-pets")
    public ResponseEntity<List<Pet>> getAllPets(@RequestParam String ownerid) {
        List<Pet> pets = petRepository.findByOwnerid(ownerid);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/public-pets")
    public ResponseEntity<List<Pet>> getPublicPets() {
        List<Pet> pets = petRepository.findAll();
        return ResponseEntity.ok(pets);
    }


    @GetMapping("/search-by-location")
    public ResponseEntity<List<Pet>>  searchByLocation(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radius
    ) {
        List<Pet> pets = petRepository.findPetsByLocation(lat, lng, radius);

        return ResponseEntity.ok(pets);
    }




    @PutMapping("alter-pets/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable String id, @RequestBody @Valid PetUpdateDTO body) {
        // Verificar se o Pet existe

        Optional<Pet> existingPetOptional = petRepository.findById(id);
        if (existingPetOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        System.out.println("Alter body: ' " + body);
        System.out.println("Alter body.location: " + body);
        // Atualizar o Pet no banco usando o método de repositório
        petRepository.updateById(
                id,
                body.getNome(),
                body.getTipo(),
                body.getRaca(),
                body.getCor(),
                body.getStatus(),
                body.getDatahora(),
                body.getFoto(),
                body.getPais(),
                body.getEstado(),
                body.getCidade(),
                body.getBairro(),
                body.getRua(),
                body.getLocation()
        );

        // Consultar novamente para retornar ao cliente
        Pet updatedPet = petRepository.findById(id).orElse(null);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("delete-pets/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable String id, HttpServletRequest request) {
        System.out.println("existingPetOptional: "  );
 // Verificar se o Pet existe
    Optional<Pet> existingPetOptional = petRepository.findById(id);

        System.out.println("existingPetOptional: " + existingPetOptional );
    if (existingPetOptional.isEmpty()) {
        return ResponseEntity.notFound().build();

    }
 // Atualizar o Pet no banco usando o método de repositório
    petRepository.deletePetById(String.valueOf(id));
 // Consultar novamente para retornar ao cliente
    Pet deleted = petRepository.findById(String.valueOf(id)).orElse(null);
        return ResponseEntity.noContent().build();
    }




    @GetMapping("/similares/{id}")
    public ResponseEntity<List<Pet>>  searchByEmbedding(
            @PathVariable String id,
            @RequestParam double maxDistance,
            @RequestParam int limite


    ) {
        System.out.println("===-----------------------------------------------------------------===");
        System.out.println("Id: " + id);
        System.out.println("Max Distance: " + maxDistance);
        System.out.println("Limite: x" + limite);
        System.out.println("===-----------------------------------------------------------------===");
        List<Pet> pets = petRepository.buscarSimilares(id, maxDistance, limite);
        return ResponseEntity.ok(pets);
    }


}



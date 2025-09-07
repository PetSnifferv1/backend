package br.com.petsniffer.app.controller;

import br.com.petsniffer.app.application.PetService;
import br.com.petsniffer.app.domain.pets.Pet;
import br.com.petsniffer.app.domain.pets.PetStatus;
import br.com.petsniffer.app.domain.user.User;
import br.com.petsniffer.app.interfaces.dtos.PetResponseDTO;
import br.com.petsniffer.app.interfaces.dtos.PetUpdateDTO;
import com.amazonaws.services.s3.AmazonS3;
import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://www.petsniffer.com.br",
        "https://www.petsniffer.com.br",
        "http://petsniffer-alb-298396905.us-east-1.elb.amazonaws.com",
        "https://petsniffer-alb-298396905.us-east-1.elb.amazonaws.com"
})

@RestController()
@RequestMapping("pets")
public class PetController {

    @Autowired
    private PetService petService;

    private AmazonS3 s3Client;
    private final String bucketName = "petsniffer-pets";

    @PermitAll
    @PostMapping("/fileup")
    public ResponseEntity<PetResponseDTO> handleFileBodyUpload(
        @RequestParam("file") MultipartFile file,
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
        @RequestParam("location") String location
    ) throws IOException {
        PetResponseDTO response = petService.createPetWithFile(
            file, nome, tipo, raca, cor, status, datahora, ownerid, pais, estado, cidade, bairro, rua, location
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-pets")
    public ResponseEntity<List<PetResponseDTO>> getAllPets(@RequestParam String ownerid) {
        List<PetResponseDTO> dtos = petService.getPetsByOwner(ownerid);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/public-pets")
    public ResponseEntity<List<PetResponseDTO>> getPublicPets() {
        List<PetResponseDTO> dtos = petService.getPublicPets();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search-by-location")
    public ResponseEntity<List<PetResponseDTO>> searchByLocation(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radius
    ) {
        List<PetResponseDTO> dtos = petService.searchByLocation(lat, lng, radius);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("alter-pets/{id}")
    public ResponseEntity<PetResponseDTO> updatePet(@PathVariable String id, @RequestBody @Valid PetUpdateDTO body) {
        // Checagem de ownerid
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }
        Pet pet = petService.getPetById(id);
        if (!pet.getOwnerid().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        PetResponseDTO dto = petService.updatePet(id, body);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/alter-pet-foto/{id}")
    public ResponseEntity<PetResponseDTO> updatePetWithPhoto(
            @PathVariable String id,
            @RequestParam(value = "file", required = false) MultipartFile file,
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
            @RequestParam("location") String location
    ) throws IOException {
        // Checagem de ownerid
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }
        Pet pet = petService.getPetById(id);
        if (!pet.getOwnerid().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg") || contentType.equals("image/gif"))) {
                return ResponseEntity.badRequest().body(null);
            }
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        PetResponseDTO response = petService.updatePetWithPhoto(
            id, file, nome, tipo, raca, cor, status, datahora, ownerid, pais, estado, cidade, bairro, rua, location
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete-pets/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable String id) {
        // Checagem de ownerid
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(401).build();
        }
        Pet pet = petService.getPetById(id);
        if (!pet.getOwnerid().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/similares/{id}")
    public ResponseEntity<List<PetResponseDTO>> searchByEmbedding(
            @PathVariable String id,
            @RequestParam String tipo,
            @RequestParam double maxDistance,
            @RequestParam int limite
    ) {
        List<PetResponseDTO> dtos = petService.searchByEmbedding(id, tipo, maxDistance, limite);
        return ResponseEntity.ok(dtos);
    }

}



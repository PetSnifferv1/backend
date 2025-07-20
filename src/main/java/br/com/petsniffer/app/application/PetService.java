package br.com.petsniffer.app.application;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;

import br.com.petsniffer.app.domain.pets.Pet;
import br.com.petsniffer.app.domain.pets.PetStatus;
import br.com.petsniffer.app.interfaces.dtos.PetRequestDTO;
import br.com.petsniffer.app.interfaces.dtos.PetResponseDTO;
import br.com.petsniffer.app.interfaces.dtos.PetUpdateDTO;
import br.com.petsniffer.app.interfaces.mapper.PetMapper;
import br.com.petsniffer.app.repositories.PetRepository;

@Service
public class PetService {
    @Autowired
    private PetRepository petRepository;

    private AmazonS3 s3Client;
    private final String bucketName = "petsniffer-pets";

    public PetResponseDTO createPet(PetRequestDTO dto) {
        Pet pet = PetMapper.toEntity(dto);
        pet.setId(UUID.randomUUID().toString());
        petRepository.save(pet);
        return PetMapper.toResponseDTO(pet);
    }

    public PetResponseDTO createPetWithFile(
        MultipartFile file,
        String nome,
        String tipo,
        String raca,
        String cor,
        PetStatus status,
        LocalDateTime datahora,
        String ownerid,
        String pais,
        String estado,
        String cidade,
        String bairro,
        String rua,
        String location
    ) throws IOException {
        String petId = UUID.randomUUID().toString();
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = petId + fileExtension;
        String s3Key = fileName;
        InputStream inputStream = file.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        s3Client.putObject(bucketName, s3Key, inputStream, metadata);
        String imageUrl = s3Client.getUrl(bucketName, s3Key).toString();
        Pet newPet = new Pet();
        newPet.setId(petId);
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
        newPet.setFoto(imageUrl);
        petRepository.save(newPet);
        return PetMapper.toResponseDTO(newPet);
    }

    public List<PetResponseDTO> getPetsByOwner(String ownerId) {
        List<Pet> pets = petRepository.findByOwnerid(ownerId);
        return pets.stream().map(PetMapper::toResponseDTO).collect(Collectors.toList());
    }

    public List<PetResponseDTO> getPublicPets() {
        List<Pet> pets = petRepository.findAll();
        return pets.stream().map(PetMapper::toResponseDTO).collect(Collectors.toList());
    }

    public PetResponseDTO updatePet(String id, PetUpdateDTO dto) {
        Pet pet = petRepository.findById(id).orElseThrow();
        // Atualiza os campos do pet com os dados do DTO
        if (dto.getNome() != null) pet.setNome(dto.getNome());
        if (dto.getTipo() != null) pet.setTipo(dto.getTipo());
        if (dto.getRaca() != null) pet.setRaca(dto.getRaca());
        if (dto.getCor() != null) pet.setCor(dto.getCor());
        if (dto.getStatus() != null) pet.setStatus(dto.getStatus());
        if (dto.getDatahora() != null) pet.setDatahora(dto.getDatahora());
        if (dto.getFoto() != null) pet.setFoto(dto.getFoto());
        if (dto.getOwnerid() != null) pet.setOwnerid(dto.getOwnerid());
        if (dto.getPais() != null) pet.setPais(dto.getPais());
        if (dto.getEstado() != null) pet.setEstado(dto.getEstado());
        if (dto.getCidade() != null) pet.setCidade(dto.getCidade());
        if (dto.getBairro() != null) pet.setBairro(dto.getBairro());
        if (dto.getRua() != null) pet.setRua(dto.getRua());
        if (dto.getLocation() != null) pet.setLocation(dto.getLocation());
        if (dto.getEmbedding() != null) pet.setEmbedding(dto.getEmbedding());
        petRepository.save(pet);
        return PetMapper.toResponseDTO(pet);
    }

    public PetResponseDTO updatePetWithPhoto(
            String id,
            MultipartFile file,
            String nome,
            String tipo,
            String raca,
            String cor,
            PetStatus status,
            LocalDateTime datahora,
            String ownerid,
            String pais,
            String estado,
            String cidade,
            String bairro,
            String rua,
            String location
    ) throws IOException {
        Pet pet = petRepository.findById(id).orElseThrow();
        if (file != null && !file.isEmpty()) {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = id + fileExtension;
            String s3Key = fileName;
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3Client.putObject(bucketName, s3Key, inputStream, metadata);
            String imageUrl = s3Client.getUrl(bucketName, s3Key).toString();
            pet.setFoto(imageUrl);
        }
        pet.setNome(nome);
        pet.setTipo(tipo);
        pet.setRaca(raca);
        pet.setCor(cor);
        pet.setStatus(status);
        pet.setDatahora(datahora);
        pet.setOwnerid(ownerid);
        pet.setPais(pais);
        pet.setEstado(estado);
        pet.setCidade(cidade);
        pet.setBairro(bairro);
        pet.setRua(rua);
        pet.setLocation(location);
        petRepository.save(pet);
        return PetMapper.toResponseDTO(pet);
    }

    public void deletePet(String id) {
        petRepository.deletePetById(id);
    }

    public List<PetResponseDTO> searchByLocation(double lat, double lng, double radius) {
        List<Pet> pets = petRepository.findPetsByLocation(lat, lng, radius);
        return pets.stream().map(PetMapper::toResponseDTO).collect(Collectors.toList());
    }

    public List<PetResponseDTO> searchByEmbedding(String id, String tipo, double maxDistance, int limite) {
        List<Pet> pets = petRepository.buscarSimilares(id, tipo, maxDistance, limite);
        return pets.stream().map(PetMapper::toResponseDTO).collect(Collectors.toList());
    }

    public Pet getPetById(String id) {
        return petRepository.findById(id).orElseThrow();
    }
} 
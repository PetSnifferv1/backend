package br.com.petsniffer.app.interfaces.mapper;

import br.com.petsniffer.app.domain.pets.Pet;
import br.com.petsniffer.app.interfaces.dtos.PetRequestDTO;
import br.com.petsniffer.app.interfaces.dtos.PetUpdateDTO;
import br.com.petsniffer.app.interfaces.dtos.PetResponseDTO;

public class PetMapper {
    public static Pet toEntity(PetRequestDTO dto) {
        Pet pet = new Pet();
        pet.setNome(dto.nome());
        pet.setTipo(dto.tipo());
        pet.setRaca(dto.raca());
        pet.setCor(dto.cor());
        pet.setStatus(dto.status());
        pet.setDatahora(dto.datahora());
        pet.setFoto(dto.foto());
        pet.setOwnerid(dto.ownerid());
        pet.setPais(dto.pais());
        pet.setEstado(dto.estado());
        pet.setCidade(dto.cidade());
        pet.setBairro(dto.bairro());
        pet.setRua(dto.rua());
        pet.setLocation(dto.location());
        pet.setEmbedding(dto.embedding());
        return pet;
    }

    public static Pet toEntity(PetUpdateDTO dto) {
        Pet pet = new Pet();
        pet.setId(dto.getId());
        pet.setNome(dto.getNome());
        pet.setTipo(dto.getTipo());
        pet.setRaca(dto.getRaca());
        pet.setCor(dto.getCor());
        pet.setStatus(dto.getStatus());
        pet.setDatahora(dto.getDatahora());
        pet.setFoto(dto.getFoto());
        pet.setOwnerid(dto.getOwnerid());
        pet.setPais(dto.getPais());
        pet.setEstado(dto.getEstado());
        pet.setCidade(dto.getCidade());
        pet.setBairro(dto.getBairro());
        pet.setRua(dto.getRua());
        pet.setLocation(dto.getLocation());
        pet.setEmbedding(dto.getEmbedding());
        return pet;
    }

    public static PetResponseDTO toResponseDTO(Pet pet) {
        return new PetResponseDTO(pet);
    }
} 
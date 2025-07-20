package br.com.petsniffer.app.interfaces.dtos;

import br.com.petsniffer.app.domain.pets.Pet;
import br.com.petsniffer.app.domain.pets.PetStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PetResponseDTO(String id,
                             String nome,
                             String tipo,
                             String raca,
                             String cor,
                             PetStatus status,
                             LocalDateTime datahora,
                             String foto,
                             String ownerid,
                             String pais,
                             String estado,
                             String cidade,
                             String bairro,
                             String rua,
                             String location,
                             List<Float> embedding) {
    public PetResponseDTO(Pet pet){
        this(pet.getId(),
                pet.getNome(),
                pet.getTipo(),
                pet.getRaca(),
                pet.getCor(),
                pet.getStatus(),
                pet.getDatahora(),
                pet.getFoto(),
                pet.getOwnerid(),
                pet.getPais(),
                pet.getEstado(),
                pet.getCidade(),
                pet.getBairro(),
                pet.getRua(),
                pet.getLocation(),
                pet.getEmbedding());
    }


}

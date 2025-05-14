package br.com.petsniffer.app.domain.pets.dtos;

import br.com.petsniffer.app.domain.pets.PetStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PetRequestDTO(
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
        List<Float> embedding) {}


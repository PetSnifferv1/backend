package br.com.petsniffer.app.domain.pets.dtos;

import br.com.petsniffer.app.domain.pets.PetStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class PetUpdateDTO {
    private String id;
    private String nome;
    private String tipo;
    private String raca;
    private String cor;
    private PetStatus status;
    private LocalDateTime datahora;
    private String foto;
    private String ownerid;
    private String pais;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String location;
    private List<Float> embedding;}


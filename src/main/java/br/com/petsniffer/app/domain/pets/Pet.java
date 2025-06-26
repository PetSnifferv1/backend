package br.com.petsniffer.app.domain.pets;


import br.com.petsniffer.app.domain.pets.dtos.PetRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "pet")
@Entity(name = "pet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pet {
    @Id

//    @GeneratedValue(strategy = GenerationType.UUID) // Tem que gerar o Id no momento de gerar o nome do arquivo de imagem
    // por enquanto.. quando implemtar as melhoria e adequações devo voltar e gerar
    private String id;
    private String nome;
    private String tipo;
    private String raca;
    private String cor;
    @Enumerated(EnumType.STRING)
    private PetStatus status;
    private LocalDateTime datahora;

    private String foto;
    @Column(name = "ownerid", nullable = false) // Mapeia o campo ownerId no banco
    private String ownerid;
    private String pais;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String location;

    @Transient
    private List<Float> embedding;

    public Pet(PetRequestDTO dto) {
        this.nome       = dto.nome();
        this.tipo       = dto.tipo();
        this.raca       = dto.raca();
        this.cor        = dto.cor();
        this.status     = dto.status();
        this.datahora   = dto.datahora();
        this.foto       = dto.foto();
        this.ownerid    = dto.ownerid();
        this.pais       = dto.pais();
        this.estado     = dto.estado();
        this.cidade     = dto.cidade();
        this.bairro     = dto.bairro();
        this.rua        = dto.rua();
        this.location   = dto.location();
        this.embedding  = dto.embedding();
    }

}











package br.com.petsniffer.app.repositories;

import br.com.petsniffer.app.domain.pets.Pet;
import br.com.petsniffer.app.domain.pets.PetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, String> {

    List<Pet> findByOwnerid(String ownerid);


    List<Pet> findAll();

    @Transactional
    @Modifying
    @Query("UPDATE pet p SET " +
            "p.nome = :nome, " +
            "p.tipo= :tipo, " +
            "p.raca = :raca," +
            "p.cor = :cor, " +
            "p.status = :status, " +
            "p.datahora = :datahora, " +
            "p.foto = :foto, " +
            "p.pais = :pais , " +
            "p.estado = :estado , " +
            "p.cidade = :cidade , " +
            "p.bairro = :bairro , " +
            "p.rua = :rua , " +
            "p.location = :location " +
            "WHERE p.id = :id")

    void updateById(String id,
                    String nome,
                    String tipo,
                    String raca,
                    String cor,
                    PetStatus status,
                    LocalDateTime datahora,
                    String foto,
                    String pais,
                    String estado,
                    String cidade,
                    String bairro,
                    String rua,
                    String location);

    @Transactional
    @Modifying
    @Query("DELETE FROM pet p WHERE p.id = :id")
    void deletePetById(String id);

    @Transactional
    @Modifying
    void deleteAllByOwnerid(String ownerid);

    @Transactional
    @Modifying
    @Query(value = """

            SELECT p.*,\s
           (6371 * ACOS(
               COS(RADIANS(:latitude)) * COS(RADIANS(CAST(NULLIF(SPLIT_PART(p.location, ',', 1), '') AS DOUBLE PRECISION))) *\s
               COS(RADIANS(CAST(NULLIF(SPLIT_PART(p.location, ',', 2), '') AS DOUBLE PRECISION)) - RADIANS(:longitude)) +\s
               SIN(RADIANS(:latitude)) * SIN(RADIANS(CAST(NULLIF(SPLIT_PART(p.location, ',', 1), '') AS DOUBLE PRECISION)))
           )) AS distance
            FROM pet p
            WHERE p.location IS NOT NULL\s
      AND p.location <> ''
      AND (6371 * ACOS(
               COS(RADIANS(:latitude)) * COS(RADIANS(CAST(NULLIF(SPLIT_PART(p.location, ',', 1), '') AS DOUBLE PRECISION))) *\s
               COS(RADIANS(CAST(NULLIF(SPLIT_PART(p.location, ',', 2), '') AS DOUBLE PRECISION)) - RADIANS(:longitude)) +\s
               SIN(RADIANS(:latitude)) * SIN(RADIANS(CAST(NULLIF(SPLIT_PART(p.location, ',', 1), '') AS DOUBLE PRECISION)))
           )) < :radius
            ORDER BY distance;            
        """, nativeQuery = true)
    List<Pet> findPetsByLocation(
            @Param("latitude")  double latitude,
            @Param("longitude") double longitude,
            @Param("radius")    double radius
    );

    @Query(value = """

            WITH target_pet AS (
                     SELECT embedding, tipo FROM pet WHERE id = :id
                 )
                 SELECT p.*
                 FROM pet p, target_pet tp
                 WHERE p.id != :id
                   AND p.tipo = tp.tipo  
                   AND p.embedding IS NOT NULL
                   AND (p.embedding <=> tp.embedding) <= CAST(:maxDistance AS double precision)
                 ORDER BY p.embedding <=> tp.embedding
                 LIMIT :limite;
    """, nativeQuery = true)
    List<Pet> buscarSimilares(
            @Param("id") String id,
            @Param("tipo") String tipo,
            @Param("maxDistance") double maxDistance,
            @Param("limite") int limite
    );

}







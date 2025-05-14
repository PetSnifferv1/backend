package br.com.petsniffer.app.services;

import br.com.petsniffer.app.domain.pets.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetEmbeddingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Insere um pet com embedding
    public void insertPetWithEmbedding(Pet pet, List<Float> embedding) {
        String sql = "INSERT INTO pet (id, nome, tipo, raca, cor, status, datahora, foto, ownerid, pais, estado, cidade, bairro, rua, location, embedding) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::vector)";
        jdbcTemplate.update(sql,
                pet.getId(), pet.getNome(), pet.getTipo(), pet.getRaca(), pet.getCor(), pet.getStatus().toString(),
                pet.getDatahora(), pet.getFoto(), pet.getOwnerid(), pet.getPais(), pet.getEstado(), pet.getCidade(),
                pet.getBairro(), pet.getRua(), pet.getLocation(), embeddingToPgvector(embedding)
        );
    }

    // Atualiza o embedding de um pet
    public void updatePetEmbedding(String petId, List<Float> embedding) {
        String sql = "UPDATE pet SET embedding = ?::vector WHERE id = ?";
        jdbcTemplate.update(sql, embeddingToPgvector(embedding), petId);
    }

    // Busca pets por similaridade de embedding
    public List<Pet> findPetsBySimilarity(List<Float> queryEmbedding, int limit) {
        String sql = "SELECT * FROM pet ORDER BY embedding <=> ?::vector LIMIT ?";
        return jdbcTemplate.query(sql, new Object[]{embeddingToPgvector(queryEmbedding), limit}, petRowMapper);
    }

    // Converte List<Float> para formato pgvector
    private String embeddingToPgvector(List<Float> embedding) {
        return "[" + embedding.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";
    }

    // Mapeia ResultSet para Pet (ajuste conforme seus campos)
    private final RowMapper<Pet> petRowMapper = new RowMapper<>() {
        @Override
        public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
            Pet pet = new Pet();
            pet.setId(rs.getString("id"));
            pet.setNome(rs.getString("nome"));
            pet.setTipo(rs.getString("tipo"));
            pet.setRaca(rs.getString("raca"));
            pet.setCor(rs.getString("cor"));
            // Adicione os outros campos conforme necessário
            return pet;
        }
    };
} 
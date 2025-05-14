package br.com.petsniffer.app.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class FloatListConverter implements AttributeConverter<List<Float>, Object> {

    @Override
    public Object convertToDatabaseColumn(List<Float> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        String value = "[" + attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "]";
        try {
            PGobject obj = new PGobject();
            obj.setType("vector");
            obj.setValue(value);
            return obj;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Float> convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        String data = dbData.toString();
        String[] values = data.substring(1, data.length() - 1).split(",");
        return Arrays.stream(values)
                .map(String::trim)
                .map(Float::parseFloat)
                .collect(Collectors.toList());
    }
} 
package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.format(FORMATTER);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String databaseValue) {
        if (databaseValue == null || databaseValue.trim().isEmpty()) {
            return null;
        }

        // Handle Unix epoch milliseconds if they exist in the database
        if (databaseValue.matches("^\\d+$")) {
            long timestamp = Long.parseLong(databaseValue);
            return LocalDateTime.ofEpochSecond(timestamp / 1000, 0, java.time.ZoneOffset.UTC);
        }

        // Handle ISO 8601 format
        return LocalDateTime.parse(databaseValue, FORMATTER);
    }
}


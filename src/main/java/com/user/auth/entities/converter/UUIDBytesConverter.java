package com.user.auth.entities.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.nio.ByteBuffer;
import java.util.UUID;

@Converter(autoApply = false)
public class UUIDBytesConverter implements AttributeConverter<UUID, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(UUID uuid) {
    	System.out.println("Yes");
        if (uuid == null) return null;
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    @Override
    public UUID convertToEntityAttribute(byte[] dbData) {
    	System.err.println("Yes1");
        if (dbData == null) return null;
        if (dbData.length != 16) {
            throw new IllegalArgumentException("Expected 16 bytes for UUID, got " + dbData.length);
        }
        ByteBuffer bb = ByteBuffer.wrap(dbData);
        long msb = bb.getLong();
        long lsb = bb.getLong();
        return new UUID(msb, lsb);
    }
}


package com.acme.checkout.domain.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDate;

@WritingConverter
public class LocalDateWriteConverter implements Converter<LocalDate,String> {
    @Override
    public String convert(LocalDate source) {
        return source.toString();
    }
}

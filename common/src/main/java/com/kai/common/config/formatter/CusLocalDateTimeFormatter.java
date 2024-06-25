package com.kai.common.config.formatter;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class CusLocalDateTimeFormatter implements Formatter<LocalDateTime> {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm-ss");


    @Override
    public LocalDateTime parse(String s, Locale locale) throws ParseException {
        LocalDateTime localDateTime = null;
        try {
            localDateTime = LocalDateTime.parse(s, dateTimeFormatter.withLocale(locale));
        } catch (Exception e) {

        }

        return localDateTime;
    }

    @Override
    public String print(LocalDateTime localDateTime, Locale locale) {
        return dateTimeFormatter.withLocale(locale).format(localDateTime);
    }
}

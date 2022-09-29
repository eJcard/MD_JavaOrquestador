package uy.com.md.common.jaxb.adapters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String marshal(LocalDate date) {
        return date.format(dateFormat);
    }

    @Override
    public LocalDate unmarshal(String date) {
        return LocalDate.parse(date, dateFormat);
    }

}
package uy.com.md.common.util;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

	private static Logger log = LoggerFactory.getLogger(DateUtil.class);
	
	public static final String FORMATO_CORTO = "yyyy-MM-dd";

	private DateUtil() {
		super();
	}

	public static Converter<LocalDate, XMLGregorianCalendar> getLocalDateToGregorian() {

		return new AbstractConverter<LocalDate, XMLGregorianCalendar>() {
			protected XMLGregorianCalendar convert(LocalDate source) {
				if (source == null) {
					return null;
				}
				XMLGregorianCalendar xmlGregorianCalendar = null;
				try {
					xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(source.toString());
				} catch (DatatypeConfigurationException e) {
					log.error("getLocalDateToGregorian", e);
				}
				return xmlGregorianCalendar;
			}
		};
	}

	public static Converter<LocalDateTime, XMLGregorianCalendar> getLocalDateTimeToGregorian() {

		return new AbstractConverter<LocalDateTime, XMLGregorianCalendar>() {
			protected XMLGregorianCalendar convert(LocalDateTime source) {
				if (source == null) {
					return null;
				}
				XMLGregorianCalendar xmlGregorianCalendar = null;
				try {
					xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(source.toString());
				} catch (DatatypeConfigurationException e) {
					log.error("getLocalDateTimeToGregorian", e);
				}
				return xmlGregorianCalendar;
			}
		};
	}

	public static Converter<String, XMLGregorianCalendar> getStringToGregorian() {

		return new AbstractConverter<String, XMLGregorianCalendar>() {
			protected XMLGregorianCalendar convert(String source) {
				if (source == null) {
					return null;
				}
				XMLGregorianCalendar xmlGregorianCalendar = null;
				try {
					if(source.contains("-")){
						xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(source.substring(0, 4) + "-" + source.substring(5, 7) + "-" + source.substring(8, 10));
					}else {
						xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(source.substring(0, 4) + "-" + source.substring(4, 6) + "-" + source.substring(6, 8));
					}
				} catch (DatatypeConfigurationException e) {
					log.error("getStringToGregorian", e);
				}
				return xmlGregorianCalendar;
			}
		};
	}

	public static Converter<String, LocalDate> getStringToLocalDate() {
		return new AbstractConverter<String, LocalDate>() {
			protected LocalDate convert(String source) {
				if (source == null) {
					return null;
				}
				LocalDate localDate = null;
				try {
					DateTimeFormatter format = DateTimeFormatter.ofPattern(FORMATO_CORTO);

					localDate = LocalDate.parse(source, format);
				} catch (Exception e) {
					log.error("getStringToLocalDate", e);
				}
				return localDate;
			}
		};
	}

	public static Converter<String, LocalDateTime> getStringToLocalDateTime() {
		return new AbstractConverter<String, LocalDateTime>() {
			protected LocalDateTime convert(String source) {
				if (source == null) {
					return null;
				}
				LocalDateTime localDateTime = null;
				try {
					DateTimeFormatter format = DateTimeFormatter.ofPattern(FORMATO_CORTO);

					localDateTime = LocalDateTime.parse(source, format);
				} catch (Exception e) {
					log.error("AbstractConverter", e);
				}
				return localDateTime;
			}
		};
	}

	public static XMLGregorianCalendar convertStringToXMLGregorianCalendar(String date) throws DatatypeConfigurationException {
		XMLGregorianCalendar result = DatatypeFactory.newInstance().newXMLGregorianCalendar(date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8));
		return result;
	}

	public static LocalDateTime mapStringToLocalDateTime(String source) {
		LocalDateTime localDateTime = null;
		try {
			localDateTime = LocalDateTime.parse(source);
		} catch (Exception e) {
			log.error("convertStringToXMLGregorianCalendar", e);
		}
		return localDateTime;
	}

	public static String xMLGregorianCalendarToString(XMLGregorianCalendar gregorian, String format) {
		String date = null;
		try {
			if (gregorian != null) {
				LocalDate localDate = LocalDate.of(gregorian.getYear(), gregorian.getMonth(), gregorian.getDay());
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
				return formatter.format(localDate);
			}
		} catch (Exception e) {
			log.error("xMLGregorianCalendarToString", e);
		}
		return date;
	}
}

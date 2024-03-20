package com.ceri.planningfx.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EvenementEntity{

    private String summary;
    private String description;
    private String dateStartString;
    private String dateEndString;
    private Date dateStart;
    private Date dateEnd;
    public EvenementEntity() {
    }

    public void mapDate() throws ParseException {
        dateStart = this.parseDate(dateStartString);
        dateEnd = this.parseDate(dateEndString);
    }
    public Date parseDate(String dateString) throws ParseException {
        // Formats de date possibles
        String[] dateFormats = {"yyyyMMdd'T'HHmmss'Z'", "yyyyMMdd"};

        // Essayer de parser la date avec différents formats
        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                if (format.contains("Z")) {
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Assurez-vous que le fuseau horaire est correct
                }
                return sdf.parse(dateString);
            } catch (ParseException e) {
                // Ignorer cette exception et essayer le prochain format
            }
        }
        throw new ParseException("Unparseable date: " + dateString, 0);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateStartString() {
        return dateStartString;
    }

    public void setDateStartString(String dateStartString) {
        this.dateStartString = dateStartString;
    }

    public String getDateEndString() {
        return dateEndString;
    }

    public void setDateEndString(String dateEndString) {
        this.dateEndString = dateEndString;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }
}

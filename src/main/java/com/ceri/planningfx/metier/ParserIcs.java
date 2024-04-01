package com.ceri.planningfx.metier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ceri.planningfx.models.EvenementEntity;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
public class ParserIcs implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String PATH =  System.getProperty("user.dir") + "\\src\\main\\resources\\com\\ceri\\planningfx\\data\\";
    private  Calendar calendar;
    private  List<VEvent> events;

    private int min = 24;
    public ParserIcs() {
        System.out.println("dir : " + System.getProperty("user.dir"));
        calendar = new Calendar();
        events = new ArrayList<>();
    }

    public  Map<LocalDate, List<EvenementEntity>> parse() {
        Map<LocalDate, List<EvenementEntity>> eventsMap = new HashMap<>();
        try {
            FileInputStream fin = this.getIcsFile("planning\\users\\uapv2200555.ics");
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);
            List<VEvent> events = calendar.getComponents("VEVENT");

            for (VEvent event : events) {
                EvenementEntity evenementEntity = new EvenementEntity();
                if(event.getDescription().isPresent()) {
                    //System.out.println("description : " + event.getDescription().get().getValue());
                    //afficher la liste des paramètres de la description
                    String description = event.getDescription().get().getValue();
                    //couper le string en list separer par retour a la ligne
                    String[] descriptionList = description.split("\n");
                    String summary = "";
                    for (String s : descriptionList) {
                        if (s.contains("Matière")) {
                            evenementEntity.setMatiere(s);
                            summary = summary + s + "\n";
                        } else if (s.contains("Salle")) {
                            evenementEntity.setSalle(s);
                            summary = summary + s + "\n";
                        } else if (s.contains("Professeur")) {
                            evenementEntity.setProfesseur(s);
                            summary = summary + s + "\n";
                        } else if (s.contains("Type")) {
                            evenementEntity.setType(s);
                            summary = summary + s + "\n";
                        }
                    }
                    evenementEntity.setSummary(summary);
                } else {
                    evenementEntity.setSummary("pas de description");
                }
                evenementEntity.setDateStartString(event.getDateTimeStart().get().getValue());
                evenementEntity.setDateEndString(event.getDateTimeEnd().get().getValue());
                evenementEntity.mapDate();
                if(evenementEntity.getDateStart().getHours() < this.min ) {
                    this.min = evenementEntity.getDateStart().getHours();
                }
                LocalDate eventDate = evenementEntity.getDateStart().toInstant().
                        atZone(ZoneId.systemDefault()).toLocalDate();
                eventsMap.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(evenementEntity);
            }
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return eventsMap;
    }
    public  FileInputStream getIcsFile(String path) {
        try {
            //path to resource
            return new FileInputStream(PATH + path);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int getMin() {
        return min;
    }
}

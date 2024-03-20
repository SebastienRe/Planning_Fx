package com.ceri.planningfx.metier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
public class ParserIcs implements Serializable {

    private static final long serialVersionUID = 1L;
    //à mettre en variable d'environnement plus tard
    private static final String PATH = "C:\\projets\\S2\\javafx\\Planning_fx\\src\\main\\resources\\com\\ceri\\planningfx\\data\\";
    private static Calendar calendar;
    private static List<VEvent> events;
    public ParserIcs() {
        calendar = new Calendar();
        events = new ArrayList<>();
    }

    public static void parse(String ics) {
        try {
            FileInputStream fin = getIcsFile(ics);
            CalendarBuilder builder = new CalendarBuilder();
            calendar = builder.build(fin);
            events = calendar.getComponents("VEVENT");
            for (VEvent event : events) {
                System.out.println(event.getDateTimeStart().get().getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static FileInputStream getIcsFile(String path) {
        try {
            //path to resource
            return new FileInputStream(PATH + path);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}

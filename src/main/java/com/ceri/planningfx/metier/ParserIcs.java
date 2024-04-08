package com.ceri.planningfx.metier;

import java.io.*;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

import com.ceri.planningfx.models.EvenementEntity;
import com.ceri.planningfx.models.FiltresCollections;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;

public class ParserIcs implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String PATH = System.getProperty("user.dir") +
            File.separator + "data" +
            File.separator + "planning";

    public static String foleder = "";
    public static String file = "";
    private int min = 24;
    public FiltresCollections filtresCollections;

    public ParserIcs() {
        filtresCollections = new FiltresCollections();
    }

    public Map<LocalDate, List<EvenementEntity>> parse() {
        Map<LocalDate, List<EvenementEntity>> eventsMap = new HashMap<>();
        try {
            FileInputStream fin = this.getIcsFile(foleder, file);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);
            List<VEvent> events = calendar.getComponents("VEVENT");

            for (VEvent event : events) {
                EvenementEntity evenementEntity = new EvenementEntity();
                if (event.getDescription().isPresent()) {
                    // System.out.println("description : " +
                    // event.getDescription().get().getValue());
                    // afficher la liste des paramètres de la description
                    String description = event.getDescription().get().getValue();
                    // couper le string en list separer par retour a la ligne
                    String[] descriptionList = description.split("\n");
                    String summary = "";
                    for (String s : descriptionList) {
                        if (s.contains("Matière")) {
                            evenementEntity.setMatiere(s);
                            filtresCollections.listDesMatieres.add(s);
                            summary = summary + s + "\n";
                        } else if (s.contains("Enseignant")) {
                            evenementEntity.setProfesseur(s);
                            summary = summary + s + "\n";
                        } else if (s.contains("Salle")) {
                            evenementEntity.setSalle(s);
                            filtresCollections.listDesSalles.add(s);
                            summary = summary + s + "\n";
                        } else if (s.contains("Type")) {
                            filtresCollections.typesDeCours.add(s);
                            evenementEntity.setType(s);
                            summary = summary + s + "\n";
                        } else if (s.contains("TD")) {
                            String[] td = s.split("\\,");
                            filtresCollections.listDesGroupes.addAll(Arrays.asList(td));

                        }
                    }
                    evenementEntity.setSummary(summary);
                } else if (event.getSummary().isPresent()) {
                    evenementEntity.setSummary(event.getSummary().get().getValue());
                } else {
                    evenementEntity.setSummary("pas de description");
                }
                evenementEntity.setDateStartString(event.getDateTimeStart().get().getValue());
                evenementEntity.setDateEndString(event.getDateTimeEnd().get().getValue());
                evenementEntity.mapDate();
                if (evenementEntity.getDateStart().getHours() < this.min) {
                    this.min = evenementEntity.getDateStart().getHours();
                }
                LocalDate eventDate = evenementEntity.getDateStart().toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate();
                eventsMap.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(evenementEntity);
            }
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return eventsMap;
    }

    public FileInputStream getIcsFile(String folder, String file) {
        try {

            String filePath = PATH + File.separator + folder + File.separator + file;
            // path to resource
            return new FileInputStream(filePath);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int getMin() {
        return min;
    }

    public Map<LocalDate, List<EvenementEntity>> parseWithFiltre(String filtre) {
        Map<LocalDate, List<EvenementEntity>> eventsMap = new HashMap<>();
        try {
            FileInputStream fin = this.getIcsFile(foleder, file);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);
            List<VEvent> events = calendar.getComponents("VEVENT");

            for (VEvent event : events) {
                EvenementEntity evenementEntity = new EvenementEntity();
                if (event.getDescription().isPresent()) {
                    String description = event.getDescription().get().getValue();
                    String[] descriptionList = description.split("\n");
                    String summary = "";

                    // Vérifier si la description contient le filtre
                    boolean containsFilter = false;
                    for (String s : descriptionList) {
                        if (s.contains(filtre)) {
                            containsFilter = true;
                            break;
                        }
                    }

                    // Si la description contient le filtre, ajouter l'événement
                    if (containsFilter) {
                        for (String s : descriptionList) {
                            if (s.contains("Matière")) {
                                evenementEntity.setMatiere(s);
                                filtresCollections.listDesMatieres.add(s);
                                summary = summary + s + "\n";
                            } else if (s.contains("Enseignant")) {
                                evenementEntity.setProfesseur(s);
                                summary = summary + s + "\n";
                            } else if (s.contains("Salle")) {
                                evenementEntity.setSalle(s);
                                filtresCollections.listDesSalles.add(s);
                                summary = summary + s + "\n";
                            } else if (s.contains("Type")) {
                                filtresCollections.typesDeCours.add(s);
                                evenementEntity.setType(s);
                                summary = summary + s + "\n";
                            } else if (s.contains("TD")) {
                                String[] td = s.split("\\,");
                                filtresCollections.listDesGroupes.addAll(Arrays.asList(td));
                            }
                        }
                        evenementEntity.setSummary(summary);
                        evenementEntity.setDateStartString(event.getDateTimeStart().get().getValue());
                        evenementEntity.setDateEndString(event.getDateTimeEnd().get().getValue());
                        evenementEntity.mapDate();
                        if (evenementEntity.getDateStart().getHours() < this.min) {
                            this.min = evenementEntity.getDateStart().getHours();
                        }
                        LocalDate eventDate = evenementEntity.getDateStart().toInstant().atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        System.out.println("Adding event: " + evenementEntity.getSummary() + " "
                                + evenementEntity.getDateStart() + " " + evenementEntity.getDateEnd());
                        eventsMap.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(evenementEntity);
                    }
                }
            }
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return eventsMap;
    }

    public Temporal transformDate(String date, String time) {
        String[] dateParts = date.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);

        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        return LocalDateTime.of(year, month, day, hour, minute).atZone(ZoneId.systemDefault()).toInstant();
    }

    public String addEvent(String date, String time, String description) {
        // date est au format 24/12/2020
        // time est au format 12:00-14:00
        // description est une chaine de caractère contenant les informations de
        // l'événement
        // retourne ok si tout s'est bien passé sinon retourne une chain de caractère
        // expliquant l'erreur
        System.out.println("Adding event: '" + date + "' '" + time + "' '" + description + "'");
        System.out.println(this.transformDate(date, time.split("-")[0]));
        System.out.println(this.transformDate(date, time.split("-")[1]));
        try {
            Temporal startDate = this.transformDate(date, time.split("-")[0]);
            Temporal endDate = this.transformDate(date, time.split("-")[1]);

            VEvent event = new VEvent(
                    startDate,
                    endDate,
                    description);

            // Ajout de l'événement au fichier ICS
            FileInputStream fin = this.getIcsFile(foleder ,file);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);

            // controlle de chevauchement de date
            for (Component component : calendar.getComponents()) {
                if (component instanceof VEvent) {
                    VEvent vEvent = (VEvent) component;
                    String start = vEvent.getStartDate().get().getValue();
                    String end = vEvent.getEndDate().get().getValue();
                    String newStart = event.getStartDate().get().getValue();
                    String newEnd = event.getEndDate().get().getValue();

                    if (start.equals(newStart) || end.equals(newEnd) // Si les dates sont identiques
                            || (start.compareTo(newStart) < 0 && end.compareTo(newEnd) > 0) // Si l'événement est inclus dans un autre
                            || (start.compareTo(newStart) > 0 && start.compareTo(newEnd) < 0)  // Si l'événement commence avant un autre
                            || (end.compareTo(newStart) > 0 && end.compareTo(newEnd) < 0)) // Si l'événement finit après un autre
                        throw new Exception("Chevauchement de date");

                    }
                }

            // Ajout de l'événement au calendrier
            calendar.add(event);

            // serialisation de calendar
            this.serializeCalendar(calendar);

            System.out.println("Event added: " + event);
            return "ok";

        } catch(Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public void serializeCalendar(Calendar calendar) throws IOException {

        // Création du chemin complet du fichier
        String filePath = PATH + File.separator + foleder + File.separator + file;

        // Créer un objet FileOutputStream pour écrire dans un fichier
        FileOutputStream fout = new FileOutputStream(filePath);

        // Utiliser un objet CalendarOutputter pour sérialiser le calendrier
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(calendar, fout);

        // Fermer le flux de sortie
        fout.close();

        System.out.println("Calendrier sérialisé avec succès dans " + filePath);
    }
}

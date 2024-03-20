package com.ceri.planningfx.controller;

import com.ceri.planningfx.models.EvenementEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.*;

import net.fortuna.ical4j.data.CalendarBuilder;

public class EdtController {
    ZonedDateTime dateFocus;
    ZonedDateTime today;

    @FXML
    private Text year;

    @FXML
    private Text month;

    @FXML
    private FlowPane calendar;

    @FXML
    private Button nextWeekButton;
    @FXML
    private Button previousWeekButton;

    // Define grid pane for displaying events
    @FXML
    private GridPane eventGridPane;

    public void initialize() {

        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        nextWeekButton.setOnAction(this::moveToNextWeek);
        previousWeekButton.setOnAction(this::moveToPreviousWeek);
        drawCalendar(calendar);
    }

    private void moveToNextWeek(ActionEvent event) {
        dateFocus = dateFocus.plusWeeks(1); // Avance d'une semaine
        drawCalendar(calendar);
    }

    private void moveToPreviousWeek(ActionEvent event) {
        dateFocus = dateFocus.minusWeeks(1); // Recule d'une semaine
        drawCalendar(calendar);
    }

    private void drawCalendar(FlowPane calendar) {
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

        // Calcul des dates des cinq jours de travail de la semaine
        LocalDate mondayOfWeek = dateFocus.with(DayOfWeek.MONDAY).toLocalDate();
        List<LocalDate> weekDays = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            LocalDate day = mondayOfWeek.plusDays(i);
            weekDays.add(day);
        }

        double calendarWidth = 800;
        double calendarHeight = 600;
        double strokeWidth = 1;
        double spacingH = 10;
        double spacingV = 10;

        Map<LocalDate, List<EvenementEntity>> calendarEventMap = getCalendarEventsWeek(dateFocus);

        for (LocalDate currentDay : weekDays) {
            StackPane stackPane = new StackPane();

            Rectangle rectangle = new Rectangle();
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.BLACK);
            rectangle.setStrokeWidth(strokeWidth);
            double rectangleWidth = (calendarWidth / 6) - strokeWidth - spacingH; // Adjusting for 5 days
            rectangle.setWidth(rectangleWidth);
            double rectangleHeight = (calendarHeight / 1) - strokeWidth - spacingV; // Full height
            rectangle.setHeight(rectangleHeight);
            stackPane.getChildren().add(rectangle);

            // Ajout des événements au bon jour de la semaine
            List<EvenementEntity> calendarEvents = calendarEventMap.getOrDefault(currentDay, new ArrayList<>());
            createCalendarEvents(calendarEvents, rectangleHeight, rectangleWidth, stackPane);

            // Ajout du stackPane au bon emplacement dans le FlowPane
            int dayOfWeek = currentDay.getDayOfWeek().getValue(); // 1: Monday, 2: Tuesday, ..., 7: Sunday
            calendar.getChildren().add(dayOfWeek - 1, stackPane); // -1 to adjust index for Monday being the first day
        }
    }

    private Map<LocalDate, List<EvenementEntity>> getCalendarEventsWeek(ZonedDateTime date) {
        Map<LocalDate, List<EvenementEntity>> eventsMap = new HashMap<>();
        try {
            FileInputStream fin = new FileInputStream("C:\\projets\\S2\\javafx\\Planning_fx\\" +
                    "src\\main\\resources\\com\\ceri\\planningfx\\data\\planning\\uapv2200555.ics");
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);
            List<VEvent> events = calendar.getComponents("VEVENT");

            for (VEvent event : events) {
                EvenementEntity evenementEntity = new EvenementEntity();
                if(event.getDescription().isPresent()) {
                    evenementEntity.setSummary(event.getDescription().get().getValue());
                } else {
                    evenementEntity.setSummary("pas de description");
                }
                evenementEntity.setDateStartString(event.getDateTimeStart().get().getValue());
                evenementEntity.setDateEndString(event.getDateTimeEnd().get().getValue());
                evenementEntity.mapDate();

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

    private void createCalendarEvents(List<EvenementEntity> calendarEvents,
                                      double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox calendarEventsBox = new VBox();
        double spacing = 5; // Espacement entre les blocs

        // Tri des événements par heure de début
        calendarEvents.sort(Comparator.comparing(e -> e.getDateStart()));

        for (EvenementEntity event : calendarEvents) {
            String summary = event.getSummary();
            Text eventText = new Text(summary);

            // Récupérer les heures de début et de fin de l'événement
            int startHour = event.getDateStart().getHours();
            int startMinute = event.getDateStart().getMinutes();
            int endHour = event.getDateEnd().getHours();
            int endMinute = event.getDateEnd().getMinutes();

            // Formater les heures de début et de fin
            String startTime = String.format("%02d:%02d", startHour, startMinute);
            String endTime = String.format("%02d:%02d", endHour, endMinute);

            // Ajouter les heures de début et de fin à côté du résumé de l'événement
            String eventDescription = startTime + " - " + endTime + "\n" + summary;
            eventText.setText(eventDescription);

            // Créer un bloc pour contenir le texte de l'événement
            StackPane eventBlock = new StackPane();
            eventBlock.getChildren().add(eventText);
            eventBlock.setStyle("-fx-background-color: GRAY; -fx-padding: 2px;"); // Ajustez la taille des marges
            // Ajuster la taille du bloc en fonction de la taille du texte
            eventText.wrappingWidthProperty().bind(calendarEventsBox.widthProperty().subtract(10)); // Réduire la largeur de 10 pour la marge
            eventText.setStyle("-fx-font-size: 10px;"); // Taille de police ajustable

            // Ajouter l'événement à la grille
            placeEventInGrid(eventBlock, startHour, startMinute, endHour, endMinute, rectangleHeight);

            // Ajouter le bloc à la boîte des événements du calendrier
            calendarEventsBox.getChildren().add(eventBlock);
        }

        // Espacement entre les blocs
        calendarEventsBox.setSpacing(spacing);

        // Ajouter la boîte des événements directement au haut de la stackPane
        stackPane.getChildren().add(0, calendarEventsBox);
    }

    // Fonction pour placer un événement dans la grille
    private void placeEventInGrid(StackPane eventBlock, int startHour, int startMinute, int endHour, int endMinute, double rectangleHeight) {
        // Calculate position and size in grid pane
        double totalMinutes = Duration.between(LocalTime.of(8, 0), LocalTime.of(startHour, startMinute)).toMinutes();
        double eventStartY = (totalMinutes / 660) * rectangleHeight;

        double eventDuration = Duration.between(LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute)).toMinutes();
        double eventHeight = (eventDuration / 660) * rectangleHeight;

        // Set position and size of event in grid pane
        eventBlock.setLayoutY(eventStartY);
        eventBlock.setPrefHeight(eventHeight);

        // Add event to grid pane
        eventGridPane.getChildren().add(eventBlock);
    }

}

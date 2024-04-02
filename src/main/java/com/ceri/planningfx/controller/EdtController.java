package com.ceri.planningfx.controller;

import com.ceri.planningfx.metier.ParserIcs;
import com.ceri.planningfx.models.EvenementEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.text.DateFormatSymbols;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.time.format.DateTimeFormatter;
import java.util.*;


public class EdtController {

    int min = 24;
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


    private String[] monthNames;

    Map<LocalDate, List<EvenementEntity>> calendarEventMap;

    public void initialize() {

        ParserIcs parserIcs = new ParserIcs();
        calendarEventMap = parserIcs.parse();
        this.min = parserIcs.getMin();
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.FRENCH);
        monthNames = dfs.getMonths();

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
        month.setText(monthNames[dateFocus.getMonthValue() - 1]);

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

        //Map<LocalDate, List<EvenementEntity>> calendarEventMap = getCalendarEventsWeek(dateFocus);
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

            // Ajout d'un Text pour afficher la date complète au-dessus de chaque jour
            Text dateText = new Text(currentDay.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
            StackPane.setAlignment(dateText, Pos.TOP_CENTER); // Alignement au centre en haut
            stackPane.getChildren().add(dateText);

            // Ajout des événements au bon jour de la semaine
            List<EvenementEntity> calendarEvents = calendarEventMap.getOrDefault(currentDay, new ArrayList<>());
            createCalendarEvents(calendarEvents, rectangleHeight, rectangleWidth, stackPane);

            // Ajout du stackPane au bon emplacement dans le FlowPane
            int dayOfWeek = currentDay.getDayOfWeek().getValue(); // 1: Monday, 2: Tuesday, ..., 7: Sunday
            calendar.getChildren().add(dayOfWeek - 1, stackPane); // -1 to adjust index for Monday being the first day
        }
    }


    private void createCalendarEvents(List<EvenementEntity> calendarEvents,
                                      double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox calendarEventsBox = new VBox();
        double spacing = 5; // Espacement entre les blocs

        // Tri des événements par heure de début
        calendarEvents.sort(Comparator.comparing(e -> e.getDateStart()));
        for (EvenementEntity event : calendarEvents) {
            if (event.getDateStart().getHours() < 6) {
                continue;
            }
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
            LocalTime eventStartTime = LocalTime.of(startHour, startMinute);
            LocalTime eventEndTime = LocalTime.of(endHour, endMinute);
            LocalTime currentTime = LocalTime.now();
            if (currentTime.isAfter(eventStartTime) && currentTime.isBefore(eventEndTime)
            && event.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(today.toLocalDate())
            ) {
                eventBlock.setStyle("-fx-background-color: GREEN; -fx-padding: 2px;");
            } else {
                eventBlock.setStyle("-fx-background-color: GRAY; -fx-padding: 2px;");
            }
            if (event.getType() != null && event.getType().contains("Evaluation"))
            {
                eventBlock.setStyle("-fx-background-color: RED; -fx-padding: 2px;");
            }

            //eventBlock.setStyle("-fx-background-color: GRAY; -fx-padding: 2px;"); // Ajustez la taille des marges
            // Ajuster la taille du bloc en fonction de la taille du texte
            eventText.wrappingWidthProperty().bind(calendarEventsBox.widthProperty().subtract(10)); // Réduire la largeur de 10 pour la marge
            eventText.setStyle("-fx-font-size: 10px;"); // Taille de police ajustable

            // Ajouter le bloc à la boîte des événements du calendrier
            calendarEventsBox.getChildren().add(eventBlock);
        }

        // Espacement entre les blocs
        calendarEventsBox.setSpacing(spacing);
        if (calendarEvents.size() > 0)
            calendarEventsBox.setTranslateY((calendarEvents.get(0).getDateStart().
                    getHours() - min - 5) * 15);
        stackPane.getChildren().add(calendarEventsBox);
    }

}

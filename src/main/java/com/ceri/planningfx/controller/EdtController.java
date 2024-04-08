package com.ceri.planningfx.controller;

import com.ceri.planningfx.metier.ParserIcs;
import com.ceri.planningfx.models.EvenementEntity;
import com.ceri.planningfx.models.FiltresCollections;
import com.ceri.planningfx.utilities.AccountService;
import com.ceri.planningfx.utilities.HeaderManager;
import com.ceri.planningfx.utilities.MailService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

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
    private HBox optionButtons;

    @FXML
    private Button addEventButton;
    @FXML
    private Button reserveRoomButton;

    private MailService mailService;
    private String[] monthNames;

    public FiltresCollections filtresCollections;

    Map<LocalDate, List<EvenementEntity>> calendarEventMap;

    ParserIcs parserIcs;


    public void initialize() {

        HeaderManager.setEdtController(this);
        mailService = new MailService();

        parserIcs = new ParserIcs();
        calendarEventMap = parserIcs.parse();
        this.min = parserIcs.getMin();

        DateFormatSymbols dfs = new DateFormatSymbols(Locale.FRENCH);
        monthNames = dfs.getMonths();
        today = ZonedDateTime.now();

        dateFocus = ZonedDateTime.now();
        drawCalendar(calendar);
        this.filtresCollections = parserIcs.filtresCollections;
    }

    public void initOptionsButtons() {
        boolean canReserveRoom = ParserIcs.foleder.equals("salle") && AccountService.getConnectedAccount().get("role").equals("professeur");
        boolean canAddEvent = ParserIcs.foleder.equals("users") && AccountService.getConnectedAccount().get("username").equals(ParserIcs.file.split("\\.")[0]);
        addEventButton.setVisible(canAddEvent);
        reserveRoomButton.setVisible(canReserveRoom);
        optionButtons.setVisible(canReserveRoom || canAddEvent);
    }

    @FXML
    private void moveToNextWeek(ActionEvent event) {
        dateFocus = dateFocus.plusWeeks(1); // Avance d'une semaine
        drawCalendar(calendar);
    }

    @FXML
    private void moveToPreviousWeek(ActionEvent event) {
        dateFocus = dateFocus.minusWeeks(1); // Recule d'une semaine
        drawCalendar(calendar);
    }

    private void createDialogToCreateEvent(String title){
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(title);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);

        // DatePicker pour la date de l'événement
        DatePicker eventDateField = new DatePicker();
        eventDateField.setPromptText("Date");
        // controller de champ
        Pattern datePattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
        eventDateField.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!datePattern.matcher(newValue).matches()) {
                eventDateField.setStyle("-fx-border-color: red;");
            } else {
                eventDateField.setStyle("");
            }
        });

        // TextField pour l'heure de début et de fin
        Label label = new Label("Heure de début et de fin (format HH:MM-HH:MM)");
        TextField startEndTimeField = new TextField();
        startEndTimeField.setPromptText("HH:MM-HH:MM");
        //controller de champ
        Pattern timePattern = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$");
        startEndTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!timePattern.matcher(newValue).matches()) {
                startEndTimeField.setStyle("-fx-border-color: red;");
            } else {
                startEndTimeField.setStyle("");
            }
        });

        TextField DescriptionField = new TextField();
        DescriptionField.setPromptText("Description");
        // controller de champ
        DescriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                DescriptionField.setStyle("-fx-border-color: red;");
            } else {
                DescriptionField.setStyle("");
            }
        });

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button addButton = new Button("Ajouter");
        addButton.setOnAction(e -> {
            // Ajoutez ici le code pour ajouter l'événement
            if (timePattern.matcher(startEndTimeField.getText()).matches() && !DescriptionField.getText().isEmpty() && datePattern.matcher(eventDateField.getEditor().getText()).matches()) {
                // Ajoutez ici le code pour ajouter l'événement
                String retour = parserIcs.addEvent(String.valueOf(eventDateField.getValue()), startEndTimeField.getText(), DescriptionField.getText());
                if (retour.equals("ok")) {
                    dialogStage.close();
                    refresh();
                } else {
                    errorLabel.setText(retour);
                    errorLabel.setVisible(true);
                }
            } else {
                errorLabel.setText("Veuillez remplir tous les champs correctement");
                errorLabel.setVisible(true);
            }
        });

        vbox.getChildren().addAll(eventDateField, label, startEndTimeField, DescriptionField, addButton, errorLabel);

        Scene scene = new Scene(vbox, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.show();

    }



    @FXML
    private void add_event(ActionEvent event) {
        createDialogToCreateEvent("Ajouter un événement");
    }

    @FXML
    private void reserve_room(ActionEvent event) {
        createDialogToCreateEvent("Réserver une salle");
    }

    private void drawCalendar(FlowPane calendar) {
        this.initOptionsButtons();
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

            Text dateText = new Text(currentDay.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
            StackPane.setAlignment(dateText, Pos.TOP_CENTER); // Alignement au centre en haut
            stackPane.getChildren().add(dateText);

            List<EvenementEntity> calendarEvents = calendarEventMap.getOrDefault(currentDay, new ArrayList<>());
            createCalendarEvents(calendarEvents, rectangleHeight, rectangleWidth, stackPane);

            int dayOfWeek = currentDay.getDayOfWeek().getValue(); // 1: Monday, 2: Tuesday, ..., 7: Sunday
            calendar.getChildren().add(dayOfWeek - 1, stackPane); // -1 to adjust index for Monday being the first day
        }
    }

    private void createCalendarEvents(List<EvenementEntity> calendarEvents,
                                      double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox calendarEventsBox = new VBox();
        double spacing = 5; // Espacement entre les blocs

        calendarEvents.sort(Comparator.comparing(e -> e.getDateStart()));
        for (EvenementEntity event : calendarEvents) {
            if (event.getDateStart().getHours() < 6) {
                continue;
            }
            String summary = event.getSummary();
            Text eventText = new Text(summary);

            int startHour = event.getDateStart().getHours();
            int startMinute = event.getDateStart().getMinutes();
            int endHour = event.getDateEnd().getHours();
            int endMinute = event.getDateEnd().getMinutes();

            String startTime = String.format("%02d:%02d", startHour, startMinute);
            String endTime = String.format("%02d:%02d", endHour, endMinute);

            String eventDescription = startTime + " - " + endTime + "\n" + summary;
            eventText.setText(eventDescription);

            StackPane eventBlock = new StackPane();
            eventBlock.getChildren().add(eventText);
            LocalTime eventStartTime = LocalTime.of(startHour, startMinute);
            LocalTime eventEndTime = LocalTime.of(endHour, endMinute);
            LocalTime currentTime = LocalTime.now();
            if (currentTime.isAfter(eventStartTime) && currentTime.isBefore(eventEndTime)
                    && event.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(today.toLocalDate())) {
                eventBlock.setStyle("-fx-background-color: GREEN; -fx-padding: 2px;");
            } else {
                eventBlock.setStyle("-fx-background-color: GRAY; -fx-padding: 2px;");
            }
            if (event.getType() != null && event.getType().contains("Evaluation")) {
                eventBlock.setStyle("-fx-background-color: RED; -fx-padding: 2px;");
            }

            eventText.wrappingWidthProperty().bind(calendarEventsBox.widthProperty().subtract(10)); // Réduire la largeur de 10 pour la marge
            eventText.setStyle("-fx-font-size: 10px;"); // Taille de police ajustable

            eventBlock.setOnMouseClicked(event2 -> {
                if (event2.getClickCount() == 1) {
                    if (eventBlock.getStyle().contains("GRAY") && ParserIcs.foleder.equals("formations")) {
                        showEmailDialog();
                    }
                }
            });

            calendarEventsBox.getChildren().add(eventBlock);
        }

        calendarEventsBox.setSpacing(spacing);
        if (calendarEvents.size() > 0)
            //chercher la position de l'heure de debut de la premiere seance
            for (EvenementEntity event : calendarEvents) {
                if (event.getDateStart().getHours() > this.min) {
                    this.min = event.getDateStart().getHours();
                }
            }
        calendarEventsBox.setTranslateY((min) * 3);
        stackPane.getChildren().add(calendarEventsBox);
    }

    private void showEmailDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Envoyer un e-mail");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);

        TextField recipientEmailField = new TextField();
        recipientEmailField.setPromptText("Adresse e-mail");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Sujet");

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Corps du message");

        Button sendButton = new Button("Envoyer");
        sendButton.setOnAction(event -> {
            try {
                mailService.sendEmail(recipientEmailField.getText(), subjectField.getText(), messageArea.getText());
                dialogStage.close();
            } catch (MessagingException e) {
                e.printStackTrace(); // Vous pouvez gérer l'erreur ici en affichant un message d'erreur à l'utilisateur
            }
        });

        vbox.getChildren().addAll(recipientEmailField, subjectField, messageArea, sendButton);

        Scene scene = new Scene(vbox, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    public void refresh() {
        ParserIcs parserIcs = new ParserIcs();
        calendarEventMap = parserIcs.parse();
        this.min = parserIcs.getMin();
        drawCalendar(calendar);
        this.filtresCollections = parserIcs.filtresCollections;
        if (HeaderManager.getFiltresController() != null) HeaderManager.getFiltresController().refresh();
    }
    public void refreshWithFiltre(String filtre) {
        ParserIcs parserIcs = new ParserIcs();
        calendarEventMap = parserIcs.parseWithFiltre(filtre);
        this.min = parserIcs.getMin();
        drawCalendar(calendar);
    }
}

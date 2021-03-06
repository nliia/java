package ru.kpfu.itis.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.kpfu.itis.ViewLoader;
import ru.kpfu.itis.model.Booking;
import ru.kpfu.itis.model.Cottage;
import ru.kpfu.itis.model.User;
import ru.kpfu.itis.service.BookingService;
import ru.kpfu.itis.service.CottageService;
import ru.kpfu.itis.util.DateUtil;

import javax.annotation.PostConstruct;
import java.util.List;

import static ru.kpfu.itis.util.AlertShower.showAlert;

/**
 * @author Liia
 */
public class MainController {

    @Autowired
    private CottageService cottageService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ViewLoader viewLoader;

    @FXML
    private TableView<Cottage> cottageTable;
    @FXML
    private TableView<Booking> bookingTable;

    @FXML
    private Label places;
    @FXML
    private Label parking;
    @FXML
    private Label playground;
    @FXML
    private Label pavilion;
    @FXML
    private Label animalpermit;

    @FXML
    private Button addCotBtn;
    @FXML
    private Button editCotBtn;
    @FXML
    private Button deleteCotBtn;

    @FXML
    private Label arrivalDate;
    @FXML
    private Label departureDate;
    @FXML
    private Label phoneNumber;

    @FXML
    private Tab bookingsTabPane;
    @FXML
    private Button addBookBtn;

    private ObservableList<Cottage> cottageObservableList;
    private ObservableList<Booking> bookingObservableList;

    public MainController() {
    }

    @FXML
    public void initialize() {
    }

    @PostConstruct
    public void init() {
        List<Cottage> cottages = cottageService.getAll();
        cottageObservableList = FXCollections.observableArrayList(cottages);

        TableColumn<Cottage, Long> idColumn = new TableColumn<>("Number");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Cottage, Integer> nameColumn = new TableColumn<>("Cost(per day)");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));


        cottageTable.getColumns().setAll(idColumn, nameColumn);
        showCottageDetails(null);
        cottageTable.setItems(cottageObservableList);
        cottageTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCottageDetails(newValue));

        //same for bookings

        List<Booking> bookings = bookingService.getAll();
        bookingObservableList = FXCollections.observableArrayList(bookings);

        TableColumn<Booking, String> personNameBookColumn = new TableColumn<>("Person name");
        personNameBookColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Booking, Long> cottageNumberBookColumn = new TableColumn<>("Cottage number");
        cottageNumberBookColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getCottage().getId()));


        bookingTable.getColumns().setAll(personNameBookColumn, cottageNumberBookColumn);
        showCottageDetails(null);
        bookingTable.setItems(bookingObservableList);
        bookingTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookingDetails(newValue));


    }

    @FXML
    private void deleteCottage() {
        int selectedIndex = cottageTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            cottageService.delete(cottageTable.getSelectionModel().getSelectedItem().getId());
            cottageTable.getItems().remove(selectedIndex);
        } else {
            showAlert("Please select a cottage in the cottageTable");
        }
    }

    @FXML
    private void deleteBooking() {
        int selectedIndex = bookingTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            bookingService.delete(bookingTable.getSelectionModel().getSelectedItem().getId());
            bookingTable.getItems().remove(selectedIndex);
        } else {
            showAlert("Please select a booking in the bookingTable");
        }
    }

    @FXML
    private void showEditCottage() {
        int selectedIndex = cottageTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewLoader.showCottageEdit(cottageTable.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void showEditBooking() {
        int selectedIndex = bookingTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Long id = bookingTable.getSelectionModel().getSelectedItem().getId();
            viewLoader.showBookingEdit(bookingTable.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void showAddCottage() {
        viewLoader.showCottageAdd();
    }

    @FXML
    private void showAddBooking() {
        int selectedIndex = cottageTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            viewLoader.showBookingAdd(cottageTable.getSelectionModel().getSelectedItem());
        }else{
            showAlert("Please, choose a cottage");
        }
    }

    @FXML
    private void logout() {
        SecurityContextHolder.clearContext();
        Stage stage = (Stage) bookingTable.getScene().getWindow();
        stage.getScene().setRoot(new Pane());
        stage.close();
        viewLoader.showAuth();
    }

    public void setVisibleBtns() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole().equals("USER")) {
            addCotBtn.setVisible(false);
            editCotBtn.setVisible(false);
            deleteCotBtn.setVisible(false);
            bookingsTabPane.setDisable(true);
        }
        if(user.getRole().equals("ADMIN")){
            addBookBtn.setVisible(false);
        }
    }

    public void refresh() {
        init();
    }

    protected void showCottageDetails(Cottage cottage) {

        if (cottage != null) {
            places.setText(String.valueOf(cottage.getPlaceAmount()));
            if (cottage.getPavilion())
                pavilion.setText("Yes");
            else
                pavilion.setText("No");

            if (cottage.getPlayground())
                playground.setText("Yes");
            else
                playground.setText("No");

            if (cottage.getParking())
                parking.setText("Yes");
            else
                parking.setText("No");

            if (cottage.getAnimalsPermission())
                animalpermit.setText("Yes");
            else
                animalpermit.setText("No");
        } else {
            places.setText("");
            parking.setText("");
            pavilion.setText("");
            playground.setText("");
            animalpermit.setText("");
        }
    }

    protected void showBookingDetails(Booking booking) {

        if (booking != null) {
            arrivalDate.setText(DateUtil.format(booking.getArriveDate()));
            departureDate.setText(DateUtil.format(booking.getDepartureDate()));
            phoneNumber.setText(booking.getPhoneNumber());

        } else {

            arrivalDate.setText("");
            departureDate.setText("");
            phoneNumber.setText("");
        }
    }
}

package RMS.ui.addOrder;

import RMS.dataBase.DataBaseHandler;
import RMS.ui.main.MainController;
import RMS.ui.manageMenu.ManageMenuController;
import RMS.ui.manageOrder.ManageOrderController;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddOrderController implements Initializable {
    ObservableList<Menu> list = FXCollections.observableArrayList();
    ObservableList<AddItemToOrder> selectedItem = FXCollections.observableArrayList();
    double totalAddValue = 0.0;
    int getIdGlob = 0;
    private Boolean isInEditMode = Boolean.FALSE;
    String loginId;
    @FXML
    private AnchorPane rootPane;

    @FXML
    private TableView<AddItemToOrder> orderTableView;

    @FXML
    private TableColumn<AddItemToOrder, String> oderIdCol;

    @FXML
    private TableColumn<AddItemToOrder, String> oderNameCol;

    @FXML
    private TableColumn<AddItemToOrder, String> orderQuantityCol;

    @FXML
    private TableColumn<AddItemToOrder, String> orderPriceCol;

    @FXML
    private Label showTotalCharge;

    @FXML
    private Label staffName;

    @FXML
    private TextField quantityField;

    @FXML
    private JFXButton addMenuItem;

    @FXML
    private JFXButton deleteItem;

    @FXML
    private JFXButton addOrder;

    @FXML
    private TableView<Menu> menuItemTableView;

    @FXML
    private TableColumn<Menu, String> idCol;

    @FXML
    private TableColumn<Menu, String> nameCol;

    @FXML
    private TableColumn<Menu, String> priceCol;

    @FXML
    private TableColumn<Menu, String> typeCol;

    @FXML
    private JFXButton showAllMenu;

    @FXML
    private JFXButton showMainMenu;

    @FXML
    private JFXButton showDrinkMenu;

    @FXML
    private JFXButton showAlcoholMenu;

    @FXML
    private JFXButton showDessertMenu;

    DataBaseHandler dataBaseHandler;
    static String FirstNameG;
    static String LastNameG;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCol();
        loadData("menu_type = 'Drink' or menu_type ='Main' or menu_type ='Dessert' or menu_type = 'Alcohol'");
        initOrderCol();
        quantityField.setText("1");
        dataBaseHandler = DataBaseHandler.getInstance();
        staffName.setText(FirstNameG + " " + LastNameG);


    }

    private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    private void loadData(String menuType) {
        list.clear();
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM menu_item WHERE " + menuType;
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String price = rs.getString("price");
                String type = rs.getString("menu_type");

                list.add(new Menu(id, name, price, type));
            }

        } catch (SQLException e) {
            Logger.getLogger(AddOrderController.class.getName()).log(Level.SEVERE, null, e);
        }

        menuItemTableView.getItems().setAll(list);
    }

    public void manageInfo(String text) {
        DataBaseHandler handler = DataBaseHandler.getInstance();
        String qu = "SELECT * FROM `staff` WHERE id =" + "'" + text + "'";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String FirstName = rs.getString("firstName");
                String LastName = rs.getString("lastName");

                FirstNameG = FirstName;
                LastNameG = LastName;
            }
        } catch (SQLException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    public static class Menu {
        private final SimpleStringProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty price;
        private final SimpleStringProperty type;

        public Menu(String id, String name, String price, String type) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.price = new SimpleStringProperty(price);
            this.type = new SimpleStringProperty(type);
        }

        public String getId() {
            return id.get();
        }

        public String getName() {
            return name.get();
        }

        public String getPrice() {
            return price.get();
        }

        public String getType() {
            return type.get();
        }
    }

    private void initOrderCol() {
        oderIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        oderNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        orderPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        orderQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void loadOrderData() {
        if (menuItemTableView.getSelectionModel().getSelectedIndex() != -1) {
            orderTableView.refresh();
            String initID = menuItemTableView.getSelectionModel().getSelectedItem().getId();
            String initName = menuItemTableView.getSelectionModel().getSelectedItem().getName();
            String initPrice = menuItemTableView.getSelectionModel().getSelectedItem().getPrice();
            String initQuantity = quantityField.getText();

            selectedItem.add(new AddItemToOrder(initID, initName, initPrice, initQuantity));
            orderTableView.getItems().setAll(selectedItem);
            totalAddValue += Double.parseDouble(quantityField.getText()) * Double.parseDouble(menuItemTableView.getSelectionModel().getSelectedItem().getPrice());
            showTotalCharge.setText("" + totalAddValue);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("Warning");
            alert.setContentText("Please select an item");
            alert.showAndWait();
        }
    }

    @FXML
    void deleteMenuItem(ActionEvent event) {
        double multiPrice = 0.0;
        AddItemToOrder myOrder = new AddItemToOrder();
        myOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (orderTableView.getSelectionModel().getSelectedIndex() != -1) {
            selectedItem.remove(orderTableView.getSelectionModel().getSelectedIndex());
            orderTableView.getSelectionModel().clearSelection();
            orderTableView.getItems().clear();
            orderTableView.getItems().addAll(selectedItem);
            multiPrice = Double.parseDouble(myOrder.getQuantity()) * Double.parseDouble(myOrder.getPrice());
            totalAddValue = totalAddValue - multiPrice;
            if (totalAddValue < 0) {
                totalAddValue = 0.0;
            }
            showTotalCharge.setText("" + totalAddValue);
        }
    }

    public static class AddItemToOrder {
        private SimpleStringProperty id;
        private SimpleStringProperty name;
        private SimpleStringProperty price;
        private SimpleStringProperty quantity;

        public AddItemToOrder() {
        }

        public AddItemToOrder(String id, String name, String price, String quantity) {
            this.id = new SimpleStringProperty(id);
            this.name = new SimpleStringProperty(name);
            this.price = new SimpleStringProperty(price);
            this.quantity = new SimpleStringProperty(quantity);
        }

        public String getId() {
            return id.get();
        }

        public String getName() {
            return name.get();
        }

        public String getPrice() {
            return price.get();
        }

        public String getQuantity() {
            return quantity.get();
        }
    }

    @FXML
    void addNewMenuItem(ActionEvent event) {
        loadOrderData();
    }

    public void addAllOrder() {
        String id_order = "";
        AddItemToOrder order = new AddItemToOrder();
        List<List<String>> arrayList = new ArrayList<>();
        for (int i = 0; i < orderTableView.getItems().size(); i++) {
            order = orderTableView.getItems().get(i);
            arrayList.add(new ArrayList<>());
            arrayList.get(i).add(order.getId());
            arrayList.get(i).add(order.getName());
            arrayList.get(i).add(order.getQuantity());
            arrayList.get(i).add(order.getPrice());
        }
        String id_qu = "SELECT id FROM order_table ORDER BY id DESC LIMIT 1";
        ResultSet rs = dataBaseHandler.execQuery(id_qu);
        try {
            while (rs.next()) {
                id_order = rs.getString("id");
            }
        } catch (SQLException e) {
            Logger.getLogger(AddOrderController.class.getName()).log(Level.SEVERE, null, e);
        }

        for (int i = 0; i < orderTableView.getItems().size(); i++) {
            String qu = "INSERT INTO edit_order_table (id, name, quantity, price, orderId) VALUE (" +
                    "'" + arrayList.get(i).get(0) + "'," +
                    "'" + arrayList.get(i).get(1) + "'," +
                    "'" + arrayList.get(i).get(2) + "'," +
                    "'" + arrayList.get(i).get(3) + "'," +
                    "'" + id_order + "'" +
                    ")";
            dataBaseHandler.execAction(qu);
        }
    }

    private void handelEditMenuItem() {
        if (orderTableView.getItems().size() != 0) {
            Alert alertx = new Alert(Alert.AlertType.CONFIRMATION);
            alertx.setHeaderText(null);
            alertx.setTitle("Updating order");
            alertx.setContentText("Are you sure you want to " + "Update the order" + " ?");
            Optional<ButtonType> answer = alertx.showAndWait();
            if (answer.get() == ButtonType.OK) {
                    AddItemToOrder order = new AddItemToOrder();
                    List<List<String>> arrayList = new ArrayList<>();
                    for (int i = 0; i < orderTableView.getItems().size(); i++) {
                        order = orderTableView.getItems().get(i);
                        arrayList.add(new ArrayList<>());
                        arrayList.get(i).add(order.getId());
                        arrayList.get(i).add(order.getName());
                        arrayList.get(i).add(order.getQuantity());
                        arrayList.get(i).add(order.getPrice());
                    }
                    String delete = "DELETE FROM edit_order_table WHERE orderId =" + getIdGlob;
                    dataBaseHandler.execAction(delete);
                    for (int i = 0; i < orderTableView.getItems().size(); i++) {
                        String qu = "INSERT INTO edit_order_table (id, name, quantity, price, orderId) VALUE (" +
                                "'" + arrayList.get(i).get(0) + "'," +
                                "'" + arrayList.get(i).get(1) + "'," +
                                "'" + arrayList.get(i).get(2) + "'," +
                                "'" + arrayList.get(i).get(3) + "'," +
                                "'" + getIdGlob + "'" +
                                ")";
                        dataBaseHandler.execAction(qu);
                    }
                String update = "UPDATE order_table SET totalPrice = " + showTotalCharge.getText() + " WHERE id = " + getIdGlob;
                dataBaseHandler.execAction(update);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Order Updated");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Failed");
                    alert.showAndWait();
                }
            }
        }

    @FXML
    void addOrder(ActionEvent event) {
        if (isInEditMode) {
            handelEditMenuItem();
            return;
        }
        if (orderTableView.getItems().size() != 0) {

            Alert alertx = new Alert(Alert.AlertType.CONFIRMATION);
            alertx.setHeaderText(null);
            alertx.setTitle("Adding order");
            alertx.setContentText("Are you sure you want to " + "check out" + " ?");
            Optional<ButtonType> answer = alertx.showAndWait();
            if (answer.get() == ButtonType.OK) {
                String qu = "INSERT INTO order_table (staffFirstName, staffLastName, totalPrice) VALUE (" +
                        "'" + FirstNameG + "'," +
                        "'" + LastNameG + "'," +
                        "'" + showTotalCharge.getText() + "'" +
                        ")";
                if (dataBaseHandler.execAction(qu)) {
                    addAllOrder();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Order added");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Failed");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Order Failed");
                alert.setContentText("Order Process Cancelled");

            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("No item in the order list ");
            alert.showAndWait();
        }
    }

    @FXML
    void showAlcoholType(ActionEvent event) {
        loadData("menu_type = 'Alcohol'");
    }

    @FXML
    void showAllType(ActionEvent event) {
        loadData("menu_type = 'Drink' or menu_type ='Main' or menu_type ='Dessert' or menu_type = 'Alcohol'");
    }

    @FXML
    void showDessertType(ActionEvent event) {
        loadData("menu_type ='Dessert'");
    }

    @FXML
    void showDrinkType(ActionEvent event) {
        loadData("menu_type = 'Drink'");
    }

    @FXML
    void showMainType(ActionEvent event) {
        loadData("menu_type ='Main'");
    }


    public void inflateUI(ManageOrderController.Order order) {
            DataBaseHandler handler = DataBaseHandler.getInstance();
            String qu = "SELECT * FROM `edit_order_table` WHERE orderId =" + order.getId();
            ResultSet rs = handler.execQuery(qu);
            try {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String price = rs.getString("price");
                    String quantity = rs.getString("quantity");
                    selectedItem.add(new AddItemToOrder(id, name, price, quantity));
                }
            } catch (SQLException e) {
                Logger.getLogger(AddOrderController.class.getName()).log(Level.SEVERE, null, e);
            }
            totalAddValue = Double.parseDouble(order.getTotalPrice());
            showTotalCharge.setText(order.getTotalPrice());
            orderTableView.getItems().setAll(selectedItem);
            isInEditMode = Boolean.TRUE;
            getIdGlob = Integer.parseInt(order.getId());
        }
}


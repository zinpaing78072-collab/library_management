package com.example.controller;

import com.example.model.Member;
import com.example.service.MemberService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class MemberController {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    @FXML private TextField txtSearchByName;
    @FXML private TableView<Member> tblMember;
    @FXML private TableColumn<Member, Number> colMID;
    @FXML private TableColumn<Member, String> colName;
    @FXML private TableColumn<Member, String> colNRC;
    @FXML private TableColumn<Member, String> colDOB;
    @FXML private TableColumn<Member, String> colGender;
    @FXML private TableColumn<Member, String> colEmail;
    @FXML private TableColumn<Member, String> colPhone;
    @FXML private TableColumn<Member, String> colAddress;
    @FXML private TableColumn<Member, String> colRegisterDate;
    @FXML private TableColumn<Member, String> colExpiredDate;
    @FXML private TableColumn<Member, Number> colBCount;

    @FXML private TextField txtName;
    @FXML private TextField txtNRCNo;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhoneNo;
    @FXML private TextArea txtAddress;
    @FXML private DatePicker dateDOB;
    @FXML private DatePicker dateRegister;
    @FXML private DatePicker dateExpire;
    @FXML private RadioButton rbtMale;
    @FXML private RadioButton rbtFemale;

    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final ToggleGroup genderGroup = new ToggleGroup();
    private MemberService memberService;

    public void setMemberService(MemberService memberService) {
        this.memberService = Objects.requireNonNull(memberService, "memberService");
    }

    @FXML
    private void initialize() {
        configureColumns();
        tblMember.setItems(members);
        tblMember.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldMember, selectedMember) -> {
                    if (selectedMember != null) {
                        populateForm(selectedMember);
                    }
                });
        txtSearchByName.textProperty().addListener((observable, oldText, newText) -> searchMembers());
        loadMembers();
    }

    private void configureColumns() {
        colMID.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getMemberId()));
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colNRC.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNrc()));
        colDOB.setCellValueFactory(cell -> new SimpleObjectProperty<>(displayDate(cell.getValue().getDateOfBirth())));
        colGender.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGender()));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        colPhone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhoneNo()));
        colAddress.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAddress()));
        colRegisterDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(displayDate(cell.getValue().getRegisterDate())));
        colExpiredDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(displayDate(cell.getValue().getExpiredDate())));
        colBCount.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getBookCount()));
    }

    private void loadMembers() {
        try {
            members.setAll(memberService.getMembers());
        } catch (SQLException exception) {
            showError("Could not load members", exception.getMessage());
        }
    }

    private void searchMembers() {
        try {
            members.setAll(memberService.searchMembers(txtSearchByName.getText()));
        } catch (SQLException exception) {
            showError("Could not search members", exception.getMessage());
        }
    }

    private void populateForm(Member member) {
        txtName.setText(member.getName());
        txtNRCNo.setText(member.getNrc());
        dateDOB.setValue(member.getDateOfBirth());
        if ("Male".equalsIgnoreCase(member.getGender())) {
            genderGroup.selectToggle(rbtMale);
        } else if ("Female".equalsIgnoreCase(member.getGender())) {
            genderGroup.selectToggle(rbtFemale);
        } else {
            genderGroup.selectToggle(null);
        }
        txtEmail.setText(member.getEmail());
        txtPhoneNo.setText(member.getPhoneNo());
        txtAddress.setText(member.getAddress());
        dateRegister.setValue(member.getRegisterDate());
        dateExpire.setValue(member.getExpiredDate());
    }

    @FXML
    private void clickRegister() {
        try {
            Member selected = tblMember.getSelectionModel().getSelectedItem();
            Member member = formToMember(selected == null ? 0 : selected.getMemberId(),
                    selected == null ? 0 : selected.getBookCount());
            memberService.registerMember(member);
            clearForm();
            searchMembers();
            showInformation("Member registered.");
        } catch (IllegalArgumentException | SQLException exception) {
            showError("Could not register the member", exception.getMessage());
        }
    }

    @FXML
    private void clickClear() {
        clearForm();
    }

    private void clearForm() {
        tblMember.getSelectionModel().clearSelection();
        txtName.clear();
        txtNRCNo.clear();
        dateDOB.setValue(null);
        genderGroup.selectToggle(null);
        txtEmail.clear();
        txtPhoneNo.clear();
        txtAddress.clear();
        dateRegister.setValue(null);
        dateExpire.setValue(null);
    }

    private Member formToMember(int memberId, int bookCount) {
        return new Member(memberId, text(txtName), text(txtNRCNo), dateDOB.getValue(), selectedGender(),
                text(txtEmail), text(txtPhoneNo), text(txtAddress), dateRegister.getValue(), dateExpire.getValue(),
                bookCount);
    }

    private String selectedGender() {
        if (rbtMale.isSelected()) {
            return "Male";
        }
        if (rbtFemale.isSelected()) {
            return "Female";
        }
        throw new IllegalArgumentException("Select a gender.");
    }

    private static String text(TextField field) {
        return field.getText().trim();
    }

    private static String text(TextArea field) {
        return field.getText().trim();
    }

    private static String displayDate(LocalDate date) {
        return date == null ? "" : date.format(DISPLAY_DATE);
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Member management");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String message, String detail) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Member management");
        alert.setHeaderText(null);
        if (detail != null) {
            alert.setContentText(detail);
        }
        alert.showAndWait();
    }
}


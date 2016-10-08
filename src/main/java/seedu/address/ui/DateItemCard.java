package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import seedu.address.model.item.ReadOnlyDateItem;

public class DateItemCard extends UiPart{

    private static final String FXML = "DateItemListCard.fxml";

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label tags;

    private ReadOnlyDateItem dateItem;
    private int displayedIndex;

    public DateItemCard(){

    }

    public static DateItemCard load(ReadOnlyDateItem dateItem, int displayedIndex){
        DateItemCard card = new DateItemCard();
        card.dateItem = dateItem;
        card.displayedIndex = displayedIndex;
        return UiPartLoader.loadUiPart(card);
    }

    @FXML
    public void initialize() {
        name.setText(dateItem.getName().name);
        phone.setText(dateItem.getDateToOrderBy().toString());
        id.setText(displayedIndex + "");
    }

    public HBox getLayout() {
        return cardPane;
    }

    @Override
    public void setNode(Node node) {
        cardPane = (HBox)node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}

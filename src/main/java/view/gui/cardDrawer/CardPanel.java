package view.gui.cardDrawer;

import model.card.ICard;
import model.deckOfCards.IDeckOfCards;
import model.deckOfCards.impl.DeckOfCards;
import view.gui.GUIConstants;
import view.gui.specialViews.BackgroundPanel;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

/**
 * If everything works right this class was
 * created by Konraifen88 on 30.09.2015.
 * If it doesn't work I don't know who the hell wrote it.
 */
public class CardPanel extends BackgroundPanel {

    private List<DrawnCard> allDrawnCards;

    private IDeckOfCards allCards;

    private IDeckOfCards chosenCards;

    private SpringLayout layout;

    public CardPanel() {
        allCards = new DeckOfCards();
        chosenCards = new DeckOfCards();
        allDrawnCards = new LinkedList<>();
        layout = new SpringLayout();
        this.setLayout(layout);
        this.setVisible(true);
    }

    @Override
    public SpringLayout getLayout() {
        return layout;
    }

    private void addCard(ICard card) {
        DrawnCard drawnCard = new DrawnCard(card);
        allDrawnCards.add(drawnCard);
        this.add(drawnCard);
        updateView();
    }

    public List<DrawnCard> setMultipleCards(IDeckOfCards cards) {
        allDrawnCards = new LinkedList<>();
        cards.forEach(this::addCard);
        return allDrawnCards;
    }

    private void updateView() {
        applyDeferral(GUIConstants.CARD_ADJUSTMENT);
    }


    private void applyDeferral(double deferral) {
        for (JComponent card : allDrawnCards) {
            setDeferral(card, deferral);
        }
    }

    private void setDeferral(JComponent card, double deferral) {
        if (card == allDrawnCards.get(0)) {
            layout.putConstraint(SpringLayout.WEST, card, GUIConstants.CARD_POSITION_LEFT_BORDER, SpringLayout.WEST,
                    this);
            layout.putConstraint(SpringLayout.NORTH, card, GUIConstants.CARD_POSITION_TOP_BORDER, SpringLayout.NORTH,
                    this);
        } else {
            @SuppressWarnings("SuspiciousMethodCalls") int pos = allDrawnCards.indexOf(card);
            layout.putConstraint(SpringLayout.WEST, card, (int) deferral, SpringLayout.EAST,
                    allDrawnCards.get(pos - 1));
            layout.putConstraint(SpringLayout.NORTH, card, GUIConstants.CARD_POSITION_TOP_BORDER, SpringLayout.NORTH,
                    this);
        }
    }

    public IDeckOfCards getChosenCards() {
        return getAllChosenCards();
    }


    private IDeckOfCards getAllChosenCards() {
        chosenCards = new DeckOfCards();
        allDrawnCards.forEach(this::addCardIfChosen);
        return chosenCards;
    }

    private void addCardIfChosen(DrawnCard drawnCard) {
        if (drawnCard.isChosen()) {
            chosenCards.add(drawnCard.getCard());
        }
    }

    protected List<DrawnCard> getAllDrawnCards() {
        return allDrawnCards;
    }
}

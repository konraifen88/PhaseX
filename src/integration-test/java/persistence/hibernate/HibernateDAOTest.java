package persistence.hibernate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.UIController;
import controller.impl.ActorController;
import model.card.CardColor;
import model.card.CardValue;
import model.card.ICard;
import model.card.impl.Card;
import model.card.impl.CardDeserializer;
import model.deck.IDeckOfCards;
import model.deck.impl.DeckOfCards;
import model.player.IPlayer;
import model.player.impl.Player;
import model.stack.ICardStack;
import model.stack.impl.ColorStack;
import model.stack.impl.PairStack;
import model.stack.impl.StreetStack;
import model.stack.json.GsonStackDeserializer;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.CardCreator;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.*;

/**
 * Created by tabuechn on 05.04.2016.
 */
public class HibernateDAOTest {

    private String klaus;
    private String herbert;
    private IDeckOfCards deck;
    private HibernateControllerData ctrlData;
    private Gson gson;
    private HibernateDAO hdao;

    @Before
    public void setUp() throws Exception {
        klaus = "Klaus";
        herbert = "Herbert";
        deck = CardCreator.giveDeckOfCards();
        ctrlData = new HibernateControllerData();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ICard.class, new CardDeserializer());
        gsonBuilder.registerTypeAdapter(ICardStack.class, new GsonStackDeserializer());
        gson = gsonBuilder.create();
        hdao = new HibernateDAO();
    }

    @After
    public void tearDown() throws Exception {
        deleteTest();
    }

    @Test
    public void deleteTest() throws Exception {
        Session session = HibernateUtil.getInstance().getCurrentSession();
        Transaction trans = session.beginTransaction();
        Criteria criteria = session.createCriteria(HibernateControllerData.class);
        List testList = criteria.list();
        int i = 0;
        for (Object o : testList) {
            session.delete(o);
            i++;
        }
        System.out.println("number of Objects in DB: " +i);
        trans.commit();
    }

    @Test
    public void saveAndDeleteStacks() throws Exception {
        ActorController ctrl = new ActorController();
        ctrl.startGame(herbert);
        ctrl.setSecondPlayerName(klaus);

        List<ICardStack> testStacks = new LinkedList<>();

        ICardStack pairStack = new PairStack(getPairStack(4));
        ICardStack colorStack = new ColorStack(getColorStack(CardColor.RED));
        ICardStack streetStack = new StreetStack(getStreetStack());

        testStacks.add(pairStack);
        testStacks.add(colorStack);
        testStacks.add(streetStack);
        ctrl.setAllStacks(testStacks);

        hdao.saveGame(ctrl);
        //ljkhgfdghj

        Session session = HibernateUtil.getInstance().getCurrentSession();
        Transaction trans = session.beginTransaction();
        Criteria criteria = session.createCriteria(HibernateControllerData.class);
        List testlist = criteria.list();
        int i = 0;
        for (Object o : testlist) {
            HibernateControllerData cd = (HibernateControllerData) o;
            assertFalse(cd.getStack1().isEmpty());
            assertFalse(cd.getStack2().isEmpty());

            ICardStack stack1res = gson.fromJson(cd.getStack1(), ICardStack.class);
            assertTrue(stack1res instanceof PairStack);
            assertEquals(pairStack.getList(), stack1res.getList());

            ICardStack stack2res = gson.fromJson(cd.getStack2(), ICardStack.class);
            assertTrue(stack2res instanceof ColorStack);
            assertEquals(colorStack.getList(), stack2res.getList());

            ICardStack stack3res = gson.fromJson(cd.getStack3(), ICardStack.class);
            assertTrue(stack3res instanceof StreetStack);
            assertEquals(streetStack.getList(), stack3res.getList());


            session.delete(o);
            i++;
        }
        trans.commit();
        assertEquals(i, 1);
    }


    @Test
    public void saveAndLoadGame() throws Exception {
        ActorController ctrl = new ActorController();
        IPlayer herberObject = new Player(0);
        herberObject.setName(herbert);
        herberObject.setDeckOfCards(getPairStack(2));
        ctrl.startGame(herberObject.getPlayerName());
        ctrl.setSecondPlayerName(klaus);
        ctrl.setPlayer1(herberObject);

        List<ICardStack> testStacks = new LinkedList<>();

        ICardStack pairStack = new PairStack(getPairStack(4));
        ICardStack colorStack = new ColorStack(getColorStack(CardColor.RED));
        ICardStack streetStack = new StreetStack(getStreetStack());

        testStacks.add(pairStack);
        testStacks.add(colorStack);
        testStacks.add(streetStack);
        ctrl.setAllStacks(testStacks);

        hdao.saveGame(ctrl);

        UIController loadedController = hdao.loadGame(herberObject);
        assertEquals(ctrl.getStatusMessage(), loadedController.getStatusMessage());
        assertEquals(ctrl.getRoundState().toString(), loadedController.getRoundState().toString());
        assertEquals(testStacks.size(), loadedController.getAllStacks().size());
        assertEquals(ctrl.getPlayersArray()[0].getDeckOfCards(), loadedController.getPlayersArray()[0].getDeckOfCards());
        assertEquals(ctrl.getPlayersArray()[1].getDeckOfCards(), loadedController.getPlayersArray()[1].getDeckOfCards());

        assertEquals(herberObject.getDeckOfCards(), loadedController.getPlayersArray()[0].getDeckOfCards());
        for (int i = 0; i < testStacks.size(); ++i) {
            assertEquals(ctrl.getAllStacks().get(i).getList(), loadedController.getAllStacks().get(i).getList());
        }
    }


    @Test
    public void saveAndDeletePlayers() throws Exception {
        ActorController ctrl = new ActorController();
        ctrl.startGame(herbert);
        ctrl.setSecondPlayerName(klaus);


        hdao.saveGame(ctrl);


        Session session = HibernateUtil.getInstance().getCurrentSession();
        Transaction trans = session.beginTransaction();

        Criteria criteria = session.createCriteria(HibernateControllerData.class);
        List testlist = criteria.list();
        int i = 0;
        for (Object o : testlist) {
            HibernateControllerData cd = (HibernateControllerData) o;
            assertEquals(herbert, cd.getPlayer1().getPlayerName());
            DeckOfCards herbertDeck = gson.fromJson(cd.getPlayer1Pile(), DeckOfCards.class);
            assertEquals(ctrl.getPlayersArray()[0].getDeckOfCards(), herbertDeck);
            assertEquals(ctrl.getPlayersArray()[0].getPhase().toString(), cd.getPlayer1PhaseString());

            assertEquals(klaus, cd.getPlayer2().getPlayerName());
            DeckOfCards klausDeck = gson.fromJson(cd.getPlayer2Pile(), DeckOfCards.class);
            assertEquals(ctrl.getPlayersArray()[1].getDeckOfCards(), klausDeck);
            assertEquals(ctrl.getPlayersArray()[1].getPhase().toString(), cd.getPlayer2PhaseString());

            assertEquals(ctrl.getCurrentPlayerIndex(),cd.getCurrentPlayerIndex());

            session.delete(o);
            i++;
        }
        trans.commit();
        assertEquals(i, 1);
    }

    private IDeckOfCards getColorStack(CardColor color) {
        IDeckOfCards retDeck = new DeckOfCards();
        for (int i = 1; i < 9; i++) {
            ICard card = new Card(CardValue.byOrdinal(i), color);
            retDeck.add(card);
        }
        return retDeck;
    }

    private IDeckOfCards getStreetStack() {
        IDeckOfCards retDeck = new DeckOfCards();
        for (int i = 1; i < 6; i++) {
            ICard card = new Card(CardValue.byOrdinal(i), CardColor.BLUE);
            retDeck.add(card);
        }
        return retDeck;
    }

    private IDeckOfCards getPairStack(int pairValue) {
        IDeckOfCards pairStack = new DeckOfCards();
        for (int i = 0; i < 4; i++) {
            ICard card = new Card(CardValue.byOrdinal(pairValue), CardColor.GREEN);
            pairStack.add(card);
        }
        return pairStack;
    }
}
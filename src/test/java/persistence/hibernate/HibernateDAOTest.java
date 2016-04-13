package persistence.hibernate;

import com.google.gson.Gson;
import model.deck.IDeckOfCards;
import model.player.impl.Player;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import util.CardCreator;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by tabuechn on 05.04.2016.
 */
public class HibernateDAOTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    @Ignore
    public void saveAndDeleteObject() throws Exception {
        ControllerData test = new ControllerData();
        String klaus = "Klaus";
        String herbert = "Herbert";
        IDeckOfCards deck = CardCreator.giveDeckOfCards();
        Gson gson = new Gson();
        String testString = gson.toJson(deck);
        System.out.println(testString);
        System.out.println(testString.length());
        Player herbertObject = new Player(0);
        herbertObject.setName(herbert);
        test.setPlayer1(herbertObject);
        test.setPlayer1Pile(testString);
        Player klausObject= new Player(1);
        klausObject.setName(klaus);
        test.setPlayer2(klausObject);
        Session session = HibernateUtil.getInstance().getCurrentSession();
        Transaction trans = session.beginTransaction();
        session.save(test);
        trans.commit();

        session = HibernateUtil.getInstance().getCurrentSession();
        trans = session.beginTransaction();

        Criteria criteria = session.createCriteria(ControllerData.class);
        List testlist =criteria.list();
        int i = 0;
        for(Object o : testlist) {
            ControllerData pt = (ControllerData) o;
            assertEquals(herbert,pt.getPlayer1().getPlayerName());
            assertEquals(testString, pt.getPlayer1Pile());
            assertEquals(klaus,pt.getPlayer2().getPlayerName());
            session.delete(o);
            i++;
        }
        assertEquals(i,1);
        trans.commit();
    }
}
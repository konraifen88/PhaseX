package persistence.couchDB;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import model.player.IPlayer;
import model.player.impl.Player;
import org.ektorp.support.CouchDbDocument;

/**
 * If everything works right this class was
 * created by Konraifen88 on 25.04.2016.
 * If it doesn't work I don't know who the hell wrote it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouchCardData extends CouchDbDocument {

    @JsonProperty("_id")
    private String id;

    @JsonDeserialize(as = Player.class)
    private IPlayer player;

    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public IPlayer getPlayer() {
        return player;
    }

    public void setPlayer(IPlayer player) {
        this.player = player;
    }
}

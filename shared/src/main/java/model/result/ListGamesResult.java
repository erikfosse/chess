package model.result;

import model.GameRecord;
import model.request.GeneralApi;

import java.util.ArrayList;

public record ListGamesResult(ArrayList<GameRecord> games) implements GeneralApi {

    public ListGamesResult() {
        this(new ArrayList<GameRecord>());
    }
}



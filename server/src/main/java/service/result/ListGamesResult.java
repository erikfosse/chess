package service.result;

import service.request.GeneralApi;

import java.util.ArrayList;

public record ListGamesResult(ArrayList<SingleGameResult> games) implements GeneralApi {
}


package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    private static final String BOT_TOKEN = "7025174805:AAGxsa_1Se_gs2LMVf0VptacRNtm4LALkv0";

    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            MyBot bot = new MyBot(BOT_TOKEN);
            telegramBotsApi.registerBot(bot);
            System.out.println("Bot muvaffaqiyatli ishga tushdi!");
        } catch (TelegramApiException e) {
            System.err.println("Bot ishga tushishida xatolik: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

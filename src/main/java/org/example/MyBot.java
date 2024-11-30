package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {

    private final String channelUsername = "https://t.me/sarvar_ustoz1";
    private final String channelInviteLink = "https://t.me/sarvar_ustoz1";

    public MyBot(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Integer userId = Math.toIntExact(update.getMessage().getFrom().getId());

            if (text.equals("/start")) {
                boolean isSubscribed = checkSubscription(userId);

                if (isSubscribed) {
                    sendResponse(chatId.toString(), "Xush kelibsiz! Siz obuna bo'lgansiz.");
                } else {
                    sendSubscriptionMessage(chatId);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer userId = Math.toIntExact(update.getCallbackQuery().getFrom().getId());

            if (data.equals("check")) {
                boolean isSubscribed = checkSubscription(userId);

                if (isSubscribed) {
                    sendResponse(chatId.toString(), "Obuna bo'lgansiz");
                } else {
                    sendResponse(chatId.toString(), "Obuna bo'lmagansiz. Iltimos, obuna bo'ling: " + channelInviteLink);
                }
            }
        }
    }

    private boolean checkSubscription(Integer userId) {
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId("https://t.me/sarvar_ustoz1"); // @username formatida
        getChatMember.setUserId(Long.valueOf(userId));

        try {
            ChatMember chatMember = execute(getChatMember);
            String status = chatMember.getStatus();
            return status.equals("member") || status.equals("creator") || status.equals("administrator");
        } catch (TelegramApiException e) {
            System.out.println("Subscription check error: " + e.getMessage());
            return false;
        }
    }

    private void sendSubscriptionMessage(Long chatId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Kanalga obuna bo'lish");
        button1.setUrl("https://t.me/sarvar_ustoz1");
        row1.add(button1);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Tekshirish");
        button2.setCallbackData("check");
        row2.add(button2);

        rowList.add(row1);
        rowList.add(row2);

        markup.setKeyboard(rowList);

        SendMessage message = new SendMessage();
        message.setText("Quyidagi kanalga obuna bo'ling");
        message.setChatId(chatId);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "@Tekin_Marafon_Bot";
    }

    @Override
    public String getBotToken() {
        return "7025174805:AAGxsa_1Se_gs2LMVf0VptacRNtm4LALkv0";
    }
}

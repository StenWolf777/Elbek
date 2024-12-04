package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBot extends TelegramLongPollingBot {

    private final String channelUsername = "https://t.me/+V2aiMaRMTxc5R8BS";
    private final String channelInviteLink = "https://t.me/+V2aiMaRMTxc5R8BS";

    private final Map<Long, String> userStates = new HashMap<>();
    private static final String MAIN_MENU = "MAIN_MENU";

    private final String botToken;

    public MyBot(String botToken) {
        super(botToken);
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        try {
            if (update.hasMessage() && update.getMessage().getChat().getType().equals("channel")) {
                return;
            }

            if (update.hasMessage()) {
                Long chatId = update.getMessage().getChatId();
                Integer messageId = update.getMessage().getMessageId();
                Long userId = update.getMessage().getFrom().getId();

                boolean isSubscribed = checkSubscription(userId);

                if (!isSubscribed && update.getMessage().hasText() &&
                        !update.getMessage().getText().equals("/start")) {
                    deleteMessage(chatId, messageId);
                    sendSubscriptionMessage(chatId);
                    return;
                }

                if (update.getMessage().hasText()) {
                    String text = update.getMessage().getText();

                    if (text.equals("/start")) {
                        handleStart(chatId, userId);
                        return;
                    }

                    if (!isSubscribed) {
                        deleteMessage(chatId, messageId);
                        sendSubscriptionMessage(chatId);
                        return;
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            if (data.equals("check")){
                GetChatMember member = new GetChatMember();
                member.setChatId("-1001466343985");
                member.setUserId(update.getCallbackQuery().getMessage().getChatId());
                ChatMember user = null;
                try {
                    user = execute(member);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                String status = user.getStatus();
                if (status.equals("member") || status.equals("creator") || status.equals("administrator")){
                    SendMessage message = new SendMessage();
                    message.setText("✅Obuna bo'lgansiz");
                    message.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    SendMessage message = new SendMessage();
                    message.setText("❌Obuna bo'lmagansiz");
                    message.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

//            } else if (update.hasCallbackQuery()) {
//                String data = update.getCallbackQuery().getData();
//                Long userId = update.getCallbackQuery().getFrom().getId();
//                org.telegram.telegrambots.meta.api.objects.Message message = null;
//                try {
//                    message = update.getCallbackQuery().getMessage();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return;
//                }
//
//                if (message != null && "check".equals(data)) {
//                    Long chatId = message.getChatId();
//                    onCheckSubscription(chatId, userId);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private boolean checkSubscription(Long userId) {
        try {
            GetChatMember getChatMember = new GetChatMember();
            getChatMember.setChatId(-1001104046689L);
            getChatMember.setUserId(userId);

                ChatMember chatMember = execute(getChatMember);

            System.out.println(chatMember);
            String status = chatMember.getStatus();

            return status.equals("member") ||
                    status.equals("administrator") ||
                    status.equals("creator");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendSubscriptionMessage(Long chatId) {
        try {
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton subscribeButton = new InlineKeyboardButton();
            subscribeButton.setText("📢 Kanalga obuna bo'lish");
            subscribeButton.setUrl(channelInviteLink);
            row1.add(subscribeButton);

            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton checkButton = new InlineKeyboardButton();
            checkButton.setText("✅ Obunani tekshirish");
            checkButton.setCallbackData("check");
            row2.add(checkButton);

            rowList.add(row1);
            rowList.add(row2);
            markup.setKeyboard(rowList);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("❌ Botdan foydalanish uchun kanalimizga obuna bo'lishingiz shart!\n\n" +
                    "📢 Kanal: " + channelUsername + "\n\n" +
                    "✅ Obuna bo'lgach, \"Obunani tekshirish\" tugmasini bosing.");
            message.setReplyMarkup(markup);
            message.enableHtml(true);

            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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

    private void onCheckSubscription(Long chatId, Long userId) {
        boolean isSubscribed = checkSubscription(userId);

        if (isSubscribed) {
            sendResponse(chatId.toString(), "✅ Tabriklaymiz! Siz kanalga obuna bo'ldingiz.\n" +
                    "Bot xizmatlaridan foydalanishingiz mumkin.");
            showMainMenu(chatId);
        } else {
            sendResponse(chatId.toString(), "❌ Siz hali kanalga obuna bo'lmagansiz.\n" +
                    "Iltimos, avval kanalga obuna bo'ling!");
            sendSubscriptionMessage(chatId);
        }
    }

    private void handleStart(Long chatId, Long userId) {
        boolean isSubscribed = checkSubscription(userId);
        if (isSubscribed) {
            showMainMenu(chatId);
        } else {
            sendSubscriptionMessage(chatId);
        }
    }

    private void showMainMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Botdan foydalanishingiz mumkin ✅");

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
        return botToken;
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(-1001104046689L);
    }
}

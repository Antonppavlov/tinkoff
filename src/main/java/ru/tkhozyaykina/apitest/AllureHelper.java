/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tkhozyaykina.apitest;

import io.qameta.allure.Attachment;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.FormattedMessage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


/**
 * служебный класс для формирования отчетов allure
 */
@Log4j2
public class AllureHelper {

    public static final String CONTENT_TYPE_XML = "text/xml";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_PLAIN = "text/plain";
    public static final String CONTENT_TYPE_JSON = "application/json";

    private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.toString();

    /**
     * маскировать конфиденциальные данные и вложенить xml (в виде строки) в протокол allure
     *
     * @param name    описание файла
     * @param message тело вложения (xml)
     * @return массив байт вложения для прикрепеления к отчету
     */
    public static byte[] saveXmlAttachment(String name, String message) {
        //todo перекодировать message в кодировку из заголовка? или отдельный метод, возвращающий байтовый массив, если кодировка не utf-8
        return saveAttachment(name, message, CONTENT_TYPE_XML);
    }

    /**
     * приложить документ (строку) в протокол allure
     * <p>
     * <p> попутно маскирует конфиденциальные данные
     *
     * @param name    описание файла
     * @param message тело вложения
     * @param type    тип вложения с указанием кодировки (например, "text/xml", "application/json" или "text/plain; charset=utf-8") c
     * @return массив байт вложения для прикрепеления к отчету
     */
    public static byte[] saveAttachment(String name, String message, String type) {
        String charset = Operations.getContentEncoding(type, DEFAULT_ENCODING).toUpperCase();
        String typeOnly = type.split(";")[0];
        return saveAttachmentMaskedUni(name, message, charset, typeOnly);
    }

    /**
     * вложенить строку в протокол allure
     *
     * @param name    описание файла
     * @param message тело вложения
     * @param charset кодировка строки
     * @param type    тип вложения (например, "text/xml"). почему-то как параметр для @Attachment не работает. должна быть только константа
     * @return массив байт вложения для прикрепеления к отчету
     */
    private static byte[] saveAttachmentMaskedUni(String name, String message, String charset, String type) {
        switch (type) {
            case CONTENT_TYPE_XML:
                return saveXmlAttachmentMasked(name, message, charset);
            case CONTENT_TYPE_HTML:
                return saveHtmlAttachmentMasked(name, message, charset); // todo некоторые сервисы возвращают нечто похожее на XML, но с типом ответа "text/html".
            // Попробуем обрабатывать как XML
            case CONTENT_TYPE_JSON:
                return saveJsonAttachmentMasked(name, message, charset);
            default:
                return saveTextAttachmentMasked(name, message, charset);
        }
    }

    /**
     * сделать вложение текста в отчет allure (как есть - маскирование конфиденциальных данных было произведено ранее)
     * @param name      наименование вложения
     * @param message   строка для вложения в отчет
     * @param charset   кодировка
     * @return
     */
    @Attachment(value = "name", type = "text/plain")
    private static byte[] saveTextAttachmentMasked(String name, String message, String charset) {
        return saveAttachmentMasked(name, message, charset);
    }

    /**
     * сделать вложение xml в отчет allure (как есть - маскирование конфиденциальных данных было произведено ранее)
     * @param name      наименование вложения
     * @param message   xml-строка для вложения в отчет
     * @param charset   кодировка
     * @return
     */
    @Attachment(value = "name", type = "text/xml")
    private static byte[] saveXmlAttachmentMasked(String name, String message, String charset) {
        return saveAttachmentMasked(name, message, charset);
    }

    /**
     * сделать вложение html в отчет allure (как есть - маскирование конфиденциальных данных было произведено ранее)
     * @param name      наименование вложения
     * @param message   html-строка для вложения в отчет
     * @param charset   кодировка
     * @return
     */
    @Attachment(value = "name", type = "text/html")
    private static byte[] saveHtmlAttachmentMasked(String name, String message, String charset) {
        return saveAttachmentMasked(name, message, charset);
    }

    /**
     * сделать вложение json в отчет allure (как есть - маскирование конфиденциальных данных было произведено ранее)
     * @param name      наименование вложения
     * @param message   json-строка для вложения в отчет
     * @param charset   кодировка
     * @return
     */
    @Attachment(value = "name", type = "application/json")
    private static byte[] saveJsonAttachmentMasked(String name, String message, String charset) {
        return saveAttachmentMasked(name, message, charset);
    }

    /**
     * сделать вложение в отчет allure (как есть - маскирование конфиденциальных данных было произведено ранее)
     * @param name      наименование вложения
     * @param message   собственно строка с вложением
     * @param charset   кодировка
     * @return
     */
    private static byte[] saveAttachmentMasked(String name, String message, String charset) {
        try {
            return message.getBytes(charset);
        } catch (UnsupportedEncodingException ex) {
            log.warn(new FormattedMessage("Неподдерживаемая кодировка {}", charset), ex);
            return message.getBytes();
        }
    }

}

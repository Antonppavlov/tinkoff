/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.tkhozyaykina.apitest;

/**
 * исключение чтения ресурса (файла с тестовыми данными)
 */
public class ReadResourceException extends Exception {

    /**
     * исключение чтения ресурса (файла с тестовыми данными)
     *
     * @param fileName относительное имя файла-ресурса
     * @param ex       исходное исключение
     */
    public ReadResourceException(String fileName, Throwable ex) {
        super("Ошибка чтения файла-ресурса '" + fileName + "'", ex);
    }

    public ReadResourceException(String fileName) {
        super("Ошибка чтения файла-ресурса '" + fileName + "'");
    }


}

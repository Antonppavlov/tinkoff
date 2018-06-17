package ru.tkhozyaykina.apitest;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * класс утилит, часто используемых в тестах
 */
@Log4j2
public class Operations {
    private static Random rand = new Random();

    /**
     * чтение файла-ресурса в строку.
     * <p>
     * <p>не считывает (пропускает) BOM (маркер UTF)
     *
     * @param fileName относительный путь на файл
     * @param fileName относительный путь на файл
     * @param encoding кодировка файла.
     *                 Если в файле присутсвовал BOM, то параметр игнорируется
     * @return строку, содержащую тело файла
     * @throws ReadResourceException ошибка чтения файла
     */
    public static String readResource(String fileName, final Charset encoding) throws ReadResourceException {
        return readResource(fileName, encoding, Operations.class.getClassLoader());
    }

    /**
     * чтение файла-ресурса в строку.
     * <p>
     * <p>не считывает (пропускает) BOM (маркер UTF)
     *
     * @param fileName    относительный путь на файл
     * @param encoding    кодировка файла.
     *                    Если в файле присутсвовал BOM, то параметр игнорируется
     * @param classLoader класслоадер, загружающий ресурсы
     * @return строку, содержащую тело файла
     * @throws ReadResourceException ошибка чтения файла
     */
    public static String readResource(String fileName, final Charset encoding, ClassLoader classLoader) throws ReadResourceException {
        String result;

        log.debug("Чтение ресурса '{}'", fileName);

        try (InputStream is = classLoader.getResourceAsStream(fileName);
             BOMInputStream bomIS
                     = new BOMInputStream(is,
                     ByteOrderMark.UTF_8,
                     ByteOrderMark.UTF_16LE,
                     ByteOrderMark.UTF_16BE,
                     ByteOrderMark.UTF_32LE,
                     ByteOrderMark.UTF_32BE)) {
            if (is == null) {
                throw new ReadResourceException(fileName);
            }
            Charset enc;
            if (bomIS.hasBOM()) {
                enc = Charset.forName(bomIS.getBOMCharsetName());
                log.debug(" . установлена кодировка {} согласно BOM в файле", enc);
            } else {  // No BOM found
                // todo читать кодировку из заголовка xml-файла (по флагу, что это xml) determineXmlEncoding()
                enc = encoding;
                log.debug(" . использована кодировка по умолчанию ({})", enc);
            }
            result = inputStream2String(bomIS, enc);
        } catch (IOException ex) {
            throw new ReadResourceException(fileName, ex);
        }
        return result;
    }

    /**
     * чтение входного потока в строку
     *
     * @param is  поток
     * @param enc кодировка потока
     * @return строка, с содержимым потока
     * @throws IOException ошибка чтения потока
     */
    public static String inputStream2String(final InputStream is, Charset enc) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            IOUtils.copy(is, writer, enc.name());
            return writer.toString();
        }
    }

    /**
     * Определить кодировку xml-файла, представленного строкой по его заголовку
     *
     * @param message строка с xml
     * @return строку с именем кодировки
     */
    public static String determineXmlEncoding(String message) {
        Pattern toFind = Pattern.compile("<\\?\\s*xml.*\\sencoding\\s*=\\s*\"(.+)\".*\\?>", Pattern.DOTALL);
        String xmlCharSet = null;
        Matcher mc = toFind.matcher(message);
        if (mc.find()) {
            xmlCharSet = mc.group(1);
        }
        return xmlCharSet;
    }

    /**
     * чтение файла-ресурса в кодировке UTF-8 в строку Если в файле присутсвовал
     * BOM, то используем кодировку, которую он задает
     *
     * @param fileName относительный путь на файл
     * @return строку, содержащую тело файла
     * @throws ReadResourceException ошибка чтения файла
     */
    public static String readResource(final String fileName) throws ReadResourceException {
        return readResource(fileName, StandardCharsets.UTF_8);
    }

    /**
     * конвертировать xml строку в DOM объект
     *
     * @param xmlString строка с xml
     * @return DOM xml
     * @throws ParserConfigurationException ошибка инициализации парсера
     * @throws SAXException                 ошибка разбора xml
     */
    public static Document xmlStringToDocument(String xmlString) throws ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (StringReader sr = new StringReader(xmlString)) {
            InputSource is = new InputSource(sr);
            Document document = builder.parse(is);

            return document;
        } catch (IOException ex) {
            log.catching(ex); // такого не бывает, вроде
        }
        return null;
    }

    /**
     * считать файл свойств
     *
     * @param fileName имя файла
     * @return набор значений свойств
     * @throws ReadResourceException ошибка чтения файла конфигурации
     */
    public static Properties getConfigProps(String fileName) throws ReadResourceException {
        Properties config = new Properties();

        try (InputStream is = Operations.class.getClassLoader().getResourceAsStream(fileName)) {
            config.load(is);
        } catch (IOException ex) {
            throw new ReadResourceException(fileName, ex);
        }
        return config;
    }

    /**
     * получить значение XPath выражения, примененного к xml-документу
     *
     * @param xml       строка, содержащая xml-документ
     * @param xpathExpr выражение XPath
     * @return значение выражения
     * @throws ParserConfigurationException ошибка инициализации парсера
     * @throws SAXException                 ошибка разбора xml
     * @throws XPathExpressionException     ошибка разбора xpath выражения
     */
    public static String getXPathValue(String xml, String xpathExpr) throws ParserConfigurationException, SAXException, XPathExpressionException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  //todo не тормозит? может держать один на поток?
        DocumentBuilder db = dbf.newDocumentBuilder();
        String res = null;
        try (StringReader sr = new StringReader(xml)) {
            InputSource is = new InputSource();
            is.setCharacterStream(sr);
            Document doc = db.parse(is);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(xpathExpr);
            res = expr.evaluate(doc);
        } catch (IOException ex) {
            log.catching(ex); // такого не бывает, вроде
        }
        return res;
    }

    /**
     * заменить последнее вхождение искомой подстроки
     *
     * @param string    строка, в которой ищем
     * @param searchFor искомая подстрока на замену
     * @param replaceTo на что меняем
     * @return строка с замененным последним вхождением искомой подстроки
     */
    private static String replaceLast(String string, String searchFor, String replaceTo) {
        int lastIndex = string.lastIndexOf(searchFor);
        if (lastIndex < 0) {
            return string;
        }
        String tail = string.substring(lastIndex).replaceFirst(searchFor, replaceTo);
        return string.substring(0, lastIndex) + tail;
    }

    /**
     * случайное целое число в заданном интервале
     *
     * @param min левая граница интервала
     * @param max правая граница интервала
     * @return случайное целое число в заданном интервале
     * @see #newDocId()
     */
    public static int randInt(int min, int max) {
        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    /**
     * сформировать новый id запроса
     * пока возвращаем randomUUID
     *
     * @return строковый id запроса
     * @see #randInt(int, int)
     */
    public static String newDocId() {
        return UUID.randomUUID().toString();
    }

    /**
     * получить имя кодировки из описателя Content-Type
     *
     * @param contentType    тип содержимого (тип + кодировка)
     * @param defaultCharset кодировка по умолчанию
     * @return имя кодовой страницы
     */
    public static String getContentEncoding(String contentType, String defaultCharset) {
        String charset = defaultCharset;
        for (String param : contentType.replace(" ", "").split(";")) {
            if (param.startsWith("charset=")) {
                charset = param.split("=", 2)[1].replaceAll("\"", "");
                break;
            }
        }
        return charset;
    }

    /**
     * раскрыть шаблон (подставить значения параметров)
     *
     * @param template шаблон для раскрытия (строка, в которой можно использовать подстановки вида {0} - фигурные скобки с номером параметра)
     * @param args     значения параметров для подстановки
     * @return раскрытый шаблон
     */
    public static String formatTemplate(String template, String... args) {
        String res = template;
        for (int i = 0; i < args.length; i++) {
            res = res.replaceAll("\\{" + i + "\\}", args[i]);
        }
        Pattern pattern = Pattern.compile("\\{\\d{1,2}\\}");
        Matcher matcher = pattern.matcher(res);
        if (matcher.find()) {
            log.warn("не все параметры шаблона были раскрыты. \n template=[{}]\n args={}", template, args);
        }
        return res;
    }
}

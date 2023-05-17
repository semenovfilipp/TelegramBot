package org.example.service.impl;

import lombok.extern.log4j.Log4j;
import org.example.dao.AppDocumentDAO;
import org.example.dao.AppPhotoDAO;
import org.example.dao.BinaryContentDAO;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.example.exception.UploadFileException;
import org.example.service.FileService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

//Получает message из телеграм, выполняет необходимые действия для скачивания файла и сохраняет его в БД
@Log4j
@Service
public class FileServiceImpl implements FileService {
    //Внедлряем три properties из ap.properties(node)
    @Value("5825700143:AAES0rLO8lS7bbv0n1gf3BNe06wihuaJMoo")
    private String token;
    @Value("https://api.telegram.org/bot{token}/getFile?file_id={fileId}")
    private String fileInfoUri;
    @Value("https://api.telegram.org/file/bot{token}/{filePath}")
    private String fileStorageUri;
    //Бины для сохранения обьектов
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;

    //Внедряем bean
    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO,AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }
    @Override
    public AppDocument processDoc (Message telegramMessage) {
        Document telegramDoc = telegramMessage.getDocument();
        //Достаем из update fileID
        String fileId = telegramDoc.getFileId();
        //http get запрос к серверу телеграмма
        ResponseEntity<String> response = getFilePath(fileId);
        //В случае успеха работаем с объектом response который содержит данные из телеграм
        if (response.getStatusCode() == HttpStatus.OK) {
            //Преобразуем боди из респонса в объект
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            //Мы достали message, а из него объект телеграма document
            //На основе этого генерируем свою сущность, в том числе сэтим в нее
            // только что сохраненный объект BinaryContent
            //После чего appDocument сохраняется в БД

            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }
    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        //
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(0);
        //Достаем из update fileID
        String fileId = telegramPhoto.getFileId();
        //http get запрос к серверу телеграмма
        ResponseEntity<String> response = getFilePath(fileId);
        //В случае успеха работаем с объектом response который содержит данные из телеграм
        if (response.getStatusCode() == HttpStatus.OK) {
            //Преобразуем боди из респонса в объект
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            //Мы достали message, а из него объект телеграма document
            //На основе этого генерируем свою сущность, в том числе сэтим в нее
            // только что сохраненный объект BinaryContent
            //После чего appDocument сохраняется в БД

            AppPhoto transientAppDoc = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }



    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        //Делаем повторный запрос к серверу телеграм и по известному пути скачиваем файл в виде массива байт
        byte[] fileInByte = downloadFile(filePath);
        //Создаем объект который не привязан к БД и помещаем в него этот массив byte[]
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        //Сохраняем объект в БД и возвращаем persistentObject
        //Те объект привязанный к сессии hebirnate и уже с сгенерированным первичным ключом id
        return binaryContentDAO.save(transientBinaryContent);
    }
    //Отдельный метод для получения файла из респонс

    private static String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        //Достаем нужные нам данные - file_path
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }



    //Достает  значения из полей объекта телеграм и сэтит их в нащ объект
    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFieldId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }
    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFieldId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }



    //При промощи RestTemplate мы делаем http запрос в телеграм
    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

//В метод exchange передается uri запроса, параметры которые нужно поставить в этот запрос
        //Тип возвращаемого значения, объект request и http метод
        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }
    //Формируем окончательный uri, подставляем туда token и путь к файлу на сервере телеграмма

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token", token)
                .replace("{filePath}", filePath);
        //Создаем объект   URL на котором мы можем создать Stream для скачивания объекта
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }
        //TODO подумать над оптимизацией

        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }
}
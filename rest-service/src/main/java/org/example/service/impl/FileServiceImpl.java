package org.example.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.example.dao.AppDocumentDAO;
import org.example.dao.AppPhotoDAO;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.example.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j // добавляет логгирование
@Service //создает бин спринг
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO){
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

//Парсит id из строки в тип лонг и по нему объект документа
    @Override
    public AppDocument getDocument(String docId) {
        //TODO
        var id = Long.parseLong(docId);
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        //TODO
        var id = Long.parseLong(photoId);
        return appPhotoDAO.findById(id).orElse(null);
    }

    //getFileSystemResource предназначен для того что бы массив байт из БД преобразовать в объект класса
    //fileSystemResourse,который можно отправить в теле ответа пользователю и иего браузер скачает файл
    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            //TODO добавить генерацию временных названий
            File temp = File.createTempFile("tempFile", ".bin");//временный файл с расширением бин
            temp.deleteOnExit(); // удаляет временный файл из постоянной памяти компьютера.Метод регестрирует файл в очередь на удаление
            FileUtils.writeByteArrayToFile(temp,binaryContent.getFileAsArrayOfBytes());//записываем массив байт в объект временного файла
            return new FileSystemResource(temp); //оборачиваем его в файл system resource
        } catch (IOException e){
            log.error(e);
            return null; //делаем возврат
        }
    }
}

package org.example.service;

import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    //Для получения документа
    AppDocument getDocument(String id);
    //Для получения фото
    AppPhoto getPhoto(String id);
    //Преобразование массива байт в объект файл systemResource,
    // который необходим для передачи ответа в теле http ответа
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);

}

package org.example.controller;
//обработка http запросов со стороны пользователя

import lombok.extern.log4j.Log4j;
import org.example.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j
@RequestMapping("/file") // Позволяет задать часть uri пути, который будет общим для всех методов в данном контроллере
@RestController
// Мы в ответ возвращаем RawData (Spring не будет у себя искать шаблон страницы view, а сразу вернет те данные которые мы поместим в боди)
//обычно это json или xml, но в нашем случае это массив байт
public class FileController {
    private final FileService fileService; // bean файлсервиса

    public FileController(FileService fileService){
        this.fileService = fileService;
    }
    //Spring class Bulider, который собирает http ответ
    //RequestParam описывает аннотации гет запроса, которые могут придти во входящем запросе
    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")//Указывает какой тип http запроса обрабатывает данный метод
    public ResponseEntity<?> getDoc(@RequestParam ("id") String id){
        //TODO
        var doc = fileService.getDocument(id); // по id из fileService получаем объект документа
        //Если придет null, то вернем пользователю BadRequest
        if (doc == null){
            return ResponseEntity.badRequest().build();
        }
        //из документа получаем объект binaryContent
        var binaryContent = doc.getBinaryContent();

        var fileSystemResources = fileService.getFileSystemResource(binaryContent);
        //Eternal server error - ошибка на нашем сервере
        if (fileSystemResources == null){
            return ResponseEntity.internalServerError().build();
        }
        // Успешный ответ пользователю
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMimeType())) //добавляет к ответу одноименный хедер, чтолбы браузер из потока байт создал фал с нужным расширением
                .header ("Content-disposition", "attachment ; filename=" + doc.getDocName()) //указывает браузеру как именнно воспринимать полученную информацию
                .body(fileSystemResources); //задаем имя
    }
    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam ("id") String id){
        //TODO
        var photo = fileService.getPhoto(id);// по id из fileService получаем объект документа
        //Если придет null, то вернем пользователю BadRequest
        if (photo == null){
            return ResponseEntity.badRequest().build();
        }
        //из документа получаем объект binaryContent
        var binaryContent = photo.getBinaryContent();

        var fileSystemResources = fileService.getFileSystemResource(binaryContent);
        //Eternal server error - ошибка на нашем сервере
        if (fileSystemResources == null){
            return ResponseEntity.internalServerError().build();
        }
        // Успешный ответ пользователю
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) //добавляет к ответу одноименный хедер, чтолбы браузер из потока байт создал фал с нужным расширением
                .header ("Content-disposition", "attachment ;") //указывает браузеру как именнно воспринимать полученную информацию
                .body(fileSystemResources); //задаем имя
    }


}

package org.example.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_photo")
public class AppPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Первичный ключ id
    private Long id;
    private String telegramFieldId;

    @OneToOne
    //Одной записи из BinaryContent может соответствовать только одна запись из таблицы app_document
    private BinaryContent binaryContent;
    //Для хранения фото используется инт, для документов лонг
    private Integer fileSize;


}

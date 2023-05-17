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
@Table(name = "app_document")
public class AppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Первичный ключ id
    private Long id;
    private String telegramFieldId;
    private String docName;

    @OneToOne
    //Одной записи из BinaryContent может соответствовать только одна запись из таблицы app_document
    private BinaryContent binaryContent;
    private String mimeType;
    private Long fileSize;


}

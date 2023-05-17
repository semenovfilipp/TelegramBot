package org.example.entity;
///Клас в который сохраняются все наши текущие апдейты
// и им присваевается уникальный id-ключ

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;

///Аннотации позволяют сгенерировать мусорный код при компиляции сообщения
//@Data
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
//Конструктор по умолчанию - без параметров
@NoArgsConstructor
//Конструктор, который содержит все поля этого класса
@AllArgsConstructor
///Entity говорит что наш класс является сущностью, которая связана с таблицей в нашей БД
@Entity
//Table Задает имя таблицы
@Table(name = "raw_data")

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class RawData {
    @Id
    //Стратегия при помощи которой создаются значения первичных ключей
    //Ее суть состоит в том, что мы сами позволяем базе данных генерировать значения для первичных ключей

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //
    @Type(type = "jsonb")
    //
    @Column(columnDefinition = "jsonb")
    private Update event;

}

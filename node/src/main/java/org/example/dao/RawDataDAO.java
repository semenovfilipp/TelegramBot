package org.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.entity.RawData;


//Слой для работы с базой данный
public interface RawDataDAO extends JpaRepository <RawData, Long> {
}

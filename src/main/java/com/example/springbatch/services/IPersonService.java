package com.example.springbatch.services;

import com.example.springbatch.entities.Person;

import java.util.List;

public interface IPersonService {

    void saveAll(List<Person> persons);
}

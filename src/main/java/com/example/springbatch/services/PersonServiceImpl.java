package com.example.springbatch.services;

import com.example.springbatch.entities.Person;
import com.example.springbatch.repositories.IPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonServiceImpl implements IPersonService {

    private final IPersonRepository repository;

    @Autowired
    public PersonServiceImpl(IPersonRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void saveAll(List<Person> persons) {
        repository.saveAll(persons);
    }
}

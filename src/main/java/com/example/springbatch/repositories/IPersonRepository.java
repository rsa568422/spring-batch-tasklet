package com.example.springbatch.repositories;

import com.example.springbatch.entities.Person;
import org.springframework.data.repository.CrudRepository;

public interface IPersonRepository extends CrudRepository<Person, Long> {
}

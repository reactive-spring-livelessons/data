package com.example.reactivemongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class ReactiveMongodbApplication {

		public static void main(String[] args) throws InterruptedException {
				SpringApplication.run(ReactiveMongodbApplication.class, args);
				Thread.sleep(1000 * 10);
		}
}

@Log
@Component
class PersonRunner implements ApplicationRunner {

		private final PersonRepository personRepository;

		PersonRunner(PersonRepository personRepository) {
				this.personRepository = personRepository;
		}

		@Override
		public void run(ApplicationArguments args) throws Exception {

				Flux<String> names = Flux.just("Chase", "Tammie", "Paul", "Emma",
					"Kimly", "Mia", "Valerie", "Kathleen", "Mario", "Mark", "Dave");

				this.personRepository
					.deleteAll()
					.thenMany(names
						.map(n -> new Person(null, n, address()))
						.flatMap(this.personRepository::save))
					.thenMany(this.personRepository.findAll())
					.subscribe(p -> log.info(p.toString()));

				Flux<Person> byName = this.personRepository.findByName("Chase");
				byName.subscribe(System.out::println);
		}

		private Address address() {
				List<String> streetNames = Arrays.asList("Main St.", "Shirley Ave.", "Harrison St.", "Reseda Blvd.");
				int indx = (int) (Math.random() * streetNames.size() - 1);
				int street = (int) (Math.random() * 1000);
				String streetName = street + " " + streetNames.get(indx);
				return new Address(streetName);
		}

}

interface PersonRepository extends ReactiveMongoRepository<Person, String> {
		Flux<Person> findByName(String first);
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
class Person {

		@Id
		private String id;
		private String name;
		private Address address;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Address {
		private String street;
}
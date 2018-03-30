package com.example.reactivedata;

import lombok.AllArgsConstructor;
import lombok.Data;
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
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication
public class ReactiveDataApplication {

		public static void main(String[] args) throws InterruptedException {
				SpringApplication.run(ReactiveDataApplication.class, args);

				Thread.sleep(1000 * 10);
		}
}

@Log
@Component
class DataInitializer implements ApplicationRunner {

		private final ReservationRepository reservationRepository;

		DataInitializer(ReservationRepository reservationRepository) {
				this.reservationRepository = reservationRepository;
		}

		@Override
		public void run(ApplicationArguments args) throws Exception {
				this.reservationRepository
					.deleteAll()
					.thenMany(Flux.just("Pete", "Julie", "Josh", "Marcin", "Phil")
						.map(name -> new Reservation(null, name))
						.flatMap(this.reservationRepository::save))
					.thenMany(this.reservationRepository.findAll())
					.subscribe(System.out::println);
		}
}

interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {

		Flux<Reservation> findByReservationName(String rn);
}


@Data
@AllArgsConstructor
@Document
class Reservation {

		@Id
		private String id;

		private String reservationName;
}
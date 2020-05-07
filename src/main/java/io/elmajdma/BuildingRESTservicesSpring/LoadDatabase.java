package io.elmajdma.BuildingRESTservicesSpring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {
    @Bean
    CommandLineRunner initDatabase(EmployeeRepositiory repositiory, OrderRepository orderRepository){
        return  args ->{
            log.info("Preloading" + repositiory.save(new Employee("Mohamed","abu", "geologist")));
            log.info("Preloading"+ repositiory.save(new Employee("ahmed","ali", "petroogy")));
            log.info("Preloaded data"+ orderRepository.save(new Order("Oppo",Status.COMPLETED)));
            log.info("Preloaded data"+ orderRepository.save(new Order("Mac",Status.IN_PROGRESS)));
            orderRepository.findAll().forEach(order -> {
                log.info("preloade"+ order);
            });
        };
    }
}

package io.elmajdma.BuildingRESTservicesSpring;

import io.elmajdma.BuildingRESTservicesSpring.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepositiory extends JpaRepository<Employee,Long> {

}

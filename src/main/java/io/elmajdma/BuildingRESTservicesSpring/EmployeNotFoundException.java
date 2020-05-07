package io.elmajdma.BuildingRESTservicesSpring;

public class EmployeNotFoundException extends RuntimeException{
    EmployeNotFoundException(Long id) {
        super( "Cloud not find employee " +id);
    }


}

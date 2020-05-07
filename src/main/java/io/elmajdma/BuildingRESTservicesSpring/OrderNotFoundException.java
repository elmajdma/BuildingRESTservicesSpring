package io.elmajdma.BuildingRESTservicesSpring;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(Long id) {
        super("Order No. "+id+" Not Found");
    }
}

package io.elmajdma.BuildingRESTservicesSpring;


import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController {
    private final OrderRepository orderRepository;
    private final OrderModelAssembler orderModelAssembler;

    public OrderController(OrderRepository orderRepository, OrderModelAssembler orderModelAssembler) {
        this.orderRepository = orderRepository;
        this.orderModelAssembler = orderModelAssembler;
    }

    @GetMapping("/orders/{id}")
    public EntityModel<Order> one(@PathVariable Long id) {
        Order order=orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        return orderModelAssembler.toModel(order);
    }


    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> all() {
       List<EntityModel<Order>> orders= orderRepository.findAll().stream()
                .map(orderModelAssembler:: toModel).collect(Collectors.toList());
       return new CollectionModel<>(orders, linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @PostMapping("/orders")
    public ResponseEntity<?> newOrder(@RequestBody Order order) throws URISyntaxException {
        order.setStatus(Status.IN_PROGRESS);
        Order newOrder=orderRepository.save(order);

        return ResponseEntity.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
                .body(orderModelAssembler.toModel(newOrder));
    }

    @PutMapping("/orders/{id}")
    ResponseEntity<?> complete( @PathVariable Long id) {
        Order updateOrder=orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if(updateOrder.getStatus()==Status.IN_PROGRESS){
            updateOrder.setStatus(Status.COMPLETED);
            return  ResponseEntity.ok(orderModelAssembler.toModel(orderRepository.save(updateOrder)));
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method Not Allowed",
                        "You can not complete an order that is in the "+updateOrder.getStatus()+" Status"));

    }

    @DeleteMapping("/orders/{id}/cancel")
    ResponseEntity<RepresentationModel> cancel(@PathVariable Long id) {
        Order order=orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        if(order.getStatus()==Status.IN_PROGRESS){
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(orderModelAssembler.toModel(orderRepository.save(order)));
        }
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method Not Allowed",
                        "You can not cancel an order that is in the "+order.getStatus()+" Status"));

    }
}

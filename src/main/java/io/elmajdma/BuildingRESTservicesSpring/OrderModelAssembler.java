package io.elmajdma.BuildingRESTservicesSpring;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order,EntityModel<Order>> {
    private static final String orders="orders";
    @Override
    public EntityModel<Order> toModel(Order entity) {

        //unconditional link to single item resourse
        EntityModel<Order> orderEntityModel=new EntityModel<>(entity,
                linkTo(methodOn(OrderController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).all()).withRel(orders));

        //conditional links
        if(entity.getStatus()==Status.IN_PROGRESS){
            orderEntityModel.add(linkTo(methodOn(OrderController.class).cancel(entity.getId())).withRel("cancel"));
            orderEntityModel.add(linkTo(methodOn(OrderController.class).complete(entity.getId())).withRel("complete"));
        }
        return orderEntityModel;
    }
}

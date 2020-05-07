package io.elmajdma.BuildingRESTservicesSpring;


import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController
public class EmployeeController {
    private final EmployeeRepositiory repositiory;
    private final EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeRepositiory repositiory, EmployeeModelAssembler assembler) {
        this.repositiory = repositiory;
        this.assembler = assembler;
    }

    //Aggregate  root
//    @GetMapping("/employees")
//    List<Employee> allEmployees() {
//        return repositiory.findAll();
//    }
   /* @GetMapping("/employees")
 CollectionModel<EntityModel<Employee>> allEmployees(){
        List<EntityModel<Employee>> employees =repositiory.findAll()
                .stream().map(employee -> new EntityModel<>(employee,
                        linkTo(methodOn(EmployeeController.class).one(employee.getId())).withRel("employee_data"),
                        linkTo(methodOn(EmployeeController.class).allEmployees())
                                .withRel("employees"))).collect(Collectors.toList());
        return new CollectionModel<>(employees,
                linkTo(methodOn(EmployeeController.class).allEmployees()).withSelfRel());
 }*/
    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> allEmployees(){
        List<EntityModel<Employee>> employees =repositiory.findAll()
                .stream().map(assembler::toModel).collect(Collectors.toList());
        return new CollectionModel<>(employees,
                linkTo(methodOn(EmployeeController.class).allEmployees()).withSelfRel());
    }





    /*@PostMapping("/employees")
    Employee newEmployee(@RequestBody Employee newEmployee) {
        return repositiory.save(newEmployee);
    }*/

    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) throws URISyntaxException {
       EntityModel<Employee> entityModel=assembler.toModel(repositiory.save(newEmployee));
       return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
               .body(entityModel);
        //return repositiory.save(newEmployee);
    }

    /*@GetMapping("/employees/{id}")
    Employee getSingleEmployee(@PathVariable Long id){
        return repositiory.findById(id).orElseThrow(()->new EmployeNotFoundException(id));
    }*/
    /*@GetMapping("/employees/{id}")
    EntityModel<Employee> one(@PathVariable Long id) {
        Employee employee = repositiory.findById(id)
                .orElseThrow(() -> new EmployeNotFoundException(id));
        return new EntityModel<>(employee,
                linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).allEmployees()).withRel("employees"));
    }*/
    @GetMapping("/employees/{id}")
    EntityModel<Employee> one(@PathVariable Long id) {
        Employee employee = repositiory.findById(id)
                .orElseThrow(() -> new EmployeNotFoundException(id));
        return assembler.toModel(employee);
    }

    /*@PutMapping("/employees/{id}")
    Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        return repositiory.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repositiory.save(employee);
                    // }).orElseThrow(()->new EmployeNotFoundException(id));
                }).orElseGet(() -> {
                    newEmployee.setId(id);
                    return repositiory.save(newEmployee);
                });
    }*/
    @PutMapping("/employees/{id}")
    ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployeee, @PathVariable Long id)throws URISyntaxException {
        Employee updatedEmployee=repositiory.findById(id)
                .map(employee -> {
                    employee.setName(newEmployeee.getName());
                    employee.setRole(newEmployeee.getRole());
                    return repositiory.save(employee);
                })
                .orElseGet(()-> {
                     newEmployeee.setId(id);
                     return repositiory.save(newEmployeee);
                });
        EntityModel<Employee> entityModel=assembler.toModel(updatedEmployee);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);

    }


    /*@DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable Long id) {
        repositiory.deleteById(id);
    }*/

    @DeleteMapping("/employees/{id}")
    ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        repositiory.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}

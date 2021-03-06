package org.springframework.samples.petclinic.customer.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.common.data.BindingErrorsResponse;
import org.springframework.samples.petclinic.common.web.Endpoints;
import org.springframework.samples.petclinic.customer.model.Owner;
import org.springframework.samples.petclinic.customer.service.OwnerService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoints.OWNERS)
public class OwnerController {
  private final OwnerService ownerService;

  @GetMapping(value = "/*/lastname/{lastName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Collection<Owner>> getOwnersList(@PathVariable("lastName") String ownerLastName) {
    return new ResponseEntity<>(ownerService.findOwnerByLastName(ownerLastName), HttpStatus.OK);
  }

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Collection<Owner>> getOwners() {
    return new ResponseEntity<>(ownerService.findAllOwners(), HttpStatus.OK);
  }

  @GetMapping(value = "/{ownerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Owner> getOwner(@PathVariable("ownerId") Integer ownerId) {
    return new ResponseEntity<>(ownerService.getOwnerById(ownerId), HttpStatus.OK);
  }

  @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Owner> addOwner(@RequestBody @Valid Owner owner, BindingResult bindingResult,
                                        UriComponentsBuilder ucBuilder) {
    BindingErrorsResponse errors = new BindingErrorsResponse();
    HttpHeaders headers = new HttpHeaders();
    if (bindingResult.hasErrors() || (owner == null)) {
      errors.addAllErrors(bindingResult);
      headers.add("errors", errors.toJSON());
      return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
    }
    ownerService.saveOwner(owner);
    headers.setLocation(ucBuilder.path("/api/owners/{id}").buildAndExpand(owner.getId()).toUri());
    return new ResponseEntity<>(owner, headers, HttpStatus.CREATED);
  }

  @PutMapping(value = "/{ownerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Owner> updateOwner(@PathVariable("ownerId") Integer ownerId,
                                           @RequestBody @Valid Owner owner,
                                           BindingResult bindingResult) {
    BindingErrorsResponse errors = new BindingErrorsResponse();
    HttpHeaders headers = new HttpHeaders();
    if (bindingResult.hasErrors() || (owner == null)) {
      errors.addAllErrors(bindingResult);
      headers.add("errors", errors.toJSON());
      return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(ownerService.updateOwner(owner), HttpStatus.OK);
  }

  @DeleteMapping(value = "/{ownerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Void> deleteOwner(@PathVariable("ownerId") Integer ownerId) {
    ownerService.deleteOwner(ownerId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}

package test.albo.mx.marvel.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import test.albo.mx.marvel.dto.ResponseCharacters;
import test.albo.mx.marvel.dto.ResponseColaborators;
import test.albo.mx.marvel.service.MarvelService;
import test.albo.mx.marvel.util.InternalException;

@RestController
@RequestMapping("/marvel")
public class MarvelController {
	
	@Autowired
	MarvelService marvelService;
	
	@GetMapping(value = "/colaborators/{hero}")
	public ResponseEntity<ResponseColaborators> getColaborators(@PathVariable("hero") String hero) {
		String uuid = UUID.randomUUID().toString();
		
		ResponseColaborators response = marvelService.getColaborators(uuid, hero);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/characters/{hero}")
	public ResponseEntity<ResponseCharacters> getCharacters(@PathVariable("hero") String hero) {
		String uuid = UUID.randomUUID().toString();
		
		ResponseCharacters response = marvelService.getCharacters(uuid, hero);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(value = "/synchronization")
	public ResponseEntity<String> prueba() throws JsonProcessingException, InternalException {
		String uuid = UUID.randomUUID().toString();
		
		marvelService.syncLibrary(uuid);
		return new ResponseEntity<>("synchronized", HttpStatus.OK);
	}
}

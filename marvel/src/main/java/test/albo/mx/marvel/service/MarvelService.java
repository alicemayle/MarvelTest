package test.albo.mx.marvel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import test.albo.mx.marvel.dao.CharacterComicRepository;
import test.albo.mx.marvel.dao.CreatorRepository;
import test.albo.mx.marvel.dao.HeroRepository;
import test.albo.mx.marvel.dto.CharacterComic;
import test.albo.mx.marvel.dto.CharacterInteracted;
import test.albo.mx.marvel.dto.Creator;
import test.albo.mx.marvel.dto.Hero;
import test.albo.mx.marvel.dto.ResponseCharacters;
import test.albo.mx.marvel.dto.ResponseColaborators;
import test.albo.mx.marvel.dto.external.CharacterExternal;
import test.albo.mx.marvel.dto.external.ComicExternal;
import test.albo.mx.marvel.dto.external.ItemCharacterExternal;
import test.albo.mx.marvel.dto.external.ItemCreatorExternal;
import test.albo.mx.marvel.dto.external.ResponseCharactersExternal;
import test.albo.mx.marvel.util.Constants;
import test.albo.mx.marvel.util.InternalException;

@Service
@Slf4j
public class MarvelService {
	
	@Value("${marvel.api.characters.url}")
	private String marvelCharactersUrl;
	
	@Value("${marvel.public.apikey}")
	private String apikey;
	
	@Value("${marvel.hash}")
	private String hash;
	
	@Value("${marvel.hero.ironman}")
	private String ironman;
	
	@Value("${marvel.hero.capamerica}")
	private String capamerica;
	
	@Autowired
	private CreatorRepository creatorRepository;
	
	@Autowired
	private HeroRepository heroRepository;
	
	@Autowired
	private CharacterComicRepository characterComicRepository;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public ResponseColaborators getColaborators(String uuid, String hero) {
		ResponseColaborators colaborators = new ResponseColaborators();
		
		List<String> editors = creatorRepository.findByHeroAndRole(Constants.CREATOR_EDITOR, hero);
		List<String> writers = creatorRepository.findByHeroAndRole(Constants.CREATOR_WRITER, hero);
		List<String> colorists = creatorRepository.findByHeroAndRole(Constants.CREATOR_COLORIST, hero);
		Hero heroSave = heroRepository.findByHero(hero);
		
		colaborators.setEditors(editors);
		colaborators.setWriters(writers);
		colaborators.setColorists(colorists);
		colaborators.setLastSync(heroSave.getLastSync());
		
		return colaborators;
	}

	public ResponseCharacters getCharacters(String uuid, String hero) {
		ResponseCharacters response = new ResponseCharacters();
		List<CharacterInteracted> charactersInteractedList = new ArrayList<>();
		
		Hero heroSave = heroRepository.findByHero(hero);
		
		List<Integer> idCharactersList = characterComicRepository.findByHero(hero);
		for(Integer idCharacter : idCharactersList) {
			List<CharacterComic> characterComicList = characterComicRepository.findByIdCharacter(idCharacter);
			
			List<String> comics = new ArrayList<>();
			for (CharacterComic characterComic : characterComicList) {
				comics.add(characterComic.getTitleComic());
			}
			
			CharacterInteracted characterInteracted = new CharacterInteracted();
			characterInteracted.setCharacter(characterComicList.get(0).getName());
			characterInteracted.setComics(comics);
			
			charactersInteractedList.add(characterInteracted);
		}
		
		response.setLastSync(heroSave.getLastSync());
		response.setCharacters(charactersInteractedList);
		return response;
	}

	public void syncLibrary(String uuid) throws JsonProcessingException, InternalException {
//		syncCharacter(uuid, ironman);
		syncCharacter(uuid, capamerica);
	}
	
	public void syncCharacter(String uuid, String hero) throws InternalException, JsonProcessingException {
		ResponseEntity<Object> responseTmpCharacter;
		ResponseCharactersExternal responseExternal;
		Integer offset = 0;
		String heroIdentifier;
		
		if (hero.equals(ironman))
			heroIdentifier = Constants.HERO_IRONMAN;
		else heroIdentifier = Constants.HERO_CAPAMERICA;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<>(headers);
		try {
			responseTmpCharacter = restTemplate.exchange(
					marvelCharactersUrl.concat("?ts=1")
					.concat("&apikey=").concat(apikey)
					.concat("&hash=").concat(hash)
					.concat("&name=").concat(hero),
					HttpMethod.GET, entity,
					new ParameterizedTypeReference<Object>() {
					});

			responseExternal = mapper.convertValue(responseTmpCharacter.getBody(),
					new TypeReference<ResponseCharactersExternal>() {
					});
			
			CharacterExternal character = mapper.convertValue(responseExternal.getData().getResults().get(0),
					new TypeReference<CharacterExternal>() {
			});
			
			for(Integer i = 0; i<= character.getComics().getAvailable(); i=i+Constants.LIMIT) {
				syncCharacterComics(uuid, character, heroIdentifier, i);
				offset = i;
			}
			
			if (character.getComics().getAvailable() % 100 > 0)
				syncCharacterComics(uuid, character, heroIdentifier, offset + (character.getComics().getAvailable() % 100));
			
			updateHero(uuid, heroIdentifier, character.getId());
			
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			
			log.error("{} : Error server: {}", uuid, e.getMessage());
			throw new InternalException(e.getMessage());
		}
	}
	
	private void syncCharacterComics(String uuid, CharacterExternal character, String hero, Integer offset) throws InternalException {
		ResponseEntity<Object> responseTmpComics;
		ResponseCharactersExternal responseExternal;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Object> entity = new HttpEntity<>(headers);
		try {
			responseTmpComics = restTemplate.exchange(
					marvelCharactersUrl
					.concat("/").concat(character.getId().toString())
					.concat("/comics?ts=1&limit=").concat(Constants.LIMIT.toString())
					.concat("&offset=").concat(offset.toString())
					.concat("&apikey=").concat(apikey)
					.concat("&hash=").concat(hash),
					HttpMethod.GET, entity,
					new ParameterizedTypeReference<Object>() {
					});
			
			responseExternal = mapper.convertValue(responseTmpComics.getBody(),
					new TypeReference<ResponseCharactersExternal>() {
					});
			
			List<ComicExternal> comics = mapper.convertValue(responseExternal.getData().getResults(),
					new TypeReference<List<ComicExternal>>() {
			});
			
			saveCreators(uuid, hero, character.getId(), comics);
			saveCharactersComics(uuid, hero, comics);
			
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			
			log.error("{} : Error server: {}", uuid, e.getMessage());
			throw new InternalException(e.getMessage());
		}
	}

	private void saveCreators(String uuid, String hero, Integer idCharacter, List<ComicExternal> comics) {
		for (ComicExternal comic : comics) {
			for (ItemCreatorExternal itemCreator : comic.getCreators().getItems()) {
				Creator creator = new Creator();
				
				String[] parts = itemCreator.getResourceURI().split("/");
				Integer idCreator = Integer.parseInt(parts[parts.length -1]);
						
				List<Creator> creators = creatorRepository.findByIdCreatorIdCharacterIdComic(idCreator, idCharacter, comic.getId());
				if(creators.isEmpty()) {
					creator.setIdCharacter(idCharacter);
					creator.setIdComic(comic.getId());
					creator.setIdCreator(idCreator);
				} else {
					creator = creators.get(0);
				}
				
				creator.setHero(hero);
				creator.setName(itemCreator.getName());
				creator.setDescriptionRole(itemCreator.getRole());
				
				if (itemCreator.getRole().toLowerCase().contains(Constants.CREATOR_EDITOR))
					creator.setRole(Constants.CREATOR_EDITOR);
				if (itemCreator.getRole().toLowerCase().contains(Constants.CREATOR_WRITER))
					creator.setRole(Constants.CREATOR_WRITER);
				if (itemCreator.getRole().toLowerCase().contains(Constants.CREATOR_COLORIST))
					creator.setRole(Constants.CREATOR_COLORIST);
				
				creatorRepository.save(creator);
			}
		}	
	}
	
	private void saveCharactersComics(String uuid, String hero, List<ComicExternal> comics) {		
		for (ComicExternal comic : comics) {
			for (ItemCharacterExternal itemCharacter : comic.getCharacters().getItems()) {
				CharacterComic character = new CharacterComic();
				
				String[] parts = itemCharacter.getResourceURI().split("/");
				Integer idCharacter = Integer.parseInt(parts[parts.length -1]);
				
				List<CharacterComic> charactersComic = characterComicRepository.findByIdCharacterIdComicHero(idCharacter, comic.getId(), hero);
				if(charactersComic.isEmpty()) {
					character.setHero(hero);
					character.setIdCharacter(idCharacter);
					character.setIdComic(comic.getId());
				} else {
					character = charactersComic.get(0);
				}
				character.setName(itemCharacter.getName());
				character.setTitleComic(comic.getTitle());
				
				characterComicRepository.save(character);
			}
		}
		
	}
	
	private void updateHero(String uuid, String hero, Integer idCharacter) {
		Hero heroSave = heroRepository.findByHero(hero);
		if (heroSave != null) {
			heroSave.setLastSync(LocalDateTime.now());
			heroRepository.save(heroSave);
		} else {
			Hero newHero = new Hero();
			newHero.setHero(hero);
			newHero.setIdCharacter(idCharacter);
			newHero.setLastSync(LocalDateTime.now());
			newHero.setName(hero);
		}
	}

}

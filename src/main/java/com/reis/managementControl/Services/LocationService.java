package com.reis.managementControl.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.managementControl.Entities.Location;
import com.reis.managementControl.Repositories.LocationRepository;

@Service
public class LocationService {

	@Autowired
	private LocationRepository repository;
	
	public List<Location> findAll(){
		return repository.findAll();
	}
	
	public Location findById(Long id) {
		Location location = repository.findById(id).orElseThrow();
		return location;
	}
	
	public void save (Location location) {
		repository.save(location);
	}
	
	public void update(Location location) {
		repository.save(location);
	}
	
	public void delete(Location location) {
		repository.deleteById(location.getId());
	}
}

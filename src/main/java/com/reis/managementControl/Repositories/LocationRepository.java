package com.reis.managementControl.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.managementControl.Entities.Location;

public interface LocationRepository extends JpaRepository<Location, Long>  {

}

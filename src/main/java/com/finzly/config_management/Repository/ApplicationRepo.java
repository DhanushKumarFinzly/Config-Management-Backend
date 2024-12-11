package com.finzly.config_management.Repository;

import com.finzly.config_management.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application,String> {

}

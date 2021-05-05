package com.mahesh.ipl.ipldashboard.repository;

import com.mahesh.ipl.ipldashboard.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findByTeamName(String teamName);
}

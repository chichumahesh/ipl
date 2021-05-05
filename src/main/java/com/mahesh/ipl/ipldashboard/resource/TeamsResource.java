package com.mahesh.ipl.ipldashboard.resource;

import com.mahesh.ipl.ipldashboard.model.Match;
import com.mahesh.ipl.ipldashboard.model.Team;
import com.mahesh.ipl.ipldashboard.repository.MatchRepository;
import com.mahesh.ipl.ipldashboard.repository.TeamRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TeamsResource {

    private TeamRepository teamRepository;
    private MatchRepository matchRepository;

   public TeamsResource (TeamRepository teamRepository, MatchRepository matchRepository) {
       this.teamRepository = teamRepository;
       this.matchRepository = matchRepository;
   }

    @GetMapping("/teams/{teamName}")
    public Team getTeamInfo(@PathVariable("teamName") String teamName) {
        Team team = teamRepository.findByTeamName(teamName);
        // try using jpql
        List<Match> matchList = matchRepository.findLatestMatchByTeam(teamName, 4);
        team.setMatches(matchList);

        return team;
    }
}

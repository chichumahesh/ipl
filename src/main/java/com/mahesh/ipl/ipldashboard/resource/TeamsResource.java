package com.mahesh.ipl.ipldashboard.resource;

import com.mahesh.ipl.ipldashboard.model.Match;
import com.mahesh.ipl.ipldashboard.model.Team;
import com.mahesh.ipl.ipldashboard.repository.MatchRepository;
import com.mahesh.ipl.ipldashboard.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class TeamsResource {

    private TeamRepository teamRepository;
    private MatchRepository matchRepository;

    @Autowired
    EntityManager entityManager;

//   public TeamsResource() {
//
//   }

  //@Autowired
   public TeamsResource (TeamRepository teamRepository, MatchRepository matchRepository) {
       this.teamRepository = teamRepository;
       this.matchRepository = matchRepository;
   }

    @GetMapping("/team")
    public List<Team> getAllTeams() {
        List<Team> teamList =  teamRepository.findAll();
        return teamList;
    }

    @GetMapping("/teams/{teamName}")
    public Team getTeamInfo(@PathVariable("teamName") String teamName) {
        Team team = teamRepository.findByTeamName(teamName);

        List<Match> matchList = matchRepository.findLatestMatchByTeam(teamName, 4);

        //*************JPQL with out pageable
//        List<Match> matchList = entityManager.createQuery("select m from Match m where m.team1=:teamName or m.team2=:teamName order by m.date desc", Match.class)
//                .setParameter("teamName", teamName)
//                .setMaxResults(4).getResultList()
//                .stream()
//                .collect(Collectors.toList());

        //***********JPQL with Pageable
        //List<Match> matchList = matchRepository.getMatchInfoOfTeamJPQL(teamName, teamName, PageRequest.of(0,4));
        team.setMatches(matchList);

        return team;
    }

    @GetMapping("/teams/{teamName}/matches")
    public List<Match> getMatchesofTeam(@PathVariable("teamName") String teamName, @RequestParam int year) {

        LocalDate startDate = LocalDate.of(year,1,1);
        LocalDate endDate = LocalDate.of(year + 1, 1,1);

        return matchRepository.getMatchesByTeamBetweenDates(teamName, startDate, endDate);

       // return matchRepository.getByTeam1AndDateBetweenOrTeam2AndDateBetweenOrderByDateDesc(teamName,startDate,endDate,teamName,startDate,endDate);
    }

}

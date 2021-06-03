package com.mahesh.ipl.ipldashboard.repository;

import com.mahesh.ipl.ipldashboard.model.Match;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {


    @Query("from Match m where m.team1=:teamName1 or m.team2=:teamName2 order by m.date desc")
    List<Match> getMatchInfoOfTeamJPQL(@Param("teamName1") String teamName1, @Param("teamName2") String teamName2, Pageable pageable);

    List<Match> getByTeam1OrTeam2OrderByDateDesc(String teamName1, String teamName2, Pageable pageable);

    // because of presidence LHS of OR will be executed separately following RHS of OR so modified with the below method
   // List<Match> getByTeam1OrTeam2AndDateBetweenOrderByDateDesc(String teamName1, String teamName2, LocalDate fromDate, LocalDate toDate );

//    List<Match> getByTeam1AndDateBetweenOrTeam2AndDateBetweenOrderByDateDesc(
//            String teamName1, LocalDate fromDate,
//            LocalDate toDate, String teamName2,
//            LocalDate fromDate1, LocalDate toDate1 );

    @Query("from Match m where (m.team1 = :teamName or m.team2 = :teamName) and m.date between :dateStart and :dateEnd order by m.date desc")
    List<Match> getMatchesByTeamBetweenDates(@Param("teamName") String teamName,
                                             @Param("dateStart") LocalDate fromDate,
                                             @Param("dateEnd") LocalDate toDate);

    default List<Match> findLatestMatchByTeam(String teamName, int count) {
        return getByTeam1OrTeam2OrderByDateDesc(teamName, teamName, PageRequest.of(0,count));
    }
}

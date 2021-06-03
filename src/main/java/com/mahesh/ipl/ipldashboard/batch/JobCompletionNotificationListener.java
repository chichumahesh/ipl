package com.mahesh.ipl.ipldashboard.batch;

import com.mahesh.ipl.ipldashboard.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            Map<String, Team> teamData = new HashMap<>();

            entityManager.createQuery("select m.team1, count(m.id) from Match m group by m.team1", Object[].class)
                    .getResultList()
                    .stream()
                    .map(obj -> new Team((String) obj[0], (long) obj[1]))
                    .forEach(team -> teamData.put(team.getTeamName(), team));


            entityManager.createQuery("select m.team2, count(m.id) from Match m group by m.team2", Object[].class)
                    .getResultList()
                    .stream()
                    .forEach(e -> {
                        Team team = teamData.get((String) e[0]);
                        team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
                    });

            entityManager.createQuery("select m.winner, count(m.id) from Match m group by m.winner", Object[].class)
                    .getResultList()
                    .stream()
                    .forEach(e -> {
                        Team team = teamData.get((String) e[0]);
                        if (team != null) {
                            team.setTotalWins((long) e[1]);
                        }
                    });

            teamData.values().forEach(team -> entityManager.persist(team));

            teamData.values().forEach(team -> System.out.println(team));


//           jdbcTemplate.query("SELECT team1, team2, date FROM match",
//                    (rs, row) -> "Team1 .." + rs.getString(1) + "Team2..." + rs.getString(2) +" Date...."+ rs.getString(3)
//            ).forEach(str -> System.out.println(str));


//            List<Match> matchList = jdbcTemplate.query("SELECT team1, team2, date FROM match order by date desc limit 4",
//                    (resultSet, i) -> {
//
//                Match match = new Match();
//                match.setTeam1(resultSet.getString(1));
//                match.setTeam2(resultSet.getString(2));
//                match.setDate(new java.sql.Date(resultSet.getDate(3).getTime()).toLocalDate());
//
//
//                return match;
//            });
//            System.out.println(matchList);

        }
    }

}
